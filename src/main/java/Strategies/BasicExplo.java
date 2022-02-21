package Strategies;

import Enums.Moves;

public class BasicExplo extends Strategy{
    private Moves[] moves = {Moves.WALK, Moves.TURN_AROUND,Moves.TURN_LEFT,Moves.TURN_RIGHT};
    public BasicExplo(){


    }
    @Override
    public Moves decideOnMove(String[][] vision){
        double random = (Math.random()*4)-0.00000001;
        int decision = (int) random;
        return moves [decision];
    }
}
