package Strategies;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;

public class Strategy { // Template for making strategies, basic explo is an implemented example
    // this actually decides on moves, as name of method suggests
    public Moves decideOnMove(String[][] vision, int[] yx, Rotations rot, Variables vr) {
        return Moves.WALK;
    }

    public void printMappings() {
    }
    public void setBooleans(boolean b, boolean c){}
}
