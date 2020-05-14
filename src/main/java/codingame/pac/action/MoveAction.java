package codingame.pac.action;


import codingame.pac.Coord;
import codingame.pac.PacmanType;
import codingame.pac.agent.Pacman;

public class MoveAction implements Action {

    private Coord destination;
    private int id;
    private Pacman pacman;

    public Coord getTarget() {
        return destination;
    }

    public MoveAction(Coord destination, boolean activateSpeed, int id, Pacman pacman) {
        this.destination = destination;
        this.id = id;
        this.pacman = pacman;
    }

    @Override
    public PacmanType getType() {
        return null;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.MOVE;
    }

    public String print() {
        return getActionType().toString() + " " + id + " " + destination.print() + " MV "+destination.shortPrint();
    }

    @Override
    public boolean areSame(Action action) {
        if (action instanceof MoveAction) {
            Coord destination = ((MoveAction) action).destination;
            return destination.equals(this.destination) || destination.isNeighborOf(this.destination);
        }
        return false;
    }

    @Override
    public String print(int taskTour) {
        return print() + ":" + taskTour;
    }
}
