package minesweeper.View;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import minesweeper.model.Minesweeper;
import minesweeper.model.MinesweeperException;
import minesweeper.model.GameState;
import minesweeper.model.Location;

public class TileHandler implements EventHandler<ActionEvent> {

    private final Minesweeper game;
    private final Location location;
    private final Label moveCountLabel;
    private final Label gamestatus;

    public TileHandler(Minesweeper game, int row, int col, Label moveCountLabel, Label gamestatus) {
        this.game = game;
        this.location = new Location(row, col);
        this.moveCountLabel = moveCountLabel;
        this.gamestatus = gamestatus;
    }

    @Override
    public void handle(ActionEvent event) {
        // make Selection
        try {
            // Can only make a selection if game has not been started or is in progress
            if (game.getGameState() != GameState.LOST && game.getGameState() != GameState.WON) {
                game.makeSelection(location);

                moveCountLabel.setText("Total Moves: " + game.getMoveCount());

                if (game.getGameState() == GameState.LOST) {
                    gamestatus.setText("You lost!");
                    gamestatus.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                }
                else if (game.getGameState() == GameState.WON) {
                    gamestatus.setText("You Won!");
                    gamestatus.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                }
            }
        }
        catch (MinesweeperException e) {}
    }
    
}
