import java.awt.Point;
public class Location extends Point
{
    /**
     * Default constructor for a location
     * @param   row     The row this location has
     * @param   col     The column this location has
     */
    public Location(int row, int column) {
        super(row, column);
    }
    /**
     * Directly returns the row
     * @return  The exact row
     */
    public int getRow() {
        return super.x;
    }
    /**
     * Directly returns the column
     * @return  The exact column
     */
    public int getCol() {
        return super.y;
    }
}
