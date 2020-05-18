package codingame.pac;

import codingame.pac.action.Action;
import codingame.pac.action.WaitAction;
import codingame.pac.cell.Cell;
import codingame.pac.cell.Coord;
import codingame.pac.cell.Direction;
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

    public boolean canSpeedUpOrSwitch() {
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
                /*if (isOnSpeedMode() && !pellet.isSuper() && newDistance < 2) {
                    continue;
                }*/
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

    public boolean isAlive() {
        return !PacManType.DEAD.equals(typeId);
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
        System.err.println(infoMe() + " set wait task");
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

    public Coord nextCoord(Coord coord) {
        if (isOnSpeedMode()) {
            if (coord.distanceTo(this.getCoord()) <= 1) {
                Coord nextCoord = goForwardByStep(coord);
                return nextCoord != null ? nextCoord : coord;
            }
        }
        return coord;
    }

    private Coord goForwardByStep(Coord coord) {
        Direction direction = getMoveDirection(coord);
        if (direction != null) {
            switch (direction) {
                case LEFT:
                    return getNextCoordIfIsAFloor(new Coord(coord.x - 1, coord.y));
                case RIGHT:
                    return getNextCoordIfIsAFloor(new Coord(coord.x + 1, coord.y));
                case UP:
                    return getNextCoordIfIsAFloor(new Coord(coord.x, coord.y - 1));
                case DOWN:
                    return getNextCoordIfIsAFloor(new Coord(coord.x, coord.y + 1));
            }
        }
        return null;
    }

    private Coord getNextCoordIfIsAFloor(Coord coord) {
        Cell cell = Grid.cellsMap.get(coord);
        return cell != null && !cell.isWall() ? cell.getCoord() : null;
    }

    private Direction getMoveDirection(Coord coord) {
        int x = this.getCoord().x;
        int y = this.getCoord().y;

        if (x == coord.x) {
            if (y < coord.y) {
                return Direction.DOWN;
            } else {
                return Direction.UP;
            }
        }else if (y == coord.y) {
            if (x < coord.x) {
                return Direction.RIGHT;
            } else {
                return Direction.LEFT;
            }
        }
        return null;
    }

    public String printTaskInfo() {
        return this.task.printInfo();
    }

    public boolean isVisibleToMe(PacMan otherPacMan) {
        return getCoord().isCrossedWith(otherPacMan.getCoord());
    }

    public String infoMe() {
        return pacId + "-" + typeId + "-" + getCoord();
    }

    public boolean hasSameType(PacManType pacManType) {
        return this.typeId.equals(pacManType);
    }

    public PacManType attackType(PacMan crossedPac) {
        switch (crossedPac.typeId) {
            case ROCK: return PacManType.PAPER;
            case PAPER: return PacManType.SCISSORS;
            case SCISSORS: return PacManType.ROCK;
        }
        return typeId;
    }

    public boolean noNeedToKeepWaiting(int counter) {
        if(isOnSpeedMode()) {
            return counter <= 0 ? true : false;
        }
        return true;
    }
}
