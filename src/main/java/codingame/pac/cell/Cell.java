package codingame.pac.cell;

import codingame.pac.Coord;
import codingame.pac.Grid;

public class Cell {
    private Coord coordinates;

    public Cell(Coord coordinates) {
        this.coordinates = coordinates;
    }

    public Cell rightCell(Cell[][] cells) {
        if (coordinates.getX() + 1 < Grid.width) {
            Cell cell = cells[coordinates.getY()][coordinates.getX() + 1];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell leftCell(Cell[][] cells) {
        if (coordinates.getX() - 1 >= 0) {
            Cell cell = cells[coordinates.getY()][coordinates.getX() - 1];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell upCell(Cell[][] cells) {
        if (coordinates.getY() - 1 >= 0) {
            Cell cell = cells[coordinates.getY() - 1][coordinates.getX()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell downCell(Cell[][] cells) {
        if (coordinates.getY() + 1 < Grid.height) {
            Cell cell = cells[coordinates.getY() + 1][coordinates.getX()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public boolean isWall() {
       return false;
    }

    public Coord getCoordinates() {
        return coordinates;
    }

    public void noPellet() {
        // do noting
    }
}
