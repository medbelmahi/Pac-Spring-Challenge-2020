package codingame.pac;

import codingame.Pellet;

import java.util.LinkedList;

public class Game {
    Grid grid;

    private Gamer me;
    private Gamer opponent;
    private LinkedList<Pellet> pellets;
    private LinkedList<Pellet> superPellets;

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

    public void play() {
        me.play(pellets, superPellets);
        /*Pellet pellet = pellets.pop();
        return "MOVE 0 " + pellet.getCoord().print();*/
    }

    public void setPellets(LinkedList<Pellet> pellets) {
        this.pellets = pellets;
    }

    public void setSuperPellets(LinkedList<Pellet> superPellets) {
        this.superPellets = superPellets;
    }
}
