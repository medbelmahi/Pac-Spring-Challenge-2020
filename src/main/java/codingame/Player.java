package codingame;

import codingame.pac.ActionBuilder;
import codingame.pac.Grid;
import codingame.pac.PacMan;
import codingame.pac.Pellet;
import codingame.pac.action.Action;
import codingame.pac.action.MoveAction;
import codingame.pac.action.SpeedAction;
import codingame.pac.cell.Cell;
import codingame.pac.cell.CellFactory;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;
import codingame.pac.engine.Game;
import codingame.pac.pathfinder.CrossedPathsSolution;
import codingame.pac.pathfinder.PathFinder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {

    public static void main(String args[]) {
        long startTime = System.nanoTime();
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }
        Cell[][] cells = new Cell[width][height];
        HashMap<Coord, Cell> cellsMap = new HashMap<>();
        Set<Floor> floors = new HashSet<>();
        for (int i = 0; i < height; i++) {
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            System.err.println(row);
            char[] inputTypes = row.toCharArray();
            for (int x = 0; x < inputTypes.length; x++) {
                Coord coord = new Coord(x, i);
                Cell cell = CellFactory.createCell(inputTypes[x], coord);
                cells[x][i] = cell;
                cellsMap.put(coord, cell);
                if (!cell.isWall()) {
                    floors.add((Floor) cell);
                }
            }
        }

        Grid grid = new Grid(width, height, cellsMap, cells);
        PathFinder pathfinder = new PathFinder().setGrid(grid);

        Map<String, PacMan> pacManMap = new HashMap<>();
        Set<Pellet> pellets = new HashSet<>();
        Map<Coord, Pellet> pelletMap = new HashMap<>();
        int tour = 0;
        // game loop
        while (true) {
            startTime = System.nanoTime();
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
            Game.updateBasedOnPacManVisibility(newVisiblePellets, pacManMap.values(), cells);
            pellets.parallelStream().forEach(Pellet::notTargeted);

            grid.printGrid();
            Game.printEndTime(startTime, "0 - Tour number ("+tour +")");
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
                        }else {
                            moveAction = ActionBuilder.buildFindPelletAction(floors.stream().filter(floor -> floor.isHidden()).collect(Collectors.toSet()), pacMan);
                            if (moveAction != null) {
                                actionsList.add(moveAction);
                            }
                        }
                    }
                }
            });

            Game.ifCrossedPathsDoSwitchTask(pacManMap.values().stream().filter(pacMan -> pacMan.isAlive(finalTour) && pacMan.hasMoveTask()).collect(Collectors.toSet()), cells, pathfinder);

            String actions = String.join(" | ", actionsList.stream().map(Action::printCommand).collect(Collectors.toList()));
            Game.printEndTime(startTime, "99 - Tour number ("+tour +")");
            System.out.println(actions); // MOVE <pacId> <x> <y>
        }
    }




}
