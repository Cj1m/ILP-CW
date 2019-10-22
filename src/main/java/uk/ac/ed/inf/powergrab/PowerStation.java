package uk.ac.ed.inf.powergrab;

public class PowerStation {
    private double coins;
    private double power;
    private Position position;
    private final double RANGE = 0.00025;

    public PowerStation(Position position, double coins, double power){
        this.position = position;
        this.coins = coins;
        this.power = power;
    }

    public boolean inRange(Position dronePosition){
        double distanceToDrone = Math.sqrt(Math.pow((this.position.latitude - dronePosition.latitude), 2) +
                                            Math.pow((this.position.longitude - dronePosition.longitude), 2));
        return distanceToDrone <= this.RANGE;
    }

    public double takeCoins(double droneCoins){
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
        double powerToTake = this.power;

        // Account for edge case where drone has less power than the power deficit
        // This prevents the drone's power from going negative
        if(this.power < 0 && Math.abs(this.power) > dronePower){
            powerToTake = -dronePower;
        }

        this.power -= powerToTake;
        return powerToTake;
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
