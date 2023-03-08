package minesweeper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Random;
import java.util.Set;

public class Minesweeper {
    public static final char MINE = 'M';
    public static final char COVERED = '-';

    private static final int IS_MINE = -1;
    private static final boolean IS_COVERED = true;

    private final Random rand;

    private char[][] board;
    private int moveCount;
    private TreeMap<Location, Integer> values; 
    private TreeMap<Location, Boolean> covers; // stores true if location is covered and false if it is not covered
    private GameState state;

    // Keeping track of values in case of reset
    private final int ROWS;
    private final int COLS;
    private final int MINECOUNT;

    private MinesweeperObserver observer; // observer to notify

    public Minesweeper(int rows, int cols, int minecount) throws MinesweeperException {
        //sets board to 2d char array
        this.board = new char [rows][cols];
        this.values = new TreeMap<>();
        this.covers = new TreeMap<>();
        this.moveCount = 0;
        this.state = GameState.NOT_STARTED;

        // values in case of reset
        this.ROWS = rows;
        this.COLS = cols;
        this.MINECOUNT = minecount;

        rand = new Random();

        if (rows <= 2 || cols <= 2){
            throw new MinesweeperException("invalid # of tiles");
        }
        if (minecount > rows * cols){
            throw new MinesweeperException("MineCount greater than # of tiles");
        }

        //for loop visits each coord in array
        int x;
        int y;
        int row, col;

        Set<Location> mineLocations = new TreeSet<>();

        // creating a set for mine locations
        while (mineLocations.size() < minecount) {
            row = rand.nextInt(rows);
            col = rand.nextInt(cols);

            Location loc = new Location(row, col);

            mineLocations.add(loc);
        }

        //put locations whether or not they are a mine in map, if not a mine, value = 0;
        //then iterate through map again and adjust values to match how many neighbors are mines.
        for(x = 0; x < rows ; x++){
            for(y = 0; y < cols; y++){
                Location temp = new Location(x, y);
                if (mineLocations.contains(temp)){
                    values.put(temp, IS_MINE);
                }
                else {
                    values.put(temp, 0);
                }
                covers.put(temp, IS_COVERED);
                board[x][y] = COVERED;
            }
        }

        //iterates through the list and counts how many of the neighbors are mines, stores value, next to the key (location)

        for(x = 0; x < rows ; x++){
            for(y = 0; y < cols; y++){
                Location temp = new Location(x, y);
                int mines = 0;
                if (values.get(temp) != IS_MINE){
                    if (values.get(new Location(x+1, y)) != null){
                        if (values.get(new Location(x+1, y)) == IS_MINE){
                            mines++;
                        }   
                    }
                    if (values.get(new Location(x, y+1)) != null){
                        if (values.get(new Location(x, y+1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x+1, y+1)) != null){
                        if (values.get(new Location(x+1, y+1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x+1, y-1)) != null){
                        if (values.get(new Location(x+1, y-1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x-1, y+1)) != null){
                        if (values.get(new Location(x-1, y+1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x-1, y)) != null){
                        if (values.get(new Location(x-1, y)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x, y-1)) != null){
                        if (values.get(new Location(x, y-1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x-1, y-1)) != null){
                        if (values.get(new Location(x-1, y-1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    values.put(temp, mines);
                } 
            }
        }
    }

    public Minesweeper(Minesweeper minesweeper){
        this.board = new char[minesweeper.ROWS][minesweeper.COLS];

        char[][] minesweeperBoard = minesweeper.getBoard();

        for (int row = 0; row < minesweeper.ROWS; row++) {
            for (int col = 0; col < minesweeper.COLS; col++) {
                this.board[row][col] = minesweeperBoard[row][col];
            }
        }

        this.values = new TreeMap<>();

        for (Location loc: minesweeper.values.keySet()) {
            values.put(loc, minesweeper.values.get(loc));
        }

        this.covers = new TreeMap<>();

        for (Location loc: minesweeper.covers.keySet()) {
            covers.put(loc, minesweeper.covers.get(loc));
        }

        this.moveCount = minesweeper.getMoveCount();
        this.state = minesweeper.getGameState();

        // values in case of reset
        this.ROWS = minesweeper.ROWS;
        this.COLS = minesweeper.COLS;
        this.MINECOUNT = minesweeper.MINECOUNT;

        rand = new Random();
    }

    public char getSymbol(Location location) throws MinesweeperException {
        return board[location.getRow()][location.getCol()];
    }

    private void notifyObserver(Location location){
        if (observer != null) {
            observer.cellUpdated(location);
        }
    }

    public TreeMap<Location, Boolean> getCovers(){
        return covers;
    }

    public void getNeighbors(Location location) throws MinesweeperException {
        int x = location.getRow();
        int y = location.getCol();

        // Reveal neighbors if this is a blank tile
        if (values.get(location) == 0) {
            if (values.get(new Location(x+1, y)) != null && values.get(new Location(x+1, y)) != IS_MINE) {
                if (covers.get(new Location(x + 1, y))){
                    makeSelection(new Location(x + 1, y));
                    moveCount--;
                }
            }
            if (values.get(new Location(x, y+1)) != null && values.get(new Location(x, y+1)) != IS_MINE){
                if (covers.get(new Location(x, y + 1))){
                    makeSelection(new Location(x, y + 1));
                    moveCount--;
                }
            }
            if (values.get(new Location(x+1, y+1)) != null && values.get(new Location(x+1, y+1)) != IS_MINE){
                if (covers.get(new Location(x + 1, y + 1))){
                    makeSelection(new Location(x + 1, y + 1));
                    moveCount--;
                }
            }
            if (values.get(new Location(x+1, y-1)) != null && values.get(new Location(x+1, y-1)) != IS_MINE){
                if (covers.get(new Location(x + 1, y - 1))){
                    makeSelection(new Location(x+1, y-1));
                    moveCount--;
                }
            }
            if (values.get(new Location(x-1, y+1)) != null && values.get(new Location(x-1, y+1)) != IS_MINE){
                if (covers.get(new Location(x - 1, y + 1))){
                    makeSelection(new Location(x-1, y+1));
                    moveCount--;
                }
            }
            if (values.get(new Location(x-1, y)) != null && values.get(new Location(x-1, y)) != IS_MINE){
                if (covers.get(new Location(x - 1, y))){
                    makeSelection(new Location(x-1, y));
                    moveCount--;
                }
            }
            if (values.get(new Location(x, y-1)) != null && values.get(new Location(x, y-1)) != IS_MINE){
                if (covers.get(new Location(x, y - 1))){
                    makeSelection(new Location(x, y-1));
                    moveCount--;
                }
            }
            if (values.get(new Location(x-1, y-1)) != null && values.get(new Location(x-1, y-1)) != IS_MINE){
                if (covers.get(new Location(x - 1, y - 1))){
                    makeSelection(new Location(x-1, y-1));
                    moveCount--;
                }
            }
        }
    }

    public void makeSelection(Location location) throws MinesweeperException {

        if (this.state == GameState.NOT_STARTED) {
            this.state = GameState.IN_PROGRESS;
        }

        // Throws exception for an invalid location
        if (covers.get(location) == null || !covers.get(location)) {
            throw new MinesweeperException("Invalid Location");
        }

        // Checks to see if the selected spot has a mine
        else if (values.get(location) == IS_MINE) {
            this.state = GameState.LOST;
            revealAllMines();
            moveCount++;
        }
        else if (covers.get(location)) {
            board[location.getRow()][location.getCol()] = (char)(values.get(location) + '0'); // converts int to char
            covers.put(location, !IS_COVERED);
            
            moveCount++;

            if (values.get(location) == 0) {
                getNeighbors(location);
                notifyObserver(location);
            }
        }
        if (getPossibleSelections().size() == 0) {
            this.state = GameState.WON;
            revealAllMines();
        }

        notifyObserver(location);
    }

    /**
     * Reveals all the mines on the board
     */
    private void revealAllMines() {
        for (Location l: values.keySet()) {
            if (values.get(l) == IS_MINE) {
                board[l.getRow()][l.getCol()] = MINE;
                notifyObserver(l);
            }
        }
    }

    public int getMoveCount() {
        return moveCount;
    }
    
    public GameState getGameState() {
        return state;
    }

    /**
     * @return a List of locations that are covered and safe
     */
    public List<Location> getPossibleSelections() {
        boolean covered;
        boolean hasMine;

        ArrayList<Location> possibleSelections = new ArrayList<>();

        // iterate through locations in the covers map
        for (Location location: covers.keySet()) {
            covered = covers.get(location); // true if location is covered
            hasMine = values.get(location) == IS_MINE; // true if location has mine

            // if location is covered and does not have a mine
            if (covered && !hasMine) {
                possibleSelections.add(location); // add the location to the possible selections
            }
        }

        return possibleSelections;
    }

    public boolean isCovered(Location location) throws MinesweeperException{
        return covers.get(location);
    }

    public char[][] getBoard() {
        return board;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0 ; i < board.length; i++) {
            builder.append((i + 1) + " ");
            for(int j = 0; j < board[i].length; j++) {
                builder.append(board[i][j]);
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * @param observer the observer to notify
     */
    public void register(MinesweeperObserver observer) {
        this.observer = observer;
    }

    /**
     * Resets the game
     */
    public void reset() {
        //sets board to 2d char array
        this.board = new char [ROWS][COLS];
        this.values = new TreeMap<>();
        this.covers = new TreeMap<>();
        this.moveCount = 0;
        this.state = GameState.NOT_STARTED;

        //for loop visits each coord in array
        int x;
        int y;
        int row, col;

        Set<Location> mineLocations = new TreeSet<>();

        // creating a set for mine locations
        while (mineLocations.size() < MINECOUNT) {
            row = rand.nextInt(ROWS);
            col = rand.nextInt(COLS);

            Location loc = new Location(row, col);

            mineLocations.add(loc);
        }

        //put locations whether or not they are a mine in map, if not a mine, value = 0;
        //then iterate through map again and adjust values to match how many neighbors are mines.
        for(x = 0; x < ROWS ; x++){
            for(y = 0; y < COLS; y++){
                Location temp = new Location(x, y);
                if (mineLocations.contains(temp)){
                    values.put(temp, IS_MINE);
                }
                else {
                    values.put(temp, 0);
                }
                covers.put(temp, IS_COVERED);
                board[x][y] = COVERED;
            }
        }

        //iterates through the list and counts how many of the neighbors are mines, stores value, next to the key (location)
        for(x = 0; x < ROWS; x++){
            for(y = 0; y < COLS; y++){
                Location temp = new Location(x, y);
                int mines = 0;
                if (values.get(temp) != IS_MINE){
                    if (values.get(new Location(x+1, y)) != null){
                        if (values.get(new Location(x+1, y)) == IS_MINE){
                            mines++;
                        }   
                    }
                    if (values.get(new Location(x, y+1)) != null){
                        if (values.get(new Location(x, y+1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x+1, y+1)) != null){
                        if (values.get(new Location(x+1, y+1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x+1, y-1)) != null){
                        if (values.get(new Location(x+1, y-1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x-1, y+1)) != null){
                        if (values.get(new Location(x-1, y+1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x-1, y)) != null){
                        if (values.get(new Location(x-1, y)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x, y-1)) != null){
                        if (values.get(new Location(x, y-1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    if (values.get(new Location(x-1, y-1)) != null){
                        if (values.get(new Location(x-1, y-1)) == IS_MINE){
                            mines++;
                        } 
                    }
                    values.put(temp, mines);
                } 
            }
        } 
    }
}
   
