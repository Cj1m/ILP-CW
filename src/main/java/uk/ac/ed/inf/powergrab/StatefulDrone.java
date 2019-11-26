package uk.ac.ed.inf.powergrab;

public class StatefulDrone extends Drone {
    private PowerStation lastVisitedPowerStation;

    public StatefulDrone(Position startPosition, Map map, Long seed) {
        super(startPosition, map, seed);
    }

    public void move(){
        super.move();

        // Record the visited power station
        PowerStation visitedPs = this.map.getInRangePowerStation(this.position);
        if(visitedPs != null) lastVisitedPowerStation = visitedPs;
    }

    @Override
    Direction pickDirection() {
        Position targetPosition = getTargetPowerStationPosition();

        double deltaLatitude = targetPosition.latitude - this.position.latitude;
        double deltaLongitude = targetPosition.longitude - this.position.longitude;

        double angleToTargetPosition = Math.toDegrees(Math.atan2(deltaLongitude, deltaLatitude));

        if(angleToTargetPosition < 0) angleToTargetPosition += 360;

        Direction directionToMove = getClosestDirectionToAngle(angleToTargetPosition);

        return getClosestDirectionToAngle(angleToTargetPosition);
    }

    private Direction getClosestDirectionToAngle(double angle){
        Direction[] allDirections = Direction.values();
        Direction bestDirection = null;
        double leastError = 360;

        for(int i = 0; i < allDirections.length; i++){
            Direction direction = allDirections[i];
            Position nextPosition = this.position.nextPosition(direction);

            // Skip directions which lead drone out of play area
            if(!nextPosition.inPlayArea()){
                continue;
            }

            PowerStation psAtDirection = this.map.getInRangePowerStation(nextPosition);
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

        return bestDirection;
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

        if(bestHeuristic == 0){
            bestPowerStation = this.lastVisitedPowerStation;
        }

        return bestPowerStation.getPosition();
    }

    private double calculatePowerStationHeuristic(PowerStation ps){
        double distanceToPs= ps.getDistanceToPosition(this.position);

        return (ps.getCoins() + ps.getPower()) / Math.pow(distanceToPs,3);
    }

}
