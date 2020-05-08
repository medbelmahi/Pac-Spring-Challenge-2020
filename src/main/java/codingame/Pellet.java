package codingame;

import codingame.pac.Coord;

public class Pellet {
    private Coord coord;
    private int amount;

    public Pellet(Coord coord, int amount) {
        this.coord = coord;
        this.amount = amount;
    }

    public Coord getCoord() {
        return coord;
    }
}
