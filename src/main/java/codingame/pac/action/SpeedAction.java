package codingame.pac.action;

import codingame.pac.PacmanType;

public class SpeedAction implements Action {
  @Override
  public ActionType getActionType() {
      return ActionType.SPEED;
  }

  @Override
  public PacmanType getType() {
      return null;
  }
}
