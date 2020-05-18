
import java.util.*;
import java.io.*;
import java.math.*;
import java.util.stream.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;





/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class Pellet {
    private final Coord coord;
    private final Floor floor;
    private final int value;
    private boolean stillHere;
    private boolean targeted;

    public Pellet(Coord coord, Floor floor, int value) {
        this.coord = coord;
        this.floor = floor;
        this.value = value;
        this.stillHere = true;
        floor.setPallet(this);
    }

    public boolean isSuper() {
        return this.value > 1;
    }

    public MoveAction targeted(PacMan pacMan) {
        this.targeted = true;

        MoveAction moveAction = new MoveAction(pacMan, coord);
        pacMan.setTask(new EatTask(moveAction, this));
        return moveAction;
    }

    public double distanceTo(Floor floor) {
        return this.floor.distanceTo(floor);
    }

    public void setStillHere(boolean stillHere) {
        this.stillHere = stillHere;
    }

    public boolean isStillHere() {
        return stillHere;
    }

    public boolean isTargeted() {
        return targeted;
    }

    public void notTargeted() {
        this.targeted = false;
    }

    @Override
    public String toString() {
        return coord.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pellet pellet = (Pellet) o;
        return coord.equals(pellet.coord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coord);
    }

    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }
}




/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class PacMan {
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






/*
 * Find the best path from a Coord to another Coord
 * currently : using Astar algorithm
 */
class PathFinder {
  public static class PathFinderResult {
    public static final PathFinderResult NO_PATH = new PathFinderResult();
    public List<Coord> path = new ArrayList<>();
    public int weightedLength = -1;
    public boolean isNearest = false;

    public boolean hasNextCoord() {
      return path.size() > 1;
    }

    public Coord getNextCoord() {
      return path.get(1);
    }

    public boolean hasNoPath() {
      return weightedLength == -1;
    }
  }

  Grid grid = null;
  Coord from = null;
  Coord to = null;
  private Function<Coord, Integer> weightFunction = (Coord coord) -> (1);

  public PathFinder setGrid(Grid grid) {
      this.grid = grid;
      return this;
  }

  public PathFinder from(Coord Coord) {
    from = Coord;
    return this;
  }

  public PathFinder to(Coord Coord) {
    to = Coord;
    return this;
  }

  public PathFinder withWeightFunction(Function<Coord, Integer> weightFunction) {
    this.weightFunction = weightFunction;
    return this;
  }

  public PathFinderResult findPath() {
    if (from == null || to == null) {
      return new PathFinderResult();
    }

    AStar a = new AStar(grid, from, to, weightFunction);
    List<PathItem> pathItems = a.find();
    PathFinderResult pfr = new PathFinderResult();

    if (pathItems.isEmpty()) {
        pfr.isNearest = true;
        pathItems = new AStar(grid, from, a.getNearest(), weightFunction).find();
    }

    pfr.path = pathItems.stream()
        .map(item -> item.coord)
        .collect(Collectors.toList());
    pfr.weightedLength = pathItems.get(pathItems.size() - 1).cumulativeLength;
    return pfr;
  }
}


enum CrossedPathsSolution {
    SWITCH, WAIT, NO_NEED, CHANGE;
}




/**
 * PATH : A*
 *
 */
class AStar {
    Map<Coord, PathItem> closedList = new HashMap<>();
    PriorityQueue<PathItem> openList = new PriorityQueue<PathItem>(Comparator.comparingInt(PathItem::getTotalPrevisionalLength));
    List<PathItem> path = new ArrayList<PathItem>();

    Grid grid;
    Coord from;
    Coord target;
    Coord nearest;

    int dirOffset;
    private Function<Coord, Integer> weightFunction;

    public AStar(Grid grid, Coord from, Coord target, Function<Coord, Integer> weightFunction) {
        this.grid = grid;
        this.from = from;
        this.target = target;
        this.weightFunction = weightFunction;
        this.nearest = from;
    }

    public List<PathItem> find() {
        PathItem item = getPathItemLinkedList();
        path.clear();
        if (item != null) {
            calculatePath(item);
        }
        return path;
    }

    void calculatePath(PathItem item) {
        PathItem i = item;
        while (i != null) {
            path.add(0, i);
            i = i.precedent;
        }
    }

    PathItem getPathItemLinkedList() {
        PathItem root = new PathItem();
        root.coord = this.from;
        openList.add(root);

        while (openList.size() > 0) {
            PathItem visiting = openList.remove(); // imagine it's the best
            Coord visitingCoord = visiting.coord;

            if (visitingCoord.equals(target)) {
                return visiting;
            }
            if (closedList.containsKey(visitingCoord)) {
                continue;
            }
            closedList.put(visitingCoord, visiting);

            List<Coord> neighbors = grid.getNeighbours(visitingCoord);
            for (Coord neighbor : neighbors) {
                if (!grid.get(neighbor).isWall()) {
                    addToOpenList(visiting, visitingCoord, neighbor);
                }
            }

            if (grid.calculateDistance(visitingCoord, target) < grid.calculateDistance(nearest, target)) {
                this.nearest = visitingCoord;
            }
        }
        return null; // not found !
    }

    void addToOpenList(PathItem visiting, Coord fromCoord, Coord toCoord) {
        if (closedList.containsKey(toCoord)) {
            return;
        }
        PathItem pi = new PathItem();
        pi.coord = toCoord;
        pi.cumulativeLength = visiting.cumulativeLength + weightFunction.apply(toCoord);
        int manh = grid.calculateDistance(fromCoord, toCoord);
        pi.totalPrevisionalLength = pi.cumulativeLength + manh;
        pi.precedent = visiting;
        openList.add(pi);
    }

    public Coord getNearest() {
        return nearest;
    }

}
/** End of PATH */




class PathItem {
    public int cumulativeLength = 0;
    int totalPrevisionalLength = 0;
    PathItem precedent = null;
    Coord coord;

    public int getTotalPrevisionalLength() {
        return totalPrevisionalLength;
    }
}


enum PacManType {
    ROCK, PAPER, SCISSORS, DEAD
}




/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class Grid {
    public static int width;
    public static int height;
    public static HashMap<Coord, Cell> cellsMap;
    private Cell[][] cells;

    public Grid(int width, int height, HashMap<Coord, Cell> cellsMap, Cell[][] cells) {
        this.width = width;
        this.height = height;
        this.cellsMap = cellsMap;
        this.cells = cells;
    }

    public List<Coord> getNeighbours(Coord pos) {
        return Arrays
                .stream(Config.ADJACENCY)
                .map(delta -> getCoordNeighbour(pos, delta))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Optional<Coord> getCoordNeighbour(Coord pos, Coord delta) {
        Coord n = pos.add(delta);
        if (Config.MAP_WRAPS) {
            n = new Coord((n.x + width) % width, n.y);
        }

        if (get(n) != Cell.NO_CELL) {
            return Optional.of(n);
        }
        return Optional.empty();
    }

    public Cell get(Coord coord) {
        return get(coord.x, coord.y);
    }

    public Cell get(int x, int y) {
        return cellsMap.getOrDefault(new Coord(x, y), Cell.NO_CELL);
    }

    public int calculateDistance(Coord a, Coord b) {
        int dv = Math.abs(a.y - b.y);
        int dh = Math.min(
                Math.abs(a.x - b.x),
                Math.min(a.x + width - b.x, b.x + width - a.x)
        );
        return dv + dh;
    }

    public void printGrid() {
        for (int y = 0; y < height; y++) {
            System.err.print("|");
            for (int x = 0; x < width; x++) {
                System.err.print(cells[x][y]);
            }
            System.err.println("|");
        }
    }
}




/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class ActionBuilder {
    public static MoveAction buildMoveAction(Set<Pellet> pellets, PacMan pacMan) {
        if (pellets.isEmpty()) {
            return null;
        }

        Pellet pellet = pacMan.getNearestPellets(pellets);

        return pellet.targeted(pacMan);
    }

    public static MoveAction buildFindPelletAction(Set<Floor> floors, PacMan pacMan) {
        if (floors.isEmpty()) {
            return null;
        }

        Floor floor = pacMan.getDeepestFloor(floors);

        return floor.targeted(pacMan);
    }

    public static Action buildAttackAction(PacMan pacMan, PacMan crossedPac) {
        if (!crossedPac.canSpeedUpOrSwitch()) {
            PacManType pacManType = pacMan.attackType(crossedPac);

            if (pacMan.hasSameType(pacManType)) {
                return null;
            } else {
                 new SwitchAction(pacMan);
            }
        } else {
            return null;
        }
        return null;
    }
}



/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class CellFactory {
    public static Cell createCell(char inputType, Coord coord) {
        switch (inputType) {
            case ' ': return new Floor(coord);
            case '#': return new Wall(coord);
            default: throw new IllegalArgumentException("No Cell With this type ");
        }
    }
}


enum CellType {
    WALL, FLOOR;
}



/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class Coord {

    public final int x;
    public final int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }

    public double distanceTo(Coord coord) {
        return euclideanTo(coord);
    }

    public double euclideanTo(int x, int y) {
        return Math.sqrt(sqrEuclideanTo(x, y));
    }

    private double sqrEuclideanTo(int x, int y) {
        return Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2);
    }

    public double euclideanTo(Coord d) {
        return euclideanTo(d.x, d.y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coord add(Coord d) {
        return new Coord(x + d.x, y + d.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return x == coord.x &&
                y == coord.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public boolean isCrossedWith(Coord coord) {
        return coord.x == this.x || coord.y == this.y;
    }
}


/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class Wall extends Cell {
    public Wall(Coord coord) {
        super(coord);
    }

    @Override
    public boolean isWall() {
        return true;
    }

    @Override
    public String toString() {
        return "#";
    }
}


/**
 * Mohamed BELMAHI created on 15/05/2020
 */
enum  FloorStatus {
    HIDDEN, HAS_SUPER_PELLET, HAS_SIMPLE_PELLET, EMPTY
}




/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class Floor extends Cell {

    private Pellet pallet;
    private FloorStatus floorStatus;
    private boolean targeted;
    private PacMan targetedForDiscovery;

    Floor(Coord coord) {
        super(coord);
        this.floorStatus = FloorStatus.HIDDEN;
    }

    public void setPallet(Pellet pallet) {
        this.pallet = pallet;
        this.floorStatus = pallet.isSuper() ? FloorStatus.HAS_SUPER_PELLET : FloorStatus.HAS_SIMPLE_PELLET;
    }

    public double distanceTo(Floor floor) {
        return coord.distanceTo(floor.coord);
    }

    public void noPellet() {
        if (pallet != null) {
            pallet.setStillHere(false);
        }
        this.floorStatus = FloorStatus.EMPTY;
    }

    @Override
    public String toString() {
        switch (this.floorStatus) {
            case EMPTY: return " ";
            case HAS_SIMPLE_PELLET: return "o";
            case HAS_SUPER_PELLET: return "O";
            case HIDDEN: return "?";
        }
        return " ";
    }

    public boolean isHidden() {
        return FloorStatus.HIDDEN.equals(floorStatus);
    }

    public MoveAction targeted(PacMan pacMan) {
        this.targeted = true;

        MoveAction moveAction = new MoveAction(pacMan, coord);
        pacMan.setTask(new FindPelletTask(moveAction, this));
        this.targetedForDiscovery = pacMan;
        markStreetAsTargeted(pacMan);
        return moveAction;
    }

    private void markStreetAsTargeted(PacMan pacMan) {
        int x = this.coord.x;
        int y = this.coord.y;

        for (int i = x - 1; i >= 0; i--) {
            Cell cell = Grid.cellsMap.get(new Coord(i, y));
            if (cell == null || cell.isWall()) {
                break;
            }
            ((Floor) cell).setTargetedForDiscovery(pacMan);
        }

        for (int i = x + 1; i < Grid.width; i++) {
            Cell cell = Grid.cellsMap.get(new Coord(i, y));
            if (cell == null || cell.isWall()) {
                break;
            }
            ((Floor) cell).setTargetedForDiscovery(pacMan);
        }

        for (int i = y - 1; i >= 0; i--) {
            Cell cell = Grid.cellsMap.get(new Coord(x, i));
            if (cell == null || cell.isWall()) {
                break;
            }
            ((Floor) cell).setTargetedForDiscovery(pacMan);
        }

        for (int i = y + 1; i < Grid.height; i++) {
            Cell cell = Grid.cellsMap.get(new Coord(x, i));
            if (cell == null || cell.isWall()) {
                break;
            }
            ((Floor) cell).setTargetedForDiscovery(pacMan);
        }
    }

    public boolean isTargeted() {
        return targeted;
    }

    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }

    public Set<Floor> getSortedEdgesBasedOnDistanceFromTarget(Floor target, Set<Floor> edges) {
        Set<Floor> sortedEdgesBasedOnDistanceFromTarget = new TreeSet<Floor>((floor1, floor2) -> {

            double floor1DistanceToTarget = floor1.distanceTo(target);
            double floor2DistanceToTarget = floor2.distanceTo(target);
            return floor1DistanceToTarget < floor2DistanceToTarget ? -1 : 1;
        });

        sortedEdgesBasedOnDistanceFromTarget.addAll(edges);

        return sortedEdgesBasedOnDistanceFromTarget;
    }

    public boolean isEmpty() {
        return FloorStatus.EMPTY.equals(floorStatus);
    }

    public boolean isNotTargetedForDiscovery() {
        return targetedForDiscovery == null || !targetedForDiscovery.isAlive();
    }

    public void setTargetedForDiscovery(PacMan targetedForDiscovery) {
        this.targetedForDiscovery = targetedForDiscovery;
    }
}




/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class Cell {
    public static final Cell NO_CELL = new Floor(new Coord(1000, 1000)) {
        public boolean isValid() {
            return false;
        }

        public void copy(Cell other) {
            throw new RuntimeException("Invalid cell");
        }

        public void setType(CellType type) {
            throw new RuntimeException("Invalid cell");
        }
    };
    protected Coord coord;

    public Cell(Coord coord) {
        this.coord = coord;
    }

    public int getX() {
        return coord.getX();
    }

    public int getY() {
        return coord.getY();
    }

    public boolean isWall() {
        return false;
    }

    public Coord getCoord() {
        return coord;
    }

    public Cell rightCell(Cell[][] cells) {
        if (coord.getX() + 1 < Grid.width) {
            Cell cell = cells[coord.getX() + 1][coord.getY()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell leftCell(Cell[][] cells) {
        if (coord.getX() - 1 >= 0) {
            Cell cell = cells[coord.getX() - 1][coord.getY()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell upCell(Cell[][] cells) {
        if (coord.getY() - 1 >= 0) {
            Cell cell = cells[coord.getX()][coord.getY() - 1];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell downCell(Cell[][] cells) {
        if (coord.getY() + 1 < Grid.height) {
            Cell cell = cells[coord.getX()][coord.getY() + 1];
            if (!cell.isWall()) return cell;
        }
        return null;
    }
}


enum Direction {
    LEFT, RIGHT, UP, DOWN
}




/**
 * Mohamed BELMAHI created on 16/05/2020
 */
class SwitchAction extends Action {

    public SwitchAction(PacMan pacMan) {
        super(pacMan);
    }

    @Override
    public ActionType type() {
        return ActionType.SWITCH;
    }

    @Override
    protected String msg() {
        return "SW-"+type().toString();
    }
}



/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class WaitAction extends Action {
    int counter = 2;
    public WaitAction(PacMan pacMan) {
        super(pacMan);
    }

    @Override
    public ActionType type() {
        return ActionType.WAIT;
    }

    @Override
    public String print(int pacId) {
        counter--;
        return super.print(pacId);
    }

    @Override
    protected String msg() {
        return "W";
    }

    public boolean isFinished() {
        return pacMan.noNeedToKeepWaiting(counter);
    }
}




/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class MoveAction extends Action {

    private Coord coord;
    public MoveAction(PacMan pacMan, Coord coord) {
        super(pacMan);
        this.coord = coord;
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    @Override
    public String print(int pacId) {
        coord = pacMan.nextCoord(coord);
        return String.join(" ", Arrays.asList(type().toString(), String.valueOf(pacId), coord.toString(), msg()));
    }

    @Override
    protected String msg() {
        return "M-" + coord.getX() + ":" + coord.getY();
    }

    public Coord targetCoord() {
        return coord;
    }

    public boolean isReached() {
        return this.coord.distanceTo(pacMan.getCoord()) == 0;
    }
}


/**
 * Mohamed BELMAHI created on 14/05/2020
 */
enum ActionType {
    WAIT, MOVE, MSG, SPEED, SWITCH
}




/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class SpeedAction extends Action {
    public SpeedAction(PacMan pacMan) {
        super(pacMan);
    }

    @Override
    public ActionType type() {
        return ActionType.SPEED;
    }

    @Override
    protected String msg() {
        return "S";
    }
}




/**
 * Mohamed BELMAHI created on 14/05/2020
 */
abstract class Action {
    PacMan pacMan;

    public Action(PacMan pacMan) {
        this.pacMan = pacMan;
    }

    public abstract ActionType type();

    public String print(int pacId) {
        return String.join(" ", Arrays.asList(type().toString(), String.valueOf(pacId), msg()));
    }
    protected abstract String msg();

    public void changeItPacWith(Action action) {
        PacMan temp = this.pacMan;
        this.pacMan = action.pacMan;
        action.pacMan = temp;
    }

    public String printCommand() {
        return pacMan.doCommand(this);
    }
}





/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class Node<T extends Floor> {
    private T node;
    private Set<T> children;

    Node(T node, Set<T> children) {
       this.node = node;
        this.children = children;
    }

    public Set<T> getChildren() {
        return children;
    }

    public T getNode() {
        return node;
    }
}




/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class Graph {
    Map<Coord, Node> nodes = new HashMap<>();

    public Node getNodeByCoord(Coord coord) {
        return nodes.get(coord);
    }

    public void addNode(Floor currentCell, Set<Floor> edges) {
        nodes.put(currentCell.getCoord(), new Node(currentCell, edges));
    }
}




/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class GraphBuilder {
    public static Graph CreateGraph(Set<Floor> places, Cell[][] cells) {
        Graph graph = new Graph();

        for (Floor currentCell : places) {

            Cell right = currentCell.rightCell(cells);
            Cell left = currentCell.leftCell(cells);
            Cell up = currentCell.upCell(cells);
            Cell down = currentCell.downCell(cells);

            Set<Floor> edges = new HashSet<>();
            addToEdges(right, edges);
            addToEdges(left, edges);
            addToEdges(up, edges);
            addToEdges(down, edges);

            graph.addNode(currentCell, edges);
        }

        return graph;
    }

    private static void addToEdges(Cell right, Set<Floor> edges) {
        if (right != null) {
            edges.add((Floor) right);
        }
    }
}




/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class BreadthFirstSearch {
    private Graph graph;

    public BreadthFirstSearch(Graph graph){
        this.graph = graph;
    }

    public boolean compute(Coord from, Coord to){

        Node startNode = graph.getNodeByCoord(from);
        Node goalNode = graph.getNodeByCoord(to);

        if(startNode.equals(goalNode)){
            System.out.println("Goal Node Found!");
            System.out.println(startNode);
        }

        Queue<Floor> queue = new LinkedList<>();
        ArrayList<Floor> explored = new ArrayList<>();
        queue.add(startNode.getNode());
        explored.add(startNode.getNode());

        while(!queue.isEmpty()){
            Floor current = queue.remove();
            if(current.equals(goalNode.getNode())) {
                System.out.println(explored);
                return true;
            }
            else{
                Node currentNode = graph.getNodeByCoord(current.getCoord());
                if(currentNode.getChildren().isEmpty())
                    return false;
                else
                    queue.addAll(current.getSortedEdgesBasedOnDistanceFromTarget(goalNode.getNode(), currentNode.getChildren()));
            }
            explored.add(current);
        }

        return false;

    }

    public List<Floor> getOptimalPath(final Floor source, final Floor destination) {

        List<Floor> alreadyList = new ArrayList<>();

        final List<Floor> path = recursive(source, destination, alreadyList);
        return path;
    }

    private List<Floor> recursive(Floor current, Floor destination, List<Floor> alreadyList) {
        final List<Floor> path = new ArrayList<>();

        alreadyList.add(current);
        if (current == destination) {
            path.add(current);
            return path;
        }

        //System.err.println("current : " + current.getCoordinates().toString());
        Node currentNode = graph.getNodeByCoord(current.getCoord());

        Set<Floor> sortedEdges = current.getSortedEdgesBasedOnDistanceFromTarget(destination, currentNode.getChildren());

        for (final Floor edge : sortedEdges) {
            if (!alreadyList.contains(edge)) {
                if (edge != destination) {
                    path.add(edge);
                }
                final List<Floor> recursivePath = recursive(edge, destination, alreadyList);
                if (!recursivePath.isEmpty() && recursivePath.get(recursivePath.size() - 1) == destination) {
                    path.addAll(recursivePath);
                    return path;
                } else {
                    path.remove(path.size() - 1);
                }
            }
        }

        if (!path.isEmpty() && path.get(path.size() - 1) != destination) {
            return new ArrayList<>();
        }

        return path;
    }
}




/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class EatTask extends Task {
    private Pellet pellet;

    public EatTask(Action action, Pellet pellet) {
        super(action);
        this.pellet = pellet;
    }

    @Override
    public boolean isFinished() {
        return !this.pellet.isStillHere();
    }

    @Override
    public Action keepTargeting() {
        pellet.setTargeted(true);
        return action;
    }

    @Override
    public boolean isMoveTask() {
        return true;
    }

    @Override
    public Coord moveTarget() {
        return isMoveTask() ? ((MoveAction) action).targetCoord() : null;
    }

    @Override
    public String printInfo() {
        return getClass().getName() + " to pellet " + pellet.toString();
    }
}



/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class WaitTask extends Task {
    public WaitTask(Action action) {
        super(action);
    }

    @Override
    public boolean isFinished() {
        return ((WaitAction) action).isFinished();
    }

    @Override
    public Action keepTargeting() {
        return action;
    }

    @Override
    public boolean isMoveTask() {
        return false;
    }

    @Override
    public Coord moveTarget() {
        return null;
    }

    @Override
    public String printInfo() {
        return getClass().getName();
    }
}



/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class FindPelletTask extends Task {
    private Floor floor;

    public FindPelletTask(MoveAction moveAction, Floor floor) {
        super(moveAction);
        this.floor = floor;
    }

    @Override
    public boolean isFinished() {
        return floor.isEmpty() || ((MoveAction) action).isReached();
    }

    @Override
    public Action keepTargeting() {
        floor.setTargeted(true);
        return action;
    }

    @Override
    public boolean isMoveTask() {
        return true;
    }

    @Override
    public Coord moveTarget() {
        return floor.getCoord();
    }

    @Override
    public String printInfo() {
        return getClass().getName() + " Floor : " + floor.getCoord();
    }
}



/**
 * Mohamed BELMAHI created on 15/05/2020
 */
abstract class Task {
    Action action;

    Task(Action action){
        this.action = action;
    }
    public abstract boolean isFinished();

    public abstract Action keepTargeting();

    public abstract boolean isMoveTask();

    public void switchTo(Task task) {
         this.action.changeItPacWith(task.action);
    }

    public abstract Coord moveTarget();

    public abstract String printInfo();
}



/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class Config {
    public static final Coord[] ADJACENCY = { new Coord(-1, 0), new Coord(1, 0), new Coord(0, -1), new Coord(0, 1) };
    public static boolean MAP_WRAPS = true;
    public static final int ID_ROCK = 0;
    public static final int ID_PAPER = 1;
    public static final int ID_SCISSORS = 2;
    public static final int ID_NEUTRAL = -1;
}





/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class Game {

    private PathFinder pathfinder;
    private final Grid grid;
    private final Set<Floor> floors;
    private final HashMap<Coord, Cell> cellsMap;
    private final Cell[][] cells;

    public Game(PathFinder pathfinder, Grid grid, Set<Floor> floors, HashMap<Coord, Cell> cellsMap, Cell[][] cells) {

        this.pathfinder = pathfinder;
        this.grid = grid;
        this.floors = floors;
        this.cellsMap = cellsMap;
        this.cells = cells;
    }

    public static CrossedPathsSolution calculatePathsAndCheckIfTheyAreCrossed(PacMan pacMan1, PacMan pacMan2, PathFinder pathfinder, boolean reverseCheck) {
        if (!reverseCheck){
            List<Coord> path1 = getPath(pacMan1.getCoord(), pacMan1.getTarget(), pathfinder);
            List<Coord> path2 = getPath(pacMan2.getCoord(), pacMan2.getTarget(), pathfinder);
            return isCrossedPaths(path1, path2, pacMan1, pacMan2);
        } else {
            List<Coord> path1 = getPath(pacMan1.getCoord(), pacMan2.getTarget(), pathfinder);
            List<Coord> path2 = getPath(pacMan2.getCoord(), pacMan1.getTarget(), pathfinder);
            return isCrossedPaths(path1, path2, pacMan1, pacMan2);
        }
    }

    private static List<Coord> getPath(Coord source, Coord target, PathFinder pathfinder) {
        return pathfinder
                .from(source)
                .to(target)
                .findPath().path;
    }

    public static CrossedPathsSolution isCrossedPaths(List<Coord> path1, List<Coord> path2, PacMan pacMan1, PacMan pacMan2) {
        if (path1.size() > 1 && path2.size() > 1) {
            double distanceTo = pacMan1.distanceTo(pacMan2);
            if (distanceTo <= 1.0) {
                if (path1.get(0).equals(path2.get(1))
                        && path1.get(1).equals(path2.get(0))){
                    return CrossedPathsSolution.SWITCH;
                }
            }else if (path1.get(1).equals(path2.get(1))) {
                if (distanceTo <= 1.5) {
                    return CrossedPathsSolution.WAIT;
                } else if (distanceTo <= 2.0){
                    return CrossedPathsSolution.SWITCH;
                }
            }
        }
        return CrossedPathsSolution.NO_NEED;
    }

    public static Stream<PacMan> otherPacmen(PacMan pac, Collection<PacMan> collection) {
        return collection.stream().filter(p -> p != pac);
    }

    public static Set<Pellet> updateBasedOnPacManVisibility(Map<Coord, Pellet> newVisiblePellets, Collection<PacMan> pacManMap, Cell[][] cells) {
        Set<Coord> visibleCoords = getAllVisibleCoords(pacManMap.stream(), cells);
        for (Coord visibleCoord : visibleCoords) {
            Pellet pellet = newVisiblePellets.get(visibleCoord);
            if (pellet == null) {
                Floor floor = (Floor) cells[visibleCoord.getX()][visibleCoord.getY()];
                floor.noPellet();
            }
        }
        return null;
    }

    public static Set<Coord> getAllVisibleCoords(Stream<PacMan> alivePacmen, Cell[][] cells) {
        Set<Coord> visibleCoords = new HashSet<>();
        alivePacmen.forEach(pacman -> {
            pacman.myVisibleCells(cells).forEach(cell -> visibleCoords.add(cell.getCoord()));
        });
        return visibleCoords;
    }

    public static void printEndTime(long startTime, String message) {
        long endTime = System.nanoTime();
        long durationInNano = (endTime - startTime);  //Total execution time in nano seconds
        long durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNano);

        System.err.println(message + " = " + durationInMillis + "ms");
    }

    public static void ifCrossedPathsDoSwitchTask(Set<PacMan> pacManSet, PathFinder pathfinder, List<Action> actionsList, Set<Floor> floors, int finalTour) {
        List<PacMan> blockedPac = new ArrayList<>();

        pacManSet.forEach(pacMan -> {
            if (!blockedPac.contains(pacMan)) {
                Stream<PacMan> pacManStream = Game.otherPacmen(pacMan, pacManSet);
                Optional<PacMan> firstOne = pacManStream.filter(pac -> pac.distanceTo(pacMan) <= 2.0).findFirst();
                if (firstOne.isPresent() && !blockedPac.contains(firstOne.get())) {
                    CrossedPathsSolution crossedPathsSolution = Game.calculatePathsAndCheckIfTheyAreCrossed(pacMan, firstOne.get(), pathfinder, false);
                    if (crossedPathsSolution.equals(CrossedPathsSolution.SWITCH)) {
                        /*crossedPathsSolution = Game.calculatePathsAndCheckIfTheyAreCrossed(pacMan, firstOne.get(), pathfinder, true);
                        if (crossedPathsSolution.equals(CrossedPathsSolution.SWITCH)) {
                            actionsList.remove(pacMan.getCurrentAction());
                            MoveAction moveAction = ActionBuilder.buildFindPelletAction(floors.stream().filter(floor -> floor.isHidden() && floor.isNotTargetedForDiscovery()).collect(Collectors.toSet()), pacMan);
                            if (moveAction != null) {
                                actionsList.add(moveAction);
                            } else {
                                pacMan.setWaitTask();
                                actionsList.add(pacMan.getCurrentAction());
                            }
                        } else {*/
                            pacMan.switchTasksWith(firstOne.get());
                            blockedPac.add(pacMan);
                            blockedPac.add(firstOne.get());
                        //}
                    } else if (CrossedPathsSolution.WAIT.equals(crossedPathsSolution)) {
                        pacMan.setWaitTask();
                    }
                }
            }
        });
    }

    public PacMan checkIfThEnemyIsAttackingMe(PacMan pacMan, List<PacMan> otherPacMen) {
        if (otherPacMen.isEmpty()) {
            return null;
        }

        Optional<PacMan> first = otherPacMen.stream().filter(otherPacMan -> pacMan.isVisibleToMe(otherPacMan)).sorted((o1, o2) -> {
            return o1.distanceTo(pacMan) > o2.distanceTo(pacMan) ? 1 : -1;
        }).findFirst();

        if (!first.isPresent()) {
            return null;
        }

        PacMan attacker = first.get();
        System.err.println(pacMan.infoMe() + " is crossing " + attacker.infoMe());
        if (attacker.distanceTo(pacMan) <= 3) {
            System.err.println(pacMan.infoMe() + " under attack by " + attacker.infoMe());
            return attacker;
        }
        return null;
    }
}




/**
 * Grab the pellets as fast as you can!
 **/
class Player {

    public static void main(String args[]) {
        long startTime = System.nanoTime();
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }
        Cell[][] cells = new Cell[width][height];
        HashMap<Coord, Cell> cellsMap = new HashMap<>();
        Set<Floor> floors = new HashSet<>();
        for (int i = 0; i < height; i++) {
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            System.err.println(row);
            char[] inputTypes = row.toCharArray();
            for (int x = 0; x < inputTypes.length; x++) {
                Coord coord = new Coord(x, i);
                Cell cell = CellFactory.createCell(inputTypes[x], coord);
                cells[x][i] = cell;
                cellsMap.put(coord, cell);
                if (!cell.isWall()) {
                    floors.add((Floor) cell);
                }
            }
        }

        Grid grid = new Grid(width, height, cellsMap, cells);
        PathFinder pathfinder = new PathFinder().setGrid(grid);

        Game game = new Game(pathfinder, grid, floors, cellsMap, cells);

        Game.printEndTime(startTime, "PATH");

        Map<String, PacMan> pacManMap = new HashMap<>();
        Set<Pellet> pellets = new HashSet<>();
        Set<PacMan> myPacMen = new HashSet<>();
        List<PacMan> otherPacMen = new ArrayList<>(5);
        Map<Coord, Pellet> pelletMap = new HashMap<>();
        int tour = 0;
        // game loop
        while (true) {
            startTime = System.nanoTime();
            tour++;
            int myScore = in.nextInt();
            int opponentScore = in.nextInt();
            int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            otherPacMen.clear();
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours
                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues


                PacMan pacMan = pacManMap.get(pacId + "-" + mine);
                if (pacMan != null) {
                    pacMan.update(typeId, cells[x][y], speedTurnsLeft, abilityCooldown);
                    if (!mine) {
                        otherPacMen.add(pacMan);
                    }
                } else {
                    pacMan = new PacMan(pacId, cells[x][y], typeId, speedTurnsLeft, abilityCooldown);
                    pacManMap.put(pacId + "-" + mine, pacMan);
                    if (mine) {
                        myPacMen.add(pacMan);
                    } else {
                        otherPacMen.add(pacMan);
                    }
                }
                Floor floor = (Floor) cells[x][y];
                floor.noPellet();
            }

            Map<Coord, Pellet> newVisiblePellets = new HashMap<>();
            int visiblePelletCount = in.nextInt(); // all pellets in sight
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth

                Coord coord = new Coord(x, y);
                Pellet pellet = pelletMap.get(coord);
                if (pellet == null) {
                    pellet = new Pellet(coord, (Floor) cells[x][y], value);
                    pellets.add(pellet);
                    pelletMap.put(coord, pellet);
                }

                newVisiblePellets.put(coord, pellet);
            }
            int finalTour = tour;
            Set<PacMan> alivePacMen = myPacMen.stream().filter(pacMan -> pacMan.isAlive()).collect(Collectors.toSet());

            Game.updateBasedOnPacManVisibility(newVisiblePellets, alivePacMen, cells);
            pellets.parallelStream().forEach(Pellet::notTargeted);

            //grid.printGrid();
            Game.printEndTime(startTime, "0 - Tour number ("+tour +")");
            List<Action> actionsList = new ArrayList<>();

            Set<Pellet> finalPellets = pellets.parallelStream().filter(pellet -> pellet.isStillHere()).collect(Collectors.toSet());

            alivePacMen.stream().filter(pacMan -> pacMan.hasTask()).forEach(pacMan -> {
                boolean canSpeedUpOrSwitch = pacMan.canSpeedUpOrSwitch();
                if (canSpeedUpOrSwitch) {
                    actionsList.add(new SpeedAction(pacMan));
                } else {
                    actionsList.add(pacMan.getCurrentAction());
                }
            });
            Game.printEndTime(startTime, "1 - Tour number ("+tour +")");
            alivePacMen.stream().filter(pacMan -> !pacMan.hasTask()).forEach(pacMan -> {
                Action moveAction = null;
                if (pacMan.canSpeedUpOrSwitch()) {
                    moveAction = new SpeedAction(pacMan);
                }
                if ((moveAction != null)){
                    actionsList.add(moveAction);
                }
                else {
                    moveAction = ActionBuilder.buildMoveAction(finalPellets.stream()
                            .filter(pellet -> pellet.isSuper() && !pellet.isTargeted()).collect(Collectors.toSet()), pacMan);
                    if (moveAction != null) {
                        actionsList.add(moveAction);
                    } else {
                        moveAction = ActionBuilder.buildMoveAction(finalPellets.stream()
                                .filter(pellet -> !pellet.isSuper() && !pellet.isTargeted()).collect(Collectors.toSet()), pacMan);
                        if (moveAction != null) {
                            actionsList.add(moveAction);
                        }else {
                            moveAction = ActionBuilder.buildFindPelletAction(floors.stream().filter(floor -> floor.isHidden() && floor.isNotTargetedForDiscovery()).collect(Collectors.toSet()), pacMan);
                            if (moveAction != null) {
                                actionsList.add(moveAction);
                            }
                        }
                    }
                }
            });
            Game.printEndTime(startTime, "3 - Tour number ("+tour +")");
            Game.ifCrossedPathsDoSwitchTask(alivePacMen.stream().filter(PacMan::hasMoveTask).collect(Collectors.toSet()), pathfinder, actionsList, floors, finalTour);

            String actions = String.join(" | ", actionsList.stream().map(Action::printCommand).collect(Collectors.toList()));
            Game.printEndTime(startTime, "99 - Tour number ("+tour +")");
            System.out.println(actions); // MOVE <pacId> <x> <y>
        }
    }




}