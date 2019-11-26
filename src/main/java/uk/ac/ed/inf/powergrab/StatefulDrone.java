package uk.ac.ed.inf.powergrab;

public class StatefulDrone extends Drone {

    public StatefulDrone(Position startPosition, Map map, Long seed) {
        super(startPosition, map, seed);
    }

    @Override
    Direction pickDirection() {
        Position targetPosition = getTargetPowerStationPosition();

        double deltaLatitude = targetPosition.latitude - this.position.latitude;
        double deltaLongitude = targetPosition.longitude - this.position.longitude;

        double angleToTargetPosition = Math.toDegrees(Math.atan2(deltaLongitude, deltaLatitude));

        if(angleToTargetPosition < 0){
            angleToTargetPosition += 360;
        }

        Direction directionToMove = getClosestDirectionToAngle(angleToTargetPosition);
        //while(!this.position.nextPosition(directionToMove).inPlayArea()){
            //TODO
        //}
        return getClosestDirectionToAngle(angleToTargetPosition);
    }

    private Direction getClosestDirectionToAngle(double angle){
        Direction[] allDirections = Direction.values();
        double directionIncrements = 360.0 / allDirections.length;
        int indexOfClosestAngle =  ((int) Math.round(angle / directionIncrements)) % allDirections.length;

        return allDirections[indexOfClosestAngle];
    }

    private Position getTargetPowerStationPosition(){
        PowerStation[] powerStations = this.map.getPowerStations();

        PowerStation bestPowerStation = powerStations[0];
        double bestHeuristic = this.calculatePowerStationHeuristic(bestPowerStation);
        for(PowerStation ps : powerStations){
            double heuristic = this.calculatePowerStationHeuristic(ps);
            if(heuristic > bestHeuristic){
                bestHeuristic = heuristic;
                bestPowerStation = ps;
            }
        }

        return bestPowerStation.getPosition();
    }

    private double calculatePowerStationHeuristic(PowerStation ps){
        double distanceToPs= ps.getDistanceToPosition(this.position);

        return (ps.getCoins() + ps.getPower()) / distanceToPs;
    }

}
