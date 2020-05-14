package codingame.pac;

import codingame.Pellet;
import codingame.pac.action.Action;
import codingame.pac.mission.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Game {
    Grid grid;
    private int availableSuperPellets = 4;
    private Gamer me;
    private Gamer opponent;
    private LinkedList<Pellet> pellets;
    private Set<Pellet> superPellets;
    public static int tour;
    private Map<Integer, List<Task>> tasksByTour;

    public Game(Grid grid){
        this.grid = grid;
        tour = 1;
        tasksByTour = new HashMap<>();
    }


    public Grid getGrid() {
        return grid;
    }

    public void setMe(Gamer me) {
        this.me = me;
    }

    public void setOpponent(Gamer opponent) {
        this.opponent = opponent;
    }

    public String play() {
        return me.play(pellets, superPellets, grid);
    }

    public void setPellets(LinkedList<Pellet> pellets) {
        this.pellets = pellets;
    }

    public void setSuperPellets(Set<Pellet> superPellets) {
        this.superPellets = superPellets;
    }

    public boolean isSuperPelletsAvailable() {
        return availableSuperPellets >= 1;
    }

    public void decreaseSuperPellets() {
        this.availableSuperPellets--;
    }

    public void nextTour() {
        tour++;
    }

    public void addTask(int taskTour, Task task) {
        List<Task> tasks = this.tasksByTour.get(taskTour);
        if (tasks != null) {
            tasks.add(task);
        } else {
            List<Task> taskList = new ArrayList<>();
            taskList.add(task);
            this.tasksByTour.put(taskTour, taskList);
        }
    }

    public Gamer getMe() {
        return me;
    }

    public boolean hasConflict(Action action, int taskTour) {
        List<Task> tasks = this.tasksByTour.get(taskTour);
        if (tasks == null || tasks.isEmpty()) {
            return false;
        }

        return tasks.stream().anyMatch(task -> task.withSameAction(action));
    }

    public LinkedList<Pellet> getPellets() {
        return pellets;
    }
}
