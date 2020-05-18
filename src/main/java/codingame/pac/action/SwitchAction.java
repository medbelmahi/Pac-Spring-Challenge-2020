package codingame.pac.action;

import codingame.pac.PacMan;

/**
 * Mohamed BELMAHI created on 16/05/2020
 */
public class SwitchAction extends Action {

    public SwitchAction(PacMan pacMan) {
        super(pacMan);
    }

    @Override
    public ActionType type() {
        return ActionType.SWITCH;
    }

    @Override
    protected String msg() {
        return "SW-"+type().toString();
    }
}
