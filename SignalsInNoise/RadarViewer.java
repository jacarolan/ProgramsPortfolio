import javax.swing.JFrame;
import java.util.*;
import java.lang.*;
import java.awt.*;
/**
 * Class that contains the main method for the program and creates the frame containing the component.
 * 
 * @author @gcschmit
 * @version 19 July 2014
 */
public class RadarViewer
{
    private static int count = 0;
    /**
     * main method for the program which creates and configures the frame for the program
     *
     */
    public static void main(String[] args) throws Exception
    {
        // create the radar, set the monster location, and perform the initial scan
        final int ROWS = 100;
        final int COLS = 100;
        final int WAITTIME = 10;
        Radar radar = new Radar(ROWS, COLS);
        //NOTE: This must be kept low for moving monster detectio to be possible
        radar.setNoiseFraction(0.05);

        // prompt the user to optionally enter the location of the monster
        //  (if they don't, leave the location randomly determined)
        int scol = (int)(Math.random()*100);
        int srow = (int)(Math.random()*100);
        int sdir = (int)(Math.random()*36)*10;
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter the start row (-1 for random, must be between 0 and 99 inclusive): ");
        int inp = in.nextInt();
        if(inp > 0) {
            srow = inp;
        }
        System.out.println("Please enter the start column (-1 for random, must be between 0 and 99 inclusive): ");
        inp = in.nextInt();
        if(inp > 0) {
            scol = inp;
        }
        System.out.println("Please enter the start direction (-1 for random, must be multiple of 10 between 0-350 inclusive): ");
        inp = in.nextInt();
        if(inp > 0) {
            sdir = inp;
        }
        //
        // !!! add code here !!!
        //

        radar.scan();
        JFrame frame = new JFrame();
        frame.setTitle("Signals in Noise Lab");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // a frame contains a single component; create the radar component and add it to the frame
        RadarComponent component = new RadarComponent(radar);
        frame.add(component);

        // set the size of the frame to encompass the contained component
        frame.pack();

        // make the frame visible which will result in the paintComponent method being invoked on the
        //  component.
        frame.setVisible(true);
        radar.setMonsterLocation(new Location(srow, scol));
        radar.setMonsterDir(sdir);

        // perform 100 scans of the radar wiht a slight pause between each
        // after each scan, instruct the Java Run-Time to redraw the window
        System.out.println("ACTUAL MONSTER ORIGIN (IN FORM ROW | COL): " + radar.findMonster().getRow()+ " | "+ radar.findMonster().getCol());
        System.out.println("ACTUAL MONSTER DIRECTION: " + radar.getMonsterDir());
        while (true) {
            System.out.println("Scanning...");
            for (int i = 0; i < 100; i++) {
                Thread.sleep(WAITTIME);

                radar.colData();
                radar.moveMonster();

                frame.repaint();
                count++;
            }
            radar.moveScan();
            double[] confidence = radar.getConfidence();
            if (confidence[0] > 15 && confidence[1] > 10) {
                break;
            } else {
                System.out.println("Confidence was too low, rescanning...");
            }
        }
        System.out.println("Monster has been tracked, displaying location graphically");
        Point origin = radar.getMonsterOrigin();
        int dir = radar.getMonsterDir();
        while (true) {
            int col = (int)(origin.y+Math.cos(toRadians(dir))*count);
            int row = (int)(origin.x+Math.sin(toRadians(dir))*count);
            while (row > 99) {
                row -= 100;
            }
            while (col > 99) {
                col -= 100;
            }
            while (row < 0) {
                row += 100;
            }
            while (col < 0) {
                col += 100;
            }
            component.setMonsterLoc(new Location(row, col));
            Thread.sleep(100);
            radar.scan();
            radar.moveMonster();
            frame.repaint();
            count++;
        }
    }

    /**
     * A copy of the method to convert degrees to radians
     * See radar for documentation
     */
    private static double toRadians(double deg) {
        return deg/180.0*Math.PI;
    }

    /**
     * A copy of the method to find a point a length and direction from another point
     * See radar for documentation
     */
    private static Point lengthDir(Point o, double len, double dir) throws Exception{
        return new Point((int)(o.x+Math.cos(dir)*len), (int)(o.y+Math.sin(dir)*len));
    }
}
