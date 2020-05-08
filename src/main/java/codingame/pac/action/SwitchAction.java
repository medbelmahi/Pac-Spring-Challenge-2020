package codingame.pac.action;


import codingame.pac.PacmanType;

public class SwitchAction implements Action {

    private PacmanType type;

    public PacmanType getNewType() {
        return type;
    }

    public SwitchAction(PacmanType type) {
        this.type = type;
    }

    @Override
    public PacmanType getType() {
        return type;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SWITCH;
    }
}
