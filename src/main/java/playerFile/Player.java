
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
    SWITCH, WAIT, NO_NEED;
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
    ROCK, PAPER, SCISSORS
}




/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class Grid {
    public static int width;
    public static int height;
    private HashMap<Coord, Cell> cellsMap;
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
            this.floorStatus = FloorStatus.EMPTY;
        }
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
        return moveAction;
    }

    public boolean isTargeted() {
        return targeted;
    }

    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
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
}




/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class WaitAction extends Action {
    public WaitAction(PacMan pacMan) {
        super(pacMan);
    }

    @Override
    public ActionType type() {
        return ActionType.WAIT;
    }

    @Override
    public String print(int pacId) {
        return String.join(" ", type().toString(), String.valueOf(pacId), msg());
    }

    @Override
    protected String msg() {
        return "W";
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
    public String print(int pacId) {
        return String.join(" ", Arrays.asList(type().toString(), String.valueOf(pacId), msg()));
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

    public abstract String print(int pacId);
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
        return true;
    }

    @Override
    public Action keepTargeting() {
        return null;
    }

    @Override
    public boolean isMoveTask() {
        return false;
    }

    @Override
    public Coord moveTarget() {
        return null;
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
        return ((MoveAction) action).isReached();
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

    public static CrossedPathsSolution calculatePathsAndCheckIfTheyAreCrossed(PacMan pacMan1, PacMan pacMan2, Cell[][] cells, PathFinder pathfinder) {
        List<Coord> path1 = pathfinder
                .from(pacMan1.getCoord())
                .to(pacMan1.getTarget())
                .findPath().path;

        List<Coord> path2 = pathfinder
                .from(pacMan2.getCoord())
                .to(pacMan2.getTarget())
                .findPath().path;
        return isCrossedPaths(path1, path2, pacMan1, pacMan2);
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

    public static void ifCrossedPathsDoSwitchTask(Set<PacMan> pacManSet, Cell[][] cells, PathFinder pathfinder) {
        List<PacMan> blockedPac = new ArrayList<>();

        pacManSet.forEach(pacMan -> {
            if (!blockedPac.contains(pacMan)) {
                Stream<PacMan> pacManStream = Game.otherPacmen(pacMan, pacManSet);
                Optional<PacMan> firstOne = pacManStream.filter(pac -> pac.distanceTo(pacMan) <= 2.0).findFirst();
                if (firstOne.isPresent() && !blockedPac.contains(firstOne.get())) {
                    CrossedPathsSolution crossedPathsSolution = Game.calculatePathsAndCheckIfTheyAreCrossed(pacMan, firstOne.get(), cells, pathfinder);
                    if (crossedPathsSolution.equals(CrossedPathsSolution.SWITCH)) {
                        pacMan.switchTasksWith(firstOne.get());
                        blockedPac.add(pacMan);
                        blockedPac.add(firstOne.get());
                    } else if (CrossedPathsSolution.WAIT.equals(crossedPathsSolution)) {
                        pacMan.setWaitTask();
                    }
                }
            }
        });
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

        Map<String, PacMan> pacManMap = new HashMap<>();
        Set<Pellet> pellets = new HashSet<>();
        Map<Coord, Pellet> pelletMap = new HashMap<>();
        int tour = 0;
        // game loop
        while (true) {
            startTime = System.nanoTime();
            tour++;
            int myScore = in.nextInt();
            int opponentScore = in.nextInt();
            int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours
                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues

                if (mine) {
                    PacMan pacMan = pacManMap.get(pacId + "-" + mine);
                    if (pacMan != null) {
                        pacMan.update(typeId, cells[x][y], speedTurnsLeft, abilityCooldown);
                    } else {
                        pacMan = new PacMan(pacId, cells[x][y], typeId, speedTurnsLeft, abilityCooldown);
                        pacManMap.put(pacId + "-" + mine, pacMan);
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
            Game.updateBasedOnPacManVisibility(newVisiblePellets, pacManMap.values(), cells);
            pellets.parallelStream().forEach(Pellet::notTargeted);

            grid.printGrid();
            Game.printEndTime(startTime, "0 - Tour number ("+tour +")");
            List<Action> actionsList = new ArrayList<>();

            Set<Pellet> finalPellets = pellets.parallelStream().filter(pellet -> pellet.isStillHere()).collect(Collectors.toSet());

            int finalTour = tour;
            pacManMap.values().stream().filter(pacMan -> pacMan.isAlive(finalTour) && pacMan.hasTask()).forEach(pacMan -> {
                actionsList.add(pacMan.getCurrentAction());
            });
            pacManMap.values().stream().filter(pacMan -> pacMan.isAlive(finalTour) && !pacMan.hasTask()).forEach(pacMan -> {
                if (pacMan.canSpeedUp()) {
                    actionsList.add(new SpeedAction(pacMan));
                } else {
                    MoveAction moveAction = ActionBuilder.buildMoveAction(finalPellets.stream()
                            .filter(pellet -> pellet.isSuper() && !pellet.isTargeted()).collect(Collectors.toSet()), pacMan);
                    if (moveAction != null) {
                        actionsList.add(moveAction);
                    } else {
                        moveAction = ActionBuilder.buildMoveAction(finalPellets.stream()
                                .filter(pellet -> !pellet.isSuper() && !pellet.isTargeted()).collect(Collectors.toSet()), pacMan);
                        if (moveAction != null) {
                            actionsList.add(moveAction);
                        }else {
                            moveAction = ActionBuilder.buildFindPelletAction(floors.stream().filter(floor -> floor.isHidden()).collect(Collectors.toSet()), pacMan);
                            if (moveAction != null) {
                                actionsList.add(moveAction);
                            }
                        }
                    }
                }
            });

            Game.ifCrossedPathsDoSwitchTask(pacManMap.values().stream().filter(pacMan -> pacMan.isAlive(finalTour) && pacMan.hasMoveTask()).collect(Collectors.toSet()), cells, pathfinder);

            String actions = String.join(" | ", actionsList.stream().map(Action::printCommand).collect(Collectors.toList()));
            Game.printEndTime(startTime, "99 - Tour number ("+tour +")");
            System.out.println(actions); // MOVE <pacId> <x> <y>
        }
    }




}