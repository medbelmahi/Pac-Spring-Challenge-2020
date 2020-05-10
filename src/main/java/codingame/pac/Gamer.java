package codingame.pac;

import codingame.Pellet;
import codingame.pac.action.MoveAction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
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
        String actions = "";

        for (int i = 0; i < pacmen.size(); i++) {
            if (!superPellets.isEmpty()) {
                Pellet pellet = superPellets.pop();
                Pacman pacman = getNearestPacman(pellet, pacmen.stream().filter(p -> !p.hasAction()).collect(Collectors.toList()));
                if (pacman == null) {
                    break;
                }
                pacman.setAction(new MoveAction(pellet.getCoord(), false));
            }else {
                pacmen.stream().filter(p -> !p.hasAction()).forEach(pacman -> pacman.doAction(pellets, superPellets));
                break;
            }
        }

        for (Pacman pacman : pacmen) {
            if (pacman.getId() == 0) {
                actions += pacman.printAction();
            } else {
                actions += "|" + pacman.printAction();
            }
        }
        System.out.println(actions);
    }

    private Pacman getNearestPacman(Pellet pellet, List<Pacman> pacmen) {
        Pacman target = null;
        double minDistance = Integer.MAX_VALUE;
        for (Pacman pacman : pacmen) {
            double distance = pacman.distance(pellet.getCoord());
            if (minDistance > distance) {
                minDistance = distance;
                target = pacman;
            }
        }
        return target;
    }
}
