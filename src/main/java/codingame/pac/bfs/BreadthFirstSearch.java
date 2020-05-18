package codingame.pac.bfs;

import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class BreadthFirstSearch {
    private Graph graph;

    public BreadthFirstSearch(Graph graph){
        this.graph = graph;
    }

    public boolean compute(Coord from, Coord to){

        Node startNode = graph.getNodeByCoord(from);
        Node goalNode = graph.getNodeByCoord(to);

        if(startNode.equals(goalNode)){
            System.out.println("Goal Node Found!");
            System.out.println(startNode);
        }

        Queue<Floor> queue = new LinkedList<>();
        ArrayList<Floor> explored = new ArrayList<>();
        queue.add(startNode.getNode());
        explored.add(startNode.getNode());

        while(!queue.isEmpty()){
            Floor current = queue.remove();
            if(current.equals(goalNode.getNode())) {
                System.out.println(explored);
                return true;
            }
            else{
                Node currentNode = graph.getNodeByCoord(current.getCoord());
                if(currentNode.getChildren().isEmpty())
                    return false;
                else
                    queue.addAll(current.getSortedEdgesBasedOnDistanceFromTarget(goalNode.getNode(), currentNode.getChildren()));
            }
            explored.add(current);
        }

        return false;

    }

    public List<Floor> getOptimalPath(final Floor source, final Floor destination) {

        List<Floor> alreadyList = new ArrayList<>();

        final List<Floor> path = recursive(source, destination, alreadyList);
        return path;
    }

    private List<Floor> recursive(Floor current, Floor destination, List<Floor> alreadyList) {
        final List<Floor> path = new ArrayList<>();

        alreadyList.add(current);
        if (current == destination) {
            path.add(current);
            return path;
        }

        //System.err.println("current : " + current.getCoordinates().toString());
        Node currentNode = graph.getNodeByCoord(current.getCoord());

        Set<Floor> sortedEdges = current.getSortedEdgesBasedOnDistanceFromTarget(destination, currentNode.getChildren());

        for (final Floor edge : sortedEdges) {
            if (!alreadyList.contains(edge)) {
                if (edge != destination) {
                    path.add(edge);
                }
                final List<Floor> recursivePath = recursive(edge, destination, alreadyList);
                if (!recursivePath.isEmpty() && recursivePath.get(recursivePath.size() - 1) == destination) {
                    path.addAll(recursivePath);
                    return path;
                } else {
                    path.remove(path.size() - 1);
                }
            }
        }

        if (!path.isEmpty() && path.get(path.size() - 1) != destination) {
            return new ArrayList<>();
        }

        return path;
    }
}
