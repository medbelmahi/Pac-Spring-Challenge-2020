package codingame.pac.bfs;

import codingame.pac.cell.Cell;
import codingame.pac.cell.Floor;

import java.util.HashSet;
import java.util.Set;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class GraphBuilder {
    public static Graph CreateGraph(Set<Floor> places, Cell[][] cells) {
        Graph graph = new Graph();

        for (Floor currentCell : places) {

            Cell right = currentCell.rightCell(cells);
            Cell left = currentCell.leftCell(cells);
            Cell up = currentCell.upCell(cells);
            Cell down = currentCell.downCell(cells);

            Set<Floor> edges = new HashSet<>();
            addToEdges(right, edges);
            addToEdges(left, edges);
            addToEdges(up, edges);
            addToEdges(down, edges);

            graph.addNode(currentCell, edges);
        }

        return graph;
    }

    private static void addToEdges(Cell right, Set<Floor> edges) {
        if (right != null) {
            edges.add((Floor) right);
        }
    }
}
