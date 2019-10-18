package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

public class StatelessDrone {
    private Random rnd;
    private Position position;
    private Map map;
    private float coins;
    private float power;

    public StatelessDrone(Position startPosition, Map map){
        this.position = startPosition;
        this.map = map;
        rnd = new Random(5678);
    }

    public void move(){
        Position lastPosition = position;
        Direction direction = pickDirection();
        this.position = this.position.nextPosition(direction);
        collectPowerAndCoins();
        logMovement(lastPosition, direction, this.position, this.coins, this.power);
    }

    public Direction pickDirection(){
        int maxInd = 0;
        double maxValue = 0;
        ArrayList<Integer> zeroIndices = new ArrayList<Integer>();

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

    public void collectPowerAndCoins(){
        PowerStation[] powerStationsInRange = map.getPowerStationsInRange(this.position);
        //TODO remember your coins and power cant go negative homie
        //GAY SHIT
        float jahcoinsbefore = this.coins;
        float jahpowerbefore = this.power;
        //END OF GAY SHIT
        for(PowerStation ps : powerStationsInRange){
            this.coins += ps.takeCoins();
            this.power += ps.takePower();
        }
        //MORE GAY SHIt
        System.out.println("Coins gain:" + (this.coins - jahcoinsbefore));
        System.out.println("Power gain:" + (this.power - jahpowerbefore));
        //END OF MORe GAY SHit
    }

    private void logMovement(Position previousPosition,
                             Direction direction,
                             Position nextPosition,
                             float coins, float power){
        map.addPath(previousPosition, nextPosition);
    }
}
