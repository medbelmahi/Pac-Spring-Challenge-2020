package codingame.pac.action;

import codingame.pac.PacMan;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public abstract class Action {
    PacMan pacMan;

    public Action(PacMan pacMan) {
        this.pacMan = pacMan;
    }

    public abstract String printCommand();
    public abstract ActionType type();

    public abstract String print(int pacId);
    protected abstract String msg();
}
