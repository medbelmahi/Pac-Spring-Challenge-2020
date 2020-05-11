package codingame;

import codingame.pac.Coord;

public class Pellet {
    private Coord coord;
    private int amount;
    private boolean stillHere;

    public Pellet(Coord coord, int amount) {
        this.coord = coord;
        this.amount = amount;
        this.stillHere = true;
    }

    public Coord getCoord() {
        return coord;
    }

    public boolean isSuper() {
        return amount > 1;
    }

    public void setStillHere(boolean stillHere) {
        this.stillHere = stillHere;
    }

    public boolean isStillHere() {
        return stillHere;
    }

    public void disappear() {
        this.stillHere = false;
    }
}
