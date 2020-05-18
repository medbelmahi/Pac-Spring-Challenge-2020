package codingame.pac.cell;

import java.util.Objects;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class Coord {

    public final int x;
    public final int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }

    public double distanceTo(Coord coord) {
        return euclideanTo(coord);
    }

    public double euclideanTo(int x, int y) {
        return Math.sqrt(sqrEuclideanTo(x, y));
    }

    private double sqrEuclideanTo(int x, int y) {
        return Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2);
    }

    public double euclideanTo(Coord d) {
        return euclideanTo(d.x, d.y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coord add(Coord d) {
        return new Coord(x + d.x, y + d.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return x == coord.x &&
                y == coord.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public boolean isCrossedWith(Coord coord) {
        return coord.x == this.x || coord.y == this.y;
    }
}
