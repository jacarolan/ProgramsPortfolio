import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MyRectangle extends Shape
{
    private double radius;
    private Color color;
    private Point center;
    private boolean filled;
    private boolean xedge;
    public MyRectangle(double radius, Color color, Point center) {
        this.radius = radius;
        this.color = color;
        this.center = center;
        this.filled = true;
        this.xedge = false;
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
        if(filled) {
            g.setColor(this.color);
            g.fillRect(this.center.x-(int)radius, this.center.y-(int)radius, (int)(this.radius*2), (int)(this.radius*2));
        }
        g.setColor(new Color(0, 0, 0));
        g.drawRect(this.center.x-(int)radius, this.center.y-(int)radius, (int)(this.radius*2), (int)(this.radius*2));
    }

    public boolean isInside(Point p) {
        return (Math.abs(center.x-p.x) < radius+7 && Math.abs(center.y-p.y) < radius+7);
    }

    public void setRadius(double r) {
        this.radius = r;
    }

    public boolean onEdge(Point p) {
        if(Math.abs(Math.abs(center.y-p.y)-radius) < 7) {
            xedge = false;
            return true;
        }
        else if(Math.abs(Math.abs(center.x-p.x)-radius) < 7) {
            xedge = true;
            return true;
        }
        return false;
    }

    public void move(Point p) {
        this.center = new Point((int)p.getX(), (int)p.getY());
    }

    public void resize(int cx, int cy) {
        if(xedge) {
            this.radius = Math.abs(cx-center.x);
        } else {
            this.radius = Math.abs(cy-center.y);
        }
    }

    public Point getCenter() {
        return this.center;
    }

    public double getRadius() {
        return this.radius;
    }
}
