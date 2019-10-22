package uk.ac.ed.inf.powergrab;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StatelessDroneTest extends TestCase {
    private Map map;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public StatelessDroneTest(String testName) {
        super( testName );
        map = new Map("16","10","2019");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(StatelessDroneTest.class);
    }

    public void testCollectPowerAndCoinsPositive(){
        //Position of a positive power station
        Position position = new Position(55.9435574,-3.1921482);
        StatelessDrone drone = new StatelessDrone(position, map);

        drone.collectPowerAndCoins();

        assertEquals(332.210345174026, drone.power);
        assertEquals(23.553098722640247, drone.coins);
    }

    public void testCollectPowerAndCoinsNegative(){
        //Position of a negative power station
        Position position = new Position(55.9461935,-3.1888266);
        StatelessDrone drone = new StatelessDrone(position, map);

        drone.collectPowerAndCoins();

        assertEquals(165.94237282354469, drone.power);
        assertEquals(0.0, drone.coins);
    }

    public void testPickDirection(){
        //Position just south of a positive power station
        Position position = new Position(55.9457488 - 0.00025,-3.1895536);
        StatelessDrone drone = new StatelessDrone(position, map);

        Direction directionToMove = drone.pickDirection();

        assertEquals(Direction.N, directionToMove);
    }

    public void testMove(){
        //Position just south of a positive power station
        Position dronePosition = new Position(55.9457488 - 0.00025,-3.1895536);
        Position droneExpectedNextPosition = new Position(55.9457488 - 0.00025 + 0.0003,-3.1895536);
        StatelessDrone drone = new StatelessDrone(dronePosition, map);

        drone.move();

        assertTrue(AppTest.approxEq(droneExpectedNextPosition, drone.position));
    }
}
