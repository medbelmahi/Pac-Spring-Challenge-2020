package codingame.pac;

import codingame.pac.cell.Cell;
import codingame.pac.cell.Floor;
import codingame.pac.graph.Direction;

import java.util.LinkedList;

public class Coord {
    protected final int x;
    protected final int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double euclideanTo(int x, int y) {
        return Math.sqrt(sqrEuclideanTo(x, y));
    }

    private double sqrEuclideanTo(int x, int y) {
        return Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        Coord other = (Coord) obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public String print() {
        return x + " " + y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int manhattanTo(Coord other) {
        return manhattanTo(other.x, other.y);
    }

    public int chebyshevTo(int x, int y) {
        return Math.max(Math.abs(x - this.x), Math.abs(y - this.y));
    }

    public int manhattanTo(int x, int y) {
        return Math.abs(x - this.x) + Math.abs(y - this.y);
    }

    public Coord add(Coord d) {
        return new Coord(x + d.x, y + d.y);
    }

    public Coord subtract(Coord d) {
        return new Coord(x - d.x, y - d.y);
    }

    public double euclideanTo(Coord d) {
        return euclideanTo(d.x, d.y);
    }

    public Coord getUnitVector() {
        int newX = this.x == 0 ? 0 : this.x / Math.abs(this.x);
        int newY = this.y == 0 ? 0 : this.y / Math.abs(this.y);
        return new Coord(newX, newY);
    }

    public LinkedList<Direction> getSortedDirection(Coord destination) {

        LinkedList<Direction> directions = new LinkedList<>();

        if (this.y == destination.y) {
            if (this.x < destination.x) {
                directions.add(Direction.RIGHT);
                directions.add(Direction.DOWN);
                directions.add(Direction.UP);
                directions.add(Direction.LEFT);
            }else{
                directions.add(Direction.LEFT);
                directions.add(Direction.DOWN);
                directions.add(Direction.UP);
                directions.add(Direction.RIGHT);
            }
        } else if (this.x == destination.x) {
            if (this.y < destination.y) {
                directions.add(Direction.DOWN);
                directions.add(Direction.RIGHT);
                directions.add(Direction.LEFT);
                directions.add(Direction.UP);
            }else{
                directions.add(Direction.UP);
                directions.add(Direction.RIGHT);
                directions.add(Direction.LEFT);
                directions.add(Direction.DOWN);
            }
        } else if (this.x < destination.x && this.y < destination.y) {
            int diffX = destination.x - this.x;
            int diffY = destination.y - this.y;

            if (diffX < diffY) {
                directions.add(Direction.DOWN);
                directions.add(Direction.RIGHT);
                directions.add(Direction.LEFT);
                directions.add(Direction.UP);
            }else{
                directions.add(Direction.RIGHT);
                directions.add(Direction.DOWN);
                directions.add(Direction.LEFT);
                directions.add(Direction.UP);
            }
        }else if (this.x > destination.x && this.y > destination.y) {
            int diffX = destination.x - this.x;
            int diffY = destination.y - this.y;

            if (diffX < diffY) {
                directions.add(Direction.LEFT);
                directions.add(Direction.UP);
                directions.add(Direction.DOWN);
                directions.add(Direction.RIGHT);
            }else{
                directions.add(Direction.UP);
                directions.add(Direction.LEFT);
                directions.add(Direction.DOWN);
                directions.add(Direction.RIGHT);
            }
        }else if (this.x < destination.x && this.y > destination.y){
            directions.add(Direction.LEFT);
            directions.add(Direction.UP);
            directions.add(Direction.DOWN);
            directions.add(Direction.RIGHT);
        }if (this.x > destination.x && this.y < destination.y){
            directions.add(Direction.LEFT);
            directions.add(Direction.DOWN);
            directions.add(Direction.UP);
            directions.add(Direction.RIGHT);
        }


        return directions;
    }

    public String shortPrint() {
        return x + ":" + y;
    }

    public boolean isNeighborOf(Coord destination) {
        Floor floor = (Floor) Grid.cells[this.x][this.y];
        return Grid.graph.edgesFrom(floor).keySet().stream().anyMatch(floor1 -> floor1.getCoordinates().equals(destination));
    }
}
