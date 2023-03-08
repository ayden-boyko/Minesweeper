package minesweeper.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class MinesweeperTest {

    @Test
    public void testMinesweeperInit() {
        try {
            Minesweeper game = new Minesweeper(4, 5, 5);
            
            char[][] expected = new char[4][5];

            for (char[] row: expected) {
                Arrays.fill(row, Minesweeper.COVERED);
            }

            char[][] actual = game.getBoard();

            assertArrayEquals(expected, actual);
        }
        catch (MinesweeperException e) {
            System.out.println(e);
        }
    }

    @Test
    public void testGetPossibleSelections() {
        try {
            Minesweeper game = new Minesweeper(5, 5, 4);

            int expected = 21;        
            int actual = game.getPossibleSelections().size();

            assertEquals(expected, actual);
        }
        catch (MinesweeperException e) {
            System.out.println(e);
        }
    }

    @Test
    public void testMakeSelection() {
        try {
            Minesweeper game = new Minesweeper(5, 4, 5);
            game.makeSelection(new Location(0, 0));
            game.makeSelection(new Location(4, 3));

            int expected = 2;
            int actual = game.getMoveCount();

            assertEquals(expected, actual);
        }
        catch (MinesweeperException e) {
            System.out.println(e);
        }
    }
}
