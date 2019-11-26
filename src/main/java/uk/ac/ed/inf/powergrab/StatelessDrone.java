package uk.ac.ed.inf.powergrab;


import java.util.ArrayList;

public class StatelessDrone extends Drone {

    public StatelessDrone(Position startPosition, Map map, Long seed) {
        super(startPosition, map, seed);
    }

    @Override
    public Direction pickDirection(){
        int maxInd = 0;
        double maxHeuristic = 0;
        Direction[] possibleDirections = Direction.values();
        ArrayList<Integer> zeroStationsIndices = new ArrayList<Integer>();

        for(int i = 0; i < possibleDirections.length; i++){
            Direction direction = possibleDirections[i];
            Position positionAfterMoving = this.position.nextPosition(direction);

            // Don't consider positions outwith play area
            if (!positionAfterMoving.inPlayArea()) continue;

            PowerStation powerStation = this.map.getInRangePowerStation(positionAfterMoving);
            double heuristic = 0;

            if(powerStation != null){
                // Calculate heuristic if a power station is in range
                heuristic = powerStation.getPower() + powerStation.getCoins();
            }else {
                // Store 'safe' power station index
                zeroStationsIndices.add(i);
            }

            // Update max heuristic
            if(heuristic > maxHeuristic){
                maxHeuristic = heuristic;
                maxInd = i;
            }
        }

        Direction direction;

        // Case where there are no green stations in range
        if(maxHeuristic == 0){
            // Choose a random 'safe' direction:
            int randomDirectionIndex = rnd.nextInt(zeroStationsIndices.size());
            int randomZeroIndex = zeroStationsIndices.get(randomDirectionIndex);
            direction = possibleDirections[randomZeroIndex];
        }else{
            // In this case there is either green station(s) in range or the drone is surrounded by red stations

            // Choose the direction of the best station
            direction = possibleDirections[maxInd];
        }

        return direction;
    }
}
