package codingame.pac.pathfinder;

import codingame.pac.Grid;
import codingame.pac.cell.Cell;
import codingame.pac.cell.CellFactory;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class PathFinderTest {
    public static void main(String[] args) {
        System.out.println("hello");
        String gridInput = "###############################\n" +
                "###   #     #     #     #   ###\n" +
                "##### # # ### ### ### # # #####\n" +
                "#       #   #     #   #       #\n" +
                "# # ### ### # ### # ### ### # #\n" +
                "# # #                     # # #\n" +
                "### # ##### ### ### ##### # ###\n" +
                "# #     #   #     #   #     # #\n" +
                "# # ### # # # # # # # # ### # #\n" +
                "#     #       # #       #     #\n" +
                "### # # # ##### ##### # # # ###\n" +
                "###############################";

        Grid.height = 12;
        Grid.width = 31;
        Cell[][] cells = new Cell[Grid.width][Grid.height];
        final Set<Floor> places = new HashSet<>();
        Scanner in = new Scanner(gridInput);
        HashMap<Coord, Cell> cellsMap = new HashMap<>();
        for (int i = 0; i < Grid.height; i++) {
            int y = i;
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            char[] cellsInput = row.toCharArray();
            for (int x = 0; x < cellsInput.length; x++) {
                Coord coord = new Coord(x, y);
                Cell cell = CellFactory.createCell(cellsInput[x], coord);
                cells[x][y] = cell;
                cellsMap.put(coord, cell);
                if (cell instanceof Floor) {
                    places.add((Floor) cell);
                }
            }
        }

        in.close();

        Grid grid = new Grid(Grid.width, Grid.height, cellsMap, cells);
        grid.printGrid();

        PathFinder pathfinder = new PathFinder().setGrid(grid);
        Cell from1 = cells[19][5],  to1 = cells[26][1],  from2 = cells[21][5], to2 = cells[20][5];
        calculateCross(from1, to1, from2, to2, cells, pathfinder);
    }

    private static void calculateCross(Cell from1, Cell to1, Cell from2, Cell to2, Cell[][] cells, PathFinder pathfinder) {
        PathFinder.PathFinderResult pfr = pathfinder
                .from(from1.getCoord())
                .to(to1.getCoord())
                .findPath();

        List<Coord> path1 = pfr.path;
        for (Coord coord : path1) {
            System.out.println(coord);
        }

        System.out.println("----Distance----" + from1.getCoord().distanceTo(from2.getCoord()));

        List<Coord> path2 = pathfinder
                .from(from2.getCoord())
                .to(to2.getCoord())
                .findPath().path;
        path2.forEach(coord -> System.out.println(coord));


        System.out.println("Path are crossed type : " + isCrossedPaths(path1, path2, from1.getCoord(), from2.getCoord()));
    }

    private static CrossedPathsSolution isCrossedPaths(List<Coord> path1, List<Coord> path2, Coord pacMan1, Coord pacMan2) {
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
}
