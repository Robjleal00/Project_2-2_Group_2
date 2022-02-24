package org.openjfx.UI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Button btn1 = new Button();
        btn1.setText("Start Game");
        Button btn2 = new Button();
        btn2.setText("Exit");
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Wumpus Hunt Complete!");
            }
        });
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Game Start");
            }
        });

        Pane root = new Pane();
        btn1.setLayoutX(100);
        btn1.setLayoutY(20);
        root.getChildren().add(btn1);
        btn2.setLayoutX(100);
        btn2.setLayoutY(120);
        root.getChildren().add(btn2);
        stage.setTitle("Project 2-2 Group 2");
        stage.setScene(new Scene(root, 200, 200));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}