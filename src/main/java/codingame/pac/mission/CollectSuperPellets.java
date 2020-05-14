package codingame.pac.mission;

import codingame.Pellet;
import codingame.pac.Coord;
import codingame.pac.Game;
import codingame.pac.action.MoveAction;
import codingame.pac.action.SpeedAction;
import codingame.pac.agent.Pacman;
import codingame.pac.cell.Floor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mohamed BELMAHI created on 11/05/2020
 */
public class CollectSuperPellets extends Mission {
    private Pellet pellet;
    private Stream<Pacman> pacmenStream;

    public CollectSuperPellets(Pellet superPellet, Stream<Pacman> pacmanStream) {
        pellet = superPellet;
        pacmenStream = pacmanStream;
        this.tasks = new LinkedList<>();
    }
    @Override
    public boolean isAchievable() {
        return false;
    }

    @Override
    public boolean isRelevant() {
        return this.pellet.isStillHere();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean build(Game game) {
        List<Pacman> pacmen = this.pacmenStream.filter(p -> !p.hasMission()).collect(Collectors.toList());
        if (pacmen.isEmpty()) {
            return false;
        }

        Pacman pacman = pellet.getNearestPacman(pacmen.stream());

        int currentTour = Game.tour;

        //tasks.push(new Task(new SpeedAction(pacman.getId())));
        List<Floor> optimalPath = pacman.getOptimalPathTo(pellet.getCoord());
        int size = optimalPath.size();
        Iterator<Floor> iterator = optimalPath.iterator();
        Coord previousPos = pacman.getPosition();
        if (size > 2) {
            int taskIndex = 1;
            int speedIndex = 1;
            int index = 0;

            int speedTurnsLeft = 10;
            int abilityCountdown = 20;
            if(pacman.canSpeedUp()) {
                tasks.push(new Task(new SpeedAction(pacman.getId()), currentTour));
            }else {
                speedTurnsLeft = pacman.getSpeedTurnsLeft() * 2;
                abilityCountdown = pacman.getAbilityCooldown() * 2;
            }


            while (iterator.hasNext()) {
                currentTour++;
                Floor floor = iterator.next();

                Task currentTask = null;
                if (index == (size-1)) {
                    currentTask = new Task(new MoveAction(floor.getCoordinates(), true, pacman.getId(), pacman), currentTour);
                }
                if (taskIndex % 2 == 0) {
                    currentTask = new Task(new MoveAction(floor.getCoordinates(), true, pacman.getId(), pacman), currentTour);
                } else if(speedTurnsLeft <= 0) {
                    currentTask = new Task(new MoveAction(floor.getCoordinates(), true, pacman.getId(), pacman), currentTour);
                }

                if (abilityCountdown <= 0) {
                    currentTask = new Task(new SpeedAction(pacman.getId()), currentTour);
                    speedTurnsLeft = 10;
                    abilityCountdown = 20;
                }


                if (currentTask == null) {
                    taskIndex++;
                    speedIndex++;
                    index++;
                    speedTurnsLeft--;
                    abilityCountdown--;
                    continue;
                }

                if(!currentTask.hasConflict(game)) {
                    tasks.push(currentTask);
                    game.addTask(currentTour, currentTask);
                    previousPos = floor.getCoordinates();
                }else {
                    List<Floor> newOptimalPath = game.getMe().findOptimalPathFromTo(previousPos, pellet.getCoord(), floor);
                    iterator = newOptimalPath.iterator();
                    currentTour--;
                    continue;
                }

                taskIndex++;
                speedIndex++;
                index++;
                speedTurnsLeft--;
                abilityCountdown--;
            }
        } else {
            while (iterator.hasNext()) {
                Floor next = iterator.next();
                Task task = new Task(new MoveAction(next.getCoordinates(), true, pacman.getId(), pacman), currentTour);
                if(!task.hasConflict(game)) {
                    tasks.push(task);
                    game.addTask(currentTour, task);
                    currentTour++;
                    previousPos=next.getCoordinates();
                }else {
                    List<Floor> newOptimalPath = game.getMe().findOptimalPathFromTo(previousPos, pellet.getCoord(), next);
                    iterator = newOptimalPath.iterator();
                }
            }
        }
        this.hero = pacman;
        this.hero.setMission(this);

        return true;
    }

    @Override
    public String toString() {
        return "CollectSuperPellets{" +
                "hero=" + hero.getId() +
                ", pellet=" + pellet.toString() +"}";
    }
}
