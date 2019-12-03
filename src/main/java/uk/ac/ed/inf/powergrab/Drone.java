package uk.ac.ed.inf.powergrab;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.StringJoiner;

public abstract class Drone {
    protected Random rnd;
    protected Map map;
    protected Position position;
    protected double coins;
    protected double power;

    private StringJoiner movementLog;
    private final double POWER_TO_MOVE = 1.25;

    public Drone(Position startPosition, Map map, Long seed){
        this.position = startPosition;
        this.power = 250;
        this.map = map;
        this.rnd = new Random(seed);
        this.movementLog = new StringJoiner("\n");

        // Add start position to flight path
        this.map.addFlightPathPoint(startPosition);
    }

    // Must be implemented by StatelessDrone and StatefulDrone
    protected abstract Direction pickDirection();

    public void move(){
        // Only move if drone has sufficient power
        if(!hasPowerToMove()) return;

        Position priorPosition = this.position;

        // Pick a direction and move
        Direction directionToMove = pickDirection();
        this.position = this.position.nextPosition(directionToMove);
        this.power -= this.POWER_TO_MOVE;

        // Check if there are charging stations in range, charge if so
        collectPowerAndCoins();

        // Update flight path and log movement
        map.addFlightPathPoint(this.position);
        this.logMovement(priorPosition, directionToMove, this.position, this.coins, this.power);
    }

    public void collectPowerAndCoins(){
        // Checks if there is a charging station in range, charges if so

        ChargingStation nearestStation = map.getInRangeChargingStation(this.position);

        if(nearestStation != null){
            this.coins += nearestStation.takeCoins(this.coins);
            this.power += nearestStation.takePower(this.power);
        }
    }

    public boolean hasPowerToMove(){
        return this.power >= this.POWER_TO_MOVE;
    }

    private void logMovement(Position priorPosition,
                             Direction direction,
                             Position nextPosition,
                             double coins, double power){
        // Records movement to movement log

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
