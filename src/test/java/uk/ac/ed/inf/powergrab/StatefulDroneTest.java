package uk.ac.ed.inf.powergrab;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StatefulDroneTest extends TestCase {

    private Map map;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public StatefulDroneTest(String testName) {
        super( testName );
        this.map = new Map("16","10","2019");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(StatefulDroneTest.class);
    }

    public void testPickDirection(){
        //Position just south of a positive power station
        Position position = new Position(55.9457488 - 0.00025,-3.1895536);
        StatefulDrone drone = new StatefulDrone(position, this.map);

        Direction directionToMove = drone.pickDirection();

        assertEquals(Direction.N, directionToMove);
    }

    public void testAlwaysInPlayArea(){
        //Position just south of a positive power station
        Position position = new Position(55.944425, -3.188396);
        StatefulDrone drone = new StatefulDrone(position, this.map);

        boolean alwaysInPlayArea = true;
        for(int i = 0; i < 250; i++){
            drone.move();
            if(!drone.position.inPlayArea()){
                alwaysInPlayArea = false;
                break;
            }
        }

        assertTrue(alwaysInPlayArea);
    }

    public void testNeverLoseCoins(){
        //Position just south of a positive power station
        Position position = new Position(55.944425, -3.188396);
        StatefulDrone drone = new StatefulDrone(position, this.map);
        double previousCoins = 0;

        boolean neverLoseCoins = true;
        for(int i = 0; i < 250; i++){
            drone.move();
            if(drone.coins < previousCoins){
                neverLoseCoins = false;
                break;
            }
            previousCoins = drone.coins;
        }

        assertTrue(neverLoseCoins);
    }
}
