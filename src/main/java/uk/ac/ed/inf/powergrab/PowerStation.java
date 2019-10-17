package uk.ac.ed.inf.powergrab;

public class PowerStation {
    private double coins;
    private double power;
    private Position position;

    public PowerStation(Position position, double coins, double power){
        this.position = position;
        this.coins = coins;
        this.power = power;
    }

    public double takeCoins(){
        double coinsToTake = this.coins;
        this.coins = 0;
        return coinsToTake;
    }

    public double takePower(){
        double powerToTake = this.power;
        this.power = 0;
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
