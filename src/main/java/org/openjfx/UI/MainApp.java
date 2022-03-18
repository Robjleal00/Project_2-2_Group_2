package org.openjfx.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button button = new Button("Hello, World!");
        button.setOnAction(e -> System.out.println("Button is Pressed"));

        Scene scene = new Scene(button, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Surveillance Game");
        primaryStage.show();
    }
}
