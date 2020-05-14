package codingame.pac.cell;

import codingame.Pellet;
import codingame.pac.Coord;
import codingame.pac.graph.Direction;

import java.util.LinkedList;

public class Floor extends Cell {
    private boolean hasCherry;
    private Pellet pellet;
    private boolean hiddenPellet;
    public Floor(Coord coord, CellType floor) {
        super(coord, floor);
        hiddenPellet = true;
    }

    @Override
    public String toString() {
        if (hiddenPellet) {
            return "?";
        }
        if (pellet != null) {
            if (!pellet.isStillHere()) {
                return " ";
            } else {
                return pellet.isSuper() ? "O" : "o";
            }
        }
        return " ";
    }

    public <T extends Floor> LinkedList<Direction> getDirections(final T destination) {
        return this.getCoordinates().getSortedDirection(destination.getCoordinates());
    }

    public boolean hasPellet() {
        return !isHiddenPellet() && pellet != null && pellet.isStillHere();
    }

    public boolean hasSimplePellet() {
        return !isHiddenPellet() && pellet != null && pellet.isStillHere() && !pellet.isSuper();
    }

    public boolean hasCherry() {
        return !isHiddenPellet() && pellet != null && pellet.isStillHere() && pellet.isSuper();
    }

    public void setPellet(Pellet pellet) {
        this.pellet = pellet;
        this.hiddenPellet = false;
        this.hasPellet = true;
        this.hasCherry = pellet.isSuper();
    }

    public boolean isHiddenPellet() {
        return hiddenPellet;
    }

    @Override
    public void noPellet() {
        this.hasPellet = false;
        this.hasCherry = false;
        this.hiddenPellet = false;
        if (this.pellet != null) {
            this.pellet.setStillHere(false);
        }
    }

    public Pellet getPellet() {
        return pellet;
    }
}
