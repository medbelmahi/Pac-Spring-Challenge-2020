package codingame.pac.cell;

import codingame.pac.Coord;

public class CellPrototype {
    public static Cell getCell(char type, int x, int y) {
        Coord coord = new Coord(x, y);
        switch (type) {
            case ' ' : return new Floor(coord, CellType.FLOOR);
            case '#': return new Wall(coord, CellType.WALL);

            default: throw new IllegalArgumentException("No Cell With Type of : " + type);
        }
    }
}
