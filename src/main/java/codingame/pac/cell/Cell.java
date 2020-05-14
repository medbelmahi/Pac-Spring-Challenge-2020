package codingame.pac.cell;

import codingame.pac.Coord;
import codingame.pac.Grid;

import java.util.Set;
import java.util.TreeSet;

public class Cell {
    public static final Cell NO_CELL = new Cell(new Coord(1000, 1000), CellType.FLOOR) {
        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public void copy(Cell other) {
            throw new RuntimeException("Invalid cell");
        }

        @Override
        public void setType(CellType type) {
            throw new RuntimeException("Invalid cell");
        }

    };
    public boolean isValid() {
        return true;
    }
    public void copy(Cell source) {
        setType(source.type);
        setHasPellet(source.hasPellet);
    }

    private Coord coordinates;
    private CellType type;
    protected boolean hasPellet;

    public Cell(Coord coordinates, CellType type) {
        this.coordinates = coordinates;
        this.type = type;
    }

    public Cell rightCell(Cell[][] cells) {
        if (coordinates.getX() + 1 < Grid.width) {
            Cell cell = cells[coordinates.getX() + 1][coordinates.getY()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell leftCell(Cell[][] cells) {
        if (coordinates.getX() - 1 >= 0) {
            Cell cell = cells[coordinates.getX() - 1][coordinates.getY()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell upCell(Cell[][] cells) {
        if (coordinates.getY() - 1 >= 0) {
            Cell cell = cells[coordinates.getX()][coordinates.getY() - 1];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell downCell(Cell[][] cells) {
        if (coordinates.getY() + 1 < Grid.height) {
            Cell cell = cells[coordinates.getX()][coordinates.getY() + 1];
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

    public <T extends Floor> Set<T> getSortedEdgesBasedOnDistanceFromTarget(T target, Set<T> edges) {
        Set<T> sortedEdgesBasedOnDistanceFromTarget = new TreeSet<T>((floor1, floor2) -> {

            double floor1DistanceToTarget = floor1.distance(target);
            double floor2DistanceToTarget = floor2.distance(target);
            return floor1DistanceToTarget < floor2DistanceToTarget ? -1 : 1;
        });

        sortedEdgesBasedOnDistanceFromTarget.addAll(edges);

        return sortedEdgesBasedOnDistanceFromTarget;
    }

    protected <T extends Floor> double distance(T target) {
        return coordinates.euclideanTo(target.getCoordinates());
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public void setHasPellet(boolean hasPellet) {
        this.hasPellet = hasPellet;
    }

    public CellType getType() {
        return type;
    }
}
