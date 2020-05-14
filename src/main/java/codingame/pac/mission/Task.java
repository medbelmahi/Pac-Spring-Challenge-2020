package codingame.pac.mission;

import codingame.pac.Game;
import codingame.pac.action.Action;

/**
 * Mohamed BELMAHI created on 11/05/2020
 */
public class Task {
    private Action action;
    private int taskTour;

    public Task(Action action, int taskTour) {
        this.action = action;
        this.taskTour = taskTour;
    }

    public String print() {
        return action.print(taskTour);
    }

    public boolean hasConflict(Game game) {
        return game.hasConflict(action, taskTour);
    }

    public boolean withSameAction(Action action) {
        return this.action.areSame(action);
    }
}
