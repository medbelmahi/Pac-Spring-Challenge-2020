package bfs;

import codingame.pac.Grid;
import codingame.pac.bfs.BreadthFirstSearch;
import codingame.pac.bfs.GraphBuilder;
import codingame.pac.cell.Cell;
import codingame.pac.cell.CellFactory;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;
import codingame.pac.engine.Game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
/**
 * Mohamed BELMAHI created on 15/05/2020
 */

public class BreadthFirstSearchTest {
    public static void main(String[] args) {
        String gridInput = "#################################\n" +
                "### # #     #       #     # # ###\n" +
                "### # ##### # ##### # ##### # ###\n" +
                "            #       #            \n" +
                "##### ### ### # # # ### ### #####\n" +
                "      #       #   #       #      \n" +
                "### # # ### # ##### # ### # # ###\n" +
                "    # #   # #       # #   # #    \n" +
                "##### # # # ### # ### # # # #####\n" +
                "          #     #     #          \n" +
                "### ### # # # # # # # # # ### ###\n" +
                "#     # #   # #   # #   # #     #\n" +
                "# ### # ### # # # # # ### # ### #\n" +
                "# ### #     # # # # #     # ### #\n" +
                "#################################";

        Grid.height = 15;
        Grid.width = 33;
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

        BreadthFirstSearch bfs = new BreadthFirstSearch(GraphBuilder.CreateGraph(places, cells));

        List<Floor> optimalPath = bfs.getOptimalPath((Floor) cells[30][9], (Floor) cells[23][5]);

        long startTime = System.nanoTime();
        for (Floor source : places) {
            for (Floor target : places) {
                bfs.getOptimalPath(source, target);
            }
        }
        for (Floor floor : optimalPath) {
            //System.out.println(floor.getCoord());
        }
        Game.printEndTime(startTime, "Path");
    }
}
