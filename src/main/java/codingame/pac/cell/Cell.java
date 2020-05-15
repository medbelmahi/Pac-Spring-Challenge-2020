package codingame.pac.cell;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class Cell {
    protected Coord coord;

    public Cell(Coord coord) {
        this.coord = coord;
    }

    public int getX() {
        return coord.getX();
    }

    public int getY() {
        return coord.getY();
    }

    public boolean isWall() {
        return false;
    }

    public Coord getCoord() {
        return coord;
    }
}
