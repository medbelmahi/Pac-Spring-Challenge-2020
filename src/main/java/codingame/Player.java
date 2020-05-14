package codingame;

import codingame.pac.Coord;
import codingame.pac.Gamer;
import codingame.pac.agent.Pacman;
import codingame.pac.PacmanType;
import codingame.pac.cell.Cell;
import codingame.pac.Game;
import codingame.pac.Grid;
import codingame.pac.cell.CellPrototype;
import codingame.pac.cell.Floor;
import codingame.pac.mission.MissionEngine;
import codingame.pac.pathfinder.PathFinder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Grab the pellets as fast as you can!
 **/
public class Player {

    public static void main(String args[]) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }

        Cell[][] cells = new Cell[width][height];
        final Set<Floor> places = new HashSet<>();
        for (int i = 0; i < height; i++) {
            int y = i;
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            System.err.println(row);
            char[] cellsInput = row.toCharArray();
            for (int x = 0; x < cellsInput.length; x++) {
                Cell cell = CellPrototype.getCell(cellsInput[x], x, y);
                cells[x][y] = cell;
                if (cell instanceof Floor) {
                    places.add((Floor) cell);
                }
            }
        }

        Grid grid = new Grid(cells, places, width, height);
        Game game = new Game(grid);
        Gamer me = new Gamer(grid);
        game.setMe(me);
        Gamer opponent = new Gamer(grid);
        game.setOpponent(opponent);

        Map<String, Pacman> pacmanMap = new HashMap<>();


        // Start First Tour -------------------------------------------------------------------------------------------
        int tour = 1;
        long startTime = System.nanoTime();
        setScores(in, me, opponent, game);

        int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
        for (int i = 0; i < visiblePacCount; i++) {
            int pacId = in.nextInt(); // pac number (unique within a team)
            boolean mine = in.nextInt() != 0; // true if this pac is yours
            int x = in.nextInt(); // position in the grid
            int y = in.nextInt(); // position in the grid
            String typeId = in.next(); // unused in wood leagues
            int speedTurnsLeft = in.nextInt(); // unused in wood leagues
            int abilityCooldown = in.nextInt(); // unused in wood leagues

            Pacman pacman;
            if (mine) {
                pacman = new Pacman(pacId, 0, me, new Coord(x, y), PacmanType.fromInput(typeId), speedTurnsLeft, abilityCooldown, 1);
            } else {
                pacman = new Pacman(pacId, 0, opponent, new Coord(x, y), PacmanType.fromInput(typeId), speedTurnsLeft, abilityCooldown, 1);
            }
            pacmanMap.put(pacId + "-" + mine, pacman);
            cells[x][y].noPellet();
        }

        LinkedList<Pellet> pellets = new LinkedList<>();
        Set<Pellet> superPellets = new HashSet<>();
        Map<Coord, Pellet> newVisiblePellets = new HashMap<>();

        Set<Pellet> sortedSuperPellets = new TreeSet<Pellet>((p1, p2) -> {
            Supplier<Stream<Pacman>> alivePacmen = () -> me.getAlivePacmen();
            double p1NearestDistanceToAPacman = p1.getNearestDistanceToAPacman(alivePacmen.get());
            double p2NearestDistanceToAPacman = p2.getNearestDistanceToAPacman(alivePacmen.get());

            return p1NearestDistanceToAPacman < p2NearestDistanceToAPacman ? -1 : 1;
        });

        Set<Pellet> sortedPellets = new TreeSet<Pellet>((p1, p2) -> {
            Supplier<Stream<Pacman>> alivePacmen = () -> me.getAlivePacmen();
            double p1NearestDistanceToAPacman = p1.getNearestDistanceToAPacman(alivePacmen.get());
            double p2NearestDistanceToAPacman = p2.getNearestDistanceToAPacman(alivePacmen.get());

            return p1NearestDistanceToAPacman < p2NearestDistanceToAPacman ? -1 : 1;
        });

        int visiblePelletCount = in.nextInt(); // all pellets in sight
        for (int i = 0; i < visiblePelletCount; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            int value = in.nextInt(); // amount of points this pellet is worth

            Coord coord = new Coord(x, y);
            Pellet pellet = new Pellet(coord, value);
            if (value == 10) {
                //superPellets.add(pellet);
                sortedSuperPellets.add(pellet);
                System.err.println("super : " + coord);
            } else {
                pellets.add(pellet);
            }
            ((Floor) cells[x][y]).setPellet(pellet);
            newVisiblePellets.put(coord, pellet);
            sortedPellets.add(pellet);
        }

        MissionEngine ME = new MissionEngine(game);

        //printSuperPellets(sortedSuperPellets);

        me.updatePellets(newVisiblePellets, new HashSet<>(), cells);
        game.setPellets(pellets);
        game.setSuperPellets(superPellets);

            grid.printGrid();

        ME.collectMissions(sortedSuperPellets, me.getAlivePacmen());
        System.out.println(game.play());
        printEndTime(startTime, "First Tour");
        // Start First Tour -------------------------------------------------------------------------------------------



        // game loop after first tour
        while (true) {
            startTime = System.nanoTime();
            tour++;
            game.nextTour();
            setScores(in, me, opponent, game);

            visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours

                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues

                String key = pacId + "-" + mine;
                System.err.println("pacman : " + key + " Has ability countdown : : " + abilityCooldown);
                Pacman pacman = pacmanMap.get(key);
                if (pacman == null) {
                    pacman = new Pacman(pacId, 0, opponent, new Coord(x, y), PacmanType.fromInput(typeId), speedTurnsLeft, abilityCooldown, tour);
                    pacmanMap.put(key, pacman);
                }else {
                    pacman.setSpeedTurnsLeft(speedTurnsLeft);
                    pacman.setType(PacmanType.fromInput(typeId));
                    pacman.setPosition(new Coord(x, y));
                    pacman.setAbilityCooldown(abilityCooldown);
                    pacman.update();
                }

                cells[x][y].noPellet();
            }
            setDeadPacmen(pacmanMap.values(), tour);
            visiblePelletCount = in.nextInt(); // all pellets in sight

            //pellets.clear();
            superPellets.clear();
            newVisiblePellets.clear();
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth

                Coord coord = new Coord(x, y);
                Pellet pellet = new Pellet(coord, value);
                if (value != 10) {
                    pellets.add(pellet);
                } else {

                }
                ((Floor) cells[x][y]).setPellet(pellet);
                newVisiblePellets.put(coord, pellet);
            }
            me.updatePellets(newVisiblePellets, sortedSuperPellets, cells);
            game.setPellets(pellets);
            game.setSuperPellets(superPellets);

            grid.printGrid();
            printEndTime(startTime, "0 - Tour number ("+tour +")");
            List<Pacman> alivePacmen = me.getAlivePacmen().collect(Collectors.toList());
            if (!sortedSuperPellets.isEmpty()) {
                ME.collectMissions(sortedSuperPellets, alivePacmen.stream());
            }
            printEndTime(startTime, "1 - Tour number ("+tour +")");
            sortedPellets.clear();
            printEndTime(startTime, "3 - Tour number ("+tour +")");
            sortedPellets.addAll(game.getPellets());
            printEndTime(startTime, "4 - Tour number ("+tour +")");
            ME.collectMissions(sortedPellets, alivePacmen.stream());
            printEndTime(startTime, "5 - Tour number ("+tour +")");
            System.out.println(game.play());
            printEndTime(startTime, "Tour number ("+tour +")");
        }
    }

    private static void printSuperPellets(Set<Pellet> sortedSuperPellets) {
        System.err.println("sortedSuperPellets : ");
        for (Pellet sortedSuperPellet : sortedSuperPellets) {
            System.err.print(sortedSuperPellet + " ");
        }
        System.err.println();
    }

    private static void setDeadPacmen(Collection<Pacman> pacmen, int currentTour) {
        pacmen.forEach(pacman -> pacman.setDead(currentTour));
    }

    private static void printEndTime(long startTime, String message) {
        long endTime = System.nanoTime();
        long durationInNano = (endTime - startTime);  //Total execution time in nano seconds
        long durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNano);

        System.err.println(message + " = " + durationInMillis + "ms");
    }

    private static void setScores(Scanner in, Gamer me, Gamer opponent, Game game) {
        int myScore = in.nextInt();
        me.setScore(myScore, game);
        int opponentScore = in.nextInt();
        opponent.setScore(opponentScore, game);
    }
}
