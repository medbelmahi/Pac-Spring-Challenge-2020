package codingame.pac.action;

import codingame.pac.PacMan;
import codingame.pac.cell.Coord;

import java.util.Arrays;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class MoveAction extends Action {

    private Coord coord;
    public MoveAction(PacMan pacMan, Coord coord) {
        super(pacMan);
        this.coord = coord;
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    @Override
    public String print(int pacId) {
        return String.join(" ", Arrays.asList(type().toString(), String.valueOf(pacId), coord.toString(), msg()));
    }

    @Override
    protected String msg() {
        return "M-" + coord.getX() + ":" + coord.getY();
    }

    public Coord targetCoord() {
        return coord;
    }

    public boolean isReached() {
        return this.coord.distanceTo(pacMan.getCoord()) == 0;
    }
}
