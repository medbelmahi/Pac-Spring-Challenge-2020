package codingame.pac.cell;

import codingame.pac.Grid;
import codingame.pac.PacMan;
import codingame.pac.Pellet;
import codingame.pac.action.MoveAction;
import codingame.pac.bfs.Node;
import codingame.pac.task.EatTask;
import codingame.pac.task.FindPelletTask;

import java.util.Set;
import java.util.TreeSet;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class Floor extends Cell {

    private Pellet pallet;
    private FloorStatus floorStatus;
    private boolean targeted;
    private PacMan targetedForDiscovery;

    Floor(Coord coord) {
        super(coord);
        this.floorStatus = FloorStatus.HIDDEN;
    }

    public void setPallet(Pellet pallet) {
        this.pallet = pallet;
        this.floorStatus = pallet.isSuper() ? FloorStatus.HAS_SUPER_PELLET : FloorStatus.HAS_SIMPLE_PELLET;
    }

    public double distanceTo(Floor floor) {
        return coord.distanceTo(floor.coord);
    }

    public void noPellet() {
        if (pallet != null) {
            pallet.setStillHere(false);
        }
        this.floorStatus = FloorStatus.EMPTY;
    }

    @Override
    public String toString() {
        switch (this.floorStatus) {
            case EMPTY: return " ";
            case HAS_SIMPLE_PELLET: return "o";
            case HAS_SUPER_PELLET: return "O";
            case HIDDEN: return "?";
        }
        return " ";
    }

    public boolean isHidden() {
        return FloorStatus.HIDDEN.equals(floorStatus);
    }

    public MoveAction targeted(PacMan pacMan) {
        this.targeted = true;

        MoveAction moveAction = new MoveAction(pacMan, coord);
        pacMan.setTask(new FindPelletTask(moveAction, this));
        this.targetedForDiscovery = pacMan;
        markStreetAsTargeted(pacMan);
        return moveAction;
    }

    private void markStreetAsTargeted(PacMan pacMan) {
        int x = this.coord.x;
        int y = this.coord.y;

        for (int i = x - 1; i >= 0; i--) {
            Cell cell = Grid.cellsMap.get(new Coord(i, y));
            if (cell == null || cell.isWall()) {
                break;
            }
            ((Floor) cell).setTargetedForDiscovery(pacMan);
        }

        for (int i = x + 1; i < Grid.width; i++) {
            Cell cell = Grid.cellsMap.get(new Coord(i, y));
            if (cell == null || cell.isWall()) {
                break;
            }
            ((Floor) cell).setTargetedForDiscovery(pacMan);
        }

        for (int i = y - 1; i >= 0; i--) {
            Cell cell = Grid.cellsMap.get(new Coord(x, i));
            if (cell == null || cell.isWall()) {
                break;
            }
            ((Floor) cell).setTargetedForDiscovery(pacMan);
        }

        for (int i = y + 1; i < Grid.height; i++) {
            Cell cell = Grid.cellsMap.get(new Coord(x, i));
            if (cell == null || cell.isWall()) {
                break;
            }
            ((Floor) cell).setTargetedForDiscovery(pacMan);
        }
    }

    public boolean isTargeted() {
        return targeted;
    }

    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }

    public Set<Floor> getSortedEdgesBasedOnDistanceFromTarget(Floor target, Set<Floor> edges) {
        Set<Floor> sortedEdgesBasedOnDistanceFromTarget = new TreeSet<Floor>((floor1, floor2) -> {

            double floor1DistanceToTarget = floor1.distanceTo(target);
            double floor2DistanceToTarget = floor2.distanceTo(target);
            return floor1DistanceToTarget < floor2DistanceToTarget ? -1 : 1;
        });

        sortedEdgesBasedOnDistanceFromTarget.addAll(edges);

        return sortedEdgesBasedOnDistanceFromTarget;
    }

    public boolean isEmpty() {
        return FloorStatus.EMPTY.equals(floorStatus);
    }

    public boolean isNotTargetedForDiscovery() {
        return targetedForDiscovery == null || !targetedForDiscovery.isAlive();
    }

    public void setTargetedForDiscovery(PacMan targetedForDiscovery) {
        this.targetedForDiscovery = targetedForDiscovery;
    }
}
