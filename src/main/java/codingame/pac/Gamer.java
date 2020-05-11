package codingame.pac;

import codingame.Pellet;
import codingame.pac.action.MoveAction;
import codingame.pac.cell.Cell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Gamer {
    private List<Pacman> pacmen = new ArrayList<>();
    private int score;
    public int pellets = 0;
    private boolean timeout;
    private Grid grid;

    public Gamer(Grid grid) {
        pacmen = new ArrayList<>();
        this.grid = grid;
    }

    public void addPacman(Pacman pacman) {
        pacmen.add(pacman);

    }

    public List<Pacman> getPacmen() {
        return pacmen;
    }

    public Stream<Pacman> getAlivePacmen() {
        return pacmen.stream().filter(pac -> !pac.isDead());
    }

    public void turnReset() {
        pacmen.forEach(a -> a.turnReset());
    }


    public void setScore(int score, Game game) {
        if ((score - this.score) > 5) {
            game.decreaseSuperPellets();
        }
        this.score = score;
    }

    public String play(LinkedList<Pellet> pellets, Set<Pellet> superPellets, Grid grid) {
        this.pacmen = getAlivePacmen().collect(Collectors.toList());
        int size = pacmen.size();
        Iterator<Pellet> pelletIterator = superPellets.iterator();
        for (int i = 0; i < size; i++) {
            if (pelletIterator.hasNext()) {
                Pellet pellet = pelletIterator.next();
                Pacman pacman = getNearestPacman(pellet, pacmen.stream().filter(Pacman::available));
                pacman.setAction(new MoveAction(pellet.getCoord(), false));
            }
        }
        pacmen.stream().filter(Pacman::available).forEach(pacman -> pacman.doAction(pellets, superPellets, grid));

        List<String> actionsList = new ArrayList<>();
        getAlivePacmen().forEach(pacman -> actionsList.add(pacman.printAction()));

        return String.join(" | ", actionsList);
    }

    private Pacman getNearestPacman(Pellet pellet, Stream<Pacman> pacmen) {
        Pacman target = null;
        double minDistance = Integer.MAX_VALUE;
        Iterator<Pacman> iterator = pacmen.iterator();
        while (iterator.hasNext()) {
            Pacman pacman = iterator.next();
            double distance = pacman.distance(pellet.getCoord());
            if (minDistance > distance) {
                minDistance = distance;
                target = pacman;
            }
        }
        return target;
    }

    public void updatePellets(Map<Coord, Pellet> newVisiblePellets, Cell[][] cells) {
       Set<Coord> visibleCoords = getAllVisibleCoords(getAlivePacmen(), cells);
        for (Coord visibleCoord : visibleCoords) {
            Pellet pellet = newVisiblePellets.get(visibleCoord);
            if (pellet == null) {
                Cell cell = cells[visibleCoord.x][visibleCoord.y];
                cell.noPellet();
            }
        }
    }

    private Set<Coord> getAllVisibleCoords(Stream<Pacman> alivePacmen, Cell[][] cells) {
        Set<Coord> visibleCoords = new HashSet<>();
        alivePacmen.forEach(pacman -> {
            pacman.myVisibleCells(cells).forEach(cell -> visibleCoords.add(cell.getCoordinates()));
        });
        return visibleCoords;
    }
}
