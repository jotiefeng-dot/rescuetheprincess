package com.champlain.soft.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Random;

public class Gameboard extends Application {

    // GRID SIZE
    private static final int ROWS = 10;
    private static final int COLS = 10;

    // WINDOW SIZE
    private static final int SCENE_WIDTH = 1200;
    private static final int SCENE_HEIGHT = 1000;

    // CELL SIZE
    private static final int CELL_SIZE = 90;

    enum CellType {
        GRASS, PLAYER, PRINCESS, BOMB, WALL
    }

    private CellType[][] matrix = new CellType[ROWS][COLS];

    // PLAYER POSITION
    private int playerRow = 1;
    private int playerCol = 1;

    // LIVES
    private int lives = 3;

    // GAME OVER FLAG
    private boolean gameOver = false;

    private GridPane grid;

    private Label livesLabel = new Label();

    // IMAGES
    private Image grassImage =
            new Image("file:///C:/Users/jo-wildried/IdeaProjects/rescuetheprincess/game/src/images/grass.png");

    private Image playerImage =
            new Image("file:///C:/Users/jo-wildried/IdeaProjects/rescuetheprincess/game/src/images/player.png");

    private Image princessImage =
            new Image("file:///C:/Users/jo-wildried/IdeaProjects/rescuetheprincess/game/src/images/princess.png");

    private Image bombImage =
            new Image("file:///C:/Users/jo-wildried/IdeaProjects/rescuetheprincess/game/src/images/bomb.png");

    private Image wallImage =
            new Image("file:///C:/Users/jo-wildried/IdeaProjects/rescuetheprincess/game/src/images/wall.png");

    @Override
    public void start(Stage stage) {

        initMatrix();

        grid = new GridPane();

        drawBoard();

        BorderPane root = new BorderPane();

        root.setCenter(grid);

        // LIVES LABEL
       // livesLabel.setText("Lives: " + lives);

        //root.setTop(livesLabel);

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        // KEYBOARD MOVEMENT
        scene.setOnKeyPressed(event -> {

            if (gameOver) {
                return;
            }

            switch (event.getCode()) {
                case UP    -> movePlayer(-1, 0);
                case DOWN  -> movePlayer(1, 0);
                case LEFT  -> movePlayer(0, -1);
                case RIGHT -> movePlayer(0, 1);
            }
        });

        stage.setTitle("Rescue the Princess");
        stage.setScene(scene);
        stage.show();

        // IMPORTANT FOR KEYBOARD INPUT
        root.requestFocus();
    }

    private void initMatrix() {

        // FILL WITH GRASS
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                matrix[r][c] = CellType.GRASS;
            }
        }

        // WALLS ON PERIMETER
        for (int r = 0; r < ROWS; r++) {
            matrix[r][0] = CellType.WALL;
            matrix[r][COLS - 1] = CellType.WALL;
        }

        for (int c = 0; c < COLS; c++) {
            matrix[0][c] = CellType.WALL;
            matrix[ROWS - 1][c] = CellType.WALL;
        }

        // PLAYER AT [1,1]
        matrix[playerRow][playerCol] = CellType.PLAYER;

        Random random = new Random();

        // PRINCESS (random grass cell)
        int princessRow, princessCol;
        do {
            princessRow = random.nextInt(ROWS);
            princessCol = random.nextInt(COLS);
        } while (matrix[princessRow][princessCol] != CellType.GRASS);

        matrix[princessRow][princessCol] = CellType.PRINCESS;

        // BOMBS (3 random grass cells)
        for (int i = 0; i < 3; i++) {
            int bombRow, bombCol;
            do {
                bombRow = random.nextInt(ROWS);
                bombCol = random.nextInt(COLS);
            } while (matrix[bombRow][bombCol] != CellType.GRASS);

            matrix[bombRow][bombCol] = CellType.BOMB;
        }
    }

    private void movePlayer(int dRow, int dCol) {

        int newRow = playerRow + dRow;
        int newCol = playerCol + dCol;

        // WALL — block movement
        if (matrix[newRow][newCol] == CellType.WALL) {
            return;
        }

        // PRINCESS — win condition
        if (matrix[newRow][newCol] == CellType.PRINCESS) {

            // MOVE PLAYER TO PRINCESS CELL BEFORE DRAWING
            matrix[playerRow][playerCol] = CellType.GRASS;
            playerRow = newRow;
            playerCol = newCol;
            matrix[playerRow][playerCol] = CellType.PLAYER;
            drawBoard();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Victory");
            alert.setHeaderText(null);
            alert.setContentText("You rescued the princess!");
            alert.showAndWait();

            gameOver = true;
            return;
        }

        // BOMB — lose a life
        if (matrix[newRow][newCol] == CellType.BOMB) {

            lives--;
            livesLabel.setText("Lives: " + lives);

            // REMOVE BOMB
            matrix[newRow][newCol] = CellType.GRASS;

            // GAME OVER — no lives left
            if (lives == 0) {

                // MOVE PLAYER TO BOMB CELL BEFORE DRAWING
                matrix[playerRow][playerCol] = CellType.GRASS;
                playerRow = newRow;
                playerCol = newCol;
                matrix[playerRow][playerCol] = CellType.PLAYER;
                drawBoard();

                Alert gameOverAlert = new Alert(Alert.AlertType.ERROR);
                gameOverAlert.setTitle("Game Over");
                gameOverAlert.setHeaderText(null);
                gameOverAlert.setContentText("No lives left! Game Over!");
                gameOverAlert.showAndWait();

                gameOver = true;
                return;
            }

            // STILL HAS LIVES — warn player
            Alert bombAlert = new Alert(Alert.AlertType.WARNING);
            bombAlert.setTitle("Bomb");
            bombAlert.setHeaderText(null);
            bombAlert.setContentText("You hit a bomb! Lives left: " + lives);
            bombAlert.showAndWait();
        }

        // MOVE PLAYER
        matrix[playerRow][playerCol] = CellType.GRASS;
        playerRow = newRow;
        playerCol = newCol;
        matrix[playerRow][playerCol] = CellType.PLAYER;

        drawBoard();
    }

    private void drawBoard() {

        grid.getChildren().clear();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                StackPane cell = new StackPane();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);

                // GRASS BACKGROUND
                ImageView background = new ImageView(grassImage);
                background.setFitWidth(CELL_SIZE);
                background.setFitHeight(CELL_SIZE);
                cell.getChildren().add(background);

                // PLAYER
                if (matrix[row][col] == CellType.PLAYER) {
                    ImageView player = new ImageView(playerImage);
                    player.setFitWidth(70);
                    player.setFitHeight(70);
                    cell.getChildren().add(player);
                }

                // PRINCESS
                else if (matrix[row][col] == CellType.PRINCESS) {
                    ImageView princess = new ImageView(princessImage);
                    princess.setFitWidth(70);
                    princess.setFitHeight(70);
                    cell.getChildren().add(princess);
                }

                // BOMB
                else if (matrix[row][col] == CellType.BOMB) {
                    ImageView bomb = new ImageView(bombImage);
                    bomb.setFitWidth(70);
                    bomb.setFitHeight(70);
                    cell.getChildren().add(bomb);
                }

                // WALL
                else if (matrix[row][col] == CellType.WALL) {
                    ImageView wall = new ImageView(wallImage);
                    wall.setFitWidth(CELL_SIZE);
                    wall.setFitHeight(CELL_SIZE);
                    cell.getChildren().add(wall);
                }

                grid.add(cell, col, row);
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}