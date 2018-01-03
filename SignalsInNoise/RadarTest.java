
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.awt.*;

/**
 * Write a description of test class RadarTest here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RadarTest
{
    /**
     * Default constructor for objects of class RadarTest
     */
    public RadarTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @Test
    public void testRadar()
    {
        Radar radar = new Radar(100, 100);
        int testdir = (int)(Math.random()*36)*10;
        Location testloc = new Location((int)(Math.random()*100), (int)(Math.random()*100));
        radar.setMonsterLocation(testloc);
        radar.setMonsterDir(testdir);

        while (true) {
            for (int i = 0; i < 100; i++) {
                radar.colData();
                radar.moveMonster();
            }
            radar.moveScan();
            double[] confidence = radar.getConfidence();
            if (confidence[0] > 15 && confidence[1] > 10) {
                break;
            }
        }
        Point origin = radar.getMonsterOrigin();
        //Deviation by 1 in the origin point is acceptable for a moving monster (You can track the same path with a different origin
        assertEquals(origin.x, testloc.getRow(), 1);
        assertEquals(origin.y, testloc.getCol(), 1);
        assertEquals(testdir, radar.getMonsterDir());
    }
}
