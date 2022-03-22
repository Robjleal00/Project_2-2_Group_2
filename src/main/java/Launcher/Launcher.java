package Launcher;

import Config.Variables;
import Entities.Explorer;
import Enums.EntityType;
import Enums.Rotations;
import Logic.GameController;
import ObjectsOnMap.Teleporter;
import PathMaking.Point;
import Strategies.BasicExplo;
import org.openjfx.UI.Area;
import org.openjfx.UI.FileReader;

import java.util.ArrayList;

import java.util.ArrayList;

public class Launcher {

    public static void main(String[] args) throws InterruptedException {
        GameController gm = new GameController(11, 12);
        FileReader fileReader = new FileReader();
        Variables vr = new Variables(2,5);
        gm.addVars(vr);
        gm.printMap();
        Area spawnArea = fileReader.getSpawnArea();
        int startX = spawnArea.getLeftBoundary();
        int startY = spawnArea.getBottomBoundary();
        int spawnAreaWidth = spawnArea.getRightBoundary()- spawnArea.getLeftBoundary();
        int spawnAreaHeight = spawnArea.getTopBoundary()- spawnArea.getBottomBoundary();
        ArrayList<int[]> filledSpots = new ArrayList<>();
        for(int i = 0; i < gm.getEntitiesSize(); i++)
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

       // Teleporter t1 = new Teleporter(1,3 ,3,2,2);
       // Teleporter t2 = new Teleporter(2,5,5,9,9);
       // t1.addLink(t2);
       // gm.addObject(t1);
        //gm.addObject(t2);
       // gm.printMap();
        gm.init();
    }


    private static boolean contains(ArrayList<int[]> array, int[] value){
        for(int[] val : array){
            if(val[0]==value[0]&&val[1]==value[1])return true;
        }
        return false;
    }
}
