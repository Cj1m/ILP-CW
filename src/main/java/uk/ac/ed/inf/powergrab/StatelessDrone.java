package uk.ac.ed.inf.powergrab;


import java.util.ArrayList;

public class StatelessDrone extends Drone {

    public StatelessDrone(Position startPosition, Map map, Long seed) {
        super(startPosition, map, seed);
    }

    public Direction pickDirection(){
        int maxInd = 0;
        double maxValue = 0;
        ArrayList<Integer> zeroIndices = new ArrayList<Integer>();
        //TODO case where stateless drone is surrounded by negative powerstations
        for(int i = 0; i < 16; i++){
            Direction direction = Direction.values()[i];
            Position positionAfterMoving = this.position.nextPosition(direction);
            PowerStation[] powerStations = this.map.getPowerStationsInRange(positionAfterMoving);

            if (powerStations.length > 0){
                double heuristic = 0;
                for(PowerStation ps : powerStations){
                    heuristic += ps.getPower() + ps.getCoins();
                }

                if(heuristic > maxValue && positionAfterMoving.inPlayArea()){
                    maxValue = heuristic;
                    maxInd = i;
                }
            }else{
                if(positionAfterMoving.inPlayArea()){
                    zeroIndices.add(i);
                }
            }
        }

        Direction direction;
        if(maxValue > 0){
            direction = Direction.values()[maxInd];
        }else{
            System.out.println(zeroIndices.size());
            int randomDirectionIndex = rnd.nextInt(zeroIndices.size());
            int randomZeroIndex = zeroIndices.get(randomDirectionIndex);
            direction = Direction.values()[randomZeroIndex];
        }

        return direction;
    }
}
