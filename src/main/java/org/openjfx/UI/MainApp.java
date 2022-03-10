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
    javafx.scene.paint.Color yellow = javafx.scene.paint.Color.rgb(255,255,0,1);

    FileReader fileReader = new FileReader();
    private static int height;
    private static int width;
    private Integer[] targetArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //TODO: format problem Sys.out returns Head: name, Value: =, then proceeds to give an error for targetArea and presumable walls too as = is not valid input
        /*
        fileReader.readFile("src/main/java/map/testmap.txt");
        height = fileReader.getHeight();
        width = fileReader.getWidth();
        targetArea = fileReader.getTargetArea();
        */
        height = 80;
        width = 120;

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

        //marks target area
        //TODO: replace this code to work with areas, writing this rn it's kinda dumb, I should've made it work with areas to begin with
        /*
        int x1 = targetArea[0];
        int y1 = targetArea[1];
        int xDist = targetArea[2]-targetArea[0];
        int yDist = targetArea[3]-targetArea[1];
        for(int i = x1; i < xDist ;i++)
        {
            for(int j = y1; j < yDist; j++)
            {
                rectArray[i][j].setFill(yellow);
            }
        }
        Placeholder below with hardcoded targetArea
        */
        int x1 = 20;
        int y1 = 25;
        int xDist = 25-20; //this is 5...
        int yDist = 45-40;
        //so i = x1= 20 is already > 5....
        for(int i = 0; i < xDist ;i++)
        {
            for(int j = 0; j < yDist; j++)
            {
                rectArray[x1+i][y1+j].setFill(yellow);
            }
        }




        gridPane.setStyle("-fx-background-color: white; -fx-grid-lines-visible: true");

        borderPane = new BorderPane();
        borderPane.setTop(mainSceneTopMenu);
        borderPane.setCenter(gridPane);
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

    //Sets a clicked grid square as a wall

    /**
     *
     * @param event event given as parameter in LINE 120 covers the mouse related event of the specified action type
     *              here: onMouseClicked, need to pass it on so that the position information is also passed on
     * @param rectArray to modify the grid itself we need to pass the rectangle array to the method so that it sets the
     *                  color accordingly
     * GridPane (not the object, the class) allows identification of the column and row intrinsically as long as an EVENT
     *                  type is passed as parameter, returns an integer
     */
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
