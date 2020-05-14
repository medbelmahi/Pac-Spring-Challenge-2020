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

    @Override
    public String print() {
        return null;
    }

    @Override
    public boolean areSame(Action action) {
        return false;
    }

    @Override
    public String print(int taskTour) {
        return null;
    }
}
