package models;

import controller.Controller;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Optional;

public class GameHandler {
    private static Color BACKGROUND = Color.rgb(188, 171, 156);
    private static Color OTHER_BACKGROUND = Color.rgb(188, 171, 156, 0.8);
    private static CornerRadii DEFAULT_CORNER_RADIUS = new CornerRadii(10);
    private Scene scene;
    private int dimension;
    private Label scoreValue;

    GameHandler(int dimension) {
        this.dimension = dimension;
    }

    public void start() {
        Group root = new Group();
        scene = new Scene(root, 1300, 1000);

        Controller.getInstance().setScene(scene);

        VBox scoreBox = makeScoreBoard();
        Button exitButton = makeExitButton();
        Grid grid = new Grid(dimension, scoreValue);
        GridPane gridPane = makeAndRenderGameGrid(grid);

        root.getChildren().addAll(gridPane, scoreBox, exitButton);

        setKeyHandler(grid);
    }

    private void setKeyHandler(Grid grid) {
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                    grid.moveUp();
                    break;
                case DOWN:
                    grid.moveDown();
                    break;
                case RIGHT:
                    grid.moveRight();
                    break;
                case LEFT:
                    grid.moveLeft();
                    break;
            }
        });
    }

    private Button makeExitButton() {
        Button exitButton = new Button("Exit");
        exitButton.setFont(Font.font("FreeSans", FontWeight.EXTRA_BOLD, 60));
        exitButton.setTextFill(Color.WHITE);
        exitButton.setAlignment(Pos.CENTER);
        exitButton.setBackground(
                new Background(new BackgroundFill(OTHER_BACKGROUND, DEFAULT_CORNER_RADIUS, Insets.EMPTY))
        );
        exitButton.setPadding(new Insets(10));
        exitButton.relocate(1010, 210);
        exitButton.setPrefSize(250, 100);
        exitButton.setFocusTraversable(false);

        setAnimations(exitButton);
        setExitGameClick(exitButton);

        return exitButton;
    }

    private void setExitGameClick(Button exitButton) {
        exitButton.setOnMouseClicked(mouseEvent -> {
            Alert alert = makeExitAlert();

            Optional<ButtonType> optional = alert.showAndWait();

            optional.ifPresent(acton -> {
                if (acton == ButtonType.YES) {
                    new MainMenu().start();
                } else {
                    alert.close();
                }
            });
        });
    }

    private Alert makeExitAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit game");
        alert.setHeaderText("exit this game?");
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().add(ButtonType.YES);
        return alert;
    }

    private GridPane makeAndRenderGameGrid(Grid grid) {
        GridPane gridPane = new GridPane();
        gridPane.setBackground(
                new Background(
                        new BackgroundFill(
                                BACKGROUND, DEFAULT_CORNER_RADIUS, new Insets(35.0 / Math.pow(grid.getScale(), 0.2))
                        )
                )
        );
        gridPane.relocate(0, 0);
        gridPane.setMinSize(1000, 1000);
        gridPane.setVgap(25 * grid.getScale());
        gridPane.setHgap(25 * grid.getScale());
        gridPane.setAlignment(Pos.CENTER);
        grid.draw(gridPane);
        return gridPane;
    }

    private VBox makeScoreBoard() {
        Label scoreLabel = makeScoreLabel();
        makeScoreValue();
        return makeScoreBox(scoreLabel);
    }

    private VBox makeScoreBox(Label scoreLabel) {
        VBox scoreBox = new VBox(scoreLabel, scoreValue);
        scoreBox.relocate(1010, 40);
        scoreBox.setPadding(new Insets(10));
        scoreBox.setPrefSize(250, 150);
        scoreBox.setBackground(
                new Background(new BackgroundFill(OTHER_BACKGROUND, DEFAULT_CORNER_RADIUS, Insets.EMPTY))
        );
        scoreBox.setAlignment(Pos.CENTER);
        return scoreBox;
    }

    private void makeScoreValue() {
        scoreValue = new Label("0");
        scoreValue.setAlignment(Pos.CENTER);
        scoreValue.setFont(Font.font("FreeSans", FontWeight.EXTRA_BOLD, 70));
        scoreValue.setTextFill(Color.WHITE);
    }

    private Label makeScoreLabel() {
        Label scoreLabel = new Label("SCORE");
        scoreLabel.setAlignment(Pos.CENTER);
        scoreLabel.setFont(Font.font("FreeSans", FontWeight.LIGHT, 50));
        scoreLabel.setTextFill(Color.WHITE);
        return scoreLabel;
    }

    private void setAnimations(Button button) {
        setOnHoverAnimation(button);
        setOnExitAnimation(button);
    }

    private void setOnExitAnimation(Button button) {
        AnimationTimer defaultAnimation = new AnimationTimer() {
            private double currentTransparency = 1;

            @Override
            public void handle(long now) {
                if (currentTransparency >= 0.6) {
                    button.setBackground(
                            new Background(
                                    new BackgroundFill(
                                            Color.rgb(188, 171, 156, currentTransparency),
                                            DEFAULT_CORNER_RADIUS,
                                            new Insets(0)
                                    )
                            )
                    );

                    currentTransparency -= 0.05;
                } else {
                    this.stop();
                    currentTransparency = 1;
                }
            }
        };
        button.setOnMouseExited(mouseEvent -> {
            scene.setCursor(Cursor.DEFAULT);
            defaultAnimation.start();
        });
    }

    private void setOnHoverAnimation(Button button) {
        AnimationTimer hoverAnimation = new AnimationTimer() {
            private double currentTransparency = 0.8;

            @Override
            public void handle(long now) {
                if (currentTransparency <= 1) {
                    button.setBackground(
                            new Background(
                                    new BackgroundFill(
                                            Color.rgb(188, 171, 156, currentTransparency),
                                            DEFAULT_CORNER_RADIUS,
                                            new Insets(0)
                                    )
                            )
                    );

                    currentTransparency += 0.05;
                } else {
                    this.stop();
                    currentTransparency = 0.6;
                }
            }
        };
        button.setOnMouseEntered(mouseEvent -> {
            scene.setCursor(Cursor.HAND);
            hoverAnimation.start();
        });
    }
}
