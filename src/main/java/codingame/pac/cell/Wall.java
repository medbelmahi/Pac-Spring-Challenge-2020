package codingame.pac.cell;

/**
 * Mohamed BELMAHI created on 14/05/2020
 */
public class Wall extends Cell {
    public Wall(Coord coord) {
        super(coord);
    }

    @Override
    public boolean isWall() {
        return true;
    }
}
