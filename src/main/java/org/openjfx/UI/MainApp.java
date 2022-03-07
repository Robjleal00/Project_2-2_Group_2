package org.openjfx.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainApp extends Application implements EventHandler<ActionEvent>{
    Button launchButton;
    Scene launchScene, mainScene;
    Label welcome;
    Window window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button button = new Button("Hello, World!");
        button.setOnAction(e -> System.out.println("Button is Pressed"));

        window = primaryStage;

        welcome = new Label("Welcome!");

        launchButton = new Button("Launch Game");
        launchButton.setOnAction(this);

        StackPane layout = new StackPane();
        layout.getChildren().add(launchButton);
        layout.getChildren().add(welcome);

        Scene scene = new Scene(button, 800, 600);

        launchScene = new Scene(layout, 800, 600);

        //mainScene = new Scene(layout, 800, 600);

        VBox pane = new VBox(10);
        pane.setStyle("-fx-background-color: #383838");


        primaryStage.setScene(scene);
        primaryStage.setScene(launchScene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Surveillance Game");
        primaryStage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == launchButton) {
            System.out.println("Game Launched");
        }
    }

}
