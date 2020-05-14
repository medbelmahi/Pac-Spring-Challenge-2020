package codingame.pac.mission;

import codingame.pac.Game;
import codingame.pac.agent.Pacman;

import java.util.LinkedList;

/**
 * Mohamed BELMAHI created on 11/05/2020
 */
public abstract class Mission {
    protected LinkedList<Task> tasks;
    protected Pacman hero;

    public abstract boolean isAchievable();

    public abstract boolean isRelevant();

    public abstract boolean isFinished();

    public abstract boolean build(Game game);

    public String todoTask() {
        System.err.println(toString());
        return this.tasks.pollLast().print();
    }

    public boolean hasTasks() {
        return this.tasks.size() > 0;
    }
}
