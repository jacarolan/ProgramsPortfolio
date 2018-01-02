import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * The panel that contains the controls and indicators for the drawing editor
 * 
 * @author gcschmit
 * @version 23 June 2015
 */
public class ControlPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener
{
    /**The index for the canvas which this control adds to*/
    private DrawingPanel canvas;
    /**The Choose Color button*/
    private JButton chooseColor;
    /**The Add Circle button*/
    private JButton addCircle;
    /**The Add Square button*/
    private JButton addSquare;
    /**The Tangency Information label*/
    private JLabel infoLabel;
    /**The Rectangle that displays the current color*/
    private RectDrawer colorRect;
    /**The mouses previous x position (used to calculate change in mouse position)*/
    private int prevmx;
    /**The mouses previous y position (used to calculate change in mouse position)*/
    private int prevmy;

    private int tangencies = 0;
    private int numSolutions = 0;

    /**
     * Constructor for objects of class ControlPanel
     * @param   canvas  The index for the canvas which this panel adds to
     */
    public ControlPanel( DrawingPanel canvas) {
        this.canvas = canvas;
        this.chooseColor = new JButton("Pick a color");
        this.colorRect = new RectDrawer(10, 10, canvas);
        this.addCircle = new JButton("Add a circle");
        this.addSquare = new JButton("Add a square");
        this.infoLabel = new JLabel("Number of Solutions: \nDisplaying solution #");
        chooseColor.addActionListener(new ChooseColorHandler(canvas, colorRect, this));
        addSquare.addActionListener(new AddRectHandler(canvas, this));
        addCircle.addActionListener(new AddCircleHandler(canvas, this));
        this.add(infoLabel);
        this.add(chooseColor);
        this.add(colorRect);
        this.add(addCircle);
        this.add(addSquare);
        this.setVisible(true);
        prevmx = 0;
        prevmy = 0;
    }

    public int getTangencies() {
        return tangencies;
    }

    public int getNumSolutions() {
        return numSolutions;
    }

    /**
     * The method that returns a list of solution Matrices (containing the radii of the square around each point) which solve the points p, as well as setting the instance variables tangancies and numSolutions to the correct values
     * @param   p   The list of points to act as centers
     */
    public MyMatrix[] calcRadiiAndDeletionsSquare(Point[] p) {
        ArrayList<MyMatrix> endMatrix = new ArrayList<MyMatrix>();
        if(p.length == 1) {
            double[][] vals = {{50.0}};
            endMatrix.add(new MyMatrix(vals));
            tangencies = 0;
        } else if(p.length == 2) {
            double[][] vals;
            if(Math.abs(p[0].x-p[1].x) < Math.abs(p[0].y-p[1].y)) {
                double[][] cvals = {{50.0}, {p[0].x-p[1].x-50.0}};
                vals = cvals;
            } else {
                double[][] cvals = {{50.0}, {p[0].y-p[1].y-50.0}};
                vals = cvals;
            }
            endMatrix.add(new MyMatrix(vals));
            tangencies = 1;
        } else {
            int[] decompArray = new int[p.length]; //An array containing all integers lower than the length of p (i.e. [0, 1, 2] if p = 3
            for(int i = 0; i < decompArray.length; i++) {
                decompArray[i] = i;
            }
            int[][] possibleDecomps = allDecompositionsOfLength(decompArray, 2); //Finds every possible set of items in decompArray, for example [], [0], [1], [2], [0, 1], [1, 2], [0, 2], [0, 1, 2] if decompArray = [0, 1, 2]
            double[][] valueArray = new double[possibleDecomps.length][p.length]; //Constructs matrix of the values
            double[][] answerArray = new double[possibleDecomps.length][1]; //Constructs matrix of the required answers (these mimic a system of equations)
            for(int i = 0; i < possibleDecomps.length; i++) {
                for(int ii = 0; ii < p.length; ii++) {
                    valueArray[i][ii] = 0;
                }
                answerArray[i][0] = 0; 
                //This builds the matrices with every value being 0
            }
            for(int i = 0; i < possibleDecomps.length; i++) {
                //Runs a case for every possible condition being thrown out, i.e. we are trying to find a solution where every circle is tangent, if that is inconsistent then evaluate every other combination where all but 1 pair are tangent, and continue until a solution is found.
                for(int ii: possibleDecomps[i]) {
                    valueArray[i][ii] = 1;
                    //Sets the coefficients corresponding to the 2 radii to 1
                }
                //double dir = pointDir(p[possibleDecomps[i][0]], p[possibleDecomps[i][1]]);
                //if(Math.abs(p[possibleDecomps[i][0]].y-p[possibleDecomps[i][1]].y) > Math.abs(p[possibleDecomps[i][0]].y-p[possibleDecomps[i][1]].y)) {
                //answerArray[i][0] = Math.abs(p[possibleDecomps[i][0]].y-p[possibleDecomps[i][1]].y);
                //} else {
                answerArray[i][0] = Math.max(Math.abs(p[possibleDecomps[i][0]].x-p[possibleDecomps[i][1]].x), Math.abs(p[possibleDecomps[i][0]].y-p[possibleDecomps[i][1]].y));
                //}
                //for squares in specific, we need to evaluate based on angle whether radius1 + radius2 == |x1 - x2| or radius1 + radius2 = |y1 - y2| is the condition for edge overlap
            }
            int deletions = answerArray.length;
            MyMatrix valueMatrix = new MyMatrix(valueArray);
            MyMatrix answerMatrix = new MyMatrix(answerArray);
            //Creates the Matrix objects
            decompArray = new int[valueArray.length];
            for(int i = 0; i < decompArray.length; i++) {
                decompArray[i] = i;
            }
            boolean done = false;
            for(int r = (int)Math.max(choose(p.length, 2)-(2*(p.length-1)), 0); r < decompArray.length; r++) {
                if(!done) {
                    possibleDecomps = allDecompositionsOfLength(decompArray,r); //Finds every possible way to delete conditions from the matrix set, every one of these needs to be tested
                    for(int i = 0; i < possibleDecomps.length; i++) {
                        MyMatrix testValueMatrix = valueMatrix.copyEraseRows(possibleDecomps[i]);
                        MyMatrix testAnswerMatrix = answerMatrix.copyEraseRows(possibleDecomps[i]);
                        try {
                            MyMatrix testEndMatrix = testValueMatrix.solve(testAnswerMatrix);
                            //Finds every solution of a given decomposition. It is important to test every case that would give x amount of tangencies so that every solution is found
                            if(testEndMatrix != null) {
                                /*if(deletions > possibleDecomps[i].length) {
                                deletions = possibleDecomps[i].length;
                                endMatrix.clear();
                                }*/
                                endMatrix.add(testEndMatrix.getCopy());
                                tangencies = decompArray.length-r;
                                done = true;
                            }
                        } catch(Exception e) {
                        }
                    }
                }
            }
        }
        //It is possible for 2 different cases to give identical solutions, these should be deleted for displayings sake
        for(int i = 0; i < endMatrix.size(); i++) {
                for(int ii = 0; ii < endMatrix.size(); ii++) {
                    boolean equal = i != ii;
                    for(int iii = 0; iii < endMatrix.get(i).getArrayCopy().length; iii++) {
                        if(Math.abs(endMatrix.get(i).getArrayCopy()[iii][0]-endMatrix.get(ii).getArrayCopy()[iii][0]) > 1.0) {
                            equal = false;
                        }
                    }
                    if(equal) {
                        endMatrix.remove(i);
                        break;
                    }
                }
            }
        numSolutions = endMatrix.size();
        MyMatrix[] realRet = new MyMatrix[numSolutions];
        for(int i = 0; i < realRet.length; i++) {
            realRet[i] = endMatrix.get(i);
        }
        System.out.println("Tangencies: "+tangencies);
        System.out.println("Number of Solutions: "+numSolutions);
        for(int i = 0; i < realRet.length; i++) {
            System.out.println("Radii of the squares in solution #"+(i+1)+" (in the same order as the points were printed):");
            for(double[] ii:realRet[i].getArrayCopy()) {
                System.out.print(Math.round(Math.abs(ii[0]))+"  ");
            }
            System.out.println("");
        }
        return realRet;
    }

    /**
     * The method that returns a list of solution Matrices (containing the radii of the circle around each point) which solve the points p, as well as setting the instance variables tangancies and numSolutions to the correct values
     * @param   p   The list of points to act as centers
     */
    public MyMatrix[] calcRadiiAndDeletionsCircle(Point[] p) {
        //I wrote stuff up above this. Thats enough.
        ArrayList<MyMatrix> endMatrix = new ArrayList<MyMatrix>();
        if(p.length == 1) {
            double[][] vals = {{50.0}};
            endMatrix.add(new MyMatrix(vals));
            tangencies = 0;
        } else if(p.length == 2) {
            double[][] vals = {{50.0}, {pointDis(p[0], p[1])-50.0}};
            endMatrix.add(new MyMatrix(vals));
            tangencies = 1;
        } else {
            int[] decompArray = new int[p.length];
            long posscombs = choose(p.length, 2);
            for(int i = 0; i < decompArray.length; i++) {
                decompArray[i] = i;
            }
            int[][] possibleDecomps = allDecompositionsOfLength(decompArray, 2);
            double[][] valueArray = new double[(int)posscombs][p.length];
            double[][] answerArray = new double[(int)posscombs][1];
            for(int i = 0; i < (int)posscombs; i++) {
                for(int ii = 0; ii < p.length; ii++) {
                    valueArray[i][ii] = 0;
                }
                answerArray[i][0] = 0;
            }
            for(int i = 0; i < (int)posscombs; i++) {
                for(int ii: possibleDecomps[i]) {
                    valueArray[i][ii] = 1;
                }
                answerArray[i][0] = pointDis(p[possibleDecomps[i][0]], p[possibleDecomps[i][1]]);
            }
            int deletions = answerArray.length;

            MyMatrix valueMatrix = new MyMatrix(valueArray);
            MyMatrix answerMatrix = new MyMatrix(answerArray);

            decompArray = new int[valueArray.length];
            for(int i = 0; i < decompArray.length; i++) {
                decompArray[i] = i;
            }
            boolean done = false;
            for(int r = (int)Math.max(choose(p.length, 2)-(2*(p.length-1)), 0); r < decompArray.length; r++) {
                if(!done) {
                    long totcombs = choose(posscombs, r);
                    if(totcombs < 352716) {
                        possibleDecomps = allDecompositionsOfLength(decompArray,r); //Finds every possible way to delete conditions from the matrix set, every one of these needs to be tested
                        for(int i = 0; i < possibleDecomps.length; i++) {
                            MyMatrix testValueMatrix = valueMatrix.copyEraseRows(possibleDecomps[i]);
                            MyMatrix testAnswerMatrix = answerMatrix.copyEraseRows(possibleDecomps[i]);
                            try {
                                MyMatrix testEndMatrix = testValueMatrix.solve(testAnswerMatrix);
                                //Finds every solution of a given decomposition. It is important to test every case that would give x amount of tangencies so that every solution is found
                                if(testEndMatrix != null) {
                                    endMatrix.add(testEndMatrix.getCopy());
                                    tangencies = decompArray.length-r;
                                    done = true;
                                }
                            } catch(Exception e) {
                            }
                        }
                    }
                }
            }
            for(int i = 0; i < endMatrix.size(); i++) {
                for(int ii = 0; ii < endMatrix.size(); ii++) {
                    boolean equal = i != ii;
                    for(int iii = 0; iii < endMatrix.get(i).getArrayCopy().length; iii++) {
                        if(Math.abs(endMatrix.get(i).getArrayCopy()[iii][0]-endMatrix.get(ii).getArrayCopy()[iii][0]) > 1.0) {
                            equal = false;
                        }
                    }
                    if(equal) {
                        endMatrix.remove(i);
                        break;
                    }
                }
            }
        }
        numSolutions = endMatrix.size();
        MyMatrix[] realRet = new MyMatrix[numSolutions];
        for(int i = 0; i < realRet.length; i++) {
            realRet[i] = endMatrix.get(i);
        }
        System.out.println("Tangencies: "+tangencies);
        System.out.println("Number of Solutions: "+numSolutions);
        for(int i = 0; i < realRet.length; i++) {
            System.out.println("Radii of the circles in solution #"+(i+1)+" (in the same order as the points were printed):");
            for(double[] ii:realRet[i].getArrayCopy()) {
                System.out.print(Math.round(Math.abs(ii[0]))+"  ");
            }
            System.out.println("");
        }
        return realRet;
    }

    private static long choose(long num1, long num2) {
        try {
            return factorial(num1)/(factorial(num2)*factorial(num1-num2));
        } catch(ArithmeticException e) {}
        return 1;
    }

    private static long factorial(long num) {
        long ret = 1;
        for(long i = num; i > 1; i--) {
            ret *= i;
        }
        return ret;
    }

    private static double pointDis(Point p1, Point p2) {
        return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p2.y-p1.y)*(p2.y-p1.y));
    }

    private static double pointDir(Point p1, Point p2) {
        return Math.atan2(p2.y-p1.y, p2.x-p1.x); 
    }

    /**
     * A recursive method for finding every decomposition of a set. This needs to run in segments for large enough numbers to prevent heap space error.
     */
    private static void combinationUtil(int arr[], int data[], int start, int end, int index, int r, ArrayList<ArrayList<Integer>> masterData) {
        //if(masterData.size() < 1638400) {
        if (index == r) {
            for (int j=0; j<r; j++) {
                masterData.get(masterData.size()-1).add(data[j]);
            }
            masterData.add(new ArrayList<Integer>());
            return;
        }
        for (int i=start; i<=end && end-i+1 >= r-index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r, masterData);
        }//}
    }

    private static int[][] allDecompositionsOfLength(int arr[], int r) {
        ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
        ret.add(new ArrayList<Integer>());
        int data[]=new int[r];
        combinationUtil(arr, data, 0, arr.length-1, 0, r, ret);
        int[][] realRet = new int[ret.size()-1][];
        for(int i = 0; i < ret.size()-1; i++) {
            int[] addRet = new int[ret.get(i).size()];
            for(int ii = 0; ii < ret.get(i).size(); ii++) {
                addRet[ii] = ret.get(i).get(ii);
            }
            realRet[i] = addRet;
        }
        return realRet;
    }

    private static int[][] allDecompositions(int arr[]) {
        ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
        ret.add(new ArrayList<Integer>());
        for(int r = 0; r < arr.length; r++) {
            int data[]=new int[r];
            combinationUtil(arr, data, 0, arr.length-1, 0, r, ret);
        }
        int[][] realRet = new int[ret.size()][];
        for(int i = 0; i < ret.size(); i++) {
            try {
                int[] addRet = new int[ret.get(i).size()];
                for(int ii = 0; ii < ret.get(i).size(); ii++) {
                    addRet[ii] = ret.get(i).get(ii);
                }
                realRet[i] = addRet;
            } catch(Error e) {
                break;
            }
        }
        return realRet;
    }

    /**
     * Clears the panel and redraws all buttons and updates the color
     */
    public void paintComponent(Graphics g) {
        this.infoLabel.setText("Tangencies: "+this.tangencies+"            Number of solutions: "+this.numSolutions+"            Displaying Solution #"+(canvas.getSolutionNumber()+1));
        super.paintComponent(g);
    }

    /**
     * This event is useless... yea
     * @param   e   The event to do nothing
     */
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * This event is useful... It does stuff
     * @param   e   The event generated by this event, very important because it has a position which can be read
     */
    public void mouseDragged(MouseEvent e) {
        canvas.moveShape(e.getX()-prevmx, e.getY()-prevmy, e.getX(), e.getY());
        prevmx = e.getX();
        prevmy = e.getY();
    }

    /**
     * What do you know, another useless method. This is literally a worse mouseDragged
     * @param   e   The event which does nothing
     */
    public void mouseMoved(MouseEvent e) {}

    /**
     * The callback when the mouse is released. This is passed onto the canvas object, which runs the mouseRelease() method
     * @param   e   The mouse event, only present to allow overriding MouseListener's method
     */
    public void mouseReleased(MouseEvent e) {
        switch(e.getButton()) {
            case MouseEvent.BUTTON1: {
                canvas.mouseRelease();
            }
        }
    }

    /**
     * The callback when the Mouse is pressed. A better version of the mouseClicked callback.
     * @param   e   The event generated when the mouse is pressed
     */
    public void mousePressed(MouseEvent e) {
        switch(e.getButton()) {
            case MouseEvent.BUTTON1: {
                prevmx = e.getX();
                prevmy = e.getY();
                canvas.selectShape(prevmx, prevmy);

            }
        }
    }

    /**
     * I am just going to copy this generic statement for all these useless overriden methods.
     * @param   e   A useless event
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * I am just going to copy this generic statement for all these useless overriden methods.
     * @param   e   A useless event
     */
    public void mouseExited(MouseEvent e) {}

    /**
     * The method called when a key is pressed. If it is escape, the canvas clears its shapes.
     * @param   e   The event, the key being pressed is read.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
            canvas.clearShapes();
            canvas.repaint();
            break;
            case KeyEvent.VK_UP:
            canvas.incrementDisplayNum();
            break;
            case KeyEvent.VK_DOWN:
            canvas.decrementDisplayNum();
            break;
        }
    }

    /**
     * I am just going to copy this generic statement for all these useless overriden methods.
     * @param   e   A useless event
     */
    public void keyReleased(KeyEvent e) {}

    /**
     * I am just going to copy this generic statement for all these useless overriden methods.
     * @param   q   A useless event
     */
    public void keyTyped(KeyEvent q) {}

    /**
     * The private class which handles the Color Choose button being pressed
     * @author Joe C
     * @version 2/6/17
     */
    private class ChooseColorHandler implements ActionListener {
        /**The canvas index*/
        private DrawingPanel canvas;
        /**The Rectangle object whose color will be updated*/
        private RectDrawer colorRect;
        /**The Control Panel index*/
        private ControlPanel control;
        public ChooseColorHandler(DrawingPanel canvas, RectDrawer colorRect, ControlPanel control) {
            this.canvas = canvas;
            this.colorRect = colorRect;
            this.control = control;
        }

        public void actionPerformed(ActionEvent e) {
            canvas.pickColor();
            colorRect.repaint();
            canvas.requestFocusInWindow();
        }
    }

    /**
     * The private class which handles the Add Rectangle button being pressed
     * @author Joe C
     * @version 2/6/17
     */
    private class AddRectHandler implements ActionListener {
        private DrawingPanel canvas;
        private ControlPanel control;
        public AddRectHandler(DrawingPanel canvas, ControlPanel control) {
            this.canvas = canvas;
            this.control = control;
        }

        public void actionPerformed(ActionEvent e) {
            canvas.addRect();
            canvas.repaint();
            control.repaint();
            canvas.requestFocusInWindow();
        }
    }

    /**
     * The private class which handles the Add Circle button being pressed
     * @author Joe C
     * @version 2/6/17
     */
    private class AddCircleHandler implements ActionListener {
        private DrawingPanel canvas;
        private ControlPanel control;
        public AddCircleHandler(DrawingPanel canvas, ControlPanel control) {
            this.canvas = canvas;
            this.control = control;
        }

        public void actionPerformed(ActionEvent e) {
            canvas.addCircle();
            canvas.repaint();
            control.repaint();
            canvas.requestFocusInWindow();
        }
    }

    /**
     * The private class which displays the embedded Rectangle. This probably should be a drawn rectangle, but then formatting would be annoying.
     * @author Joe C
     * @version 2/6/17
     */
    private class RectDrawer extends JComponent {
        private int width;
        private int height;
        private DrawingPanel canvas;
        public RectDrawer(int width, int height, DrawingPanel canvas) {
            this.width = width;
            this.height = height;
            this.canvas = canvas;
        }

        public Dimension getPreferredSize() {
            return new Dimension(width, height);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(canvas.getFillColor());
            g.fillRect(0, 0, width, height);
        }
    }
}
