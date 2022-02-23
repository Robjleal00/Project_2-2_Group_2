package OptimalSearch;

import Enums.Moves;
import Enums.Rotations;

import java.util.ArrayList;
import java.util.HashMap;

import static java.util.Collections.max;

public class TreeRoot {
    private HashMap explored;
    private HashMap walls;
    private Rotations rot;
    private int depth;
    private int[]xy;
    private Moves[] avaliableMoves = {Moves.WALK,Moves.TURN_RIGHT,Moves.TURN_LEFT,Moves.TURN_AROUND};
    private int eyeRange;

    public TreeRoot( HashMap explored, HashMap walls, int[] xy, Rotations rot, int depth,int eyeRange){
        this.explored=explored;
        this.walls=walls;
        this.xy=xy;
        this.rot=rot;
        this.depth=depth;
        this.eyeRange=eyeRange;
    }
    public Moves getMove(){
        ArrayList<Integer> values = new ArrayList<Integer>();
            for (int i=0;i<avaliableMoves.length;i++){
            values.add(new TreeNode(avaliableMoves[i],(HashMap<Integer,ArrayList>)explored.clone(),(HashMap<Integer,ArrayList>)walls.clone(),xy.clone(),rot,eyeRange).getValue(depth));
        }
            int result = max(values);
            System.out.println(result);
            if(result==0){
                System.out.println(" I EXPLORED EVERYHTING APPARENTLY");
                System.out.println(explored);
                System.out.println(walls);
            }
            return avaliableMoves[values.indexOf(result)];

    }

}
