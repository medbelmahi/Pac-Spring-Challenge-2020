package codingame.pac.cell;

import codingame.pac.PacMan;
import codingame.pac.Pellet;
import codingame.pac.action.MoveAction;
import codingame.pac.task.EatTask;
import codingame.pac.task.FindPelletTask;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class Floor extends Cell {

    private Pellet pallet;
    private FloorStatus floorStatus;
    private boolean targeted;

    Floor(Coord coord) {
        super(coord);
        this.floorStatus = FloorStatus.HIDDEN;
    }

    public void setPallet(Pellet pallet) {
        this.pallet = pallet;
        this.floorStatus = pallet.isSuper() ? FloorStatus.HAS_SUPER_PELLET : FloorStatus.HAS_SIMPLE_PELLET;
    }

    public double distanceTo(Floor floor) {
        return coord.distanceTo(floor.coord);
    }

    public void noPellet() {
        if (pallet != null) {
            pallet.setStillHere(false);
            this.floorStatus = FloorStatus.EMPTY;
        }
    }

    @Override
    public String toString() {
        switch (this.floorStatus) {
            case EMPTY: return " ";
            case HAS_SIMPLE_PELLET: return "o";
            case HAS_SUPER_PELLET: return "O";
            case HIDDEN: return "?";
        }
        return " ";
    }

    public boolean isHidden() {
        return FloorStatus.HIDDEN.equals(floorStatus);
    }

    public MoveAction targeted(PacMan pacMan) {
        this.targeted = true;

        MoveAction moveAction = new MoveAction(pacMan, coord);
        pacMan.setTask(new FindPelletTask(moveAction, this));
        return moveAction;
    }

    public boolean isTargeted() {
        return targeted;
    }

    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }
}
