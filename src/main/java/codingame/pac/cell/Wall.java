package codingame.pac.cell;

import codingame.pac.Coord;

public class Wall extends Cell {
    public Wall(Coord coord) {
        super(coord);
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
