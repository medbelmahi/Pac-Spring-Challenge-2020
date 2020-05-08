package codingame.pac.action;

import codingame.pac.PacmanType;

public interface Action {

    Action NO_ACTION = new Action() {

        @Override
        public PacmanType getType() {
            return null;
        }

        @Override
        public ActionType getActionType() {
            return ActionType.WAIT;
        }
    };

    public PacmanType getType();
    public ActionType getActionType();
}
