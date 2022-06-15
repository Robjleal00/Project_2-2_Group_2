package Launcher;

import BrickAndMortar.BrickMortar;
import Config.Variables;
import Entities.Explorer;
import Enums.EntityType;
import Enums.Rotations;
import Logic.GameController;
import ObjectsOnMap.Teleporter;
import OptimalSearch.TreeRoot;
import PathMaking.Point;
import Strategies.BasicExplo;
import org.openjfx.UI.Area;
import org.openjfx.UI.FileReader;
import org.openjfx.UI.MainApp;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.ArrayList;

public class Launcher {
    // creates a game controller from a file path, use for MainApp
    public GameController makeGame(String filePath, MainApp app){
        //GM needs height width
        // walls added and stuff
        FileReader fileReader = new FileReader();
        fileReader.readFile(filePath);
        GameController gm = new GameController(fileReader.getHeight(),fileReader.getWidth(),app);

        Variables vr = new Variables(fileReader.getBaseSpeedGuard(),fileReader.getDistanceViewing());
        gm.addVars(vr);
        addGuards(fileReader,gm,vr);
        ArrayList<Area> walls = fileReader.getWalls();
        ArrayList<Teleporter> telepors = fileReader.getTeleporters();
        for(Teleporter tp : telepors){
            gm.addObject(tp);
        }
        makeWalls(walls,gm);
        return gm;


    }
    private void makeWalls(ArrayList<Area>walls, GameController gm){
        for(Area a : walls){
            int botX=a.getLeftBoundary();
            int botY=a.getBottomBoundary();
            int topX=a.getRightBoundary();
            int topY=a.getTopBoundary();
            gm.addWall(botX,botY,topX,topY);
        }

    }
    private void addGuards(FileReader fl, GameController gm,Variables vr){
        int number = fl.getNumberOfGuards();
        Area spawnArea = fl.getSpawnArea();
        int startX = spawnArea.getLeftBoundary();
        int startY = spawnArea.getBottomBoundary();
        int spawnAreaWidth = spawnArea.getRightBoundary()- spawnArea.getLeftBoundary();
        int spawnAreaHeight = spawnArea.getTopBoundary()- spawnArea.getBottomBoundary();
        ArrayList<int[]> filledSpots = new ArrayList<>();
        for(int i = 0; i < number; i++)
        {
            int randomH = (int) (Math.random()*spawnAreaHeight);
            int randomL = (int) (Math.random()*spawnAreaWidth);
            int[] pair = {randomH, randomL};
            //contains or !contains
            while(contains(filledSpots, pair))
            {
                randomH = (int) (Math.random()*spawnAreaHeight);
                randomL = (int) (Math.random()*spawnAreaWidth);
                pair = new int[]{randomH, randomL};
            }
            double rotation = Math.random();
            if(rotation < 0.25)
            {
                gm.addEntity(new Explorer(EntityType.EXPLORER, gm, new BasicExplo(), vr), startY +randomH, startX+randomL, Rotations.DOWN);
            }
            else if(rotation > 0.25 && rotation < 0.5)
            {
                gm.addEntity(new Explorer(EntityType.EXPLORER, gm, new BasicExplo(), vr), startY + randomH, startX +randomL, Rotations.UP);
            }
            else if(rotation > 0.5 && rotation < 0.75)
            {
                gm.addEntity(new Explorer(EntityType.EXPLORER, gm, new BasicExplo(), vr), startY+ randomH, startX+randomL, Rotations.LEFT);
            }
            else
            {
                gm.addEntity(new Explorer(EntityType.EXPLORER, gm, new BasicExplo(), vr), startY+randomH, startX+randomL, Rotations.RIGHT);
            }
            int[] takenSpot = {randomH, randomL};
            filledSpots.add(takenSpot);
        }
    }
    // easy launching for testing
    public static void main(String[] args) throws InterruptedException {
        GameController gm = new GameController(11, 20);
        Variables vr = new Variables(1,5,5,10);
        gm.addVars(vr);
        gm.printMap();
        //TODO: NEW STUFF: TO FIX
        gm.addEntity(new Explorer(EntityType.EXPLORER,gm,new BrickMortar(),vr),3,1,Rotations.DOWN);
        Teleporter t1 = new Teleporter(1,3 ,3,8,8);
        gm.addObject(t1);
        gm.addWall(0,5,19,5);
        //gm.init();
        gm.print("D");
        gm.print(12);

    }
 /*
 gives testing map, do what u want here
  */
    public GameController giveTest(MainApp app){
        GameController gm = new GameController(11, 20,app);
        FileReader fileReader = new FileReader();
        Variables vr = new Variables(1,5,5,10);
        gm.addVars(vr);
        gm.printMap();
        gm.addEntity(new Explorer(EntityType.EXPLORER,gm,new BasicExplo(),vr),3,1,Rotations.DOWN);
        Teleporter t1 = new Teleporter(1,3 ,3,8,8);
        // Teleporter t2 = new Teleporter(2,5,5,9,9);
        // t1.addLink(t2);
        gm.addObject(t1);
        //gm.addObject(t2);
        // gm.printMap();
        gm.addWall(0,5,19,5);
        return gm;
    }

    private static boolean contains(ArrayList<int[]> array, int[] value){
        for(int[] val : array){
            if(val[0]==value[0]&&val[1]==value[1])return true;
        }
        return false;
    }
}
