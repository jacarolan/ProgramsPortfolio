import java.lang.*;
import java.lang.Math;
import java.util.*;
import java.awt.*;
import javax.swing.*;
/**
 * The model for radar scan and accumulator
 * 
 * @author @gcschmit
 * @version 19 July 2014
 */
public class Radar
{
    // stores whether each cell triggered detection for the current scan of the radar
    // (true represents a detected monster, which may be a false positive)
    private boolean[][] currentScan;

    // value of each cell is incremented for each scan in which that cell triggers detection
    private int[][] accumulator;

    // location of the monster
    private Location monsterLocation;

    // probability that a cell will trigger a false detection (must be >= 0 and < 1)
    private double noiseFraction;

    // number of scans of the radar since construction
    private int numScans;

    //The moving monsters origin point
    private Location startMonster;

    //The monsters actual row
    private double arow;

    //The monsters actual col
    private double acol;

    //The moving monsters direction
    private int monsterDir;

    //The moving monsters direction
    private int startMonsterDir;

    //The confidence level of the moving monsters origin points (The requirement for this number should scale with grid size)
    private double confidence;

    //The confidence level of the moving monsters direction
    private double dirconfidence;

    //The "location" containing width and height of program
    private Location area;

    //The probability the monster started at this point
    private double[][] probOfPoint;

    //The entire data set over all frames
    private ArrayList<boolean[][]> data = new ArrayList<boolean[][]>();

    /**
     * Constructor for objects of class Radar
     * 
     * @param   rows    the number of rows in the radar grid
     * @param   cols    the number of columns in the radar grid
     */
    public Radar(int rows, int cols)
    {
        //THE HEADER FOR THE CONSOLE
        System.out.println("THIS TERMINAL WILL FUNCTION AS THE CONSOLE...\nUSEFUL INFORMATION WILL BE DISPLAYED HERE");
        // initialize the currentScan 2D array and the accumulator 2D array
        currentScan = new boolean[rows][cols];
        accumulator = new int[rows][cols];
        probOfPoint = new double[rows][cols];
        for (int ii = 0; ii<accumulator.length;ii++) {
            for (int i = 0; i<accumulator[0].length; i++) {
                accumulator[ii][i] = 0;
                currentScan[ii][i] = false;
            }
        }
        //
        // !!! add code here !!!
        //
        this.area = new Location(rows, cols);
        // randomly set the location of the monster (can be explicity set through the
        //  setMonsterLocation method for the unit test
        int row = (int)(Math.random() * rows);
        int col = (int)(Math.random() * cols);
        this.monsterLocation = new Location( 0,0);//row, col );

        this.noiseFraction = 0;
        this.numScans= 0;
    }

    /**
     * Directly returns the monsters current direction
     * @return  The monsters initial direction
     */
    public int getMonsterDir() {
        return startMonsterDir;
    }

    /**
     * Performs a scan of the radar. Noise is injected into the grid and the accumulator is updated.
     * This method is not meant to be called outside of this object, only functions as a standalone for a stationary monster.
     */
    public void scan()
    {
        // algorithm for performing a scan:
        //    1. set all cells in the currentScan 2D array to false
        //    2. set the location of the monster in the currentScan 2D array
        //    3. inject noise into the grid
        //    4. update the accumulator 2D array based on the state of the currentScan 2D array
        //    5. increment the numScans instance variable
        /*for (int ii = 0; ii<currentScan.length;ii++) {
        for (int i = 0; i<currentScan[0].length; i++) {
        currentScan[ii][i] = false;
        }
        }*/
        //setMonsterLocation(monsterLocation);
        injectNoise();
        for (int ii = 0; ii<accumulator.length;ii++) {
            for (int i = 0; i<accumulator[0].length; i++) {
                if (currentScan[ii][i]) {
                    accumulator[ii][i]++;
                }
            }
        }
        numScans++;
        //
        // !!! add code here !!!
        //

    }

    /**
     * Sets the location of the monster
     * 
     * @param   row     the row in which the monster is located
     * @param   col     the column in which the monster is located
     * @pre row and col must be within the bounds of the radar grid
     */
    public void setMonsterLocation(Location loc)
    {
        // remember the monster's location
        this.monsterLocation = loc;
        acol = monsterLocation.getCol();
        arow = monsterLocation.getRow();

        // update the radar grid to show that something was detected at the specified location
        currentScan[ this.monsterLocation.getRow() ][ this.monsterLocation.getCol() ] = true;
    }

    /**
     * Sets the direction of the monster
     * @param   dir     the target direction of the monster
     */
    public void setMonsterDir(int dir) {
        startMonsterDir = dir;
    }

    /**
     * Sets the probability that a given cell will generate a false detection
     * 
     * @param   fraction    the probability that a given cell will generate a flase detection expressed
     *                      as a fraction (must be >= 0 and < 1)
     */
    public void setNoiseFraction(double fraction)
    {
        this.noiseFraction = fraction;
    }

    /**
     * Returns true if the specified location in the radar grid triggered a detection.
     * 
     * @param   row     the row of the location to query for detection
     * @param   col     the column of the location to query for detection
     * @return true if the specified location in the radar grid triggered a detection
     */
    public boolean isDetected(Location loc)
    {
        return this.currentScan[ loc.getRow() ][ loc.getCol() ];
    }

    /**
     * Directly returns the location of the monster
     * @return  the location of the monster
     */
    public Location findMonster() {
        return monsterLocation;
    }

    /**
     * Returns a Location object containing the row and column of the detected monster based on analyzing the
     *  accumulator (not based on the monsterLocationRow and monsterLocationCol instance variables).
     * WORKS ONLY ON STATIONARY MONSTER
     * 
     * @return the location of the detected monster
     */
    public Location findMonsterAlgorithm()
    {
        double min = numScans;
        Location ret = new Location(0,0);
        for (int ii = 0; ii<accumulator.length;ii++) {
            for (int i = 0; i<accumulator[0].length; i++) {
                /*if (Math.abs(accumulator[ii][i]-numScans*(1-noiseFraction)) < min) {
                min = Math.abs(accumulator[ii][i]-numScans*(1-noiseFraction));
                ret = new Location(ii,i);
                }*/
                if (Math.abs(accumulator[ii][i]/(double)numScans-(noiseFraction)) < min) {
                    min = Math.abs(accumulator[ii][i]/(double)numScans-(noiseFraction));
                    ret = new Location(ii,i);
                }
            }
        }
        return ret;

        //
        // !!! add code here !!!
        //
    }

    /**
     * Moves the monster by 1 step in its current direction, called every frame
     */
    public void moveMonster() {
        acol += Math.cos(toRadians(startMonsterDir));
        arow += Math.sin(toRadians(startMonsterDir));
        while (arow > 99) {
            arow -= 100;
        }
        while (acol > 99) {
            acol -= 100;
        }
        while (acol < 0) {
            acol += 100;
        }
        while (arow < 0) {
            arow += 100;
        }

        monsterLocation = new Location((int)arow, (int)acol);
    }

    /**
     * Returns the number of times that the specified location in the radar grid has triggered a
     *  detection since the constructor of the radar object.
     * 
     * @param   row     the row of the location to query for accumulated detections
     * @param   col     the column of the location to query for accumulated detections
     * @return the number of times that the specified location in the radar grid has
     *          triggered a detection since the constructor of the radar object
     */
    public int getAccumulatedDetection(Location loc)
    {
        return accumulator[ loc.getRow() ][ loc.getCol() ];
    }

    private static boolean[][] replicate(boolean[][] array) {
        boolean[][] ret = new boolean[100][100];
        for(int i = 0; i < 100; i++) {
            for (int ii = 0; ii < 100; ii++) {
                ret[i][ii] = array[i][ii];
            }
        }
        return ret;
    }

    /**
     * The improved version of scan, executes a scan then adds the data to the running container with all of the data
     */
    public void colData() {
        this.scan();
        data.add(replicate(currentScan));
    }

    /**
     * Returns the number of rows in the radar grid
     * 
     * @return the number of rows in the radar grid
     */
    public int getNumRows()
    {
        return currentScan.length;
    }

    /**
     * Returns the number of columns in the radar grid
     * 
     * @return the number of columns in the radar grid
     */
    public int getNumCols()
    {
        return currentScan[0].length;
    }

    /**
     * Returns the number of scans that have been performed since the radar object was constructed
     * 
     * @return the number of scans that have been performed since the radar object was constructed
     */
    public int getNumScans()
    {
        return numScans;
    }

    /**
     * Mathematical algorithm to calculate a point a certain direction and distance from the origin point
     * @param   o       The origin point
     * @param   len     The length from the origin point
     * @param   dir     The direction from the origin point
     */
    private static Point lengthDir(Point o, double len, double dir) {
        return new Point((int)(o.x+Math.cos(dir)*len), (int)(o.y+Math.sin(dir)*len));
    }

    /**
     * Converts degrees to radians
     * @param   degrees The degree value to be converted
     */
    private static double toRadians(double deg) {
        return deg/180.0*Math.PI;
    }

    /**
     * The method to find the absolute value of a double
     * @param   num     The number to be taken the absolute value of
     */
    private static double absval(double num) {
        if (num < 0) {
            return -num;
        }
        return num;
    }

    /**
     * Finds the smallest number in a double array
     * @param   arr     The array to find the minimum in
     */
    private static double arraymin(double[] arr) {
        double ret = arr[0];
        for(double i:arr) {
            if (i<ret) {
                ret = i;
            }
        }
        return ret;
    }

    /**
     * The method called to analyze the data provided by collection
     */
    public void moveScan() {
        double probtrue = 1-noiseFraction;
        double[] probOfDir = new double[360];
        int ndir = 0;
        double maxprob = data.size()*data.size()+data.size();
        double maxdirprob = data.size()*data.size()+data.size();
        for (int col = 0; col < area.getCol(); col++) {
            for (int row = 0; row < area.getRow(); row++) {
                for (int i = 0; i<data.size(); i++) {
                    for (int dir = 0; dir < 360; dir += 10) {
                        Point offset = lengthDir(new Point(0, 0), i, toRadians(dir));
                        int trow = row+offset.x;
                        int tcol = col+offset.y;
                        while (trow > 99) {
                            trow -= 100;
                        }
                        while (trow < 0) {
                            trow += 100;
                        }
                        while (tcol > 99) {
                            tcol -= 100;
                        }
                        while (tcol < 0) {
                            tcol += 100;
                        }
                        if (data.get(i)[trow][tcol]) {
                            probOfPoint[row][col] += probtrue;
                        }
                        else {
                            probOfPoint[row][col] += noiseFraction;
                        }
                    }
                }
            }
        }
        int ncol = 0;
        int nrow = 0;
        for(int i = 0; i<probOfPoint.length; i++) {
            for(int ii = 0; ii<probOfPoint[0].length; ii++) {
                if (probOfPoint[i][ii] < maxprob) {
                    confidence = absval(probOfPoint[i][ii]-maxprob);
                    maxprob = probOfPoint[i][ii];
                    nrow = i;
                    ncol = ii;
                }
            }
        }
        startMonster = new Location(nrow, ncol);
        startMonsterDir = findDir(startMonster);
        startMonsterDir = 90-startMonsterDir;
        while (startMonsterDir < 0) {
            startMonsterDir += 360;
        }
        System.out.println("(Note that deviation by 1 pixel in start location is possible, as the same path is possible from a cluster of 4 pixels as a result of rounding)");
        System.out.println("The predicted origin of the monster (in format ROW | COLUMN): "+nrow+" | "+ncol);
        System.out.println("The predicted direction of the monster (in degrees): "+startMonsterDir);
        System.out.println("Confidence in Origin (Threshold confidence for rescan: 20): "+confidence);
        System.out.println("Confidence in Direction (Threshold confidence for rescan: < 10): "+dirconfidence);
    }
    
    /**
     * Directly returns the confidence of both the direction and the origin of the monster
     */
    public double[] getConfidence() {
        double[] ret = {confidence, dirconfidence};
        return ret;
    }
    
    /**
     * Directly returns the predicted starting location of the monster
     */
    public Location getMonsterOrigin() {
        return startMonster;
    }

    /**
     * The method called to analyze a single point and calculate the direction
     * @param   loc     The predicted location of the monster
     * @return  The predicted direction of the monster
     */
    private int findDir(Location loc) {
        double[] probOfDir = new double[36];
        double mindir = 0;
        int minid = 0;
        for (int dir = 0; dir < 360; dir += 10) {
            for (int i = 0; i<data.size(); i++) {
                double probtrue = 1-noiseFraction;
                Point offset = lengthDir(new Point(0, 0), i, toRadians(dir));
                int trow = loc.getRow()+offset.x;
                int tcol = loc.getCol()+offset.y;
                while (trow > 99) {
                    trow -= 100;
                }
                while (trow < 0) {
                    trow += 100;
                }
                while (tcol > 99) {
                    tcol -= 100;
                }
                while (tcol < 0) {
                    tcol += 100;
                }
                if (data.get(i)[trow][tcol]) {
                    probOfDir[dir/10] += probtrue;
                }
                else {
                    probOfDir[dir/10] += noiseFraction;
                }
            }
            if (dir == 0) {
                mindir = probOfDir[0];
                dirconfidence = arraymin(Arrays.copyOfRange(probOfDir, 1, 36))-probOfDir[0];
            }
            else if(mindir > probOfDir[dir/10]) {
                dirconfidence = mindir-probOfDir[dir/10];
                mindir = probOfDir[dir/10];
                minid = dir;
            }
        }
        return minid;
    }

    /**
     * Sets cells as falsely triggering detection based on the specified probability
     * 
     */
    private void injectNoise()
    {
        // Iterate through all cells in the currentScan 2D array to inject noise by setting false positives
        // (detected monster where is there none) or false negatives (missed detection of a monster where is one).
        // The noiseFraction instance variable is the probability that a given cell will be
        // detected as a false positive or false negative. You must handle the cell containing the monster as a
        // special case since, if noise is being injected into that cell, that is a false negative and the cell must
        // be set to false.
        for (int ii = 0; ii<currentScan.length; ii++) {
            for (int i = 0; i<currentScan[0].length; i++) {
                if (monsterLocation.getRow() == ii && monsterLocation.getCol() == i) {
                    currentScan[ii][i] = (Math.random() < noiseFraction);
                }
                else {
                    currentScan[ii][i] = (Math.random() > noiseFraction);
                }
            }
        }

        //
        // !!! add code here !!!
        //

    }
}
