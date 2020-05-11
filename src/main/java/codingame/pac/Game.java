package codingame.pac;

import codingame.Pellet;

import java.util.LinkedList;
import java.util.Set;

public class Game {
    Grid grid;
    private int availableSuperPellets = 4;
    private Gamer me;
    private Gamer opponent;
    private LinkedList<Pellet> pellets;
    private Set<Pellet> superPellets;

    public Game(Grid grid){
        this.grid = grid;
    }


    public Grid getGrid() {
        return grid;
    }

    public void setMe(Gamer me) {
        this.me = me;
    }

    public void setOpponent(Gamer opponent) {
        this.opponent = opponent;
    }

    public String play() {
        return me.play(pellets, superPellets, grid);
    }

    public void setPellets(LinkedList<Pellet> pellets) {
        this.pellets = pellets;
    }

    public void setSuperPellets(Set<Pellet> superPellets) {
        this.superPellets = superPellets;
    }

    public boolean isSuperPelletsAvailable() {
        return availableSuperPellets >= 1;
    }

    public void decreaseSuperPellets() {
        this.availableSuperPellets--;
    }
}
