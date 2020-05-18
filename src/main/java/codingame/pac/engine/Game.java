package codingame.pac.engine;

import codingame.pac.ActionBuilder;
import codingame.pac.Grid;
import codingame.pac.PacMan;
import codingame.pac.Pellet;
import codingame.pac.action.Action;
import codingame.pac.action.MoveAction;
import codingame.pac.cell.Cell;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;
import codingame.pac.pathfinder.CrossedPathsSolution;
import codingame.pac.pathfinder.PathFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class Game {

    private PathFinder pathfinder;
    private final Grid grid;
    private final Set<Floor> floors;
    private final HashMap<Coord, Cell> cellsMap;
    private final Cell[][] cells;

    public Game(PathFinder pathfinder, Grid grid, Set<Floor> floors, HashMap<Coord, Cell> cellsMap, Cell[][] cells) {

        this.pathfinder = pathfinder;
        this.grid = grid;
        this.floors = floors;
        this.cellsMap = cellsMap;
        this.cells = cells;
    }

    public static CrossedPathsSolution calculatePathsAndCheckIfTheyAreCrossed(PacMan pacMan1, PacMan pacMan2, PathFinder pathfinder, boolean reverseCheck) {
        if (!reverseCheck){
            List<Coord> path1 = getPath(pacMan1.getCoord(), pacMan1.getTarget(), pathfinder);
            List<Coord> path2 = getPath(pacMan2.getCoord(), pacMan2.getTarget(), pathfinder);
            return isCrossedPaths(path1, path2, pacMan1, pacMan2);
        } else {
            List<Coord> path1 = getPath(pacMan1.getCoord(), pacMan2.getTarget(), pathfinder);
            List<Coord> path2 = getPath(pacMan2.getCoord(), pacMan1.getTarget(), pathfinder);
            return isCrossedPaths(path1, path2, pacMan1, pacMan2);
        }
    }

    private static List<Coord> getPath(Coord source, Coord target, PathFinder pathfinder) {
        return pathfinder
                .from(source)
                .to(target)
                .findPath().path;
    }

    public static CrossedPathsSolution isCrossedPaths(List<Coord> path1, List<Coord> path2, PacMan pacMan1, PacMan pacMan2) {
        if (path1.size() > 1 && path2.size() > 1) {
            double distanceTo = pacMan1.distanceTo(pacMan2);
            if (distanceTo <= 1.0) {
                if (path1.get(0).equals(path2.get(1))
                        && path1.get(1).equals(path2.get(0))){
                    return CrossedPathsSolution.SWITCH;
                }
            }else if (path1.get(1).equals(path2.get(1))) {
                if (distanceTo <= 1.5) {
                    return CrossedPathsSolution.WAIT;
                } else if (distanceTo <= 2.0){
                    return CrossedPathsSolution.SWITCH;
                }
            }
        }
        return CrossedPathsSolution.NO_NEED;
    }

    public static Stream<PacMan> otherPacmen(PacMan pac, Collection<PacMan> collection) {
        return collection.stream().filter(p -> p != pac);
    }

    public static Set<Pellet> updateBasedOnPacManVisibility(Map<Coord, Pellet> newVisiblePellets, Collection<PacMan> pacManMap, Cell[][] cells) {
        Set<Coord> visibleCoords = getAllVisibleCoords(pacManMap.stream(), cells);
        for (Coord visibleCoord : visibleCoords) {
            Pellet pellet = newVisiblePellets.get(visibleCoord);
            if (pellet == null) {
                Floor floor = (Floor) cells[visibleCoord.getX()][visibleCoord.getY()];
                floor.noPellet();
            }
        }
        return null;
    }

    public static Set<Coord> getAllVisibleCoords(Stream<PacMan> alivePacmen, Cell[][] cells) {
        Set<Coord> visibleCoords = new HashSet<>();
        alivePacmen.forEach(pacman -> {
            pacman.myVisibleCells(cells).forEach(cell -> visibleCoords.add(cell.getCoord()));
        });
        return visibleCoords;
    }

    public static void printEndTime(long startTime, String message) {
        long endTime = System.nanoTime();
        long durationInNano = (endTime - startTime);  //Total execution time in nano seconds
        long durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNano);

        System.err.println(message + " = " + durationInMillis + "ms");
    }

    public static void ifCrossedPathsDoSwitchTask(Set<PacMan> pacManSet, PathFinder pathfinder, List<Action> actionsList, Set<Floor> floors, int finalTour) {
        List<PacMan> blockedPac = new ArrayList<>();

        pacManSet.forEach(pacMan -> {
            if (!blockedPac.contains(pacMan)) {
                Stream<PacMan> pacManStream = Game.otherPacmen(pacMan, pacManSet);
                Optional<PacMan> firstOne = pacManStream.filter(pac -> pac.distanceTo(pacMan) <= 2.0).findFirst();
                if (firstOne.isPresent() && !blockedPac.contains(firstOne.get())) {
                    CrossedPathsSolution crossedPathsSolution = Game.calculatePathsAndCheckIfTheyAreCrossed(pacMan, firstOne.get(), pathfinder, false);
                    if (crossedPathsSolution.equals(CrossedPathsSolution.SWITCH)) {
                        /*crossedPathsSolution = Game.calculatePathsAndCheckIfTheyAreCrossed(pacMan, firstOne.get(), pathfinder, true);
                        if (crossedPathsSolution.equals(CrossedPathsSolution.SWITCH)) {
                            actionsList.remove(pacMan.getCurrentAction());
                            MoveAction moveAction = ActionBuilder.buildFindPelletAction(floors.stream().filter(floor -> floor.isHidden() && floor.isNotTargetedForDiscovery()).collect(Collectors.toSet()), pacMan);
                            if (moveAction != null) {
                                actionsList.add(moveAction);
                            } else {
                                pacMan.setWaitTask();
                                actionsList.add(pacMan.getCurrentAction());
                            }
                        } else {*/
                            pacMan.switchTasksWith(firstOne.get());
                            blockedPac.add(pacMan);
                            blockedPac.add(firstOne.get());
                        //}
                    } else if (CrossedPathsSolution.WAIT.equals(crossedPathsSolution)) {
                        pacMan.setWaitTask();
                    }
                }
            }
        });
    }

    public PacMan checkIfThEnemyIsAttackingMe(PacMan pacMan, List<PacMan> otherPacMen) {
        if (otherPacMen.isEmpty()) {
            return null;
        }

        Optional<PacMan> first = otherPacMen.stream().filter(otherPacMan -> pacMan.isVisibleToMe(otherPacMan)).sorted((o1, o2) -> {
            return o1.distanceTo(pacMan) > o2.distanceTo(pacMan) ? 1 : -1;
        }).findFirst();

        if (!first.isPresent()) {
            return null;
        }

        PacMan attacker = first.get();
        System.err.println(pacMan.infoMe() + " is crossing " + attacker.infoMe());
        if (attacker.distanceTo(pacMan) <= 3) {
            System.err.println(pacMan.infoMe() + " under attack by " + attacker.infoMe());
            return attacker;
        }
        return null;
    }
}
