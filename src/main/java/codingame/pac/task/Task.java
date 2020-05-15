package codingame.pac.task;

import codingame.pac.action.Action;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public abstract class Task {
    Action action;

    Task(Action action){
        this.action = action;
    }
    public abstract boolean isFinished();

    public abstract Action keepTargeting();
}
