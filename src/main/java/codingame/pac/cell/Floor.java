package codingame.pac.cell;

import codingame.pac.Pellet;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class Floor extends Cell {

    private Pellet pallet;

    Floor(Coord coord) {
        super(coord);
    }

    public void setPallet(Pellet pallet) {
        this.pallet = pallet;
    }

    public double distanceTo(Floor floor) {
        return coord.distanceTo(floor.coord);
    }

    public void noPellet() {
        if (pallet != null) {
            System.err.println("Set Pellet to not more existing: " + pallet);
            pallet.setStillHere(false);
        }
    }
}
