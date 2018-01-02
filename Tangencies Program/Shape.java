import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public abstract class Shape
{
    public abstract double getRadius();

    public abstract Color getColor();

    public abstract Point getCenter();

    public abstract void draw(Graphics g);

    public abstract void move(Point p);

    public abstract boolean isInside(Point p);

    public abstract void setRadius(double r);

    public abstract boolean onEdge(Point p);
    
    public abstract void setFilled(boolean f);
    
    public abstract boolean getFilled();
    
    public abstract void resize(int cx, int cy);
}
