package codingame.pac;

import codingame.pac.cell.Cell;
import codingame.pac.cell.Coord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class Grid {
    public static int width;
    public static int height;
    public static HashMap<Coord, Cell> cellsMap;
    private Cell[][] cells;

    public Grid(int width, int height, HashMap<Coord, Cell> cellsMap, Cell[][] cells) {
        this.width = width;
        this.height = height;
        this.cellsMap = cellsMap;
        this.cells = cells;
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

    public void printGrid() {
        for (int y = 0; y < height; y++) {
            System.err.print("|");
            for (int x = 0; x < width; x++) {
                System.err.print(cells[x][y]);
            }
            System.err.println("|");
        }
    }
}
