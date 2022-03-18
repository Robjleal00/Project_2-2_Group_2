package Strategies;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;

public class Strategy {
    public Moves decideOnMove(String[][] vision, int[] yx, Rotations rot, Variables vr) {
        return Moves.WALK;
    }

    public void printMappings() {
    }

}
