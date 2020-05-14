package codingame.pac.mission;

import codingame.Pellet;
import codingame.pac.Game;
import codingame.pac.agent.Pacman;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mohamed BELMAHI created on 11/05/2020
 */
public class MissionEngine {
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
