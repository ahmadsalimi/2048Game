package models;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;

public class Tile {
    private static final HashMap<Integer, Color> backgroundColors = new HashMap<>();
    private static final Color OTHER_BACKGROUNDS = Color.BLACK;
    private static final Color LIGHT_COLOR = Color.rgb(249, 246, 242);
    private static final Color DARK_COLOR = Color.rgb(119, 110, 101);

    static {
        backgroundColors.put(0, Color.rgb(204, 192, 179));
        backgroundColors.put(2, Color.rgb(238, 228, 218));
        backgroundColors.put(4, Color.rgb(237, 224, 200));
        backgroundColors.put(8, Color.rgb(242, 177, 121));
        backgroundColors.put(16, Color.rgb(245, 149, 99));
        backgroundColors.put(32, Color.rgb(246, 124, 95));
        backgroundColors.put(64, Color.rgb(246, 94, 59));
        backgroundColors.put(128, Color.rgb(237, 207, 114));
        backgroundColors.put(256, Color.rgb(237, 204, 97));
        backgroundColors.put(512, Color.rgb(237, 200, 80));
        backgroundColors.put(1024, Color.rgb(237, 197, 63));
        backgroundColors.put(2048, Color.rgb(237, 194, 46));
    }

    private int number;
    private boolean alreadyMerged;
    private Label label = new Label();
    private Rectangle box;
    private double scale;

    Tile(int number, Grid grid) {
        this.scale = grid.getScale();
        box = new Rectangle(200 * scale, 200 * scale);
        box.setArcWidth(20);
        box.setArcHeight(20);
        setNumber(number);
    }

    Label getLabel() {
        return label;
    }

    Rectangle getBox() {
        return box;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        box.setFill(backgroundColors.getOrDefault(number, OTHER_BACKGROUNDS));

        if (number == 0) {
            label.setText("");
        } else {
            label.setText(String.valueOf(number));
            if (number >= 8) {
                label.setTextFill(LIGHT_COLOR);
            } else {
                label.setTextFill(DARK_COLOR);
            }
            double fontSize = 700 * scale / (5 + numberOfDigits(number));
            label.setFont(Font.font("FreeSans", FontWeight.EXTRA_BOLD, fontSize));
        }
    }

    private int numberOfDigits(int number) {
        int digits = 0;
        while (number > 0) {
            digits++;
            number /= 10;
        }
        return digits;
    }

    boolean canMergeWith(Tile other) {
        return !alreadyMerged && other != null && !other.alreadyMerged && number == other.number;
    }

    int mergeWith(Tile other) {
        if (canMergeWith(other)) {
            setNumber(number * 2);
            alreadyMerged = true;
            return number;
        }
        return -1;
    }

    void setUnMerged() {
        alreadyMerged = false;
    }
}
