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
        // App must be run with exactly 7 execution parameters
        if(args.length != 7) throw new IllegalArgumentException();

        // Store data from execution parameters
        String day = args[0];
        String month = args[1];
        String year = args[2];
        double droneStartLatitude = Double.parseDouble(args[3]);
        double droneStartLongitude = Double.parseDouble(args[4]);
        long seed = Long.parseLong(args[5]);
        String droneType = args[6];

        // Create Map and Drone
        Map map = new Map(day,month,year);
        Position startPosition = new Position(droneStartLatitude, droneStartLongitude);
        Drone drone = null;
        if(droneType.equals("stateless")){
            drone = new StatelessDrone(startPosition, map, seed);
        }else if (droneType.equals("stateful")){
            drone = new StatefulDrone(startPosition, map, seed);
        }else{
            // Drone must be either stateless or stateful
            throw new IllegalArgumentException();
        }

        // Move drone 250 times
        for(int i  = 0; i < 250; i++) {
            drone.move();
        }

        // When finished, save the geojson map and the flight log
        try {
            map.saveMapToFile(droneType, day, month, year);
            drone.saveLogToFile(droneType, day, month, year);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
