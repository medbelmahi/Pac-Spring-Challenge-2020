
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

    Grid(int width, int height) {
        this.width = width;
        this.height = height;
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



/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class Coord {

    private final int x;
    private final int y;

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
}



/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class Floor extends Cell {

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


/**
 * Mohamed BELMAHI created on 14/05/2020
 */
class Cell {
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
 * Mohamed BELMAHI created on 14/05/2020
 */
class MoveAction extends Action {

    private Coord coord;
    public MoveAction(PacMan pacMan, Coord coord) {
        super(pacMan);
        this.coord = coord;
    }

    @Override
    public String printCommand() {
        return pacMan.doCommand(this);
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
    public String printCommand() {
        return pacMan.doCommand(this);
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

    public abstract String printCommand();
    public abstract ActionType type();

    public abstract String print(int pacId);
    protected abstract String msg();
}



/**
 * Mohamed BELMAHI created on 15/05/2020
 */
class Game {
    private List<PacMan> pacmen;

    private Stream<PacMan> otherPacmen(PacMan pac) {
        return otherPacmen(pac, pacmen);
    }
    private Stream<PacMan> otherPacmen(PacMan pac, Collection<PacMan> collection) {
        return collection.stream().filter(p -> p != pac);
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
}




/**
 * Grab the pellets as fast as you can!
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }
        Cell[][] cells = new Cell[width][height];
        for (int i = 0; i < height; i++) {
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            char[] inputTypes = row.toCharArray();
            for (int x = 0; x < inputTypes.length; x++) {
                Cell cell = CellFactory.createCell(inputTypes[x], new Coord(x, i));
                cells[x][i] = cell;
            }
        }

        Map<String, PacMan> pacManMap = new HashMap<>();
        Set<Pellet> pellets = new HashSet<>();
        Map<Coord, Pellet> pelletMap = new HashMap<>();
        int tour = 0;
        // game loop
        while (true) {
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
            updateBasedOnPacManVisibility(newVisiblePellets, pacManMap.values(), cells);
            pellets.parallelStream().forEach(Pellet::notTargeted);

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
                        }
                    }
                }
            });

            System.out.println(String.join(" | ", actionsList.stream().map(Action::printCommand).collect(Collectors.toList()))); // MOVE <pacId> <x> <y>
        }
    }

    private static Set<Pellet> updateBasedOnPacManVisibility(Map<Coord, Pellet> newVisiblePellets, Collection<PacMan> pacManMap, Cell[][] cells) {
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

    private static Set<Coord> getAllVisibleCoords(Stream<PacMan> alivePacmen, Cell[][] cells) {
        Set<Coord> visibleCoords = new HashSet<>();
        alivePacmen.forEach(pacman -> {
            pacman.myVisibleCells(cells).forEach(cell -> visibleCoords.add(cell.getCoord()));
        });
        return visibleCoords;
    }
}