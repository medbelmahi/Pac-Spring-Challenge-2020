package codingame.pac;

import codingame.pac.cell.Cell;
import codingame.pac.cell.CellPrototype;
import codingame.pac.cell.Floor;
import codingame.pac.pathfinder.PathFinder;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class PathFinderTest {
    public static void main(String[] args) {
        System.out.println("hello");


        String gridInput = "#################################\n" +
                "# #     # # # #   # # # #     # #\n" +
                "# # ##### # # # # # # # ##### # #\n" +
                "# #       #     #     #       # #\n" +
                "# # # # # ### # # # ### # # # # #\n" +
                "#   #   #     #   #     #   #   #\n" +
                "##### ### # # ##### # # ### #####\n" +
                "#       #   # #   # #   #       #\n" +
                "# ##### # ### # # # ### # ##### #\n" +
                "#   #           #           #   #\n" +
                "### # # # # # # # # # # # # # ###\n" +
                "        #   #   #   #   #        \n" +
                "### # ### ##### # ##### ### # ###\n" +
                "    #                       #    \n" +
                "#################################";
        Grid.height = 15;
        Grid.width = 33;
        Cell[][] cells = new Cell[Grid.width][Grid.height];
        final Set<Floor> places = new HashSet<>();
        Scanner in = new Scanner(gridInput);
        for (int i = 0; i < Grid.height; i++) {
            int y = i;
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            //System.err.println(row);
            char[] cellsInput = row.toCharArray();
            for (int x = 0; x < cellsInput.length; x++) {
                Cell cell = CellPrototype.getCell(cellsInput[x], x, y);
                cells[x][y] = cell;
                if (cell instanceof Floor) {
                    places.add((Floor) cell);
                }
            }
        }

        in.close();

        Grid grid = new Grid(cells, places, Grid.width, Grid.height);
        grid.printGrid();

        PathFinder pathfinder = new PathFinder();
        PathFinder.PathFinderResult pfr = pathfinder.setGrid(grid)
                .from(cells[31][11].getCoordinates())
                .to(cells[1][11].getCoordinates())
                .findPath();

        for (Coord coord : pfr.path) {
            System.out.println(coord);
        }


        /*Map<String, PathFinder.PathFinderResult> allOptimalPaths = new HashMap<>();
        Object[] floors = places.toArray();

        for (int i = 0; i < floors.length; i++) {
            Coord from = ((Floor) floors[i]).getCoordinates();
            for (int j = 0; j < floors.length; j++) {
                Coord to = ((Floor) floors[j]).getCoordinates();
                PathFinder.PathFinderResult storePfr = pathfinder.setGrid(grid)
                        .from(from)
                        .to(to)
                        .findPath();

                allOptimalPaths.put(from+"-"+to, storePfr);
            }
        }

        for (String key : allOptimalPaths.keySet()) {
            System.out.println(key + " -> " + allOptimalPaths.get(key).path.size());
        }*/
    }
}
