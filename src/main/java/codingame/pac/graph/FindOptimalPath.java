package codingame.pac.graph;


import codingame.pac.Coord;
import codingame.pac.cell.Floor;

import java.util.*;

/**
 * Created by Mohamed BELMAHI on 27/09/2016.
 */
public class FindOptimalPath<T extends Floor> {

    private final GraphFindAllPaths<T> graph;
    public static final int PLACE_TO_ESCAPE = 10;

    public FindOptimalPath(final GraphFindAllPaths<T> graph) {
        if (graph == null) {
            throw new NullPointerException("The input graph cannot be null.");
        }
        this.graph = graph;
    }

    private void validate(final T source, final T destination) {

        if (source == null) {
            throw new NullPointerException("The source: " + source + " cannot be  null.");
        }
        if (destination == null) {
            throw new NullPointerException("The destination: " + destination + " cannot be  null.");
        }
        if (source.equals(destination)) {
            //throw new IllegalArgumentException("The source and destination: " + source + " cannot be the same.");
            throw new IllegalArgumentException();
        }
    }

    public List<T> getOptimalPath(final T source, final T destination) {
        try {
            validate(source, destination);
        } catch (IllegalArgumentException e) {
            return new ArrayList<T>();
        }

        List<T> alreadyList = new ArrayList<>();

        final List<T> path = recursive(source, destination, alreadyList);
        return path;
    }

    private List<T> recursive(final T current, final T destination, List<T> alreadyList) {
        final List<T> path = new ArrayList<>();

        alreadyList.add(current);
        if (current == destination) {
            path.add(current);
            return path;
        }

        //System.err.println("current : " + current.coordinates.toString());
        final Map<T, Direction> edges  = graph.edgesFrom(current);

        final LinkedList<Direction> directions = current.getDirections(destination);


        for (final Direction direction : directions) {
            for (final Map.Entry<T, Direction> entry : edges.entrySet()) {
                T entryKey = entry.getKey();
                if (direction.equals(entry.getValue()) && !alreadyList.contains(entryKey)) {
                    if (entryKey != destination) {
                        path.add(entryKey);
                    }
                    final List<T> recursivePath = recursive(entryKey, destination, alreadyList);
                    if (!recursivePath.isEmpty() && recursivePath.get(recursivePath.size() - 1) == destination) {
                        path.addAll(recursivePath);
                        return path;
                    }
                }
            }
        }

        if (!path.isEmpty() && path.get(path.size() - 1) != destination) {
            return new ArrayList<>();
        }

        return path;
    }


    public Map<T, Integer> getPlacesWithDistance(T currentPlace){
        Map<T, Integer> places = new HashMap<T, Integer>();

        final Map<T, Direction> edges  = graph.edgesFrom(currentPlace);

        for (Map.Entry<T, Direction> entry : edges.entrySet()) {
            recursivePlacesWithDistance(places, currentPlace, entry.getKey());
        }

        return places;
    }

    private void recursivePlacesWithDistance(Map<T, Integer> places, T currentPlace, T destination) {
        if (places.size() > 10) {
            places.put(destination, getOptimalPath(currentPlace, destination).size());

            final Map<T, Direction> edges  = graph.edgesFrom(destination);

            for (Map.Entry<T, Direction> entry : edges.entrySet()) {
                recursivePlacesWithDistance(places, currentPlace, entry.getKey());
            }
        }
    }

    public static void main(final String[] args) {
        final GraphFindAllPaths<Floor> graph = new GraphFindAllPaths<>();

        final Floor here = new Floor(new Coord(0, 0));
        final Floor next1 = new Floor(new Coord(0, 1));
        final Floor next2 = new Floor(new Coord(0, 2));
        final Floor next3 = new Floor(new Coord(0, 3));
        final Floor next4 = new Floor(new Coord(0, 4));
        final Floor next41 = new Floor(new Coord(1, 4));
        final Floor next5 = new Floor(new Coord(0, 5));
        final Floor next6 = new Floor(new Coord(0, 6));
        final Floor next62 = new Floor(new Coord(2, 6));

        graph.addNode(here);

        graph.addNode(next1);
        graph.addNode(next2);
        graph.addNode(next3);
        graph.addNode(next4);
        graph.addNode(next5);
        graph.addNode(next6);
        graph.addNode(next62);
        graph.addNode(next41);

        graph.addEdge(here, next1, Direction.DOWN);
        graph.addEdge(next1, next2, Direction.DOWN);
        graph.addEdge(next2, next3, Direction.DOWN);
        graph.addEdge(next3, next4, Direction.DOWN);
        graph.addEdge(next4, next5, Direction.DOWN);
        graph.addEdge(next5, next6, Direction.DOWN);
        graph.addEdge(next4, next41, Direction.RIGHT);

        //
        final Floor next51 = new Floor(new Coord(1, 5));
        graph.addNode(next51);
        graph.addEdge(next5, next51, Direction.RIGHT);

        final FindOptimalPath<Floor> findOptimalPath = new FindOptimalPath<>(graph);

        final List<Floor> optimalPath = findOptimalPath.getOptimalPath(next4, next41);

        for (final Floor floor : optimalPath) {
            System.out.println(floor.getCoordinates().toString());
        }

    }



}
