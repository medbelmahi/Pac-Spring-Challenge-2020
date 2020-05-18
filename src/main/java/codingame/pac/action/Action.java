package codingame.pac.action;

import codingame.pac.PacMan;

import java.util.Arrays;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public abstract class Action {
    PacMan pacMan;

    public Action(PacMan pacMan) {
        this.pacMan = pacMan;
    }

    public abstract ActionType type();

    public String print(int pacId) {
        return String.join(" ", Arrays.asList(type().toString(), String.valueOf(pacId), msg()));
    }
    protected abstract String msg();

    public void changeItPacWith(Action action) {
        PacMan temp = this.pacMan;
        this.pacMan = action.pacMan;
        action.pacMan = temp;
    }

    public String printCommand() {
        return pacMan.doCommand(this);
    }
}
