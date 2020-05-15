package codingame.pac.task;

import codingame.pac.Pellet;
import codingame.pac.action.Action;

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
}
