package codingame.pac.task;

import codingame.pac.Pellet;
import codingame.pac.action.Action;
import codingame.pac.action.MoveAction;
import codingame.pac.cell.Coord;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class EatTask extends Task {
    private Pellet pellet;

    public EatTask(Action action, Pellet pellet) {
        super(action);
        this.pellet = pellet;
    }

    @Override
    public boolean isFinished() {
        return !this.pellet.isStillHere();
    }

    @Override
    public Action keepTargeting() {
        pellet.setTargeted(true);
        return action;
    }

    @Override
    public boolean isMoveTask() {
        return true;
    }

    @Override
    public Coord moveTarget() {
        return isMoveTask() ? ((MoveAction) action).targetCoord() : null;
    }

    @Override
    public String printInfo() {
        return getClass().getName() + " to pellet " + pellet.toString();
    }
}
