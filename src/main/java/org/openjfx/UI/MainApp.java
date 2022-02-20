package org.openjfx.UI;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;



public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        double AREA_HEIGHT = 80;
        double AREA_WIDTH = 120;
        double SCALE = 0.3;

       new GuiMaker(AREA_HEIGHT, AREA_WIDTH, SCALE);

    }
}
