package Strategies;
import Config.Variables;
import Enums.*;
import java.util.Objects;

public class RandomExplo extends Strategy{
    private final Moves[] moves = {Moves.TURN_AROUND,Moves.TURN_LEFT, Moves.TURN_AROUND,Moves.WALK};
    @Override
    public Moves decideOnMove(String[][] vision, int[] xy, Rotations rot, Variables vr){
        int eyeRange = vision.length;
        int check = eyeRange-2;
        int random = (int) (Math.random()*3);
        if (!Objects.equals(vision[check][1], " ")) return moves[random];
        else{ random=(int) (Math.random()*4); return moves[random];}
    }
}
