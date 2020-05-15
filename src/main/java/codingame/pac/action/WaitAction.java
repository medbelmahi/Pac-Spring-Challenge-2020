package codingame.pac.action;

import codingame.pac.PacMan;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class WaitAction extends Action {
    public WaitAction(PacMan pacMan) {
        super(pacMan);
    }

    @Override
    public ActionType type() {
        return ActionType.WAIT;
    }

    @Override
    public String print(int pacId) {
        return String.join(" ", type().toString(), String.valueOf(pacId), msg());
    }

    @Override
    protected String msg() {
        return "W";
    }
}
