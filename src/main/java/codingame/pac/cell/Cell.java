package codingame.pac.cell;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class Cell {
    public static final Cell NO_CELL = new Floor(new Coord(1000, 1000)) {
        public boolean isValid() {
            return false;
        }

        public void copy(Cell other) {
            throw new RuntimeException("Invalid cell");
        }

        public void setType(CellType type) {
            throw new RuntimeException("Invalid cell");
        }
    };
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
