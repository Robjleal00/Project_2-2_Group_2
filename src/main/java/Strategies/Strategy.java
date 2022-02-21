package Strategies;

import Enums.Moves;

public class Strategy {
    public Moves decideOnMove(String[][] vision){
        return Moves.WALK;
    }
}
