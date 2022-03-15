package Launcher;

import Entities.Explorer;
import Enums.EntityType;
import Enums.Rotations;
import Logic.GameController;
import Strategies.BasicExplo;

public class Launcher {

    public static void main(String[] args) throws InterruptedException {
        GameController gm = new GameController(10, 15, 5);
        gm.printMap();
        gm.addEntity(new Explorer(EntityType.EXPLORER, gm, new BasicExplo()), 8, 1, Rotations.UP);
        gm.printMap();
        gm.init();


    }
}
