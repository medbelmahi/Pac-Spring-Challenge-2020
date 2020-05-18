package codingame.pac.task;

import codingame.pac.action.Action;
import codingame.pac.cell.Coord;

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

    public abstract boolean isMoveTask();

    public void switchTo(Task task) {
         this.action.changeItPacWith(task.action);
    }

    public abstract Coord moveTarget();

    public abstract String printInfo();
}
