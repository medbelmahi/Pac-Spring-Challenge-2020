package codingame.pac.task;

import codingame.pac.action.Action;
import codingame.pac.action.MoveAction;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class FindPelletTask extends Task {
    private Floor floor;

    public FindPelletTask(MoveAction moveAction, Floor floor) {
        super(moveAction);
        this.floor = floor;
    }

    @Override
    public boolean isFinished() {
        return floor.isEmpty() || ((MoveAction) action).isReached();
    }

    @Override
    public Action keepTargeting() {
        floor.setTargeted(true);
        return action;
    }

    @Override
    public boolean isMoveTask() {
        return true;
    }

    @Override
    public Coord moveTarget() {
        return floor.getCoord();
    }

    @Override
    public String printInfo() {
        return getClass().getName() + " Floor : " + floor.getCoord();
    }
}
