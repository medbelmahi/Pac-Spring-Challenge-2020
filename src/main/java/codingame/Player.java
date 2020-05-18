package codingame;

import codingame.pac.ActionBuilder;
import codingame.pac.Grid;
import codingame.pac.PacMan;
import codingame.pac.Pellet;
import codingame.pac.action.Action;
import codingame.pac.action.SpeedAction;
import codingame.pac.cell.Cell;
import codingame.pac.cell.CellFactory;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;
import codingame.pac.engine.Game;
import codingame.pac.pathfinder.PathFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

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

        Game game = new Game(pathfinder, grid, floors, cellsMap, cells);

        Game.printEndTime(startTime, "PATH");

        Map<String, PacMan> pacManMap = new HashMap<>();
        Set<Pellet> pellets = new HashSet<>();
        Set<PacMan> myPacMen = new HashSet<>();
        List<PacMan> otherPacMen = new ArrayList<>(5);
        Map<Coord, Pellet> pelletMap = new HashMap<>();
        int tour = 0;
        // game loop
        while (true) {
            startTime = System.nanoTime();
            tour++;
            int myScore = in.nextInt();
            int opponentScore = in.nextInt();
            int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            otherPacMen.clear();
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours
                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues


                PacMan pacMan = pacManMap.get(pacId + "-" + mine);
                if (pacMan != null) {
                    pacMan.update(typeId, cells[x][y], speedTurnsLeft, abilityCooldown);
                    if (!mine) {
                        otherPacMen.add(pacMan);
                    }
                } else {
                    pacMan = new PacMan(pacId, cells[x][y], typeId, speedTurnsLeft, abilityCooldown);
                    pacManMap.put(pacId + "-" + mine, pacMan);
                    if (mine) {
                        myPacMen.add(pacMan);
                    } else {
                        otherPacMen.add(pacMan);
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
            int finalTour = tour;
            Set<PacMan> alivePacMen = myPacMen.stream().filter(pacMan -> pacMan.isAlive()).collect(Collectors.toSet());

            Game.updateBasedOnPacManVisibility(newVisiblePellets, alivePacMen, cells);
            pellets.parallelStream().forEach(Pellet::notTargeted);

            //grid.printGrid();
            Game.printEndTime(startTime, "0 - Tour number ("+tour +")");
            List<Action> actionsList = new ArrayList<>();

            Set<Pellet> finalPellets = pellets.parallelStream().filter(pellet -> pellet.isStillHere()).collect(Collectors.toSet());

            alivePacMen.stream().filter(pacMan -> pacMan.hasTask()).forEach(pacMan -> {
                boolean canSpeedUpOrSwitch = pacMan.canSpeedUpOrSwitch();
                if (canSpeedUpOrSwitch) {
                    actionsList.add(new SpeedAction(pacMan));
                } else {
                    actionsList.add(pacMan.getCurrentAction());
                }
            });
            Game.printEndTime(startTime, "1 - Tour number ("+tour +")");
            alivePacMen.stream().filter(pacMan -> !pacMan.hasTask()).forEach(pacMan -> {
                Action moveAction = null;
                if (pacMan.canSpeedUpOrSwitch()) {
                    moveAction = new SpeedAction(pacMan);
                }
                if ((moveAction != null)){
                    actionsList.add(moveAction);
                }
                else {
                    moveAction = ActionBuilder.buildMoveAction(finalPellets.stream()
                            .filter(pellet -> pellet.isSuper() && !pellet.isTargeted()).collect(Collectors.toSet()), pacMan);
                    if (moveAction != null) {
                        actionsList.add(moveAction);
                    } else {
                        moveAction = ActionBuilder.buildMoveAction(finalPellets.stream()
                                .filter(pellet -> !pellet.isSuper() && !pellet.isTargeted()).collect(Collectors.toSet()), pacMan);
                        if (moveAction != null) {
                            actionsList.add(moveAction);
                        }else {
                            moveAction = ActionBuilder.buildFindPelletAction(floors.stream().filter(floor -> floor.isHidden() && floor.isNotTargetedForDiscovery()).collect(Collectors.toSet()), pacMan);
                            if (moveAction != null) {
                                actionsList.add(moveAction);
                            }
                        }
                    }
                }
            });
            Game.printEndTime(startTime, "3 - Tour number ("+tour +")");
            Game.ifCrossedPathsDoSwitchTask(alivePacMen.stream().filter(PacMan::hasMoveTask).collect(Collectors.toSet()), pathfinder, actionsList, floors, finalTour);

            String actions = String.join(" | ", actionsList.stream().map(Action::printCommand).collect(Collectors.toList()));
            Game.printEndTime(startTime, "99 - Tour number ("+tour +")");
            System.out.println(actions); // MOVE <pacId> <x> <y>
        }
    }




}
