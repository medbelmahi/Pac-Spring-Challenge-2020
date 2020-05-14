package codingame.pac;

import codingame.pac.cell.Cell;
import codingame.pac.cell.Floor;
import codingame.pac.graph.FindOptimalPath;
import codingame.pac.graph.GraphFindAllPaths;
import codingame.pac.graph.GraphMaker;
import codingame.pac.pathfinder.PathFinder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.swing.UIManager.get;

public class Grid {
    public static int width, height;
    Map<Coord, Cell> cellsMap = new HashMap<>();
    public static Cell[][] cells;
    Set<Floor> places;
    public static GraphFindAllPaths<Floor> graph;
    public static FindOptimalPath<Floor> findOptimalPath;
    public static PathFinder pathFinder;

    public Grid(Cell[][] cells, Set<Floor> places, int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = cells;
        this.places = places;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cellsMap.put(new Coord(x, y), cells[x][y]);
            }
        }

        this.graph = GraphMaker.constructGraph(places, cells);
        this.findOptimalPath = new FindOptimalPath<>(graph);
        pathFinder = new PathFinder();
        pathFinder.setGrid(this);
    }

    public void printGrid() {
        for (int y = 0; y < height; y++) {
            System.err.print("|");
            for (int x = 0; x < width; x++) {
                System.err.print(cells[x][y]);
            }
            System.err.println("|");
        }
    }

    public Floor randomFloor() {
        int index = (int) (Math.random() * (this.places.size()));
        Iterator<Floor> iterator = this.places.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    public List<Coord> getNeighbours(Coord pos) {
        return Arrays
                .stream(Config.ADJACENCY)
                .map(delta -> getCoordNeighbour(pos, delta))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Optional<Coord> getCoordNeighbour(Coord pos, Coord delta) {
        Coord n = pos.add(delta);
        if (Config.MAP_WRAPS) {
            n = new Coord((n.x + width) % width, n.y);
        }

        if (get(n) != Cell.NO_CELL) {
            return Optional.of(n);
        }
        return Optional.empty();
    }

    public Cell get(Coord coord) {
        return get(coord.x, coord.y);
    }

    public Cell get(int x, int y) {
        return cellsMap.getOrDefault(new Coord(x, y), Cell.NO_CELL);
    }

    public int calculateDistance(Coord a, Coord b) {
        int dv = Math.abs(a.y - b.y);
        int dh = Math.min(
                Math.abs(a.x - b.x),
                Math.min(a.x + width - b.x, b.x + width - a.x)
        );
        return dv + dh;
    }
}
