package Strategies;

import Enums.Moves;
import Enums.Rotations;

public class Strategy {
    public Moves decideOnMove(String[][] vision, int[] yx, Rotations rot){
        return Moves.WALK;
    }
}
