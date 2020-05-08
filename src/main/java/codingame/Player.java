package codingame;

import codingame.pac.Coord;
import codingame.pac.Gamer;
import codingame.pac.Pacman;
import codingame.pac.PacmanType;
import codingame.pac.cell.Cell;
import codingame.pac.Game;
import codingame.pac.Grid;
import codingame.pac.cell.CellPrototype;

import java.util.*;

/**
 * Grab the pellets as fast as you can!
 **/
public class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }

        Cell[][] cells = new Cell[width][height];
        for (int i = 0; i < height; i++) {
            int y = i;
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall

            char[] cellsInput = row.toCharArray();
            for (int x = 0; x < cellsInput.length; x++) {
                cells[x][y] = CellPrototype.getCell(cellsInput[x]);
            }
        }

        Grid grid = new Grid(cells, width, height);

        grid.printGrid();
        Game game = new Game(grid);
        Gamer me = new Gamer();
        game.setMe(me);
        Gamer opponent = new Gamer();
        game.setOpponent(opponent);

        Map<Integer, Pacman> pacmanMap = new HashMap<>();
        // Start First Tour
        setScores(in, me, opponent);

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
                pacman = new Pacman(pacId, 0, me, new Coord(x, y), PacmanType.NEUTRAL);
            } else {
                pacman = new Pacman(pacId, 0, opponent, new Coord(x, y), PacmanType.NEUTRAL);
            }
            pacmanMap.put(pacId, pacman);
        }

        LinkedList<Pellet> pellets = new LinkedList<>();
        LinkedList<Pellet> superPellets = new LinkedList<>();
        Map<Coord, Pellet> pelletMap = new HashMap<>();
        int visiblePelletCount = in.nextInt(); // all pellets in sight
        for (int i = 0; i < visiblePelletCount; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            int value = in.nextInt(); // amount of points this pellet is worth

            Coord coord = new Coord(x, y);
            Pellet pellet = new Pellet(coord, value);
            if (value == 10) {
                superPellets.add(pellet);
            } else {
                pellets.add(pellet);
            }
            pelletMap.put(coord, pellet);
        }
        game.setPellets(pellets);
        game.setSuperPellets(superPellets);
        game.play(); // MOVE <pacId> <x> <y>
        // Start First Tour



        // game loop after first tour
        while (true) {
            setScores(in, me, opponent);

            visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                Pacman pacman = pacmanMap.get(pacId);
                boolean mine = in.nextInt() != 0; // true if this pac is yours
                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                pacman.setPosition(new Coord(x, y));
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues
            }
            visiblePelletCount = in.nextInt(); // all pellets in sight

            pellets = new LinkedList<>();
            superPellets = new LinkedList<>();
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth

                Pellet pellet = new Pellet(new Coord(x, y), value);
                if (value == 10) {
                    superPellets.add(pellet);
                } else {
                    pellets.add(pellet);
                }
            }
            game.setPellets(pellets);
            game.setSuperPellets(superPellets);
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            game.play(); // MOVE <pacId> <x> <y>
        }
    }

    private static void setScores(Scanner in, Gamer me, Gamer opponent) {
        int myScore = in.nextInt();
        me.setScore(myScore);
        int opponentScore = in.nextInt();
        opponent.setScore(opponentScore);
    }
}
