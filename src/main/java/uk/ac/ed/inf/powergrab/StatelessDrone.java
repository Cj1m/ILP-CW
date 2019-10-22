package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

public class StatelessDrone {
    private Random rnd;
    private Map map;
    public Position position;
    public double coins;
    public double power;
    private final double POWER_TO_MOVE = 1.25;

    public StatelessDrone(Position startPosition, Map map){
        this.position = startPosition;
        this.power = 250;
        this.map = map;
        rnd = new Random(5678);

        //Add start position to flight path
        this.map.addFlightPathPoint(startPosition);
    }

    public void move(){
        if(!hasPowerToMove()){
            return;
        }

        Direction direction = pickDirection();
        this.position = this.position.nextPosition(direction);
        this.power -= this.POWER_TO_MOVE;
        collectPowerAndCoins();
        logMovement(direction, this.position, this.coins, this.power);
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
        //GAY SHIT
        double jahcoinsbefore = this.coins;
        double jahpowerbefore = this.power;
        //END OF GAY SHIT
        for(PowerStation ps : powerStationsInRange){
            this.coins += ps.takeCoins(this.coins);
            this.power += ps.takePower(this.power);
        }
        //MORE GAY SHIT
        System.out.println("Coins gain:" + (this.coins - jahcoinsbefore));
        System.out.println("Power gain:" + (this.power - jahpowerbefore));
        //END OF MORe GAY SHIT
    }

    public boolean hasPowerToMove(){
        return this.power >= this.POWER_TO_MOVE;
    }

    private void logMovement(Direction direction,
                             Position nextPosition,
                             double coins, double power){
        map.addFlightPathPoint(nextPosition);
    }
}
