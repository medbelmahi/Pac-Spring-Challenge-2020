package codingame;

import codingame.pac.ActionBuilder;
import codingame.pac.PacMan;
import codingame.pac.Pellet;
import codingame.pac.action.Action;
import codingame.pac.action.MoveAction;
import codingame.pac.action.SpeedAction;
import codingame.pac.cell.Cell;
import codingame.pac.cell.CellFactory;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }
        Cell[][] cells = new Cell[width][height];
        for (int i = 0; i < height; i++) {
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            char[] inputTypes = row.toCharArray();
            for (int x = 0; x < inputTypes.length; x++) {
                Cell cell = CellFactory.createCell(inputTypes[x], new Coord(x, i));
                cells[x][i] = cell;
            }
        }

        Map<String, PacMan> pacManMap = new HashMap<>();
        Set<Pellet> pellets = new HashSet<>();
        Map<Coord, Pellet> pelletMap = new HashMap<>();
        int tour = 0;
        // game loop
        while (true) {
            tour++;
            int myScore = in.nextInt();
            int opponentScore = in.nextInt();
            int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours
                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues

                if (mine) {
                    PacMan pacMan = pacManMap.get(pacId + "-" + mine);
                    if (pacMan != null) {
                        pacMan.update(typeId, cells[x][y], speedTurnsLeft, abilityCooldown);
                    } else {
                        pacMan = new PacMan(pacId, cells[x][y], typeId, speedTurnsLeft, abilityCooldown);
                        pacManMap.put(pacId + "-" + mine, pacMan);
                    }
                }
                Floor floor = (Floor) cells[x][y];
                floor.noPellet();
            }
            Map<Coord, Pellet> newVisiblePellets = new HashMap<>();
            int visiblePelletCount = in.nextInt(); // all pellets in sight
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth

                Coord coord = new Coord(x, y);
                Pellet pellet = pelletMap.get(coord);
                if (pellet == null) {
                    pellet = new Pellet(coord, (Floor) cells[x][y], value);
                    pellets.add(pellet);
                    pelletMap.put(coord, pellet);
                }

                newVisiblePellets.put(coord, pellet);
            }
            updateBasedOnPacManVisibility(newVisiblePellets, pacManMap.values(), cells);
            pellets.parallelStream().forEach(Pellet::notTargeted);

            List<Action> actionsList = new ArrayList<>();

            Set<Pellet> finalPellets = pellets.parallelStream().filter(pellet -> pellet.isStillHere()).collect(Collectors.toSet());

            int finalTour = tour;
            pacManMap.values().stream().filter(pacMan -> pacMan.isAlive(finalTour) && pacMan.hasTask()).forEach(pacMan -> {
                actionsList.add(pacMan.getCurrentAction());
            });
            pacManMap.values().stream().filter(pacMan -> pacMan.isAlive(finalTour) && !pacMan.hasTask()).forEach(pacMan -> {
                if (pacMan.canSpeedUp()) {
                    actionsList.add(new SpeedAction(pacMan));
                } else {
                    MoveAction moveAction = ActionBuilder.buildMoveAction(finalPellets.stream()
                            .filter(pellet -> pellet.isSuper() && !pellet.isTargeted()).collect(Collectors.toSet()), pacMan);
                    if (moveAction != null) {
                        actionsList.add(moveAction);
                    } else {
                        moveAction = ActionBuilder.buildMoveAction(finalPellets.stream()
                                .filter(pellet -> !pellet.isSuper() && !pellet.isTargeted()).collect(Collectors.toSet()), pacMan);
                        if (moveAction != null) {
                            actionsList.add(moveAction);
                        }
                    }
                }
            });

            System.out.println(String.join(" | ", actionsList.stream().map(Action::printCommand).collect(Collectors.toList()))); // MOVE <pacId> <x> <y>
        }
    }

    private static Set<Pellet> updateBasedOnPacManVisibility(Map<Coord, Pellet> newVisiblePellets, Collection<PacMan> pacManMap, Cell[][] cells) {
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

    private static Set<Coord> getAllVisibleCoords(Stream<PacMan> alivePacmen, Cell[][] cells) {
        Set<Coord> visibleCoords = new HashSet<>();
        alivePacmen.forEach(pacman -> {
            pacman.myVisibleCells(cells).forEach(cell -> visibleCoords.add(cell.getCoord()));
        });
        return visibleCoords;
    }
}
