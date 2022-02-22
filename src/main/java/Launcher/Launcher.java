package Launcher;

import Entities.Explorer;
import Enums.EntityType;
import Enums.Rotations;
import Logic.GameController;
import Strategies.BasicExplo;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Launcher {

    public static void main(String[] args) throws InterruptedException {
       GameController gm = new GameController(10,10,5);
        gm.printMap();
        gm.addEntity(new Explorer(EntityType.EXPLORER,gm,new BasicExplo()),1,1, Rotations.UP);
        gm.printMap();
        gm.init();







    }
}
