package codingame.pac;

import codingame.Pellet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Gamer {
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
