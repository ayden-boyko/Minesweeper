package minesweeper.model;

public interface MinesweeperObserver {

    public void cellUpdated(Location location);
    
}
