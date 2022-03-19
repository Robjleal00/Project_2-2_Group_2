package Launcher;

import Config.Variables;
import Entities.Explorer;
import Enums.EntityType;
import Enums.Rotations;
import Logic.GameController;
import ObjectsOnMap.Teleporter;
import Strategies.BasicExplo;

public class Launcher {

    public static void main(String[] args) throws InterruptedException {
        GameController gm = new GameController(20, 20);
        Variables vr = new Variables(10,5);
        gm.addVars(vr);
        gm.printMap();
        gm.addEntity(new Explorer(EntityType.EXPLORER, gm, new BasicExplo(),vr), 8, 1, Rotations.UP);
       // Teleporter t1 = new Teleporter(1,3 ,3,2,2);
       // Teleporter t2 = new Teleporter(2,5,5,9,9);
       // t1.addLink(t2);
       // gm.addObject(t1);
        //gm.addObject(t2);
        gm.printMap();
        gm.init();
    }
}
