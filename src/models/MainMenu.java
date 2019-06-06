package models;

import controller.Controller;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainMenu {
    private static final CornerRadii DEFAULT_BUTTON_RADIUS = new CornerRadii(60);
    private static final CornerRadii BOX_RADIUS = new CornerRadii(20);
    private static final Color DEFAULT_BUTTON_COLOR = Color.rgb(0, 212, 143, 0.6);
    private static final Color BOX_BACKGROUND_COLOR = Color.rgb(234, 169, 135, 0.9);
    private static final Background DEFAULT_BUTTON_BACKGROUND = new Background(
            new BackgroundFill(DEFAULT_BUTTON_COLOR, DEFAULT_BUTTON_RADIUS, Insets.EMPTY)
    );
    private static final Background BOX_BACKGROUND = new Background(
            new BackgroundFill(BOX_BACKGROUND_COLOR, BOX_RADIUS, Insets.EMPTY)
    );
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 100;
    private static final int BOX_WIDTH = 400;
    private static final int BOX_HEIGHT = 400;
    private Scene scene;

    public void start() {
        Group root = new Group();
        scene = new Scene(root, 1300, 1000);
        Controller.getInstance().setScene(scene);

        Button newGameButton = makeNewGameButton(root);
        Button quitButton = makeQuitButton();

        root.getChildren().addAll(newGameButton, quitButton);
    }

    private Button makeQuitButton() {
        Button quitButton = new Button("Quit");
        initializeButton(quitButton);
        quitButton.relocate((scene.getWidth() - BUTTON_WIDTH) / 2, (scene.getHeight() + BUTTON_HEIGHT + 50) / 2);
        setAnimations(quitButton);
        quitButton.setOnMouseClicked(mouseEvent -> System.exit(0));
        return quitButton;
    }

    private Button makeNewGameButton(Group root) {
        Button newGameButton = new Button("New Game");
        initializeButton(newGameButton);
        newGameButton.relocate((scene.getWidth() - BUTTON_WIDTH) / 2, (scene.getHeight() - BUTTON_HEIGHT - 50) / 2);
        setAnimations(newGameButton);
        setStartGameClick(root, newGameButton);
        return newGameButton;
    }

    private void setStartGameClick(Group root, Button newGameButton) {
        newGameButton.setOnMouseClicked(mouseEvent -> {
            Label label = makeDialogLabel();
            Spinner<Integer> spinner = makeSpinner();
            Button startButton = makeDialogButton("Start");
            Button cancelButton = makeDialogButton("Cancel");
            VBox box = makeDialogBox(label, spinner, startButton, cancelButton);

            startButton.setOnMouseClicked(event -> new GameHandler(spinner.getValue()).start());

            cancelButton.setOnMouseClicked(event -> root.getChildren().remove(box));

            root.getChildren().add(box);
        });
    }

    private VBox makeDialogBox(Label label, Spinner<Integer> spinner, Button startButton, Button cancelButton) {
        VBox box = new VBox(15, label, spinner, cancelButton, startButton);
        box.setPrefSize(BOX_WIDTH, BOX_HEIGHT);
        box.setBackground(BOX_BACKGROUND);
        box.setAlignment(Pos.CENTER);
        box.relocate((scene.getWidth() - BOX_WIDTH) / 2, (scene.getHeight() - BOX_HEIGHT) / 2);
        return box;
    }

    private Button makeDialogButton(String start) {
        Button startButton = new Button(start);
        startButton.setFont(Font.font("FreeSans", FontWeight.LIGHT, 30));
        return startButton;
    }

    private Spinner<Integer> makeSpinner() {
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 30, 4);
        spinner.setValueFactory(valueFactory);
        spinner.getEditor().setFont(Font.font("FreeSans", FontWeight.LIGHT, 30));
        return spinner;
    }

    private Label makeDialogLabel() {
        Label label = new Label("select grid dimensions:");
        label.setFont(Font.font("FreeSans", FontWeight.LIGHT, 30));
        return label;
    }

    private void setAnimations(Button button) {
        AnimationTimer hoverAnimation = new AnimationTimer() {
            private double currentOpacity = 0.6;
            private int currentRadius = 60;
            @Override
            public void handle(long now) {
                if (currentOpacity <= 1) {
                    button.setBackground(
                            new Background(
                                    new BackgroundFill(
                                            Color.rgb(0, 212, 143, currentOpacity),
                                            new CornerRadii(currentRadius),
                                            Insets.EMPTY
                                    )
                            )
                    );

                    currentOpacity += 0.05;
                    currentRadius -= 5;
                } else {
                    this.stop();
                    currentOpacity = 0.6;
                    currentRadius = 60;
                }
            }
        };

        button.setOnMouseEntered(mouseEvent -> {
            hoverAnimation.start();
            scene.setCursor(Cursor.HAND);
        });

        AnimationTimer defaultAnimation = new AnimationTimer() {
            private double currentOpacity = 1;
            private int currentRadius = 20;
            @Override
            public void handle(long now) {
                if (currentOpacity >= 0.6) {
                    button.setBackground(
                            new Background(
                                    new BackgroundFill(
                                            Color.rgb(0, 212, 143, currentOpacity),
                                            new CornerRadii(currentRadius),
                                            Insets.EMPTY
                                    )
                            )
                    );

                    currentOpacity -= 0.05;
                    currentRadius += 5;
                } else {
                    this.stop();
                    currentOpacity = 1;
                    currentRadius = 20;
                }
            }
        };
        button.setOnMouseExited(mouseEvent -> {
            defaultAnimation.start();
            scene.setCursor(Cursor.DEFAULT);
        });
    }

    private void initializeButton(Button newGameButton) {
        newGameButton.setFont(Font.font("FreeSans", FontWeight.EXTRA_BOLD, 45));
        newGameButton.setBackground(DEFAULT_BUTTON_BACKGROUND);
        newGameButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        newGameButton.setTextFill(Color.WHITE);
        newGameButton.setPadding(new Insets(30));
        newGameButton.setAlignment(Pos.CENTER);
    }
}
