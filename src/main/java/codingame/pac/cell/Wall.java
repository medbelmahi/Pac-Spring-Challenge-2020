package codingame.pac.cell;

import codingame.pac.Coord;

public class Wall extends Cell {
    public Wall(Coord coord, CellType wall) {
        super(coord, wall);
    }

    @Override
    public String toString() {
        return "#";
    }

    @Override
    public boolean isWall() {
        return true;
    }
}
