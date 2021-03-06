package org.openjfx.UI;

import Config.Variables;
import Entities.Entity;
import Entities.Explorer;
import Enums.EntityType;
import Enums.Rotations;
import Launcher.Launcher;
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
import javafx.scene.paint.ImagePattern;
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

    // AGENT : RED TRIANGLE
    protected Image surveillanceIMG1, agentUP, agentDOWN, agentLEFT, agentRIGHT;
    // INTRUDER : ORANGE TRIANGLE
    protected Image intruderUP, intruderDOWN, intruderLEFT, intruderRIGHT;
    // GUARD: BLUE TRIANGLE
    protected Image guardUP, guardDOWN, guardRIGHT, guardLEFT;
    protected Image teleport_icon;


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

        // LOAD IMAGES FOR AGENT: RED TRIANGLE (BASIC EXPLO)
        FileInputStream input_stream2 = new FileInputStream("src/main/java/Images/agentDOWN.png");
        agentDOWN = new Image(input_stream2);
        FileInputStream input_stream3 = new FileInputStream("src/main/java/Images/agentUP.png");
        agentUP = new Image(input_stream3);
        FileInputStream input_stream4 = new FileInputStream("src/main/java/Images/agentLEFT.png");
        agentLEFT = new Image(input_stream4);
        FileInputStream input_stream5 = new FileInputStream("src/main/java/Images/agentRIGHT.png");
        agentRIGHT = new Image(input_stream5);

        // LOAD IMAGES FOR INTRUDER: ORANGE TRIANGLE
        FileInputStream input_stream6 = new FileInputStream("src/main/java/Images/intruderDOWN.png");
        intruderDOWN = new Image(input_stream6);
        FileInputStream input_stream7 = new FileInputStream("src/main/java/Images/intruderUP.png");
        intruderUP = new Image(input_stream7);
        FileInputStream input_stream8 = new FileInputStream("src/main/java/Images/intruderLEFT.png");
        intruderLEFT = new Image(input_stream8);
        FileInputStream input_stream9 = new FileInputStream("src/main/java/Images/intruderRIGHT.png");
        intruderRIGHT = new Image(input_stream9);

        // LOAD IMAGES FOR GUARD: BLUE TRIANGLE
        FileInputStream input_stream10 = new FileInputStream("src/main/java/Images/guardDOWN.png");
        guardDOWN = new Image(input_stream10);
        FileInputStream input_stream11 = new FileInputStream("src/main/java/Images/guardUP.png");
        guardUP = new Image(input_stream11);
        FileInputStream input_stream12 = new FileInputStream("src/main/java/Images/guardLEFT.png");
        guardLEFT = new Image(input_stream12);
        FileInputStream input_stream13 = new FileInputStream("src/main/java/Images/guardRIGHT.png");
        guardRIGHT = new Image(input_stream13);

        FileInputStream input_stream14 = new FileInputStream("src/main/java/Images/teleport_icon.png");
        teleport_icon = new Image(input_stream14);


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
        Launcher launcher = new Launcher();
        // GameController gm = launcher.makeGame(filePath,this);
        GameController gm = launcher.giveTest(this);
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
                if (map[j][i].contains("W")) {
                    rectArray[i][j].setFill(black);
                }
                if (map[j][i].contains(" ")) {
                    rectArray[i][j].setFill(white);
                }
                //ADD THE ICONS FOR AGENT INTO GUI
                if (map[j][i].contains("E^")) {
                    rectArray[i][j].setFill(new ImagePattern(agentUP));
                }
                if (map[j][i].contains("E>")) {
                    rectArray[i][j].setFill(new ImagePattern(agentRIGHT));
                }
                if (map[j][i].contains("E<")) {
                    rectArray[i][j].setFill(new ImagePattern(agentLEFT));
                }
                if (map[j][i].contains("Ed")) {
                    rectArray[i][j].setFill(new ImagePattern(agentDOWN));
                }

                //ADD THE ICONS FOR GUARD INTO GUI
                if (map[j][i].contains("G^")) {
                    rectArray[i][j].setFill(new ImagePattern(guardUP));
                }
                if (map[j][i].contains("G>")) {
                    rectArray[i][j].setFill(new ImagePattern(guardRIGHT));
                }
                if (map[j][i].contains("G<")) {
                    rectArray[i][j].setFill(new ImagePattern(guardLEFT));
                }
                if (map[j][i].contains("Gd")) {
                    rectArray[i][j].setFill(new ImagePattern(guardDOWN));
                }

                //ADD THE ICONS FOR INTRUDER INTO GUI
                if (map[j][i].contains("I^")) {
                    rectArray[i][j].setFill(new ImagePattern(intruderUP));
                }
                if (map[j][i].contains("I>")) {
                    rectArray[i][j].setFill(new ImagePattern(intruderRIGHT));
                }
                if (map[j][i].contains("I<")) {
                    rectArray[i][j].setFill(new ImagePattern(intruderLEFT));
                }
                if (map[j][i].contains("Id")) {
                    rectArray[i][j].setFill(new ImagePattern(intruderDOWN));
                }

                if (map[j][i].contains("T")) {
                    rectArray[i][j].setFill(new ImagePattern(teleport_icon));
                }
                if (map[j][i].contains("P")) {
                  //  rectArray[i][j].setFill(new ImagePattern(champagne_toast));
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