package codingame.pac;

import codingame.pac.cell.Coord;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class Config {
    public static final Coord[] ADJACENCY = { new Coord(-1, 0), new Coord(1, 0), new Coord(0, -1), new Coord(0, 1) };
    public static boolean MAP_WRAPS = true;
    public static final int ID_ROCK = 0;
    public static final int ID_PAPER = 1;
    public static final int ID_SCISSORS = 2;
    public static final int ID_NEUTRAL = -1;
}
