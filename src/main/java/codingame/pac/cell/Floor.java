package codingame.pac.cell;

import codingame.pac.Coord;
import codingame.pac.graph.Direction;

import java.util.LinkedList;

public class Floor extends Cell {
    public Floor(Coord coord) {
        super(coord);
    }

    @Override
    public String toString() {
        return " ";
    }

    public <T extends Floor> LinkedList<Direction> getDirections(final T destination) {
        return this.getCoordinates().getSortedDirection(destination.getCoordinates());
    }
}
