package Launcher;

import Logic.GameController;

public class Launcher {

    public static void main(String[] args){
        GameController gm = new GameController(10,40);
        gm.printMap();
    }
}
