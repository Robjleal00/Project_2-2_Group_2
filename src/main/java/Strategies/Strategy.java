package Strategies;

import Config.Variables;
import Entities.Intruder;
import Entities.Explorer;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;

public class Strategy { // Template for making strategies, basic explo is an implemented example
    // this actually decides on moves, as name of method suggests
    public Moves decideOnMove(String[][] vision, int[] yx, Rotations rot, Variables vr) {
        return Moves.WALK;
    }

    public Moves decideOnMoveIntruder(String[][] vision, int[] xy, Rotations rot, Variables vr, GameController gm, Intruder intruder){ return Moves.WALK;}
    public void printMappings() {
    }
    public void setBooleans(boolean b, boolean c){}
    public void setAgent(Explorer e){}
    public void teleported(){}
}