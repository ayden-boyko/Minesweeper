package minesweeper.View;

import java.util.List;
import java.util.Scanner;

import minesweeper.model.GameState;
import minesweeper.model.Location;
import minesweeper.model.Minefinder;
import minesweeper.model.Minesweeper;
import minesweeper.model.MinesweeperException;


/**
 * A simple command line interface for playing a game of MineSweeper.
 */
public class MineSweeperCLI {
    
    public static void main(String[] args) throws MinesweeperException {
        try(Scanner scanner = new Scanner(System.in)) {
            boolean sentinel = true;
            System.out.print("Mines: ");
            String[] command = scanner.nextLine().split(" ");
            help();
            int mines = Integer.parseInt(String.valueOf(command[0]));
            Minesweeper board = new Minesweeper(4, 4, mines);
            System.out.println(board);
            while(sentinel) {
                System.out.print("Moves: " + board.getMoveCount() + '\n');
                System.out.print("Enter a command: ");
                command = scanner.nextLine().split(" ");
                switch(command[0]) {
                    case "quit":
                        sentinel = !quit(scanner);
                        break;
                    case "pick":
                        Location temp = new Location(Integer.parseInt(String.valueOf(command[1])), Integer.parseInt(String.valueOf(command[2])));
                        board.makeSelection(temp);
                        System.out.println(board);
                        break;
                    case "hint":
                        System.out.println("Maybe try (" + board.getPossibleSelections().get(0) +")");
                        break;
                    case "help":
                        help();
                        break;
                    case "reset":
                        board.reset();
                        System.out.println(board);
                        break;
                    case "solve":
                        Minefinder solver = Minefinder.solver(board);
                        List<Location> moves = solver.getMoveList();

                        for (Location loc: moves) {
                            System.out.println("Selection: (" + loc + ")");
                            board.makeSelection(loc);
                            System.out.println(board);
                        }

                        break;
                    default:
                        invalid(command);
                        break;
                }

                if (board.getGameState() == GameState.LOST) {
                    //sentinel = false;
                    System.out.println("Better luck next time!");
                }
                else if (board.getGameState() == GameState.WON) {
                    //sentinel = false;
                    System.out.println("Congratulations!");
                }
            }
            System.out.println("Good bye!");
        }
    }

    /**
     * Prints a help message with the available commands.
     */
    
    private static void help() {
        System.out.println("Available commands: ");
        System.out.println("  help - displays this message");
        System.out.println("  pick <row> <col> - uncovers cell at <row> <col>");
        System.out.println("  hint - displays a safe selection");
        System.out.println("  solve - executes all moves to solve the game");
        System.out.println("  reset - resets to a new game");
        System.out.println("  quit - quits the game");
        System.out.println();
    }
    

    /**
     * Prompts the user to ask if they are sure, and if so, quits the game.
     * @param scanner The scanner used to read the user response.
     * @return True if the game should quit, false if it should not.
     */
    
    private static boolean quit(Scanner scanner) {
        System.out.print("Are you sure? (y/n): ");
        String response = scanner.nextLine();
        return response.equalsIgnoreCase("y");
    }
    

    /**
     * Displays an invalid command message.
     *
     * @param command The invalid command.
     */
    
    private static void invalid(String[] command) {
        System.out.println("Invalid command: " + command[0]);
    }
    
    

    
}