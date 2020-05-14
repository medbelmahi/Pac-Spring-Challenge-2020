package codingame.pac.action;

import codingame.pac.PacmanType;

public interface Action {
    PacmanType getType();
    ActionType getActionType();
    String print();

    boolean areSame(Action action);

    String print(int taskTour);
}
