package codingame.pac;

import codingame.pac.cell.Cell;

import java.util.HashMap;
import java.util.Map;

public class Grid {
    int width, height;
    Map<Coord, Cell> cellsMap = new HashMap<>();
    Cell[][] cells;

    public Grid(Cell[][] cells, int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = cells;
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
