package codingame.pac.action;

import codingame.pac.PacMan;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class WaitAction extends Action {
    int counter = 2;
    public WaitAction(PacMan pacMan) {
        super(pacMan);
    }

    @Override
    public ActionType type() {
        return ActionType.WAIT;
    }

    @Override
    public String print(int pacId) {
        counter--;
        return super.print(pacId);
    }

    @Override
    protected String msg() {
        return "W";
    }

    public boolean isFinished() {
        return pacMan.noNeedToKeepWaiting(counter);
    }
}
