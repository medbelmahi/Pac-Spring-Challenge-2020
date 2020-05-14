
import java.util.*;
import java.io.*;
import java.math.*;
import java.util.stream.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;




class Pellet {
    private Coord coord;
    private int amount;
    private boolean stillHere;

    public Pellet(Coord coord, int amount) {
        this.coord = coord;
        this.amount = amount;
        this.stillHere = true;
    }

    public Coord getCoord() {
        return coord;
    }

    public boolean isSuper() {
        return amount > 1;
    }

    public void setStillHere(boolean stillHere) {
        this.stillHere = stillHere;
    }

    public boolean isStillHere() {
        return stillHere;
    }

    public void disappear() {
        this.stillHere = false;
    }

    public Pacman getNearestPacman(Stream<Pacman> pacmen) {
        Pacman target = null;
        double minDistance = Integer.MAX_VALUE;
        Iterator<Pacman> iterator = pacmen.iterator();
        while (iterator.hasNext()) {
            Pacman pacman = iterator.next();
            double distance = pacman.distance(this.getCoord());
            if (minDistance > distance) {
                minDistance = distance;
                target = pacman;
            }
        }
        return target;
    }

    public double getNearestDistanceToAPacman(Stream<Pacman> pacmen) {
        double minDistance = Integer.MAX_VALUE;
        Iterator<Pacman> iterator = pacmen.iterator();
        while (iterator.hasNext()) {
            Pacman pacman = iterator.next();
            double distance = pacman.distance(this.getCoord());
            if (minDistance > distance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    @Override
    public String toString() {
        return coord.toString();
    }

    public boolean isSimple() {
        return false;
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
                if (grid.get(neighbor).getType() == CellType.FLOOR) {
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

    private static void addEdgeToCurrentCell(final GraphFindAllPaths<Floor> graphFindAllPaths, Floor currentCell, Cell destination, Direction direction) {
        if (destination != null) {
            graphFindAllPaths.addNode((Floor) destination);
            graphFindAllPaths.addEdge(currentCell, (Floor) destination, direction);
        }
    }

    public static void main(String[] args) {
        String gridInput = "#################################\n" +
                "# #     # # # #   # # # #     # #\n" +
                "# # ##### # # # # # # # ##### # #\n" +
                "# #       #     #     #       # #\n" +
                "# # # # # ### # # # ### # # # # #\n" +
                "#   #   #     #   #     #   #   #\n" +
                "##### ### # # ##### # # ### #####\n" +
                "#       #   # #   # #   #       #\n" +
                "# ##### # ### # # # ### # ##### #\n" +
                "#   #           #           #   #\n" +
                "### # # # # # # # # # # # # # ###\n" +
                "        #   #   #   #   #        \n" +
                "### # ### ##### # ##### ### # ###\n" +
                "    #                       #    \n" +
                "#################################";
        Grid.height = 15;
        Grid.width = 33;
        Cell[][] cells = new Cell[Grid.width][Grid.height];
        final Set<Floor> places = new HashSet<>();
        Scanner in = new Scanner(gridInput);
        for (int i = 0; i < Grid.height; i++) {
            int y = i;
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            //System.err.println(row);
            char[] cellsInput = row.toCharArray();
            for (int x = 0; x < cellsInput.length; x++) {
                Cell cell = CellPrototype.getCell(cellsInput[x], x, y);
                cells[x][y] = cell;
                if (cell instanceof Floor) {
                    places.add((Floor) cell);
                }
            }
        }

        in.close();

        GraphFindAllPaths<Floor> graph = constructGraph(places, cells);

        final FindOptimalPath<Floor> findOptimalPath = new FindOptimalPath<>(graph);

        //final List<Floor> optimalPath = findOptimalPath.getOptimalPath((Floor) cells[11][5], (Floor) cells[12][1]);
        final List<Floor> optimalPath = findOptimalPath.getOptimalPath((Floor) cells[25][8], (Floor) cells[31][5], null);

        if (optimalPath.isEmpty()) {
            System.out.println("No path");
        }
        int i = 0;
        for (final Floor floor : optimalPath) {
            i++;
            System.out.println(i + " - " + floor.getCoordinates().toString());
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

    public List<T> getOptimalPath(final T source, final T destination, T exception) {
        try {
            validate(source, destination);
        } catch (IllegalArgumentException e) {
            return new ArrayList<T>();
        }

        List<T> alreadyList = new ArrayList<>();

        final List<T> path = recursive(source, destination, alreadyList, exception);
        return path;
    }

    private List<T> recursive(final T current, final T destination, List<T> alreadyList, T exception) {
        final List<T> path = new ArrayList<>();

        alreadyList.add(current);
        if (current == destination) {
            path.add(current);
            return path;
        }

        //System.err.println("current : " + current.getCoordinates().toString());
        final Map<T, Direction> edges  = graph.edgesFrom(current);

        //final LinkedList<Direction> directions = current.getDirections(destination);

        Set<T> currentFloorEdges = edges.keySet();
        if (exception != null && alreadyList.size() == 1) {
            currentFloorEdges = edges.keySet().stream().filter(t -> !t.equals(exception)).collect(Collectors.toSet());
        }

        Set<T> sortedEdges = current.getSortedEdgesBasedOnDistanceFromTarget(destination, currentFloorEdges);

        for (final T edge : sortedEdges) {
                if (!alreadyList.contains(edge)) {
                    if (edge != destination) {
                        path.add(edge);
                    }
                    final List<T> recursivePath = recursive(edge, destination, alreadyList, exception);
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


    public Map<T, Integer> getPlacesWithDistance(T currentPlace, T exception){
        Map<T, Integer> places = new HashMap<T, Integer>();

        final Map<T, Direction> edges  = graph.edgesFrom(currentPlace);

        for (Map.Entry<T, Direction> entry : edges.entrySet()) {
            recursivePlacesWithDistance(places, currentPlace, entry.getKey(), exception);
        }

        return places;
    }

    private void recursivePlacesWithDistance(Map<T, Integer> places, T currentPlace, T destination, T exception) {
        if (places.size() > 10) {
            places.put(destination, getOptimalPath(currentPlace, destination, exception).size());

            final Map<T, Direction> edges  = graph.edgesFrom(destination);

            for (Map.Entry<T, Direction> entry : edges.entrySet()) {
                recursivePlacesWithDistance(places, currentPlace, entry.getKey(), exception);
            }
        }
    }

    public static void main(final String[] args) {
        final GraphFindAllPaths<Floor> graph = new GraphFindAllPaths<>();

        final Floor here = new Floor(new Coord(0, 0), CellType.FLOOR);
        final Floor next1 = new Floor(new Coord(0, 1), CellType.FLOOR);
        final Floor next2 = new Floor(new Coord(0, 2), CellType.FLOOR);
        final Floor next3 = new Floor(new Coord(0, 3), CellType.FLOOR);
        final Floor next4 = new Floor(new Coord(0, 4), CellType.FLOOR);
        final Floor next41 = new Floor(new Coord(1, 4), CellType.FLOOR);
        final Floor next5 = new Floor(new Coord(0, 5), CellType.FLOOR);
        final Floor next6 = new Floor(new Coord(0, 6), CellType.FLOOR);
        final Floor next62 = new Floor(new Coord(2, 6), CellType.FLOOR);

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
        final Floor next51 = new Floor(new Coord(1, 5), CellType.FLOOR);
        graph.addNode(next51);
        graph.addEdge(next5, next51, Direction.RIGHT);

        final FindOptimalPath<Floor> findOptimalPath = new FindOptimalPath<>(graph);

        final List<Floor> optimalPath = findOptimalPath.getOptimalPath(next4, next41, null);

        for (final Floor floor : optimalPath) {
            System.out.println(floor.getCoordinates().toString());
        }

    }
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
    public static Cell[][] cells;
    Set<Floor> places;
    public static GraphFindAllPaths<Floor> graph;
    public static FindOptimalPath<Floor> findOptimalPath;
    public static PathFinder pathFinder;

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

        this.graph = GraphMaker.constructGraph(places, cells);
        this.findOptimalPath = new FindOptimalPath<>(graph);
        pathFinder = new PathFinder();
        pathFinder.setGrid(this);
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
    private Role role;
    private Mission mission;

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
            this.currentAction = new MoveAction(target.getCoord(), false, id, this);
        } else {
            Coord destination = grid.randomFloor().getCoordinates();
            this.currentAction = new MoveAction(destination, false, id, this);
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
        return this.currentAction.print();
    }

    public boolean available() {
        return !hasAction() && !isDead();
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

    public boolean hasMission() {
        return this.mission != null && mission.isRelevant() && !mission.isFinished() && mission.hasTasks();
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public String printMissionTask() {
        return mission.todoTask();
    }

    public List<Floor> getOptimalPathTo(Coord coord) {
        return this.owner.findOptimalPathFromTo(this.position, coord);
    }

    public Coord getPosition() {
        return position;
    }

    public boolean canSpeedUp() {
        return abilityCooldown <= 0;
    }

    public int getSpeedTurnsLeft() {
        return speedTurnsLeft;
    }

    public int getAbilityCooldown() {
        return abilityCooldown;
    }
}


enum Role {
    COLLECTOR, ATTACKER, SKIPPER, HELPER
}





/**
 * Mohamed BELMAHI created on 11/05/2020
 */
abstract class Mission {
    protected LinkedList<Task> tasks;
    protected Pacman hero;

    public abstract boolean isAchievable();

    public abstract boolean isRelevant();

    public abstract boolean isFinished();

    public abstract boolean build(Game game);

    public String todoTask() {
        System.err.println(toString());
        return this.tasks.pollLast().print();
    }

    public boolean hasTasks() {
        return this.tasks.size() > 0;
    }
}



/**
 * Mohamed BELMAHI created on 11/05/2020
 */
class Task {
    private Action action;
    private int taskTour;

    public Task(Action action, int taskTour) {
        this.action = action;
        this.taskTour = taskTour;
    }

    public String print() {
        return action.print(taskTour);
    }

    public boolean hasConflict(Game game) {
        return game.hasConflict(action, taskTour);
    }

    public boolean withSameAction(Action action) {
        return this.action.areSame(action);
    }
}




/**
 * Mohamed BELMAHI created on 11/05/2020
 */
class CollectSuperPellets extends Mission {
    private Pellet pellet;
    private Stream<Pacman> pacmenStream;

    public CollectSuperPellets(Pellet superPellet, Stream<Pacman> pacmanStream) {
        pellet = superPellet;
        pacmenStream = pacmanStream;
        this.tasks = new LinkedList<>();
    }
    @Override
    public boolean isAchievable() {
        return false;
    }

    @Override
    public boolean isRelevant() {
        return this.pellet.isStillHere();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean build(Game game) {
        List<Pacman> pacmen = this.pacmenStream.filter(p -> !p.hasMission()).collect(Collectors.toList());
        if (pacmen.isEmpty()) {
            return false;
        }

        Pacman pacman = pellet.getNearestPacman(pacmen.stream());

        int currentTour = Game.tour;

        //tasks.push(new Task(new SpeedAction(pacman.getId())));
        List<Floor> optimalPath = pacman.getOptimalPathTo(pellet.getCoord());
        int size = optimalPath.size();
        Iterator<Floor> iterator = optimalPath.iterator();
        Coord previousPos = pacman.getPosition();
        if (size > 2) {
            int taskIndex = 1;
            int speedIndex = 1;
            int index = 0;

            int speedTurnsLeft = 10;
            int abilityCountdown = 20;
            if(pacman.canSpeedUp()) {
                tasks.push(new Task(new SpeedAction(pacman.getId()), currentTour));
            }else {
                speedTurnsLeft = pacman.getSpeedTurnsLeft() * 2;
                abilityCountdown = pacman.getAbilityCooldown() * 2;
            }


            while (iterator.hasNext()) {
                currentTour++;
                Floor floor = iterator.next();

                Task currentTask = null;
                if (index == (size-1)) {
                    currentTask = new Task(new MoveAction(floor.getCoordinates(), true, pacman.getId(), pacman), currentTour);
                }
                if (taskIndex % 2 == 0) {
                    currentTask = new Task(new MoveAction(floor.getCoordinates(), true, pacman.getId(), pacman), currentTour);
                } else if(speedTurnsLeft <= 0) {
                    currentTask = new Task(new MoveAction(floor.getCoordinates(), true, pacman.getId(), pacman), currentTour);
                }

                if (abilityCountdown <= 0) {
                    currentTask = new Task(new SpeedAction(pacman.getId()), currentTour);
                    speedTurnsLeft = 10;
                    abilityCountdown = 20;
                }


                if (currentTask == null) {
                    taskIndex++;
                    speedIndex++;
                    index++;
                    speedTurnsLeft--;
                    abilityCountdown--;
                    continue;
                }

                if(!currentTask.hasConflict(game)) {
                    tasks.push(currentTask);
                    game.addTask(currentTour, currentTask);
                    previousPos = floor.getCoordinates();
                }else {
                    List<Floor> newOptimalPath = game.getMe().findOptimalPathFromTo(previousPos, pellet.getCoord(), floor);
                    iterator = newOptimalPath.iterator();
                    currentTour--;
                    continue;
                }

                taskIndex++;
                speedIndex++;
                index++;
                speedTurnsLeft--;
                abilityCountdown--;
            }
        } else {
            while (iterator.hasNext()) {
                Floor next = iterator.next();
                Task task = new Task(new MoveAction(next.getCoordinates(), true, pacman.getId(), pacman), currentTour);
                if(!task.hasConflict(game)) {
                    tasks.push(task);
                    game.addTask(currentTour, task);
                    currentTour++;
                    previousPos=next.getCoordinates();
                }else {
                    List<Floor> newOptimalPath = game.getMe().findOptimalPathFromTo(previousPos, pellet.getCoord(), next);
                    iterator = newOptimalPath.iterator();
                }
            }
        }
        this.hero = pacman;
        this.hero.setMission(this);

        return true;
    }

    @Override
    public String toString() {
        return "CollectSuperPellets{" +
                "hero=" + hero.getId() +
                ", pellet=" + pellet.toString() +"}";
    }
}




/**
 * Mohamed BELMAHI created on 11/05/2020
 */
class MissionEngine {
    private LinkedList<Mission> store = new LinkedList<>();
    private Game game;

    public MissionEngine(Game game) {
        this.game = game;
    }

    public void collectMissions(Set<Pellet> sortedSuperPellets, Stream<Pacman> alivePacmen) {
        System.err.println("Super Pellets Size : " + sortedSuperPellets.size());
        List<Pacman> pacmen = alivePacmen.collect(Collectors.toList());
        for (Pellet superPellet : sortedSuperPellets) {
            if (!superPellet.isStillHere()) {
                continue;
            }
            CollectSuperPellets mission = new CollectSuperPellets(superPellet, pacmen.stream());
            if (mission.build(game)) {
                store.push(mission);
            } else {
                break;
            }
        }
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
                directions.add(Direction.LEFT);
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

    public String shortPrint() {
        return x + ":" + y;
    }

    public boolean isNeighborOf(Coord destination) {
        Floor floor = (Floor) Grid.cells[this.x][this.y];
        return Grid.graph.edgesFrom(floor).keySet().stream().anyMatch(floor1 -> floor1.getCoordinates().equals(destination));
    }
}



enum  CellType {
    WALL, FLOOR;
}



class Wall extends Cell {
    public Wall(Coord coord, CellType wall) {
        super(coord, wall);
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
            case ' ' : return new Floor(coord, CellType.FLOOR);
            case '#': return new Wall(coord, CellType.WALL);

            default: throw new IllegalArgumentException("No Cell With Type of : " + type);
        }
    }
}




class Floor extends Cell {
    private boolean hasCherry;
    private Pellet pellet;
    private boolean hiddenPellet;
    public Floor(Coord coord, CellType floor) {
        super(coord, floor);
        hiddenPellet = true;
    }

    @Override
    public String toString() {
        if (hiddenPellet) {
            return "?";
        }
        if (pellet != null) {
            if (!pellet.isStillHere()) {
                return " ";
            } else {
                return pellet.isSuper() ? "O" : "o";
            }
        }
        return " ";
    }

    public <T extends Floor> LinkedList<Direction> getDirections(final T destination) {
        return this.getCoordinates().getSortedDirection(destination.getCoordinates());
    }

    public boolean hasPellet() {
        return !isHiddenPellet() && pellet != null && pellet.isStillHere();
    }

    public boolean hasSimplePellet() {
        return !isHiddenPellet() && pellet != null && pellet.isStillHere() && !pellet.isSuper();
    }

    public boolean hasCherry() {
        return !isHiddenPellet() && pellet != null && pellet.isStillHere() && pellet.isSuper();
    }

    public void setPellet(Pellet pellet) {
        this.pellet = pellet;
        this.hiddenPellet = false;
        this.hasPellet = true;
        this.hasCherry = pellet.isSuper();
    }

    public boolean isHiddenPellet() {
        return hiddenPellet;
    }

    @Override
    public void noPellet() {
        this.hasPellet = false;
        this.hasCherry = false;
        this.hiddenPellet = false;
        if (this.pellet != null) {
            this.pellet.setStillHere(false);
        }
    }

    public Pellet getPellet() {
        return pellet;
    }
}




class Cell {
    public static final Cell NO_CELL = new Cell(new Coord(1000, 1000), CellType.FLOOR) {
        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public void copy(Cell other) {
            throw new RuntimeException("Invalid cell");
        }

        @Override
        public void setType(CellType type) {
            throw new RuntimeException("Invalid cell");
        }

    };
    public boolean isValid() {
        return true;
    }
    public void copy(Cell source) {
        setType(source.type);
        setHasPellet(source.hasPellet);
    }

    private Coord coordinates;
    private CellType type;
    protected boolean hasPellet;

    public Cell(Coord coordinates, CellType type) {
        this.coordinates = coordinates;
        this.type = type;
    }

    public Cell rightCell(Cell[][] cells) {
        if (coordinates.getX() + 1 < Grid.width) {
            Cell cell = cells[coordinates.getX() + 1][coordinates.getY()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell leftCell(Cell[][] cells) {
        if (coordinates.getX() - 1 >= 0) {
            Cell cell = cells[coordinates.getX() - 1][coordinates.getY()];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell upCell(Cell[][] cells) {
        if (coordinates.getY() - 1 >= 0) {
            Cell cell = cells[coordinates.getX()][coordinates.getY() - 1];
            if (!cell.isWall()) return cell;
        }
        return null;
    }

    public Cell downCell(Cell[][] cells) {
        if (coordinates.getY() + 1 < Grid.height) {
            Cell cell = cells[coordinates.getX()][coordinates.getY() + 1];
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

    public void noPellet() {
        // do noting
    }

    public <T extends Floor> Set<T> getSortedEdgesBasedOnDistanceFromTarget(T target, Set<T> edges) {
        Set<T> sortedEdgesBasedOnDistanceFromTarget = new TreeSet<T>((floor1, floor2) -> {

            double floor1DistanceToTarget = floor1.distance(target);
            double floor2DistanceToTarget = floor2.distance(target);
            return floor1DistanceToTarget < floor2DistanceToTarget ? -1 : 1;
        });

        sortedEdgesBasedOnDistanceFromTarget.addAll(edges);

        return sortedEdgesBasedOnDistanceFromTarget;
    }

    protected <T extends Floor> double distance(T target) {
        return coordinates.euclideanTo(target.getCoordinates());
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public void setHasPellet(boolean hasPellet) {
        this.hasPellet = hasPellet;
    }

    public CellType getType() {
        return type;
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
    public String print() {
        return null;
    }

    @Override
    public boolean areSame(Action action) {
        return false;
    }

    @Override
    public String print(int taskTour) {
        return null;
    }
}




class MoveAction implements Action {

    private Coord destination;
    private int id;
    private Pacman pacman;

    public Coord getTarget() {
        return destination;
    }

    public MoveAction(Coord destination, boolean activateSpeed, int id, Pacman pacman) {
        this.destination = destination;
        this.id = id;
        this.pacman = pacman;
    }

    @Override
    public PacmanType getType() {
        return null;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.MOVE;
    }

    public String print() {
        return getActionType().toString() + " " + id + " " + destination.print() + " MV "+destination.shortPrint();
    }

    @Override
    public boolean areSame(Action action) {
        if (action instanceof MoveAction) {
            Coord destination = ((MoveAction) action).destination;
            return destination.equals(this.destination) || destination.isNeighborOf(this.destination);
        }
        return false;
    }

    @Override
    public String print(int taskTour) {
        return print() + ":" + taskTour;
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

    private int id;

    public SpeedAction(int id) {
        this.id = id;
    }

    @Override
  public ActionType getActionType() {
      return ActionType.SPEED;
  }

    @Override
    public String print() {
        return ActionType.SPEED.toString() + " " + id + " SP";
    }

    @Override
    public boolean areSame(Action action) {
        return false;
    }

    @Override
    public String print(int taskTour) {
        return print() + ":" + taskTour;
    }

    @Override
  public PacmanType getType() {
      return null;
  }
}



interface Action {
    PacmanType getType();
    ActionType getActionType();
    String print();

    boolean areSame(Action action);

    String print(int taskTour);
}




class Game {
    Grid grid;
    private int availableSuperPellets = 4;
    private Gamer me;
    private Gamer opponent;
    private LinkedList<Pellet> pellets;
    private Set<Pellet> superPellets;
    public static int tour;
    private Map<Integer, List<Task>> tasksByTour;

    public Game(Grid grid){
        this.grid = grid;
        tour = 1;
        tasksByTour = new HashMap<>();
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

    public boolean isSuperPelletsAvailable() {
        return availableSuperPellets >= 1;
    }

    public void decreaseSuperPellets() {
        this.availableSuperPellets--;
    }

    public void nextTour() {
        tour++;
    }

    public void addTask(int taskTour, Task task) {
        List<Task> tasks = this.tasksByTour.get(taskTour);
        if (tasks != null) {
            tasks.add(task);
        } else {
            List<Task> taskList = new ArrayList<>();
            taskList.add(task);
            this.tasksByTour.put(taskTour, taskList);
        }
    }

    public Gamer getMe() {
        return me;
    }

    public boolean hasConflict(Action action, int taskTour) {
        List<Task> tasks = this.tasksByTour.get(taskTour);
        if (tasks == null || tasks.isEmpty()) {
            return false;
        }

        return tasks.stream().anyMatch(task -> task.withSameAction(action));
    }

    public LinkedList<Pellet> getPellets() {
        return pellets;
    }
}


class Config {
    public static final Coord[] ADJACENCY = { new Coord(-1, 0), new Coord(1, 0), new Coord(0, -1), new Coord(0, 1) };
    public static boolean MAP_WRAPS = true;
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
    private Grid grid;

    public Gamer(Grid grid) {
        pacmen = new ArrayList<>();
        this.grid = grid;
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


    public void setScore(int score, Game game) {
        if ((score - this.score) > 5) {
            game.decreaseSuperPellets();
        }
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
                pacman.setAction(new MoveAction(pellet.getCoord(), false, pacman.getId(), pacman));
            }
        }
        pacmen.stream().filter(Pacman::available).forEach(pacman -> pacman.doAction(pellets, superPellets, grid));

        List<String> actionsList = new ArrayList<>();
        this.pacmen.forEach(pacman -> {
            if (pacman.hasMission()) {
                actionsList.add(pacman.printMissionTask());
            } else {
                actionsList.add(pacman.printAction());
            }

        });


        //simulateMission();

        return String.join(" | ", actionsList);
    }

    private void simulateMission() {
        List<String> actionsList = new ArrayList<>();

        this.pacmen.forEach(pacman -> {
            if (pacman.hasMission()) {
                actionsList.add(pacman.printMissionTask());
            }
        });

        //System.err.println("Simulate Mission : " + String.join(" | ", actionsList));
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

    public void updatePellets(Map<Coord, Pellet> newVisiblePellets, Set<Pellet> sortedSuperPellets, Cell[][] cells) {
       Set<Coord> visibleCoords = getAllVisibleCoords(getAlivePacmen(), cells);
        for (Coord visibleCoord : visibleCoords) {
            Pellet pellet = newVisiblePellets.get(visibleCoord);
            if (pellet == null) {
                Cell cell = cells[visibleCoord.x][visibleCoord.y];
                cell.noPellet();
            }
        }
        if (!sortedSuperPellets.isEmpty()) {
            List<Pellet> newSortingForSuperPellets = new ArrayList<>(sortedSuperPellets.size());
            Iterator<Pellet> iterator = sortedSuperPellets.iterator();
            while (iterator.hasNext()) {
                Pellet superPellet = iterator.next();
                Coord coord = superPellet.getCoord();
                Pellet pellet = newVisiblePellets.get(coord);
                if (pellet == null) {
                    cells[coord.x][coord.y].noPellet();
                    iterator.remove();
                } else {
                    newSortingForSuperPellets.add(pellet);
                }
            }
            sortedSuperPellets.clear();
            sortedSuperPellets.addAll(newSortingForSuperPellets);
        }
    }

    private Set<Coord> getAllVisibleCoords(Stream<Pacman> alivePacmen, Cell[][] cells) {
        Set<Coord> visibleCoords = new HashSet<>();
        alivePacmen.forEach(pacman -> {
            pacman.myVisibleCells(cells).forEach(cell -> visibleCoords.add(cell.getCoordinates()));
        });
        return visibleCoords;
    }

    public List<Floor> findOptimalPathFromTo(Coord position, Coord coord) {
        Floor from = (Floor) grid.cells[position.x][position.y];
        Floor to = (Floor) grid.cells[coord.x][coord.y];
        return grid.findOptimalPath.getOptimalPath(from, to, null);
        /*return grid.pathFinder.from(position).to(coord).findPath().path.stream()
                .map(co -> (Floor) grid.cells[co.x][co.y]).collect(Collectors.toList());*/
    }

    public List<Floor> findOptimalPathFromTo(Coord previousPos, Coord coord, Floor exception) {
        Floor from = (Floor) grid.cells[previousPos.x][previousPos.y];
        Floor to = (Floor) grid.cells[coord.x][coord.y];
        return grid.findOptimalPath.getOptimalPath(from, to, exception);
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
            System.err.println(row);
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
        Game game = new Game(grid);
        Gamer me = new Gamer(grid);
        game.setMe(me);
        Gamer opponent = new Gamer(grid);
        game.setOpponent(opponent);

        Map<String, Pacman> pacmanMap = new HashMap<>();


        // Start First Tour -------------------------------------------------------------------------------------------
        int tour = 1;
        long startTime = System.nanoTime();
        setScores(in, me, opponent, game);

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
            cells[x][y].noPellet();
        }

        LinkedList<Pellet> pellets = new LinkedList<>();
        Set<Pellet> superPellets = new HashSet<>();
        Map<Coord, Pellet> newVisiblePellets = new HashMap<>();

        Set<Pellet> sortedSuperPellets = new TreeSet<Pellet>((p1, p2) -> {
            Supplier<Stream<Pacman>> alivePacmen = () -> me.getAlivePacmen();
            double p1NearestDistanceToAPacman = p1.getNearestDistanceToAPacman(alivePacmen.get());
            double p2NearestDistanceToAPacman = p2.getNearestDistanceToAPacman(alivePacmen.get());

            return p1NearestDistanceToAPacman < p2NearestDistanceToAPacman ? -1 : 1;
        });

        Set<Pellet> sortedPellets = new TreeSet<Pellet>((p1, p2) -> {
            Supplier<Stream<Pacman>> alivePacmen = () -> me.getAlivePacmen();
            double p1NearestDistanceToAPacman = p1.getNearestDistanceToAPacman(alivePacmen.get());
            double p2NearestDistanceToAPacman = p2.getNearestDistanceToAPacman(alivePacmen.get());

            return p1NearestDistanceToAPacman < p2NearestDistanceToAPacman ? -1 : 1;
        });

        int visiblePelletCount = in.nextInt(); // all pellets in sight
        for (int i = 0; i < visiblePelletCount; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            int value = in.nextInt(); // amount of points this pellet is worth

            Coord coord = new Coord(x, y);
            Pellet pellet = new Pellet(coord, value);
            if (value == 10) {
                //superPellets.add(pellet);
                sortedSuperPellets.add(pellet);
                System.err.println("super : " + coord);
            } else {
                pellets.add(pellet);
            }
            ((Floor) cells[x][y]).setPellet(pellet);
            newVisiblePellets.put(coord, pellet);
            sortedPellets.add(pellet);
        }

        MissionEngine ME = new MissionEngine(game);

        //printSuperPellets(sortedSuperPellets);

        me.updatePellets(newVisiblePellets, new HashSet<>(), cells);
        game.setPellets(pellets);
        game.setSuperPellets(superPellets);

            grid.printGrid();

        ME.collectMissions(sortedSuperPellets, me.getAlivePacmen());
        System.out.println(game.play());
        printEndTime(startTime, "First Tour");
        // Start First Tour -------------------------------------------------------------------------------------------



        // game loop after first tour
        while (true) {
            startTime = System.nanoTime();
            tour++;
            game.nextTour();
            setScores(in, me, opponent, game);

            visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours

                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues

                String key = pacId + "-" + mine;
                System.err.println("pacman : " + key + " Has ability countdown : : " + abilityCooldown);
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

                cells[x][y].noPellet();
            }
            setDeadPacmen(pacmanMap.values(), tour);
            visiblePelletCount = in.nextInt(); // all pellets in sight

            //pellets.clear();
            superPellets.clear();
            newVisiblePellets.clear();
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth

                Coord coord = new Coord(x, y);
                Pellet pellet = new Pellet(coord, value);
                if (value != 10) {
                    pellets.add(pellet);
                } else {

                }
                ((Floor) cells[x][y]).setPellet(pellet);
                newVisiblePellets.put(coord, pellet);
            }
            me.updatePellets(newVisiblePellets, sortedSuperPellets, cells);
            game.setPellets(pellets);
            game.setSuperPellets(superPellets);

            grid.printGrid();
            printEndTime(startTime, "0 - Tour number ("+tour +")");
            List<Pacman> alivePacmen = me.getAlivePacmen().collect(Collectors.toList());
            if (!sortedSuperPellets.isEmpty()) {
                ME.collectMissions(sortedSuperPellets, alivePacmen.stream());
            }
            printEndTime(startTime, "1 - Tour number ("+tour +")");
            sortedPellets.clear();
            printEndTime(startTime, "3 - Tour number ("+tour +")");
            sortedPellets.addAll(game.getPellets());
            printEndTime(startTime, "4 - Tour number ("+tour +")");
            ME.collectMissions(sortedPellets, alivePacmen.stream());
            printEndTime(startTime, "5 - Tour number ("+tour +")");
            System.out.println(game.play());
            printEndTime(startTime, "Tour number ("+tour +")");
        }
    }

    private static void printSuperPellets(Set<Pellet> sortedSuperPellets) {
        System.err.println("sortedSuperPellets : ");
        for (Pellet sortedSuperPellet : sortedSuperPellets) {
            System.err.print(sortedSuperPellet + " ");
        }
        System.err.println();
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

    private static void setScores(Scanner in, Gamer me, Gamer opponent, Game game) {
        int myScore = in.nextInt();
        me.setScore(myScore, game);
        int opponentScore = in.nextInt();
        opponent.setScore(opponentScore, game);
    }
}