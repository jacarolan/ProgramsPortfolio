import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class MyCircle extends Shape {
    private double radius;
    private Color color;
    private Point center;
    private boolean filled;
    public MyCircle(double radius, Color color, Point center) {
        this.radius = radius;
        this.color = color;
        this.center = center;
        filled = true;
    }

    public Color getColor() {
        return color;
    }

    public void setFilled(boolean f) {
        this.filled = f;
    }

    public boolean getFilled() {
        return this.filled;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        if(filled) {
            g2.setColor(color);
            g2.fill(new Ellipse2D.Double(center.x-radius, center.y-radius, radius*2, radius*2));
        }
        g2.setColor(new Color(0, 0, 0));
        g2.draw(new Ellipse2D.Double(center.x-radius, center.y-radius, radius*2, radius*2));
    }

    public boolean isInside(Point p) {
        return Math.sqrt(Math.pow(p.x-center.x, 2)+Math.pow(p.y-center.y, 2)) < radius+7;
    }

    public void setRadius(double r) {
        this.radius = r;
    }

    public boolean onEdge(Point p) {
        return Math.abs(Math.sqrt(Math.pow(p.x-center.x, 2)+Math.pow(p.y-center.y, 2))-radius) < 7;
    }

    public void move(Point p) {
        this.center = new Point((int)p.getX(), (int)p.getY());
    }

    public Point getCenter() {
        return this.center;
    }

    public void resize(int cx, int cy) {
        this.radius = Math.sqrt(Math.pow(cx-center.x, 2)+Math.pow(cy-center.y, 2));
    }

    public double getRadius() {
        return this.radius;
    }
}
