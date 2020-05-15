package codingame.pac;

import codingame.pac.action.MoveAction;
import codingame.pac.cell.Floor;

import java.util.Set;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class ActionBuilder {
    public static MoveAction buildMoveAction(Set<Pellet> pellets, PacMan pacMan) {
        if (pellets.isEmpty()) {
            return null;
        }

        Pellet pellet = pacMan.getNearestPellets(pellets);

        return pellet.targeted(pacMan);
    }

    public static MoveAction buildFindPelletAction(Set<Floor> floors, PacMan pacMan) {
        if (floors.isEmpty()) {
            return null;
        }

        Floor floor = pacMan.getDeepestFloor(floors);

        return floor.targeted(pacMan);
    }
}
