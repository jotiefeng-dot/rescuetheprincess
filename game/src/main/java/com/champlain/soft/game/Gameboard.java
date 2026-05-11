package com.champlain.soft.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Random;

public class Gameboard extends Application {

    private static final int ROWS = 10;
    private static final int COLS = 10;

    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 800;

    enum CellType {
        GRASS, PLAYER, PRINCESS, BOMB, WALL
    }

    private CellType[][] matrix = new CellType[ROWS][COLS];

    private int playerRow = 1;
    private int playerCol = 1;

    // DIRECT IMAGE LINKS
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

    private GridPane grid;

    @Override
    public void start(Stage stage) {

        initMatrix();

        grid = new GridPane();

        drawBoard();

        BorderPane root = new BorderPane();

        root.setCenter(grid);

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        // MOVEMENT
        scene.setOnKeyPressed(event -> {

            switch (event.getCode()) {

                case UP -> movePlayer(-1, 0);

                case DOWN -> movePlayer(1, 0);

                case LEFT -> movePlayer(0, -1);

                case RIGHT -> movePlayer(0, 1);
            }
        });

        stage.setTitle("Rescue the Princess");

        stage.setScene(scene);

        stage.show();
    }

    private void initMatrix() {

        // Fill with grass
        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS; c++) {

                matrix[r][c] = CellType.GRASS;
            }
        }

        // Walls
        for (int r = 0; r < ROWS; r++) {

            matrix[r][0] = CellType.WALL;
            matrix[r][COLS - 1] = CellType.WALL;
        }

        for (int c = 0; c < COLS; c++) {

            matrix[0][c] = CellType.WALL;
            matrix[ROWS - 1][c] = CellType.WALL;
        }

        // Player
        matrix[playerRow][playerCol] = CellType.PLAYER;

        Random random = new Random();

        // Princess
        int princessRow;
        int princessCol;

        do {

            princessRow = random.nextInt(ROWS);
            princessCol = random.nextInt(COLS);

        } while (matrix[princessRow][princessCol] != CellType.GRASS);

        matrix[princessRow][princessCol] = CellType.PRINCESS;

        // Bombs
        for (int i = 0; i < 3; i++) {

            int bombRow;
            int bombCol;

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

        // Stop at walls
        if (matrix[newRow][newCol] == CellType.WALL) {

            return;
        }

        // WIN CONDITION
        if (matrix[newRow][newCol] == CellType.PRINCESS) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Victory");

            alert.setHeaderText(null);

            alert.setContentText("You rescued the princess!");

            alert.showAndWait();
        }

        // Remove old player position
        matrix[playerRow][playerCol] = CellType.GRASS;

        // Update player position
        playerRow = newRow;

        playerCol = newCol;

        // Put player in new position
        matrix[playerRow][playerCol] = CellType.PLAYER;

        drawBoard();
    }

    private void drawBoard() {

        grid.getChildren().clear();

        for (int row = 0; row < ROWS; row++) {

            for (int col = 0; col < COLS; col++) {

                StackPane cell = new StackPane();

                cell.setPrefSize(60, 60);

                // Grass background
                ImageView background = new ImageView(grassImage);

                background.setFitWidth(60);

                background.setFitHeight(60);

                cell.getChildren().add(background);

                // Player
                if (matrix[row][col] == CellType.PLAYER) {

                    ImageView player = new ImageView(playerImage);

                    player.setFitWidth(40);

                    player.setFitHeight(40);

                    cell.getChildren().add(player);
                }

                // Princess
                else if (matrix[row][col] == CellType.PRINCESS) {

                    ImageView princess = new ImageView(princessImage);

                    princess.setFitWidth(40);

                    princess.setFitHeight(40);

                    cell.getChildren().add(princess);
                }

                // Bomb
                else if (matrix[row][col] == CellType.BOMB) {

                    ImageView bomb = new ImageView(bombImage);

                    bomb.setFitWidth(40);

                    bomb.setFitHeight(40);

                    cell.getChildren().add(bomb);
                }

                // Wall
                else if (matrix[row][col] == CellType.WALL) {

                    ImageView wall = new ImageView(wallImage);

                    wall.setFitWidth(60);

                    wall.setFitHeight(60);

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