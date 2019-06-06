package controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import models.MainMenu;

public class Controller {
    private static final Controller CONTROLLER = new Controller();
    private Stage stage;

    private Controller() {
    }

    public static Controller getInstance() {
        return CONTROLLER;
    }

    public void main(Stage stage) {
        this.stage = stage;

        MainMenu mainMenu = new MainMenu();
        mainMenu.start();

        stage.setResizable(false);
        stage.setTitle("2048 Game");
        stage.show();
    }

    public void setScene(Scene scene) {
        stage.setScene(scene);
    }
}