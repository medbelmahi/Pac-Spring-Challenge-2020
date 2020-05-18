package codingame.pac.cell;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class CellFactory {
    public static Cell createCell(char inputType, Coord coord) {
        switch (inputType) {
            case ' ': return new Floor(coord);
            case '#': return new Wall(coord);
            default: throw new IllegalArgumentException("No Cell With this type ");
        }
    }
}
