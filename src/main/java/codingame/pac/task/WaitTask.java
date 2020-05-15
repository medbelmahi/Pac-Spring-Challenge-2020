package codingame.pac.task;

import codingame.pac.action.Action;
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
        return true;
    }

    @Override
    public Action keepTargeting() {
        return null;
    }

    @Override
    public boolean isMoveTask() {
        return false;
    }

    @Override
    public Coord moveTarget() {
        return null;
    }
}
