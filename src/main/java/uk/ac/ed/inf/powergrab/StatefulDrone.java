package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

public class StatefulDrone extends Drone {
    private ArrayList<Position> visitedPositions;

    public StatefulDrone(Position startPosition, Map map, Long seed) {
        super(startPosition, map, seed);
        this.visitedPositions = new ArrayList<Position>();
    }

    public void move(){
        super.move();

        // Record move
        this.visitedPositions.add(this.position);
    }

    @Override
    Direction pickDirection() {
        Position targetPosition = getTargetPowerStationPosition();

        double deltaLatitude = targetPosition.latitude - this.position.latitude;
        double deltaLongitude = targetPosition.longitude - this.position.longitude;

        double angleToTargetPosition = Math.toDegrees(Math.atan2(deltaLongitude, deltaLatitude));

        if(angleToTargetPosition < 0) angleToTargetPosition += 360;

        Direction directionToMove = getClosestDirectionToAngle(angleToTargetPosition);

        return directionToMove;
    }

    private Direction getClosestDirectionToAngle(double angle){
        Direction[] allDirections = Direction.values();
        Direction bestDirection = null;
        double leastError = 360;
        Direction reverseDirection = null;
        for(int i = 0; i < allDirections.length; i++){
            Direction direction = allDirections[i];
            Position nextPosition = this.position.nextPosition(direction);

            // Skip directions which lead drone out of play area
            if(!nextPosition.inPlayArea()){
                continue;
            }

            if(this.visitedPositions.size() > 1){
                if(this.visitedPositions.get(this.visitedPositions.size() - 2).equals(nextPosition)){
                    reverseDirection = direction;
                    continue;
                }
            }

            ChargingStation psAtDirection = this.map.getInRangePowerStation(nextPosition);
            if(psAtDirection != null){
                if(psAtDirection.getCoins() < 0){
                    continue;
                }
            }

            double directionAngle = direction.degrees();

            // Get difference between angles
            // Account for fact 0 is close to 359 degrees
            double error = 180 - Math.abs(Math.abs(angle - directionAngle) - 180);

            if(error < leastError){
                bestDirection = direction;
                leastError = error;
            }

        }

        if(bestDirection == null){
            bestDirection = reverseDirection;
        }

        return bestDirection;
    }

    private Position getTargetPowerStationPosition(){
        ChargingStation[] chargingStations = this.map.getChargingStations();

        Position bestPowerStationPosition = chargingStations[0].getPosition();
        double bestHeuristic = this.calculatePowerStationHeuristic(chargingStations[0]);
        for(ChargingStation ps : chargingStations){
            double heuristic = this.calculatePowerStationHeuristic(ps);
            if(heuristic > bestHeuristic){
                bestHeuristic = heuristic;
                bestPowerStationPosition = ps.getPosition();
            }
        }

        if(bestHeuristic == 0){
            bestPowerStationPosition = this.visitedPositions.get(this.visitedPositions.size() - 1);
        }

        return bestPowerStationPosition;
    }

    private double calculatePowerStationHeuristic(ChargingStation ps){
        double distanceToPs= ps.getDistanceToPosition(this.position);

        return (ps.getCoins() + ps.getPower()) / Math.pow(distanceToPs, 3);
    }

}
