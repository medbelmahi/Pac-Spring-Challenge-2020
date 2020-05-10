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

        @Override
        public String print(int id) {
            return ActionType.WAIT.toString() + " " + id;
        }
    };

    public PacmanType getType();
    public ActionType getActionType();

    String print(int id);
}
