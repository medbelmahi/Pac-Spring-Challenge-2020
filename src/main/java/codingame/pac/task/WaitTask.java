package codingame.pac.task;

import codingame.pac.action.Action;
import codingame.pac.action.WaitAction;
import codingame.pac.cell.Coord;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class WaitTask extends Task {
    public WaitTask(Action action) {
        super(action);
    }

    @Override
    public boolean isFinished() {
        return ((WaitAction) action).isFinished();
    }

    @Override
    public Action keepTargeting() {
        return action;
    }

    @Override
    public boolean isMoveTask() {
        return false;
    }

    @Override
    public Coord moveTarget() {
        return null;
    }

    @Override
    public String printInfo() {
        return getClass().getName();
    }
}
