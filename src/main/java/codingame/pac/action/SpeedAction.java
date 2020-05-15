package codingame.pac.action;

import codingame.pac.PacMan;

import java.util.Arrays;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class SpeedAction extends Action {
    public SpeedAction(PacMan pacMan) {
        super(pacMan);
    }

    @Override
    public ActionType type() {
        return ActionType.SPEED;
    }

    @Override
    public String print(int pacId) {
        return String.join(" ", Arrays.asList(type().toString(), String.valueOf(pacId), msg()));
    }

    @Override
    protected String msg() {
        return "S";
    }
}
