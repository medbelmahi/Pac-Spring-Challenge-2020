package codingame.pac;

import codingame.pac.cell.Cell;
import codingame.pac.cell.Floor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Grid {
    public static int width, height;
    Map<Coord, Cell> cellsMap = new HashMap<>();
    Cell[][] cells;
    Set<Floor> places;

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
