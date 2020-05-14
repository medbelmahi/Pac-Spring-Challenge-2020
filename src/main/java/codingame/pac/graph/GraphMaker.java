package codingame.pac.graph;

import codingame.pac.Grid;
import codingame.pac.cell.Cell;
import codingame.pac.cell.CellPrototype;
import codingame.pac.cell.Floor;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Mohamed BELMAHI created on 09/05/2020
 */
public class GraphMaker {

    public static GraphFindAllPaths<Floor> constructGraph(Set<Floor> places, Cell[][] cells) {
        GraphFindAllPaths<Floor> graphFindAllPaths = new GraphFindAllPaths<Floor>();

        for (Floor currentCell : places) {
            graphFindAllPaths.addNode(currentCell);

            Cell right = currentCell.rightCell(cells);
            Cell left = currentCell.leftCell(cells);
            Cell up = currentCell.upCell(cells);
            Cell down = currentCell.downCell(cells);

            addEdgeToCurrentCell(graphFindAllPaths, currentCell, right, Direction.RIGHT);
            addEdgeToCurrentCell(graphFindAllPaths, currentCell, left, Direction.LEFT);
            addEdgeToCurrentCell(graphFindAllPaths, currentCell, up, Direction.UP);
            addEdgeToCurrentCell(graphFindAllPaths, currentCell, down, Direction.DOWN);
        }

        return graphFindAllPaths;
    }

    private static void addEdgeToCurrentCell(final GraphFindAllPaths<Floor> graphFindAllPaths, Floor currentCell, Cell destination, Direction direction) {
        if (destination != null) {
            graphFindAllPaths.addNode((Floor) destination);
            graphFindAllPaths.addEdge(currentCell, (Floor) destination, direction);
        }
    }

    public static void main(String[] args) {
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

        GraphFindAllPaths<Floor> graph = constructGraph(places, cells);

        final FindOptimalPath<Floor> findOptimalPath = new FindOptimalPath<>(graph);

        //final List<Floor> optimalPath = findOptimalPath.getOptimalPath((Floor) cells[11][5], (Floor) cells[12][1]);
        final List<Floor> optimalPath = findOptimalPath.getOptimalPath((Floor) cells[25][8], (Floor) cells[31][5], null);

        if (optimalPath.isEmpty()) {
            System.out.println("No path");
        }
        int i = 0;
        for (final Floor floor : optimalPath) {
            i++;
            System.out.println(i + " - " + floor.getCoordinates().toString());
        }

    }
}
