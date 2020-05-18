package codingame.pac;

import codingame.pac.action.MoveAction;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;
import codingame.pac.task.EatTask;

import java.util.Objects;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class Pellet {
    private final Coord coord;
    private final Floor floor;
    private final int value;
    private boolean stillHere;
    private boolean targeted;

    public Pellet(Coord coord, Floor floor, int value) {
        this.coord = coord;
        this.floor = floor;
        this.value = value;
        this.stillHere = true;
        floor.setPallet(this);
    }

    public boolean isSuper() {
        return this.value > 1;
    }

    public MoveAction targeted(PacMan pacMan) {
        this.targeted = true;

        MoveAction moveAction = new MoveAction(pacMan, coord);
        pacMan.setTask(new EatTask(moveAction, this));
        return moveAction;
    }

    public double distanceTo(Floor floor) {
        return this.floor.distanceTo(floor);
    }

    public void setStillHere(boolean stillHere) {
        this.stillHere = stillHere;
    }

    public boolean isStillHere() {
        return stillHere;
    }

    public boolean isTargeted() {
        return targeted;
    }

    public void notTargeted() {
        this.targeted = false;
    }

    @Override
    public String toString() {
        return coord.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pellet pellet = (Pellet) o;
        return coord.equals(pellet.coord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coord);
    }

    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }
}
