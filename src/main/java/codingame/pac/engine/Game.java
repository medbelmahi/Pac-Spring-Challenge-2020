package codingame.pac.engine;

import codingame.pac.PacMan;
import codingame.pac.Pellet;
import codingame.pac.cell.Cell;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;
import codingame.pac.pathfinder.CrossedPathsSolution;
import codingame.pac.pathfinder.PathFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class Game {

    public static CrossedPathsSolution calculatePathsAndCheckIfTheyAreCrossed(PacMan pacMan1, PacMan pacMan2, Cell[][] cells, PathFinder pathfinder) {
        List<Coord> path1 = pathfinder
                .from(pacMan1.getCoord())
                .to(pacMan1.getTarget())
                .findPath().path;

        List<Coord> path2 = pathfinder
                .from(pacMan2.getCoord())
                .to(pacMan2.getTarget())
                .findPath().path;
        return isCrossedPaths(path1, path2, pacMan1, pacMan2);
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

    public static void ifCrossedPathsDoSwitchTask(Set<PacMan> pacManSet, Cell[][] cells, PathFinder pathfinder) {
        List<PacMan> blockedPac = new ArrayList<>();

        pacManSet.forEach(pacMan -> {
            if (!blockedPac.contains(pacMan)) {
                Stream<PacMan> pacManStream = Game.otherPacmen(pacMan, pacManSet);
                Optional<PacMan> firstOne = pacManStream.filter(pac -> pac.distanceTo(pacMan) <= 2.0).findFirst();
                if (firstOne.isPresent() && !blockedPac.contains(firstOne.get())) {
                    CrossedPathsSolution crossedPathsSolution = Game.calculatePathsAndCheckIfTheyAreCrossed(pacMan, firstOne.get(), cells, pathfinder);
                    if (crossedPathsSolution.equals(CrossedPathsSolution.SWITCH)) {
                        pacMan.switchTasksWith(firstOne.get());
                        blockedPac.add(pacMan);
                        blockedPac.add(firstOne.get());
                    } else if (CrossedPathsSolution.WAIT.equals(crossedPathsSolution)) {
                        pacMan.setWaitTask();
                    }
                }
            }
        });
    }
}
