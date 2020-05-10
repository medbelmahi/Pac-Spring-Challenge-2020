package codingame.pac;

import codingame.Pellet;
import codingame.pac.action.Action;
import codingame.pac.action.MoveAction;

import java.util.LinkedList;

public class Pacman {
    private Gamer owner;
    private int id;
    private int number;
    private Coord position;
    private PacmanType type;
    private boolean dead = false;
    private boolean binary;
    private int speedTurnsLeft;
    private int abilityCooldown;
    private int tour;
    private Action currentAction;

    public Pacman(int id, int number, Gamer owner, Coord position, PacmanType type, int speedTurnsLeft, int abilityCooldown, int tour) {
        this.owner = owner;
        this.id = id;
        this.number = number;
        this.position = position;
        this.speedTurnsLeft = speedTurnsLeft;
        this.abilityCooldown = abilityCooldown;
        this.setType(type);
        owner.getPacmen().add(this);
        this.tour = tour;
    }

    public void setType(PacmanType type) {
        this.type = type;
    }

    public void setDead() {
        this.dead = true;
    }
    public void setDead(int currentTour) {
        this.dead = isDead(currentTour);
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isDead(int currentTour) {
        return tour < currentTour;
    }

    public void turnReset() {

    }

    public void setPosition(Coord position) {
        this.position = position;
    }

    public void doAction(LinkedList<Pellet> pellets, LinkedList<Pellet> superPellets) {
        Pellet target;
        if (!superPellets.isEmpty()) {
            target = superPellets.pop();
        } else {
            if (binary) {
                target = pellets.peek();
                binary = false;
            } else {
                target = pellets.pop();
                binary = true;
            }
        }
        this.currentAction = new MoveAction(target.getCoord(), false);
    }

    public int getId() {
        return id;
    }

    public void setSpeedTurnsLeft(int speedTurnsLeft) {
        this.speedTurnsLeft = speedTurnsLeft;
    }

    public void setAbilityCooldown(int abilityCooldown) {
        this.abilityCooldown = abilityCooldown;
    }

    public void update() {
        this.tour ++;
        this.currentAction = null;
    }

    public boolean hasAction() {
        return currentAction != null;
    }

    public double distance(Coord coord) {
        return this.position.euclideanTo(coord);
    }

    public void setAction(Action action ) {
        this.currentAction = action;
    }

    public String printAction() {
        return this.currentAction.print(this.id);
    }
}
