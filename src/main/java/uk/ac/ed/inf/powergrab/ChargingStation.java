package uk.ac.ed.inf.powergrab;

public class ChargingStation {
    private double coins;
    private double power;
    private Position position;
    private final double RANGE = 0.00025;

    public ChargingStation(Position position, double coins, double power){
        this.position = position;
        this.coins = coins;
        this.power = power;
    }

    public boolean inRange(Position dronePosition){
        // Returns true if dronePosition is within range of the charging station
        // Otherwise, returns false

        double distanceToDrone = this.getDistanceToPosition(dronePosition);
        return distanceToDrone <= this.RANGE;
    }

    public double takeCoins(double droneCoins){
        // Drains coins from the charging station
        // Returns the amount of coins drained

        double coinsToTake = this.coins;

        // Account for edge case where drone has less coins than the coin deficit
        // This prevents the drone's coins from going negative
        if(this.coins < 0 && Math.abs(this.coins) > droneCoins){
            coinsToTake = -droneCoins;
        }

        this.coins -= coinsToTake;
        return coinsToTake;
    }

    public double takePower(double dronePower){
        // Drains power from the charging station
        // Returns the amount of power drained

        double powerToTake = this.power;

        // Account for edge case where drone has less power than the power deficit
        // This prevents the drone's power from going negative
        if(this.power < 0 && Math.abs(this.power) > dronePower){
            powerToTake = -dronePower;
        }

        this.power -= powerToTake;
        return powerToTake;
    }

    public double getDistanceToPosition(Position position){
        // Returns euclidean distance from charging station to position

        return Math.sqrt(Math.pow((this.position.latitude - position.latitude), 2) +
                Math.pow((this.position.longitude - position.longitude), 2));
    }

    public double getCoins(){
        return this.coins;
    }

    public double getPower(){
        return this.power;
    }

    public Position getPosition(){
        return this.position;
    }
}
