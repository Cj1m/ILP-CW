package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

public class StatefulDrone extends Drone {
    private ChargingStation targetStation;
    private ArrayList<Position> visitedPositions;

    public StatefulDrone(Position startPosition, Map map) {
        super(startPosition, map);
        this.targetStation = getTargetChargingStation();
        this.visitedPositions = new ArrayList<Position>();
    }

    @Override
    public void move(){
        super.move();

        // Find a new target charging station when current target charging station is reached
        ChargingStation inRangeStation = this.map.getInRangeChargingStation(this.position);
        if(targetStation.equals(inRangeStation)) this.targetStation = getTargetChargingStation();

        // Record move
        this.visitedPositions.add(this.position);
    }

    @Override
    protected Direction pickDirection() {
        // Determine angle to move
        double angleToTarget = getAngleToTargetPosition();

        // Determine the closest Direction to the desired angle
        Direction directionToMove = getClosestDirectionToAngle(angleToTarget);

        return directionToMove;
    }

    private Direction getClosestDirectionToAngle(double angle){
        // Returns a Direction which is approximately in the direction of angle
        // while avoiding going out of bounds and negative charging stations

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

            // Skip directions which will revert drone back to its previous position
            // This helps to avoid drone getting stuck in forward-reverse loop
            if(this.visitedPositions.size() > 1){
                Position previousPosition = this.visitedPositions.get(this.visitedPositions.size() - 2);
                if(previousPosition.equals(nextPosition)){
                    // Store reverse direction in case drone has to resort to this
                    reverseDirection = direction;
                    continue;
                }
            }

            // Skip directions which lead to negative charging stations
            ChargingStation csAtDirection = this.map.getInRangeChargingStation(nextPosition);
            if(csAtDirection != null){
                if(csAtDirection.getCoins() < 0){
                    continue;
                }
            }

            // Get difference between angles
            // Account for fact 0 is close to 359 degrees
            double directionAngle = direction.degrees();
            double error = 180 - Math.abs(Math.abs(angle - directionAngle) - 180);

            if(error < leastError){
                bestDirection = direction;
                leastError = error;
            }
        }

        // In some cases the only available direction is the reverse direction
        if(bestDirection == null) bestDirection = reverseDirection;

        return bestDirection;
    }

    private ChargingStation getTargetChargingStation(){
        // Returns position of charging station with best heuristic

        ChargingStation[] chargingStations = this.map.getChargingStations();

        // Set initial 'best' values
        ChargingStation bestChargingStation = chargingStations[0];
        double bestHeuristic = this.calculateChargingStationHeuristic(chargingStations[0]);

        // Find charging station with the highest heuristic
        for(ChargingStation cs : chargingStations){
            double heuristic = this.calculateChargingStationHeuristic(cs);
            if(heuristic > bestHeuristic){
                bestHeuristic = heuristic;
                bestChargingStation = cs;
            }
        }

        return bestChargingStation;
    }

    private double getAngleToTargetPosition(){
        // Returns bearing of target position relative to drone position

        Position targetPosition = this.targetStation.getPosition();
        double deltaLatitude = targetPosition.latitude - this.position.latitude;
        double deltaLongitude = targetPosition.longitude - this.position.longitude;

        double angleToTargetPosition = Math.toDegrees(Math.atan2(deltaLongitude, deltaLatitude));

        // Ensure angle is between 0 and 360
        if(angleToTargetPosition < 0) angleToTargetPosition += 360;

        return angleToTargetPosition;
    }

    private double calculateChargingStationHeuristic(ChargingStation chargingStation){
        // Returns heuristic for chargingStation, taking into account power, coins and distance

        double distanceToCs = chargingStation.getDistanceToPosition(this.position);
        return (chargingStation.getCoins() + chargingStation.getPower()) / Math.pow(distanceToCs, 3);
    }

}
