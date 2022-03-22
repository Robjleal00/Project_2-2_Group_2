package org.openjfx.UI;
import Config.Variables;
import Entities.Entity;
import Entities.Explorer;
import Enums.EntityType;
import Enums.Rotations;
import Logic.GameController;
import Strategies.BasicExplo;
import javafx.application.Application;
import javafx.concurrent.Task;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

public class MainApp extends Application {
    Button chooseMap, start;
    Button playButton;
    Button backButton;
    Button exitButton;
    Scene launchScene, mainScene;
    Label menu;
    GridPane gridPane;
    BorderPane borderPane;
    Label game;
    VBox launchPane;

    protected Image surveillanceIMG1, triangleUP, triangleDOWN, triangleLEFT, triangleRIGHT;

    private ArrayList<Entity> entities;

    File fileMap;
    String filePath;
    FileChooser fileChooser = new FileChooser();
    FileReader fileReader = new FileReader();

    private static int height;
    private static int width;
    private Area targetArea;
    private ArrayList<Area> walls;


    //Colors
    javafx.scene.paint.Color white = javafx.scene.paint.Color.rgb(255, 255,255, 1);
    javafx.scene.paint.Color black = javafx.scene.paint.Color.rgb(0, 0,0, 1);
    javafx.scene.paint.Color yellow = javafx.scene.paint.Color.rgb(255,255,0,1);
    javafx.scene.paint.Color red = javafx.scene.paint.Color.rgb(255, 0, 0, 1);



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //TODO: format problem Sys.out returns Head: name, Value: =, then proceeds to give an error for targetArea and presumable walls too as = is not valid input


        //chooseMap = new Button("Select a map");

        //FINISH THIS --------------------------------------------------
        game = new Label("Surveillance game");
        game.setFont(new Font("Arial", 40));
        game.setAlignment(Pos.CENTER_RIGHT);
        //game.setTextAlignment(TextAlignment.CENTER);

        FileInputStream input_stream1 = new FileInputStream("src/main/java/Images/surveillanceImage.png");
        surveillanceIMG1 = new Image(input_stream1);
        ImageView imageView1 = new ImageView();
        imageView1.setImage(surveillanceIMG1);

        //FileInputStream input_stream2 = new FileInputStream("src/main/java/Images/TriangleDOWN.png");
        triangleDOWN = new Image("src/main/java/Images/TriangleDOWN.png");
        //ImageView agentDOWN = new ImageView();
        //agentDOWN.setImage(triangleDOWN);

        //FileInputStream input_stream3 = new FileInputStream("src/main/java/Images/TriangleUP.png");
        triangleUP = new Image("src/main/java/Images/TriangleUP.png");
        //ImageView agentUP = new ImageView();
        //agentUP.setImage(triangleUP);

        //FileInputStream input_stream4 = new FileInputStream("src/main/java/Images/TriangleLEFT.png");
        triangleLEFT = new Image("src/main/java/Images/TriangleLEFT.png");
        /*ImageView agentLEFT = new ImageView();
        agentLEFT.setImage(triangleLEFT);*/

        //FileInputStream input_stream5 = new FileInputStream("src/main/java/Images/TriangleRIGHT.png");
        triangleRIGHT = new Image("src/main/java/Images/TriangleRIGHT.png");
        /*ImageView agentRIGHT = new ImageView();
        agentRIGHT.setImage(triangleRIGHT);*/


        chooseMap = new Button("Select a map");
        chooseMap.setOnAction(e -> {
            fileMap = fileChooser.showOpenDialog(primaryStage);
            if (fileMap != null){
                System.out.println(fileMap.getAbsolutePath());
                filePath = fileMap.getAbsolutePath();

            }
        });

        start = new Button("Start program");
        start.setAlignment(Pos.CENTER);
        start.setOnAction(e -> {
            primaryStage.setScene(mainScene);
        });


        //Layout for Launch Scene
        launchPane = new VBox(20);
        launchPane.setStyle("-fx-background-color: white");
        launchPane.getChildren().addAll(game, imageView1, chooseMap, start);
        launchPane.setAlignment(Pos.CENTER);
        launchScene = new Scene(launchPane, 400, 350);



        //Layout for Main Scene ------------------------------------------------------------
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


        HBox mainSceneTopMenu = new HBox(20);
        mainSceneTopMenu.getChildren().addAll(menu, playButton, backButton, exitButton);
        mainSceneTopMenu.setAlignment(Pos.CENTER_RIGHT);

        borderPane = new BorderPane();
        borderPane.setTop(mainSceneTopMenu);


        playButton.setOnAction(e -> {
            try {
                startGame();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });



        //----------------------


        mainScene = new Scene(borderPane, 1300, 1000);
        primaryStage.setScene(launchScene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Surveillance Game");

        primaryStage.show();
    }
    public void startGame() throws InterruptedException {
        GameController gm = new GameController(10, 10,this);
        Variables vr = new Variables(1,5);
        gm.addVars(vr);
        gm.printMap();
        gm.addEntity(new Explorer(EntityType.EXPLORER, gm, new BasicExplo(),vr), 7, 4, Rotations.UP);
            Task<Void> task = new Task<>(){
                @Override
                protected Void call() throws InterruptedException {
                    gm.init();
                    return null;
                }
            };

         new Thread(task).start();


    }
    public void update(String[][]map){
        borderPane.setCenter(createGrid(map));
    }


    public void readMapFile(String path){
        fileReader.readFile(path);
        height = fileReader.getHeight();
        width = fileReader.getWidth();
        targetArea = fileReader.getTargetArea();
        walls = fileReader.getWalls();
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
     *  event event given as parameter in LINE 120 covers the mouse related event of the specified action type
     *              here: onMouseClicked, need to pass it on so that the position information is also passed on
     *rectArray to modify the grid itself we need to pass the rectangle array to the method so that it sets the
     *                  color accordingly
     * GridPane (not the object, the class) allows identification of the column and row intrinsically as long as an EVENT
     *                  type is passed as parameter, returns an integer
     */
    /*public void clickGrid(javafx.scene.input.MouseEvent event, Rectangle[][] rectArray) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != gridPane) {
            // click on descendant node
            Integer colIndex = GridPane.getColumnIndex(clickedNode);
            Integer rowIndex = GridPane.getRowIndex(clickedNode);
            System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);
            rectArray[colIndex][rowIndex].setFill(black);
        }
    }*/

    public GridPane createGrid(String[][]map){
        height = map.length;
        width = map[0].length;
        gridPane =  new GridPane();
        gridPane.setMinWidth(width);
        gridPane.setMinHeight(height);
        Rectangle[][] rectArray = new Rectangle[width][height];
        for(int i = 0; i < rectArray.length; i++) {
            for (int j = 0; j < rectArray[0].length; j++) {
                rectArray[i][j] = new Rectangle(1300 / width, 1000 / height);
                rectArray[i][j].setStroke(white);
                rectArray[i][j].setStrokeWidth(0);
                if (map[i][j].contains("W")) {
                    rectArray[i][j].setFill(black);
                }
                if (map[i][j].contains(" ")) {
                    rectArray[i][j].setFill(white);
                }
                if (map[i][j].contains("E")) {//E^ UP E> RIGHT E< LEFT Ed DOWN
                    rectArray[i][j].setFill(yellow);
                }
                GridPane.setConstraints(rectArray[i][j], i, j);
                gridPane.getChildren().add(rectArray[i][j]);
            }

        }
        gridPane.setStyle("-fx-background-color: white; -fx-grid-lines-visible: false");
        //gridPane.setOnMouseClicked(event -> clickGrid(event,rectArray));
        return gridPane;
    }

    /* public void spawnIntruder(int numIntruders) {
        int intruderWidth = 10;
        int intruderHeight = 10;
        Rectangle[][] agent = new Rectangle[intruderWidth][intruderHeight];
        for(int i = 0; i < intruderWidth ;i++)
        {
            for(int j = 0; j < intruderHeight; j++)
            {
                agent[intruderWidth+i][intruderHeight+j].setFill(red);
            }
        }
        gridPane.getChildren().add(agent[intruderWidth][intruderHeight]);
    }
    */

    public void displayAgent(ArrayList<Entity> entities){
        Entity currentEntity;

        for(int i = 0; i < entities.size(); i++){
            currentEntity = entities.get(i);


            //currentEntity.getCurrentRotation();
            //int x_Entity = currentEntity.getX();
            //currentEntity.getY();
        }

    }

    public void spawnIntruder(int numIntruders) {
        int i;
        //TODO Here we need to create however many agents we choose there to be and spawn them randomly
        for (i = 0; i <= numIntruders; i++ ){
            System.out.println("Number of Agents = " + i);
        }
        Rectangle agent = new Rectangle(100, 100, 20, 20);

        agent.setFill(red);

        gridPane.getChildren().add(agent);
    }




}