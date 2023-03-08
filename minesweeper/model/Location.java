package minesweeper.model;

public class Location implements Comparable<Location> {
    private int row;
    private int col;
    
    public Location(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    @Override
    public String toString() {
        return row + ", " + col;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public int compareTo(Location o) {
        if (this.row != o.getRow()) { // compare rows first
            return this.row - o.getRow();
        }

        return this.col - o.getCol(); // if the rows are equal, compare columns
    }
}