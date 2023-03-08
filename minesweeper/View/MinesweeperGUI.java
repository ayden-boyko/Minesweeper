package minesweeper.View;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import minesweeper.model.GameState;
import minesweeper.model.Location;
import minesweeper.model.Minefinder;
import minesweeper.model.Minesweeper;
import minesweeper.model.MinesweeperException;
import minesweeper.model.MinesweeperObserver;

public class MinesweeperGUI extends Application {

    private static final int ROWS = 8;
    private static final int COLS = 8;
    private static final int MINECOUNT = 8;

    // Images
    private static final Image MINE_IMG = new Image("media/images/mine24.png");
    
    // Layout stuff
    private static final int TILE_SIZE = 50;
    private static final int TILE_FONT_SIZE = TILE_SIZE/2;
    private static final String TILE_FONT = "Arial";

    // Colors
    private static final Color COVERED_COLOR = Color.PEACHPUFF;
    private static final Color UNCOVERED_COLOR = Color.LIGHTBLUE;
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Color HINT = Color.LIGHTGREEN;

    // Map of colors and numbers
    private static HashMap<Character, Color> colorMap = new HashMap<>();

    static {
        colorMap.put('1', Color.RED);
        colorMap.put('2', Color.DARKGREEN);
        colorMap.put('3', Color.BLUE);
        colorMap.put('4', Color.PINK);
        colorMap.put('5', Color.VIOLET);
        colorMap.put('6', Color.PINK);
        colorMap.put('7', Color.ORANGE);
        colorMap.put('8', Color.YELLOW);
    }


    /**
     *  Inner class for handling Minesweeper board
     */
    public class MinesweeperBoard implements MinesweeperObserver {

        private GridPane board;
        private Minesweeper game;
        private Button[][] tiles;

        private Label moveCountLabel;
        private Label gamestatus;

        public MinesweeperBoard(Minesweeper game, Label moveCountLabel, Label gamestatus) {
            GridPane board = new GridPane();

            // Making the Tiles
            int row, col;
            Button[][] tiles = new Button[ROWS][COLS];
    
            for (row = 0; row < ROWS; row++) {
                for (col = 0; col < COLS; col++) {
                    tiles[row][col] = makeTile();
                    board.add(tiles[row][col], col, row);
                }
            }
    
            // Setting event handlers to all tiles (buttons)
            for (row = 0; row < ROWS; row++) {
                for (col = 0; col < COLS; col++) {
                    tiles[row][col].setOnAction(new TileHandler(game, row, col, moveCountLabel, gamestatus));
                }
            }

            // Register observer
            game.register(this);

            this.board = board;
            this.game = game;
            this.tiles = tiles;

            this.moveCountLabel = moveCountLabel;
            this.gamestatus = gamestatus;
        }

        //Hint method
        public void getHint(Minesweeper game){
            List<Location> possible = game.getPossibleSelections();
            Random rand = new Random();
            //gets one random location from all possible safe locations
            int randomtile = rand.nextInt(possible.size());
            
            Button tile = this.tiles[possible.get(randomtile).getRow()][possible.get(randomtile).getCol()];

            //changes background of hint tile
            tile.setBackground(new Background(new BackgroundFill(HINT, CornerRadii.EMPTY, Insets.EMPTY)));

        }

        /**
         * @return the gridpane representing the minesweeper board
         */
        public GridPane getGridPane() {
            return this.board;
        }

        /**
         * Updates the tile
         */
        @Override
        public void cellUpdated(Location location) {
            try {
                char val = game.getSymbol(location);
                Button tile = this.tiles[location.getRow()][location.getCol()];
                
                // if a +ve number of mines, set text in tile, otherwise leave blank
                if (val != '0' && val != Minesweeper.MINE) {
                    tile.setText(val + "");
                    tile.setTextFill(colorMap.get(val)); // setting the text color according to the number of mines
                }
                else if (val == Minesweeper.MINE) {
                    tile.setGraphic(new ImageView(MINE_IMG));
                }

                // uncover tile
                tile.setBackground(new Background(new BackgroundFill(UNCOVERED_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
            }
            catch (MinesweeperException e) {}
        }

        /**
         * Resets the board
         */
        public void reset() {
            board = new GridPane();

            // Making the Tiles
            int row, col;
            Button[][] tiles = new Button[ROWS][COLS];
    
            for (row = 0; row < ROWS; row++) {
                for (col = 0; col < COLS; col++) {
                    tiles[row][col] = makeTile();
                    board.add(tiles[row][col], col, row);
                }
            }
    
            // Setting event handlers to all tiles (buttons)
            for (row = 0; row < ROWS; row++) {
                for (col = 0; col < COLS; col++) {
                    tiles[row][col].setOnAction(new TileHandler(game, row, col, moveCountLabel, gamestatus));
                }
            }
        }
    }

    public Label makeLabel(String text, int fontSize, Color bgColor, Insets padding) {
        Label label = new Label(text);
        label.setTextFill(Color.BLACK);
        label.setFont(new Font(TILE_FONT, fontSize));
        label.setBackground(new Background(new BackgroundFill(bgColor, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPadding(padding);
        label.setMaxHeight(Double.POSITIVE_INFINITY);
        label.setMaxWidth(Double.POSITIVE_INFINITY);

        return label;
    }

    public Button makeSideButton(String text, int fontSize, Color bgColor, Insets padding) {
        Button button = new Button(text);
        button.setTextFill(Color.BLACK);
        button.setFont(new Font("Verdana", fontSize));
        button.setBackground(new Background(new BackgroundFill(bgColor, CornerRadii.EMPTY, Insets.EMPTY)));
        button.setPadding(padding);
        button.setMaxHeight(Double.POSITIVE_INFINITY);
        button.setMaxWidth(Double.POSITIVE_INFINITY);

        return button;
    }

    /**
     * @return a button representing a Minesweeper tile
     */
    public Button makeTile() {
        Button button = new Button();
        button.setTextFill(Color.BLACK);
        button.setFont(new Font(TILE_FONT, TILE_FONT_SIZE));
        button.setBackground(new Background(new BackgroundFill(COVERED_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        button.setMaxHeight(Double.POSITIVE_INFINITY);
        button.setMaxWidth(Double.POSITIVE_INFINITY);
        button.setMinSize(TILE_SIZE, TILE_SIZE);
        button.setBorder(new Border(new BorderStroke(BORDER_COLOR, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        return button;
    }

    public void start(Stage stage) throws Exception {
        VBox messagevbox = new VBox();
        Label totalmines = makeLabel("Total Mines: " + MINECOUNT, 24, Color.LIGHTGREY, new Insets(10));
        Label movesmade = makeLabel("Total Moves: 0", 24, Color.LIGHTGREY, new Insets(10));
        Button reset = makeSideButton("Reset", 24, Color.LIGHTGRAY, new Insets(10));
        Button hint = makeSideButton("Hint", 24, Color.LIGHTGRAY, new Insets(10));
        Button solve = makeSideButton("Solve", 24, Color.LIGHTGRAY, new Insets(10));


        messagevbox.getChildren().add(totalmines);
        messagevbox.getChildren().add(movesmade);
        messagevbox.getChildren().add(hint);
        messagevbox.getChildren().add(reset);
        messagevbox.getChildren().add(solve);

        //results
        Label results= makeLabel("Game in progress...", 24, Color.WHITE, new Insets(10));
        // The main game
        Minesweeper game = new Minesweeper(ROWS, COLS, MINECOUNT);

        // The visual board
        MinesweeperBoard board = new MinesweeperBoard(game, movesmade, results);
        
        //scene added to hbox as well as the button vbox
        HBox boardscene = new HBox(); 
        boardscene.getChildren().add(messagevbox);
        boardscene.getChildren().add(board.getGridPane());

        // Final Vbox
        VBox finalvbox = new VBox();
        
        finalvbox.getChildren().add(boardscene);
        finalvbox.getChildren().add(results);

        //hint button functionality. Incredibly sloppy :), but it works lol
        hint.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (game.getGameState() != GameState.LOST && game.getGameState() != GameState.WON) {
                    board.getHint(game);
                }
            }
        });

        //reset functionality 
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {
                    stage.close();
                    start(new Stage());
                } catch (Exception e) { }
                
            }
        });

        // solve the game on its own
        solve.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                Minefinder winConfig = Minefinder.solver(game);

                List<Location> moves;

                if (winConfig != null) {
                    moves = winConfig.getMoveList();

                    new Thread(()-> {
                        // for each winning selection
                        for (Location loc: moves) {
                            Platform.runLater(() -> {
                            // make selection
                            try {
                                game.makeSelection(loc);
                            }
                            catch (MinesweeperException e) {}
                            });

                            // sleep small amount (< 250ms)
                            try {
                                Thread.sleep(250);
                            }
                            catch (InterruptedException e) {}
                        }
                    }).start();
                }
            }
        });
        
        
        // Scene
        Scene scene = new Scene(finalvbox);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
