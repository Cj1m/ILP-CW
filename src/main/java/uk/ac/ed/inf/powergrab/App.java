package uk.ac.ed.inf.powergrab;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Map map = new Map("16","10","2019");
        Position startPosition = new Position(55.944425, -3.188396);
        StatelessDrone drone = new StatelessDrone(startPosition, map);

        for(int i  = 0; i < 250; i++) {
            drone.move();
        }

        try {
            map.saveMapToFile("stateless","16","10","2019");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
