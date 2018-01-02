import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Jama.*;
import java.util.*;

/**
 * The window for the drawing editor.
 * 
 * @author gcschmit
 * @version 23 June 2015
 */
public class DrawingEditor extends JFrame
{
    /**The reference for the drawing panel*/
    private DrawingPanel canvas;
    /**The reference for the control panel*/
    private ControlPanel controls;
    public DrawingEditor(int num, boolean urects) {
        this.setTitle( "Drawing Editor" );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        canvas = new DrawingPanel(this, num, urects);
        controls = new ControlPanel( canvas );
        canvas.setControl(controls);

        this.add(canvas, BorderLayout.NORTH);
        this.add(controls, BorderLayout.SOUTH);
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();

        this.pack();
        this.setSize(1200, 700);
        this.setVisible( true );
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("What this does (other than add drag and resize shapes): \nGiven a set of points to act as centers of circles or squares, \ncalculate the radii of these circles or squares such that\n the maximum amount of tangencies or edge sharings occur.\nControls:\nUp: Display next solution\nDown: Display previous solution\nEscape: Clear the canvas\nShapes will appear empty until selected and dragged.\nPlease Type in a number greater than 1 for the amount of random points to generate (>6 is slow),\nthen type true if you want to use squares or false if you want to use circles (i.e. 4 false):");
        DrawingEditor drawingEditor = new DrawingEditor(in.nextInt(), in.nextBoolean());
    }
}
