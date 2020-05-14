package codingame;

import codingame.pac.Coord;
import codingame.pac.agent.Pacman;

import java.util.Iterator;
import java.util.stream.Stream;

public class Pellet {
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
