package minesweeper.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import backtracker.Backtracker;
import backtracker.Configuration;

public class Minefinder implements Configuration{
    private List<Location> movelist;
    private TreeMap<Location, Boolean> covers;

    private Minesweeper game;

    public Minefinder(Minesweeper game){
        this.game = game;
        this.covers = game.getCovers();
        this.movelist = new ArrayList<>();
    }

    public Minefinder(Minesweeper game, List<Location> movelist){
        this.game = game;
        this.covers = game.getCovers();

        this.movelist = movelist;
    }

    @Override
    public Collection<Configuration> getSuccessors() {
        
        List<Configuration> successors = new ArrayList<>();

        if (this.game.getGameState() != GameState.LOST) {

            for (Location loc : covers.keySet()){
                if (covers.get(loc)){
                    Minesweeper copy = new Minesweeper(this.game);
                    List<Location> movelistcopy = new ArrayList<>();

                    for (Location location : movelist){
                        movelistcopy.add(location);
                    }

                    try {
                        copy.makeSelection(loc);
                        movelistcopy.add(loc);

                        Minefinder successor = new Minefinder(copy, movelistcopy);
                        successors.add(successor);
                    } catch (MinesweeperException e) {
                        System.out.println(e);
                    }
                }
            }
        }

        return successors;
    }

    /**
     * Config is valid if moveList only contains unique values
     * @return true if config is valid
     */
    @Override
    public boolean isValid() {
        Set<Location> set = new HashSet<>(this.movelist);

        return set.size() == this.movelist.size();
    }

    public List<Location> getMoveList(){
        return movelist;
    }

    @Override
    public boolean isGoal() {
        return game.getGameState() == GameState.WON;
    }

    @Override
    public String toString(){
        return this.movelist + "\n" + this.game;
    }

    /**
     * @param game an instance of a Minesweeper game
     * @return winning Minefinder config (null if no solution)
     */
    public static Minefinder solver(Minesweeper game){
        Minefinder finder = new Minefinder(game);
        Backtracker backtracker = new Backtracker(false);
        if (backtracker.solve(finder) != null){
            return (Minefinder)backtracker.solve(finder);
        }
        else{
            System.out.println("no solution");
        }

        return null;
    }
    
    public static void main(String[] args) throws MinesweeperException {
        Minesweeper game = new Minesweeper(3, 3, 2);
        solver(game);    
    }
}
