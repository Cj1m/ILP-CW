package uk.ac.ed.inf.powergrab;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringJoiner;

public class StatelessDrone {
    private Random rnd;
    private Map map;
    public Position position;
    public double coins;
    public double power;
    private StringJoiner movementLog;
    private final double POWER_TO_MOVE = 1.25;

    public StatelessDrone(Position startPosition, Map map, Long seed){
        this.position = startPosition;
        this.power = 250;
        this.map = map;
        this.rnd = new Random(seed);
        this.movementLog = new StringJoiner("\n");

        //Add start position to flight path
        this.map.addFlightPathPoint(startPosition);
    }

    public void move(){
        if(!hasPowerToMove()){
            return;
        }

        Position priorPosition = this.position;

        Direction directionToMove = pickDirection();
        this.position = this.position.nextPosition(directionToMove);
        this.power -= this.POWER_TO_MOVE;

        collectPowerAndCoins();

        map.addFlightPathPoint(this.position);
        logMovement(priorPosition, directionToMove, this.position, this.coins, this.power);
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

        for(PowerStation ps : powerStationsInRange){
            this.coins += ps.takeCoins(this.coins);
            this.power += ps.takePower(this.power);
        }
    }

    public boolean hasPowerToMove(){
        return this.power >= this.POWER_TO_MOVE;
    }

    private void logMovement(Position priorPosition,
                             Direction direction,
                             Position nextPosition,
                             double coins, double power){
        String logEntry = String.format("%f,%f,%s,%f,%f,%f,%f", priorPosition.latitude, priorPosition.longitude,
                direction.name(), nextPosition.latitude, nextPosition.longitude, coins, power);
        movementLog.add(logEntry);
    }

    public void saveLogToFile(String droneType, String day, String month, String year) throws IOException {
        // Build movementLog String
        String fileContent = movementLog.toString();

        // Filename in the form: dronetype-DD-MM-YYYY.txt
        String filename = String.format("./%s-%s-%s-%s.txt", droneType, day, month, year);

        // Write content to txt file
        FileWriter fileWriter = new FileWriter(filename);
        fileWriter.write(fileContent);
        fileWriter.close();
    }
}
