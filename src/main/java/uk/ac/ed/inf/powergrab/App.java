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
        String day = args[0];
        String month = args[1];
        String year = args[2];
        double droneStartLatitude = Double.parseDouble(args[3]);
        double droneStartLongitude = Double.parseDouble(args[4]);
        long seed = Long.parseLong(args[5]);
        String droneType = "stateless"; //args[6];

        Map map = new Map(day,month,year);
        Position startPosition = new Position(droneStartLatitude, droneStartLongitude);
        Drone drone = null;

        if(droneType.equals("stateless")){
            drone = new StatelessDrone(startPosition, map, seed);
        }else if (droneType.equals("stateful")){
            drone = new StatefulDrone(startPosition, map, seed);
        }

        for(int i  = 0; i < 250; i++) {
            drone.move();
        }

        try {
            map.saveMapToFile(droneType, day, month, year);
            drone.saveLogToFile(droneType, day, month, year);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
