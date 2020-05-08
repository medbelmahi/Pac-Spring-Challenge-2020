
import java.util.*;
import java.io.*;
import java.math.*;
import java.util.stream.Stream;



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




class Grid {
    int width, height;
    Map<Coord, Cell> cellsMap = new HashMap<>();
    Cell[][] cells;

    public Grid(Cell[][] cells, int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = cells;
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
}



class Wall extends Cell {
    @Override
    public String toString() {
        return "#";
    }
}


class CellPrototype {
    public static Cell getCell(char type) {
        switch (type) {
            case ' ' : return new Floor();
            case '#': return new Wall();

            default: throw new IllegalArgumentException("No Cell With Type of : " + type);
        }
    }
}


class Floor extends Cell {
    @Override
    public String toString() {
        return " ";
    }
}


class Cell {
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
    };

    public PacmanType getType();
    public ActionType getActionType();
}




class Game {
    Grid grid;

    private Gamer me;
    private Gamer opponent;
    private LinkedList<Pellet> pellets;
    private LinkedList<Pellet> superPellets;

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

    public void play() {
        me.play(pellets, superPellets);
        /*Pellet pellet = pellets.pop();
        return "MOVE 0 " + pellet.getCoord().print();*/
    }

    public void setPellets(LinkedList<Pellet> pellets) {
        this.pellets = pellets;
    }

    public void setSuperPellets(LinkedList<Pellet> superPellets) {
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

    public void play(LinkedList<Pellet> pellets, LinkedList<Pellet> superPellets) {
        for (Pacman pacman : pacmen) {
            System.out.println(pacman.doAction(pellets, superPellets));
        }
    }
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
            int y = i;
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall

            char[] cellsInput = row.toCharArray();
            for (int x = 0; x < cellsInput.length; x++) {
                cells[x][y] = CellPrototype.getCell(cellsInput[x]);
            }
        }

        Grid grid = new Grid(cells, width, height);

        grid.printGrid();
        Game game = new Game(grid);
        Gamer me = new Gamer();
        game.setMe(me);
        Gamer opponent = new Gamer();
        game.setOpponent(opponent);

        Map<Integer, Pacman> pacmanMap = new HashMap<>();
        // Start First Tour
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
                pacman = new Pacman(pacId, 0, me, new Coord(x, y), PacmanType.NEUTRAL);
            } else {
                pacman = new Pacman(pacId, 0, opponent, new Coord(x, y), PacmanType.NEUTRAL);
            }
            pacmanMap.put(pacId, pacman);
        }

        LinkedList<Pellet> pellets = new LinkedList<>();
        LinkedList<Pellet> superPellets = new LinkedList<>();
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
        game.play(); // MOVE <pacId> <x> <y>
        // Start First Tour



        // game loop after first tour
        while (true) {
            setScores(in, me, opponent);

            visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                Pacman pacman = pacmanMap.get(pacId);
                boolean mine = in.nextInt() != 0; // true if this pac is yours
                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                pacman.setPosition(new Coord(x, y));
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues
            }
            visiblePelletCount = in.nextInt(); // all pellets in sight

            pellets = new LinkedList<>();
            superPellets = new LinkedList<>();
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

            game.play(); // MOVE <pacId> <x> <y>
        }
    }

    private static void setScores(Scanner in, Gamer me, Gamer opponent) {
        int myScore = in.nextInt();
        me.setScore(myScore);
        int opponentScore = in.nextInt();
        opponent.setScore(opponentScore);
    }
}