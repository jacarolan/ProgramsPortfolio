import javax.swing.JColorChooser;
import javax.swing.JPanel;
import java.awt.*;
import java.util.*;
import java.lang.*;
import javax.swing.*;

/**
 * The panel in which draws all of the shapes in the drawing editor
 * 
 * @author gcschmit
 * @version 23 June 2015
 */
public class DrawingPanel extends JPanel
{
    /**The color to fill shapes with*/
    private Color fillColor;
    /**The list containing all shapes in this panel*/
    private ArrayList<Shape> shapes;
    /**The reference to the control panel controlling this object*/
    private ControlPanel control;
    /**The index of the currently selected shape. This is only set while you are dragging or resizing a shape*/
    private int selectedShape;
    /**Whether or not the current shape is being resized vs if it is being moved*/
    private boolean resize;
    /**The indice for the entire window*/
    private DrawingEditor editor;

    /**Whether or not we are displaying a solution (if its true the shapes won't be filled)*/
    private boolean displayMode;

    private Point[] points;
    private boolean useRects;

    private MyMatrix[] posRadii;

    private int solutionDisplay;

    /**
     * Instantiates the instance variables which need instantiation
     */
    public DrawingPanel(DrawingEditor e, int num, boolean uRects) {
        //switch(num) {
        //case 0: {
        //Point[] tpoints = {new Point(0, 90), new Point(120, 0), new Point(-120, 0), new Point(0, -60), new Point(0, 0)};
        //Point[] tpoints = {new Point(44, 50), new Point(-63, -43), new Point(-174, 86), new Point(55, -142), new Point(-142, 12)};
        points = randPoints(num, 100); 
        useRects = uRects;
        /*break;
        }
        case 1: {
        Point[] tpoints = {new Point(10, 90), new Point(120, -17), new Point(-130, 10), new Point(-10, -90)};
        points = tpoints; 
        useRects = false;
        break;
        }
        case 2: {
        Point[] tpoints = {new Point(0, 90), new Point(120, 0), new Point(-200, 0), new Point(-150, 75), new Point(-100, 150), new Point(-40, 60)};
        points = tpoints; 
        useRects = true;
        break;
        }
        case 3: {
        Point[] tpoints = {new Point(100, 100), new Point(-100, 100), new Point(50, -50), new Point(-50, -50), new Point(0, -200), new Point(-200, -200)};
        points = tpoints;
        useRects = true;
        break;
        }
        }*/
        if(useRects) {
            System.out.println("Centers of the squares:");
        } else { 
            System.out.println("Centers of the circles:");
        }
        for(Point i:points) {
            System.out.println(i);
        }
        resize = false;
        selectedShape = -1;
        shapes = new ArrayList<Shape>();
        editor = e;
        this.fillColor = new Color((int)(Math.random()*16777215)); //Picking a random color the manly way
        this.displayMode = true;
        solutionDisplay = 0;
    }

    /**
     * Creates a random set of points of n length and a maximum of r away in x and y from the center point
     */
    public Point[] randPoints(int n, int r) {
        Point[] ret = new Point[n];
        for(int i = 0; i < n; i++) {
            ret[i] = new Point((int)((Math.random()-0.5)*r*2), (int)((Math.random()-0.5)*r*2));
        }
        return ret;
    }

    /**
     * Adds a rectangle at a random position and the current color to the canvas
     */
    public void addRect() {
        Shape nshape = new MyRectangle(Math.random()*50+50, this.fillColor, new Point((int)(Math.random()*700), (int)(Math.random()*600)));
        shapes.add(nshape);
    }

    /**
     * Adds a circle at a random position and the current color to the canvas
     */
    public void addCircle() {
        Shape nshape = new MyCircle(Math.random()*50+50, this.fillColor, new Point((int)(Math.random()*700), (int)(Math.random()*600)));
        shapes.add(nshape);
    }

    /**
     * Called when this DrawingPanel should select the topmost shape at a point.
     * @param   mclick  The point at which to select a shape
     */
    public void selectShape(Point mclick) {
        int mx = (int)mclick.x;
        int my = (int)mclick.y;
        selectedShape = -1;
        for(int i = 0; i < shapes.size(); i++) {
            if(shapes.get(i).isInside(new Point(mx, my))) {
                selectedShape = i;
                resize = shapes.get(i).onEdge(new Point(mx, my));
                this.displayMode = false;
            }
        }
    }

    /**
     * Called when this DrawingPanel should select the topmost shape at an x y coordinate.
     * @param   x   The x position of the coordinate (will be truncated)
     * @param   y   The y position of the coordinate (will be truncated)
     */
    public void selectShape(double x, double y) {
        int mx = (int)x;
        int my = (int)y;
        selectedShape = -1;
        for(int i = 0; i < shapes.size(); i++) {
            if(shapes.get(i).isInside(new Point(mx, my))) {
                selectedShape = i;
                resize = shapes.get(i).onEdge(new Point(mx, my));
                this.displayMode = false;
            }
        }
    }

    /**
     * Called to indicate that no shape is being manipulated (the mouse has been released). Fills the currently selected shape, redraws, then deselects the shape.
     */
    public void mouseRelease() {
        try {
            shapes.get(selectedShape).setFilled(true);
            this.repaint();
        } catch(Exception e) {}
        selectedShape = -1;
    }

    public boolean getUseRects() {
        return this.useRects;
    }

    /**
     * The method to move or resize the currently selected shape.
     * @param   chx The change in the mouse's x position in comparison to it's previous x position
     * @param   chy The change in the mouse's y position in comparison to it's previous y position
     * @param   ox  The mouses direct x position on this panel
     * @param   oy  The mouses direct y position on this panel
     */
    public void moveShape(double chx, double chy, double ox, double oy) {
        try {
            Shape mshape = shapes.get(selectedShape);
            if(resize) {
                mshape.resize((int)ox, (int)oy);
            } else {
                mshape.move(new Point(mshape.getCenter().x+(int)chx, mshape.getCenter().y+(int)chy));
            }
            mshape.setFilled(false);
            this.repaint();
        } catch (Exception e) {}
    }

    /**
     * Gets the best size for this window (the initial size)
     * @return  The initial dimensions of this window
     */
    public Dimension getPreferredSize() {
        return new Dimension(editor.getWidth(), editor.getHeight()-65);
    }

    /**
     * Sets the index of the control panel to control this object, and adds this control as a listener for keyboard and mouse input.
     * @param   c   The index of the control panel which controls this object
     */
    public void setControl(ControlPanel c) {
        this.control = c;
        this.addKeyListener(control);
        this.addMouseListener(control);
        this.addMouseMotionListener(control);
        if(useRects) {
            this.posRadii = control.calcRadiiAndDeletionsSquare(this.points);
        } else {
            this.posRadii = control.calcRadiiAndDeletionsCircle(this.points);
        }
        this.initiateDisplay();
    }

    public int getSolutionNumber() {
        return solutionDisplay;
    }

    public void initiateDisplay() {
        this.clearShapes();
        this.displayMode = true;
        double[] rads = new double[posRadii[solutionDisplay].getArrayCopy().length];
        for(int i = 0; i < rads.length; i++) {
            rads[i] = posRadii[solutionDisplay].getArrayCopy()[i][0];
        }
        for(int i = 0; i < rads.length; i++) {
            Shape nshape = new MyCircle((int)Math.abs(rads[i]), new Color((int)(Math.random()*16777215)), new Point(points[i].x+550, points[i].y+350));
            if(useRects) {
                nshape = new MyRectangle((int)Math.abs(rads[i]), new Color((int)(Math.random()*16777215)), new Point(points[i].x+550, points[i].y+350));
            }
            shapes.add(nshape);
        }
        this.repaint();
    }

    /**
     * Clears all current shapes on the canvas. Called when escape is pressed.
     */
    public void clearShapes() {
        this.shapes.clear();
    }

    public void incrementDisplayNum() {
        this.solutionDisplay++;
        if(solutionDisplay > control.getNumSolutions()-1) {
            this.solutionDisplay = 0;
        }
        this.initiateDisplay();
    }

    public void decrementDisplayNum() {
        this.solutionDisplay--;
        if(solutionDisplay < 0) {
            this.solutionDisplay = control.getNumSolutions()-1;
        }
        this.initiateDisplay();
    }

    /**
     * Invoked when the "Pick Color" button is clicked. Displays a JColorChooser and sets the
     *  selected color as the new fill color. Leaves the fill color unchanged if the user clicks
     *  "Cancel"
     *
     */
    public void pickColor() {
        Color selectedColor = JColorChooser.showDialog( this, "select the fill color", this.fillColor );

        if( selectedColor != null ) {
            this.fillColor = selectedColor;
        }
    }

    /**
     * Paints all the current shapes onto the canvas after clearing it, then repaints the control panel.
     * @param   g   The Graphics object which does the drawing
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(int i = 0; i < shapes.size(); i++) {
            Shape dshape = shapes.get(i);
            if(this.displayMode) {
                dshape.setFilled(false);
            }
            dshape.draw(g);
        }
        control.repaint();
    }

    /**
     * Gets the current color of this object
     * @return  The currently selected fill color for shapes
     */
    public Color getFillColor() {
        return this.fillColor;
    }
}
