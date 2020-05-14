package codingame.pac.action;

import codingame.pac.PacmanType;

public class SpeedAction implements Action {

    private int id;

    public SpeedAction(int id) {
        this.id = id;
    }

    @Override
  public ActionType getActionType() {
      return ActionType.SPEED;
  }

    @Override
    public String print() {
        return ActionType.SPEED.toString() + " " + id + " SP";
    }

    @Override
    public boolean areSame(Action action) {
        return false;
    }

    @Override
    public String print(int taskTour) {
        return print() + ":" + taskTour;
    }

    @Override
  public PacmanType getType() {
      return null;
  }
}
