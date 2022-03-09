package org.openjfx.UI;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.nio.charset.StandardCharsets;

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
    //Colors
    javafx.scene.paint.Color white = javafx.scene.paint.Color.rgb(255, 255,255, 1);
    javafx.scene.paint.Color black = javafx.scene.paint.Color.rgb(0, 0,0, 1);

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
        gridPane.setMinWidth(width);
        gridPane.setMinHeight(height);


        Rectangle[][] rectArray = new Rectangle[width][height];





        for(int i = 0; i < rectArray.length; i++)
        {

            for(int j = 0; j < rectArray[0].length; j++)
            {
                rectArray[i][j] = new Rectangle(1300/width,1000/height);
                rectArray[i][j].setStroke(black);
                rectArray[i][j].setFill(white);
                GridPane.setConstraints(rectArray[i][j],i,j);
                gridPane.getChildren().add(rectArray[i][j]);
            }

        }

        //Tests that the gridPane does indeed extend further than the singular red rectangle
        gridPane.setOnMouseEntered(event -> System.out.println("Mouse entered!"));


        gridPane.setStyle("-fx-background-color: white; -fx-grid-lines-visible: true");

        borderPane = new BorderPane();

        borderPane.setTop(mainSceneTopMenu);
        borderPane.setCenter(gridPane);

        //gridPane.setOnMouseClicked(event -> colorRect(rectArray));
        gridPane.setOnMouseClicked(event -> clickGrid(event,rectArray));

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

    //Antiquated, functionality taken over by clickGrid
    public void colorRect(Rectangle[][] rectArray)
    {
        /* Left for posterity
        //Find out which rectangle is pressed on by dividing location by width/height of map
        int xPos = (int) (MouseInfo.getPointerInfo().getLocation().getX())/1300*width;
        int yPos = (int) (MouseInfo.getPointerInfo().getLocation().getY()/1000*height);
        System.out.println("x: "+xPos+"  y: "+yPos);
        rectArray[xPos][yPos].setFill(black);
        */
    }

    //Sets a clicked grid square as a wall
    public void clickGrid(javafx.scene.input.MouseEvent event, Rectangle[][] rectArray) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != gridPane) {
            // click on descendant node
            Integer colIndex = GridPane.getColumnIndex(clickedNode);
            Integer rowIndex = GridPane.getRowIndex(clickedNode);
            System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);
            rectArray[colIndex][rowIndex].setFill(black);
        }
    }





}
