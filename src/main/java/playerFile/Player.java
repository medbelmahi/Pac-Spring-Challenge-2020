
import java.util.*;
import java.io.*;
import java.math.*;
import java.util.stream.*;
import java.util.concurrent.TimeUnit;



class Pellet {
    private Coord coord;
    private int amount;

    public Pellet(Coord coord, int amount) {
        this.coord = coord;
        this.amount = amount;
    }

    public Coord getCoord() {
        return coord;
    }
}





class Pacman {
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
}


enum  CellType {
    WALL, FLOOR;
}



enum PacmanType {
    ROCK(Config.ID_ROCK), PAPER(Config.ID_PAPER), SCISSORS(Config.ID_SCISSORS), NEUTRAL(Config.ID_NEUTRAL);

    int id;

    private PacmanType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static PacmanType fromId(int id) {
        return Stream.of(values())
                .filter(type -> type.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static PacmanType fromInput(String input) {
        switch (input) {
            case "ROCK":
                return ROCK;
            case "PAPER":
                return PAPER;
            case "SCISSORS":
                return SCISSORS;
            case "NEUTRAL":
                return NEUTRAL;

        }
        throw new RuntimeException(input + " is not a valid pac type");
    }

    public static PacmanType fromCharacter(char c) {
        switch (c) {
            case 'r':
            case 'R':
                return ROCK;
            case 'p':
            case 'P':
                return PAPER;
            case 's':
            case 'S':
                return SCISSORS;
            case 'n':
            case 'N':
                return NEUTRAL;

        }
        throw new RuntimeException(c + " is not a valid pac type");
    }
}





/**
 * Created by Mohamed BELMAHI on 25/09/2016.
 */
class GraphFindAllPaths<T extends Floor> implements Iterable<T> {

    /* A map from nodes in the graph to sets of outgoing edges.  Each
     * set of edges is represented by a map from edges to doubles.
     */
    public final Map<T, Map<T, Direction>> graph = new HashMap<T, Map<T, Direction>>();

    /**
     *  Adds a new node to the graph. If the node already exists then its a
     *  no-op.
     *
     * @param node  Adds to a graph. If node is null then this is a no-op.
     * @return      true if node is added, false otherwise.
     */
    public boolean addNode(T node) {
        if (node == null) {
            throw new NullPointerException("The input node cannot be null.");
        }
        if (graph.containsKey(node)) return false;

        graph.put(node, new HashMap<T, Direction>());
        return true;
    }

    /**
     * Given the source and destination node it would add an arc from source
     * to destination node. If an arc already exists then the value would be
     * updated the new value.
     *
     * @param source                    the source node.
     * @param destination               the destination node.
     * @param direction                    if length if
     * @throws NullPointerException     if source or destination is null.
     * @throws NoSuchElementException   if either source of destination does not exists.
     */
    public void addEdge (T source, T destination, Direction direction) {
        if (source == null || destination == null) {
            throw new NullPointerException("Source and Destination, both should be non-null.");
        }
        if (!graph.containsKey(source) || !graph.containsKey(destination)) {
            throw new NoSuchElementException("Source and Destination, both should be part of graph");
        }
        /* A node would always be added so no point returning true or false */
        graph.get(source).put(destination, direction);
    }

    /**
     * Removes an edge from the graph.
     *
     * @param source        If the source node.
     * @param destination   If the destination node.
     * @throws NullPointerException     if either source or destination specified is null
     * @throws NoSuchElementException   if graph does not contain either source or destination
     */
    public void removeEdge (T source, T destination) {
        if (source == null || destination == null) {
            throw new NullPointerException("Source and Destination, both should be non-null.");
        }
        if (!graph.containsKey(source) || !graph.containsKey(destination)) {
            throw new NoSuchElementException("Source and Destination, both should be part of graph");
        }
        graph.get(source).remove(destination);
    }

    /**
     * Given a node, returns the edges going outward that node,
     * as an immutable map.
     *
     * @param node The node whose edges should be queried.
     * @return An immutable view of the edges leaving that node.
     * @throws NullPointerException   If input node is null.
     * @throws NoSuchElementException If node is not in graph.
     */
    public Map<T, Direction> edgesFrom(T node) {
        if (node == null) {
            throw new NullPointerException("The node should not be null.");
        }
        Map<T, Direction> edges = graph.get(node);
        if (edges == null) {
            throw new NoSuchElementException("Source node does not exist.");
        }
        return Collections.unmodifiableMap(edges);
    }

    /**
     * Returns the iterator that travels the nodes of a graph.
     *
     * @return an iterator that travels the nodes of a graph.
     */
    public Iterator<T> iterator() {
        return graph.keySet().iterator();
    }
}




/**
 * Mohamed BELMAHI created on 09/05/2020
 */
class GraphMaker {

    public static GraphFindAllPaths<Floor> constructGraph(Set<Floor> places, Cell[][] cells) {
        GraphFindAllPaths<Floor> graphFindAllPaths = new GraphFindAllPaths<Floor>();

        for (Floor currentCell : places) {
            graphFindAllPaths.addNode(currentCell);

            Cell right = currentCell.rightCell(cells);
            Cell left = currentCell.leftCell(cells);
            Cell up = currentCell.upCell(cells);
            Cell down = currentCell.downCell(cells);

            addEdgeToCurrentCell(graphFindAllPaths, currentCell, right, Direction.RIGHT);
            addEdgeToCurrentCell(graphFindAllPaths, currentCell, left, Direction.LEFT);
            addEdgeToCurrentCell(graphFindAllPaths, currentCell, up, Direction.UP);
            addEdgeToCurrentCell(graphFindAllPaths, currentCell, down, Direction.DOWN);
        }

        return graphFindAllPaths;
    }

    public static void addEdgeToCurrentCell(final GraphFindAllPaths<Floor> graphFindAllPaths, Floor currentCell, Cell destination, Direction direction) {
        if (destination != null) {
            graphFindAllPaths.addNode((Floor) destination);
            graphFindAllPaths.addEdge(currentCell, (Floor) destination, direction);
        }
    }
}





/**
 * Created by Mohamed BELMAHI on 27/09/2016.
 */
class FindOptimalPath<T extends Floor> {

    private final GraphFindAllPaths<T> graph;
    public static final int PLACE_TO_ESCAPE = 10;

    public FindOptimalPath(final GraphFindAllPaths<T> graph) {
        if (graph == null) {
            throw new NullPointerException("The input graph cannot be null.");
        }
        this.graph = graph;
    }

    private void validate(final T source, final T destination) {

        if (source == null) {
            throw new NullPointerException("The source: " + source + " cannot be  null.");
        }
        if (destination == null) {
            throw new NullPointerException("The destination: " + destination + " cannot be  null.");
        }
        if (source.equals(destination)) {
            //throw new IllegalArgumentException("The source and destination: " + source + " cannot be the same.");
            throw new IllegalArgumentException();
        }
    }

    public List<T> getOptimalPath(final T source, final T destination) {
        try {
            validate(source, destination);
        } catch (IllegalArgumentException e) {
            return new ArrayList<T>();
        }

        List<T> alreadyList = new ArrayList<>();

        final List<T> path = recursive(source, destination, alreadyList);
        return path;
    }

    private List<T> recursive(final T current, final T destination, List<T> alreadyList) {
        final List<T> path = new ArrayList<>();

        alreadyList.add(current);
        if (current == destination) {
            path.add(current);
            return path;
        }

        //System.err.println("current : " + current.coordinates.toString());
        final Map<T, Direction> edges  = graph.edgesFrom(current);

        final LinkedList<Direction> directions = current.getDirections(destination);


        for (final Direction direction : directions) {
            for (final Map.Entry<T, Direction> entry : edges.entrySet()) {
                T entryKey = entry.getKey();
                if (direction.equals(entry.getValue()) && !alreadyList.contains(entryKey)) {
                    if (entryKey != destination) {
                        path.add(entryKey);
                    }
                    final List<T> recursivePath = recursive(entryKey, destination, alreadyList);
                    if (!recursivePath.isEmpty() && recursivePath.get(recursivePath.size() - 1) == destination) {
                        path.addAll(recursivePath);
                        return path;
                    }
                }
            }
        }

        if (!path.isEmpty() && path.get(path.size() - 1) != destination) {
            return new ArrayList<>();
        }

        return path;
    }


    public Map<T, Integer> getPlacesWithDistance(T currentPlace){
        Map<T, Integer> places = new HashMap<T, Integer>();

        final Map<T, Direction> edges  = graph.edgesFrom(currentPlace);

        for (Map.Entry<T, Direction> entry : edges.entrySet()) {
            recursivePlacesWithDistance(places, currentPlace, entry.getKey());
        }

        return places;
    }

    private void recursivePlacesWithDistance(Map<T, Integer> places, T currentPlace, T destination) {
        if (places.size() > 10) {
            places.put(destination, getOptimalPath(currentPlace, destination).size());

            final Map<T, Direction> edges  = graph.edgesFrom(destination);

            for (Map.Entry<T, Direction> entry : edges.entrySet()) {
                recursivePlacesWithDistance(places, currentPlace, entry.getKey());
            }
        }
    }

    /*public static void main(final String[] args) {
        final GraphFindAllPaths<Floor> graph = new GraphFindAllPaths<>();

        final Floor here = new Floor(new Coord(0, 0));
        final Floor next1 = new Floor(new Coord(0, 1));
        final Floor next2 = new Floor(new Coord(0, 2));
        final Floor next3 = new Floor(new Coord(0, 3));
        final Floor next4 = new Floor(new Coord(0, 4));
        final Floor next41 = new Floor(new Coord(1, 4));
        final Floor next5 = new Floor(new Coord(0, 5));
        final Floor next6 = new Floor(new Coord(0, 6));
        final Floor next62 = new Floor(new Coord(2, 6));

        graph.addNode(here);

        graph.addNode(next1);
        graph.addNode(next2);
        graph.addNode(next3);
        graph.addNode(next4);
        graph.addNode(next5);
        graph.addNode(next6);
        graph.addNode(next62);
        graph.addNode(next41);

        graph.addEdge(here, next1, Direction.DOWN);
        graph.addEdge(next1, next2, Direction.DOWN);
        graph.addEdge(next2, next3, Direction.DOWN);
        graph.addEdge(next3, next4, Direction.DOWN);
        graph.addEdge(next4, next5, Direction.DOWN);
        graph.addEdge(next5, next6, Direction.DOWN);
        graph.addEdge(next4, next41, Direction.RIGHT);

        //
        final Floor next51 = new Floor(new Coord(1, 5));
        graph.addNode(next51);
        graph.addEdge(next5, next51, Direction.RIGHT);

        final FindOptimalPath<Floor> findOptimalPath = new FindOptimalPath<>(graph);

        final List<Floor> optimalPath = findOptimalPath.getOptimalPath(next4, next41);

        for (final Floor floor : optimalPath) {
            System.out.println(floor.getCoordinates().toString());
        }

    }*/



}


/**
 * Created by Mohamed BELMAHI on 26/09/2016.
 */
enum Direction {
    RIGHT, LEFT, UP, DOWN
}




class Grid {
    public static int width, height;
    Map<Coord, Cell> cellsMap = new HashMap<>();
    Cell[][] cells;
    Set<Floor> places;

    public Grid(Cell[][] cells, Set<Floor> places, int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = cells;
        this.places = places;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cellsMap.put(new Coord(x, y), cells[x][y]);
            }
        }
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

    public Floor randomFloor() {
        int index = (int) (Math.random() * (this.places.size()));
        Iterator<Floor> iterator = this.places.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }
}




class Coord {
    protected final int x;
    protected final int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double euclideanTo(int x, int y) {
        return Math.sqrt(sqrEuclideanTo(x, y));
    }

    private double sqrEuclideanTo(int x, int y) {
        return Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        Coord other = (Coord) obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public String print() {
        return x + " " + y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int manhattanTo(Coord other) {
        return manhattanTo(other.x, other.y);
    }

    public int chebyshevTo(int x, int y) {
        return Math.max(Math.abs(x - this.x), Math.abs(y - this.y));
    }

    public int manhattanTo(int x, int y) {
        return Math.abs(x - this.x) + Math.abs(y - this.y);
    }

    public Coord add(Coord d) {
        return new Coord(x + d.x, y + d.y);
    }

    public Coord subtract(Coord d) {
        return new Coord(x - d.x, y - d.y);
    }

    public double euclideanTo(Coord d) {
        return euclideanTo(d.x, d.y);
    }

    public Coord getUnitVector() {
        int newX = this.x == 0 ? 0 : this.x / Math.abs(this.x);
        int newY = this.y == 0 ? 0 : this.y / Math.abs(this.y);
        return new Coord(newX, newY);
    }

    public LinkedList<Direction> getSortedDirection(Coord destination) {

        LinkedList<Direction> directions = new LinkedList<>();

        if (this.y == destination.y) {
            if (this.x < destination.x) {
                directions.add(Direction.RIGHT);
                directions.add(Direction.DOWN);
                directions.add(Direction.UP);
                directions.add(Direction.LEFT);
            }else{
                directions.add(Direction.LEFT);
                directions.add(Direction.DOWN);
                directions.add(Direction.UP);
                directions.add(Direction.RIGHT);
            }
        } else if (this.x == destination.x) {
            if (this.y < destination.y) {
                directions.add(Direction.DOWN);
                directions.add(Direction.RIGHT);
                directions.add(Direction.LEFT);
                directions.add(Direction.UP);
            }else{
                directions.add(Direction.UP);
                directions.add(Direction.RIGHT);
                directions.add(Direction.LEFT);
                directions.add(Direction.DOWN);
            }
        } else if (this.x < destination.x && this.y < destination.y) {
            int diffX = destination.x - this.x;
            int diffY = destination.y - this.y;

            if (diffX < diffY) {
                directions.add(Direction.DOWN);
                directions.add(Direction.RIGHT);
                directions.add(Direction.LEFT);
                directions.add(Direction.UP);
            }else{
                directions.add(Direction.RIGHT);
                directions.add(Direction.DOWN);
                directions.add(Direction.LEFT);
                directions.add(Direction.UP);
            }
        }else if (this.x > destination.x && this.y > destination.y) {
            int diffX = destination.x - this.x;
            int diffY = destination.y - this.y;

            if (diffX < diffY) {
                directions.add(Direction.LEFT);
                directions.add(Direction.UP);
                directions.add(Direction.DOWN);
                directions.add(Direction.RIGHT);
            }else{
                directions.add(Direction.UP);
                directions.add(Direction.RIGHT);
                directions.add(Direction.DOWN);
                directions.add(Direction.RIGHT);
            }
        }else if (this.x < destination.x && this.y > destination.y){
            directions.add(Direction.LEFT);
            directions.add(Direction.UP);
            directions.add(Direction.DOWN);
            directions.add(Direction.RIGHT);
        }if (this.x > destination.x && this.y < destination.y){
            directions.add(Direction.LEFT);
            directions.add(Direction.DOWN);
            directions.add(Direction.UP);
            directions.add(Direction.RIGHT);
        }


        return directions;
    }
}




class Wall extends Cell {
    public Wall(Coord coord) {
        super(coord);
    }

    @Override
    public String toString() {
        return "#";
    }

    @Override
    public boolean isWall() {
        return true;
    }
}



class CellPrototype {
    public static Cell getCell(char type, int x, int y) {
        Coord coord = new Coord(x, y);
        switch (type) {
            case ' ' : return new Floor(coord);
            case '#': return new Wall(coord);

            default: throw new IllegalArgumentException("No Cell With Type of : " + type);
        }
    }
}




class Floor extends Cell {
    public Floor(Coord coord) {
        super(coord);
    }

    @Override
    public String toString() {
        return " ";
    }

    public <T extends Floor> LinkedList<Direction> getDirections(final T destination) {
        return this.getCoordinates().getSortedDirection(destination.getCoordinates());
    }
}



class Cell {
    private Coord coordinates;

    public Cell(Coord coordinates) {
        this.coordinates = coordinates;
    }

    public Cell rightCell(Cell[][] cells) {
        if (coordinates.getX() + 1 < Grid.width) {
            Cell cell = cells[coordinates.getY()][coordinates.getX() + 1];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell leftCell(Cell[][] cells) {
        if (coordinates.getX() - 1 >= 0) {
            Cell cell = cells[coordinates.getY()][coordinates.getX() - 1];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell upCell(Cell[][] cells) {
        if (coordinates.getY() - 1 >= 0) {
            Cell cell = cells[coordinates.getY() - 1][coordinates.getX()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell downCell(Cell[][] cells) {
        if (coordinates.getY() + 1 < Grid.height) {
            Cell cell = cells[coordinates.getY() + 1][coordinates.getX()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public boolean isWall() {
       return false;
    }

    public Coord getCoordinates() {
        return coordinates;
    }
}





class SwitchAction implements Action {

    private PacmanType type;

    public PacmanType getNewType() {
        return type;
    }

    public SwitchAction(PacmanType type) {
        this.type = type;
    }

    @Override
    public PacmanType getType() {
        return type;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SWITCH;
    }

    @Override
    public String print(int id) {
        return null;
    }
}




class MoveAction implements Action {

    private Coord destination;

    public Coord getTarget() {
        return destination;
    }

    public MoveAction(Coord destination, boolean activateSpeed) {
        this.destination = destination;
    }

    @Override
    public PacmanType getType() {
        return null;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.MOVE;
    }

    public String print(int pacmanId) {
        return getActionType().toString() + " " + pacmanId + " " + destination.print();
    }
}


enum ActionType {
  WAIT, MOVE, MSG, SPEED, SWITCH
}


@SuppressWarnings("serial")
class ActionException extends Exception {

    public ActionException(String message) {
        super(message);
    }

}



class SpeedAction implements Action {
  @Override
  public ActionType getActionType() {
      return ActionType.SPEED;
  }

    @Override
    public String print(int id) {
        return null;
    }

    @Override
  public PacmanType getType() {
      return null;
  }
}



interface Action {

    Action NO_ACTION = new Action() {

        @Override
        public PacmanType getType() {
            return null;
        }

        @Override
        public ActionType getActionType() {
            return ActionType.WAIT;
        }

        @Override
        public String print(int id) {
            return ActionType.WAIT.toString() + " " + id;
        }
    };

    public PacmanType getType();
    public ActionType getActionType();

    String print(int id);
}




class Game {
    Grid grid;

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
}


class Config {
    public static final int ID_ROCK = 0;
    public static final int ID_PAPER = 1;
    public static final int ID_SCISSORS = 2;
    public static final int ID_NEUTRAL = -1;
}




class Gamer {
    private List<Pacman> pacmen = new ArrayList<>();
    private int score;
    public int pellets = 0;
    private boolean timeout;

    public Gamer() {
        pacmen = new ArrayList<>();
    }

    public void addPacman(Pacman pacman) {
        pacmen.add(pacman);

    }

    public List<Pacman> getPacmen() {
        return pacmen;
    }

    public Stream<Pacman> getAlivePacmen() {
        return pacmen.stream().filter(pac -> !pac.isDead());
    }

    public void turnReset() {
        pacmen.forEach(a -> a.turnReset());
    }


    public void setScore(int score) {
        this.score = score;
    }

    public String play(LinkedList<Pellet> pellets, Set<Pellet> superPellets, Grid grid) {
        this.pacmen = getAlivePacmen().collect(Collectors.toList());
        int size = pacmen.size();
        Iterator<Pellet> pelletIterator = superPellets.iterator();
        for (int i = 0; i < size; i++) {
            if (pelletIterator.hasNext()) {
                Pellet pellet = pelletIterator.next();
                Pacman pacman = getNearestPacman(pellet, pacmen.stream().filter(Pacman::available));
                pacman.setAction(new MoveAction(pellet.getCoord(), false));
            }
        }
        pacmen.stream().filter(Pacman::available).forEach(pacman -> pacman.doAction(pellets, superPellets, grid));

        List<String> actionsList = new ArrayList<>();
        getAlivePacmen().forEach(pacman -> actionsList.add(pacman.printAction()));

        return String.join(" | ", actionsList);
    }

    private Pacman getNearestPacman(Pellet pellet, Stream<Pacman> pacmen) {
        Pacman target = null;
        double minDistance = Integer.MAX_VALUE;
        Iterator<Pacman> iterator = pacmen.iterator();
        while (iterator.hasNext()) {
            Pacman pacman = iterator.next();
            double distance = pacman.distance(pellet.getCoord());
            if (minDistance > distance) {
                minDistance = distance;
                target = pacman;
            }
        }
        return target;
    }
}




/**
 * Grab the pellets as fast as you can!
 **/
class Player {

    public static void main(String args[]) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }

        Cell[][] cells = new Cell[width][height];
        final Set<Floor> places = new HashSet<>();
        for (int i = 0; i < height; i++) {
            int y = i;
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall

            char[] cellsInput = row.toCharArray();
            for (int x = 0; x < cellsInput.length; x++) {
                Cell cell = CellPrototype.getCell(cellsInput[x], x, y);
                cells[x][y] = cell;
                if (cell instanceof Floor) {
                    places.add((Floor) cell);
                }
            }
        }

        Grid grid = new Grid(cells, places, width, height);

        //grid.printGrid();
        Game game = new Game(grid);
        Gamer me = new Gamer();
        game.setMe(me);
        Gamer opponent = new Gamer();
        game.setOpponent(opponent);

        Map<String, Pacman> pacmanMap = new HashMap<>();

        // Start First Tour -------------------------------------------------------------------------------------------
        int tour = 1;
        long startTime = System.nanoTime();
        setScores(in, me, opponent);

        int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
        for (int i = 0; i < visiblePacCount; i++) {
            int pacId = in.nextInt(); // pac number (unique within a team)
            boolean mine = in.nextInt() != 0; // true if this pac is yours
            int x = in.nextInt(); // position in the grid
            int y = in.nextInt(); // position in the grid
            String typeId = in.next(); // unused in wood leagues
            int speedTurnsLeft = in.nextInt(); // unused in wood leagues
            int abilityCooldown = in.nextInt(); // unused in wood leagues

            Pacman pacman;
            if (mine) {
                pacman = new Pacman(pacId, 0, me, new Coord(x, y), PacmanType.fromInput(typeId), speedTurnsLeft, abilityCooldown, 1);
            } else {
                pacman = new Pacman(pacId, 0, opponent, new Coord(x, y), PacmanType.fromInput(typeId), speedTurnsLeft, abilityCooldown, 1);
            }
            pacmanMap.put(pacId + "-" + mine, pacman);
        }

        LinkedList<Pellet> pellets = new LinkedList<>();
        Set<Pellet> superPellets = new HashSet<>();
        Map<Coord, Pellet> pelletMap = new HashMap<>();
        int visiblePelletCount = in.nextInt(); // all pellets in sight
        for (int i = 0; i < visiblePelletCount; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            int value = in.nextInt(); // amount of points this pellet is worth

            Coord coord = new Coord(x, y);
            Pellet pellet = new Pellet(coord, value);
            if (value == 10) {
                superPellets.add(pellet);
            } else {
                pellets.add(pellet);
            }
            pelletMap.put(coord, pellet);
        }
        game.setPellets(pellets);
        game.setSuperPellets(superPellets);
        System.out.println(game.play());
        printEndTime(startTime, "First Tour");
        // Start First Tour -------------------------------------------------------------------------------------------



        // game loop after first tour
        while (true) {
            startTime = System.nanoTime();
            tour++;
            setScores(in, me, opponent);

            visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours

                //System.err.println(key);

                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues

                String key = pacId + "-" + mine;
                Pacman pacman = pacmanMap.get(key);
                if (pacman == null) {
                    pacman = new Pacman(pacId, 0, opponent, new Coord(x, y), PacmanType.fromInput(typeId), speedTurnsLeft, abilityCooldown, tour);
                    pacmanMap.put(key, pacman);
                }else {
                    pacman.setSpeedTurnsLeft(speedTurnsLeft);
                    pacman.setType(PacmanType.fromInput(typeId));
                    pacman.setPosition(new Coord(x, y));
                    pacman.setAbilityCooldown(abilityCooldown);
                    pacman.update();
                }
            }
            setDeadPacmen(pacmanMap.values(), tour);
            visiblePelletCount = in.nextInt(); // all pellets in sight

            pellets.clear();
            superPellets.clear();
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth

                Pellet pellet = new Pellet(new Coord(x, y), value);
                if (value == 10) {
                    superPellets.add(pellet);
                } else {
                    pellets.add(pellet);
                }
            }
            game.setPellets(pellets);
            game.setSuperPellets(superPellets);
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println(game.play());
            printEndTime(startTime, "Tour number ("+tour +")");
        }
    }

    private static void setDeadPacmen(Collection<Pacman> pacmen, int currentTour) {
        pacmen.forEach(pacman -> pacman.setDead(currentTour));
    }

    private static void printEndTime(long startTime, String message) {
        long endTime = System.nanoTime();
        long durationInNano = (endTime - startTime);  //Total execution time in nano seconds
        long durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNano);

        System.err.println(message + " = " + durationInMillis + "ms");
    }

    private static void setScores(Scanner in, Gamer me, Gamer opponent) {
        int myScore = in.nextInt();
        me.setScore(myScore);
        int opponentScore = in.nextInt();
        opponent.setScore(opponentScore);
    }
}