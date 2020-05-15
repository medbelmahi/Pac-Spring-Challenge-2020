package codingame.pac;

import codingame.pac.action.Action;
import codingame.pac.action.WaitAction;
import codingame.pac.cell.Cell;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Floor;
import codingame.pac.task.Task;
import codingame.pac.task.WaitTask;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class PacMan {
    private int tour = 1;
    private final int pacId;
    private Floor position;
    private PacManType typeId;
    private int speedTurnsLeft;
    private int abilityCountdown;
    private Task task;

    public PacMan(int pacId, Cell cell, String typeId, int speedTurnsLeft, int abilityCountdown) {
        this.pacId = pacId;
        this.position = (Floor) cell;
        this.typeId = PacManType.valueOf(typeId);
        this.speedTurnsLeft = speedTurnsLeft;
        this.abilityCountdown = abilityCountdown;
    }

    public void update(String typeId, Cell cell, int speedTurnsLeft, int abilityCountdown) {
        this.typeId = PacManType.valueOf(typeId);
        this.position = (Floor) cell;
        this.speedTurnsLeft = speedTurnsLeft;
        this.abilityCountdown = abilityCountdown;
        this.tour++;
    }

    public boolean canSpeedUp() {
        return abilityCountdown <= 0;
    }

    public String doCommand(Action action) {
        return action.print(pacId);
    }

    public Pellet getNearestPellets(Set<Pellet> pellets) {
        double nearestDistance = 99999D;
        Pellet nearestPellet = null;

        for (Pellet pellet : pellets) {
            double newDistance = pellet.distanceTo(position);
            if (newDistance < nearestDistance) {
                if (isOnSpeedMode() && !pellet.isSuper() && newDistance < 2) {
                    continue;
                }
                nearestDistance = newDistance;
                nearestPellet = pellet;
            }
        }
        System.err.println("pac-" + pacId + " nearTo: " + nearestPellet + " Distance: " + nearestDistance);
        return nearestPellet;
    }

    public Set<Cell> myVisibleCells(Cell[][] cells) {
        Set<Cell> visibleCells = new HashSet<>();
        int baseX = position.getX();
        int baseY = position.getY();

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

    private boolean isOnSpeedMode() {
        return speedTurnsLeft > 0;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean hasTask() {
        return this.task != null && !this.task.isFinished();
    }

    public boolean isAlive(int currentTour) {
        return this.tour >= currentTour;
    }

    public Action getCurrentAction() {
        return task.keepTargeting();
    }

    public boolean hasMoveTask() {
        return task != null && !task.isFinished() && task.isMoveTask();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacMan pacMan = (PacMan) o;
        return pacId == pacMan.pacId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pacId);
    }

    public void switchTasksWith(PacMan pacMan) {
        this.task.switchTo(pacMan.task);
        Task temp = this.task;
        this.task = pacMan.task;
        pacMan.task = temp;
    }

    public double distanceTo(PacMan pacMan) {
        return this.position.distanceTo(pacMan.position);
    }

    public Coord getCoord() {
        return position.getCoord();
    }

    public Coord getTarget() {
        return task.moveTarget();
    }

    public void setWaitTask() {
        this.task = new WaitTask(new WaitAction(this));
    }

    public Floor getDeepestFloor(Set<Floor> floors) {
        double deepestDistance = 0;
        Floor deepestFloor = null;

        for (Floor floor : floors) {
            double newDistance = floor.distanceTo(position);
            if (newDistance > deepestDistance) {
                deepestDistance = newDistance;
                deepestFloor = floor;
            }
        }
        System.err.println("pac-" + pacId + " nearTo: " + deepestFloor + " Distance: " + deepestDistance);
        return deepestFloor;
    }
}
