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

public class MainApp extends Application {
    Button launchButton;
    Button playButton;
    Button backButton;
    Button exitButton;
    Scene launchScene, mainScene;
    Label welcome;
    Label menu;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        welcome = new Label("WELCOME!");

        launchButton = new Button("LAUNCH GAME");
        launchButton.setOnAction(e -> primaryStage.setScene(mainScene));

        //Layout for Launch Scene
        VBox launchPane = new VBox(20);
        launchPane.setStyle("-fx-background-color: #383838");
        welcome.setStyle("-fx-background-color: #FFFFFF");
        launchPane.getChildren().addAll(welcome, launchButton);
        launchScene = new Scene(launchPane, 800, 600);

        menu = new Label("MENU");

        playButton = new Button("PLAY");
        playButton.setOnAction(e -> System.out.println("Game is Played"));
        
        backButton = new Button("BACK TO LAUNCH");
        backButton.setOnAction(e -> primaryStage.setScene(launchScene));

        exitButton = new Button("EXIT APPLICATION");
        exitButton.setOnAction(e -> {
            closeProgram(primaryStage);
        });

        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            closeProgram(primaryStage);
        });

        //Layout for Main Scene
        HBox mainSceneLayout = new HBox(20);
        mainSceneLayout.getChildren().addAll(menu, playButton, backButton, exitButton);

        mainScene = new Scene(mainSceneLayout, 800, 600);

        primaryStage.setScene(launchScene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Surveillance Game");
        primaryStage.show();
    }


    public void closeProgram(Stage primaryStage) {
        // TODO
        // What we need to do is to properly save the state that the user is in when playing the game
        // That way if we they actually leave the game, we have some states saved for progress.
        System.out.println("FILE IS SAVED!");

        boolean result = exitAppHandler.exitApp("EXIT APPLICATION", "ARE YOU SURE YOU WANT TO CLOSE THE APPLICATION?");
        System.out.println(result);
        if(result == true){
            System.out.println("Goodbye!");
            primaryStage.close();
        }
    }

}
