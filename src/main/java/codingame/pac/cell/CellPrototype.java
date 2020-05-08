package codingame.pac.cell;

public class CellPrototype {
    public static Cell getCell(char type) {
        switch (type) {
            case ' ' : return new Floor();
            case '#': return new Wall();

            default: throw new IllegalArgumentException("No Cell With Type of : " + type);
        }
    }
}
