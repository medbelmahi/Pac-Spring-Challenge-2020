package codingame.pac;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class Game {
    private List<PacMan> pacmen;

    private Stream<PacMan> otherPacmen(PacMan pac) {
        return otherPacmen(pac, pacmen);
    }
    private Stream<PacMan> otherPacmen(PacMan pac, Collection<PacMan> collection) {
        return collection.stream().filter(p -> p != pac);
    }
}
