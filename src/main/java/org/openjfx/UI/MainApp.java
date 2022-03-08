package org.openjfx.UI;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
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
    GridPane gridPane;
    BorderPane borderPane;

    private static int height = 80;
    private static int width = 120;

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
        launchScene = new Scene(launchPane, 200, 200);

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

        //Layout for Main Scene ------------------------------------------------------------


        HBox mainSceneTopMenu = new HBox(20);
        mainSceneTopMenu.getChildren().addAll(menu, playButton, backButton, exitButton);

        //----------------------


        gridPane =  new GridPane();

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){

            }
        }

        for(int i = 0; i < width; i++) {
            ColumnConstraints column = new ColumnConstraints(10);
            gridPane.getColumnConstraints().add(column);
        }

        for(int i = 0; i < height; i++) {
            RowConstraints row = new RowConstraints(10);
            gridPane.getRowConstraints().add(row);
        }

        gridPane.setStyle("-fx-background-color: white; -fx-grid-lines-visible: true");

        borderPane = new BorderPane();

        borderPane.setTop(mainSceneTopMenu);
        borderPane.setCenter(gridPane);


        gridPane =  new GridPane();
        //gridPane.addColumn(width,);
        //gridPane.addRow(height, );
        for(int i = 0; i < width; i++) {
            ColumnConstraints column = new ColumnConstraints(40);
            gridPane.getColumnConstraints().add(column);
        }

        for(int i = 0; i < height; i++) {
            RowConstraints row = new RowConstraints(40);
            gridPane.getRowConstraints().add(row);
        }

        //----------------------


        mainScene = new Scene(borderPane, 1300, 1000);
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
