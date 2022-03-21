package Launcher;

import Config.Variables;
import Entities.Explorer;
import Enums.EntityType;
import Enums.Rotations;
import Logic.GameController;
import ObjectsOnMap.Teleporter;
import PathMaking.Point;
import Strategies.BasicExplo;

import java.util.ArrayList;

public class Launcher {

    public static void main(String[] args) throws InterruptedException {
        GameController gm = new GameController(21, 12);
        Variables vr = new Variables(1,5);
        gm.addVars(vr);
        gm.printMap();
        gm.addEntity(new Explorer(EntityType.EXPLORER, gm, new BasicExplo(),vr), 7, 4, Rotations.UP);
       // gm.addEntity(new Explorer(EntityType.EXPLORER, gm, new BasicExplo(),vr), 3, 4, Rotations.DOWN);
       // Teleporter t1 = new Teleporter(1,3 ,3,2,2);
       // Teleporter t2 = new Teleporter(2,5,5,9,9);
       // t1.addLink(t2);
       // gm.addObject(t1);
        //gm.addObject(t2);
       // gm.printMap();
        gm.init();


    }
}
