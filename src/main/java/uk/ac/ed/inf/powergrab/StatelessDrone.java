package uk.ac.ed.inf.powergrab;

import java.util.Random;

public class StatelessDrone {
    private Random rnd;
    private Position position;
    private float coins;
    private float power;

    public StatelessDrone(Position startPosition){
        this.position = startPosition;
        rnd = new Random(5678);
    }

    public void move(){
        Position lastPosition = position;
        Direction direction = pickDirection();
        logMovement(lastPosition, direction, this.position, this.coins, this.power);
    }

    public Direction pickDirection(){
        for(int i = 0; i < 16; i++){
            Direction direction = Direction.values()[i];
            if (direction is good){
                return direction;
            }
        }



        int randomDirectionIndex = rnd.nextInt(16);
    }

    private void logMovement(Position previousPosition,
                             Direction direction,
                             Position nextPosition,
                             float coins, float power){


    }
}
