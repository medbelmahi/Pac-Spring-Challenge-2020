package codingame.pac;

import codingame.Pellet;

import java.util.LinkedList;

public class Pacman {
    private Gamer owner;
    private int id;
    private int number;
    private Coord position;
    private PacmanType type;
    private boolean dead = false;

    public Pacman(int id, int number, Gamer owner, Coord position, PacmanType type) {
        this.owner = owner;
        this.id = id;
        this.number = number;
        this.position = position;
        this.setType(type);
        owner.getPacmen().add(this);
    }

    public void setType(PacmanType type) {
        this.type = type;
    }

    public void setDead() {
        this.dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public void turnReset() {

    }

    public void setPosition(Coord position) {
        this.position = position;
    }

    public String doAction(LinkedList<Pellet> pellets, LinkedList<Pellet> superPellets) {
        if (!superPellets.isEmpty()) {
             return "MOVE " + id + " " + superPellets.pop().getCoord().print();
        }
        return "MOVE " + id + " " + pellets.pop().getCoord().print();
    }
}
