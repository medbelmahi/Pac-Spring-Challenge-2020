package codingame.pac.bfs;

import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class Graph {
    Map<Coord, Node> nodes = new HashMap<>();

    public Node getNodeByCoord(Coord coord) {
        return nodes.get(coord);
    }

    public void addNode(Floor currentCell, Set<Floor> edges) {
        nodes.put(currentCell.getCoord(), new Node(currentCell, edges));
    }
}
