package codingame.pac.cell;

import codingame.pac.Grid;
import codingame.pac.bfs.Node;

import java.util.Set;
import java.util.TreeSet;

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

    public Cell rightCell(Cell[][] cells) {
        if (coord.getX() + 1 < Grid.width) {
            Cell cell = cells[coord.getX() + 1][coord.getY()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell leftCell(Cell[][] cells) {
        if (coord.getX() - 1 >= 0) {
            Cell cell = cells[coord.getX() - 1][coord.getY()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell upCell(Cell[][] cells) {
        if (coord.getY() - 1 >= 0) {
            Cell cell = cells[coord.getX()][coord.getY() - 1];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell downCell(Cell[][] cells) {
        if (coord.getY() + 1 < Grid.height) {
            Cell cell = cells[coord.getX()][coord.getY() + 1];
            if (!cell.isWall()) return cell;
        }
        return null;
    }
}
