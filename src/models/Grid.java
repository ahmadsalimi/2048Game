package models;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Optional;
import java.util.Random;

class Grid {
    private int dimension;
    private double scale;
    private Label scoreBoard;
    private Tile[][] tiles;
    private boolean checkingAvailableMoves;
    private int score;
    private Random rand = new Random();
    private GridPane gridPane;

    Grid(int dimension, Label scoreLabel) {
        this.dimension = dimension;
        this.scale = 4.0 / dimension;
        this.scoreBoard = scoreLabel;
        this.tiles = new Tile[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                tiles[i][j] = new Tile(0, this);
            }
        }
    }

    double getScale() {
        return scale;
    }

    boolean moveUp() {
        return move(0, -1, false);
    }

    boolean moveDown() {
        return move(0, 1, true);
    }

    boolean moveLeft() {
        return move(-1, 0, false);
    }

    boolean moveRight() {
        return move(1, 0, true);
    }

    private boolean move(int rowChange, int columnChange, boolean reverse) {
        boolean moved = false;

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                int row, column;
                if (reverse) {
                    row = dimension - 1 - i;
                    column = dimension - 1 - j;
                } else {
                    row = i;
                    column = j;
                }

                int nextRow = row + rowChange;
                int nextColumn = column + columnChange;

                while (nextRow >= 0 && nextRow < dimension && nextColumn >= 0 && nextColumn < dimension) {

                    Tile nextTile = tiles[nextRow][nextColumn];
                    Tile currentTile = tiles[row][column];

                    if (nextTile.getNumber() == 0) {

                        if (checkingAvailableMoves)
                            return true;
                        moved = chaneNumber(moved, nextTile, currentTile);
                        row = nextRow;
                        column = nextColumn;
                        nextRow += rowChange;
                        nextColumn += columnChange;

                    } else if (nextTile.canMergeWith(currentTile)) {

                        if (checkingAvailableMoves)
                            return true;

                        merge(currentTile, nextTile, nextRow, nextColumn);
                        moved = true;
                        break;
                    } else {
                        break;
                    }
                }

            }
        }

        if (moved) {
            clearMerged();
            addRandomTile();
            if (!movesAvailable()) {
                showFinishAlert();
            }
        }

        return moved;
    }

    private void merge(Tile currentTile, Tile nextTile, int nextRow, int nextColumn) {
        int value = nextTile.mergeWith(currentTile);
        showMergeAnimation(nextRow, nextColumn);
        increaseScore(value);
        currentTile.setNumber(0);
    }

    private boolean chaneNumber(boolean moved, Tile nextTile, Tile currentTile) {
        if (currentTile.getNumber() > 0) {
            nextTile.setNumber(currentTile.getNumber());
            currentTile.setNumber(0);
            moved = true;
        }
        return moved;
    }

    private void showMergeAnimation(int row, int column) {
        StackPane thisPane = (StackPane) gridPane.getChildren().get(row * dimension + column);
        final Label[] labelWrapper = new Label[1];

        thisPane.getChildren().stream().filter(
                node -> node instanceof Label
        ).forEach(node -> labelWrapper[0] = (Label) node);

        new AnimationTimer() {
            private long millisecond = (long) Math.pow(10, 6);
            private long firstTime;
            private long lastTime = 0;
            private Label label = labelWrapper[0];
            private double defaultFontSize = label.getFont().getSize();
            private double fontSize = defaultFontSize;

            @Override
            public void handle(long now) {
                if (firstTime == 0) {
                    firstTime = now;
                }
                if (now > lastTime + 5 * millisecond) {
                    lastTime = now;
                    if (now - firstTime <= 200 * millisecond) {
                        fontSize++;
                    } else if (now - firstTime > 400 * millisecond) {
                        fontSize = defaultFontSize;
                        this.stop();
                    } else if (now - firstTime > 200 * millisecond) {
                        fontSize--;
                    }
                    label.setFont(
                            Font.font("FreeSans", FontWeight.EXTRA_BOLD, fontSize)
                    );
                }
            }
        }.start();
    }

    private void showFinishAlert() {
        Alert alert = makeFinishAlert();
        Optional<ButtonType> optional = alert.showAndWait();

        optional.ifPresent(action -> {
            if (action == ButtonType.OK) {
                new MainMenu().start();
            }
        });
    }

    private Alert makeFinishAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Over");
        alert.setTitle("Game is finished");
        alert.setContentText("Your Score: " + score);
        return alert;
    }

    private void increaseScore(int value) {
        score += value;
        scoreBoard.setText(String.valueOf(score));
    }

    private boolean movesAvailable() {
        checkingAvailableMoves = true;
        boolean hasMoves = moveUp() || moveDown() || moveLeft() || moveRight();
        checkingAvailableMoves = false;
        return hasMoves;
    }

    private void addRandomTile() {
        int row, column;
        do {
            row = rand.nextInt(dimension);
            column = rand.nextInt(dimension);
        } while (tiles[row][column].getNumber() != 0);

        int number = rand.nextInt(10) == 0 ? 4 : 2;
        tiles[row][column].setNumber(number);

        showSpawnAnimation(row, column);
    }

    private void showSpawnAnimation(int row, int column) {
        StackPane thisPane = (StackPane) gridPane.getChildren().get(row * dimension + column);
        final Rectangle[] rectangleWrapper = new Rectangle[1];
        thisPane.getChildren().stream().filter(
                node -> node instanceof Rectangle
        ).forEach(node -> rectangleWrapper[0] = (Rectangle) node);

        new AnimationTimer() {
            private long millisecond = (long) Math.pow(10, 6);
            private long lastTime = 0;
            private Rectangle rectangle = rectangleWrapper[0];
            private double defaultSize = rectangle.getWidth();
            private double currentSize = defaultSize * 0.7;
            @Override
            public void handle(long now) {
                if (now > lastTime + millisecond) {
                    if (currentSize >= defaultSize) {
                        currentSize = defaultSize;
                        this.stop();
                        return;
                    }
                    currentSize += 2;

                    thisPane.getChildren().stream().filter(node -> node instanceof Rectangle).forEach(node -> {
                        ((Rectangle) node).setWidth(currentSize);
                        ((Rectangle) node).setHeight(currentSize);
                    });
                }
            }
        }.start();
    }

    private void clearMerged() {
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile != null) {
                    tile.setUnMerged();
                }
            }
        }
    }

    void draw(GridPane gridPane) {
        this.gridPane = gridPane;

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                gridPane.add(new StackPane(tiles[i][j].getBox(), tiles[i][j].getLabel()), i, j, 1, 1);
            }
        }

        addRandomTile();
        addRandomTile();
    }
}
