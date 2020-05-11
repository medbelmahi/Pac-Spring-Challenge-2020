package codingame.pac;

import codingame.Pellet;
import codingame.pac.action.Action;
import codingame.pac.action.MoveAction;
import codingame.pac.cell.Cell;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
        owner.addPacman(this);
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

    public void doAction(LinkedList<Pellet> pellets, Set<Pellet> superPellets, Grid grid) {
        Pellet target = null;
        if (!pellets.isEmpty()) {
            if (binary) {
                target = pellets.peek();
                binary = false;
            } else {
                target = pellets.pop();
                binary = true;
            }
        }
        if (target != null) {
            this.currentAction = new MoveAction(target.getCoord(), false);
        } else {
            Coord destination = grid.randomFloor().getCoordinates();
            this.currentAction = new MoveAction(destination, false);
        }
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

    public boolean available() {
        return !hasAction() && !isDead();
    }

    public Set<Cell> myVisibleCells(Cell[][] cells) {
        Set<Cell> visibleCells = new HashSet<>();
        int baseX = position.x;
        int baseY = position.y;

        for (int x = baseX + 1; x < Grid.width; x++) {
            if (cells[x][baseY].isWall()) break;
             visibleCells.add(cells[x][baseY]);
        }
        for (int x = baseX - 1; x >= 0; x--) {
            if (cells[x][baseY].isWall()) break;
            visibleCells.add(cells[x][baseY]);
        }

        for (int y = baseY + 1; y < Grid.height; y++) {
            if (cells[baseX][y].isWall()) break;
            visibleCells.add(cells[baseX][y]);
        }
        for (int y = baseY - 1; y >= 0; y--) {
            if (cells[baseX][y].isWall()) break;
            visibleCells.add(cells[baseX][y]);
        }
        return visibleCells;
    }
}
