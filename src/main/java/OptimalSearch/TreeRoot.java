package OptimalSearch;

import Enums.Moves;
import Enums.Rotations;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import static java.util.Collections.max;
import static java.util.Collections.min;

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
            values.add(new TreeNode(avaliableMoves[i],deepClone(explored),deepClone(walls),xy.clone(),rot,eyeRange).getValue(depth));
        }
            int result = max(values);
           // System.out.println(result);
            if(result==0){
                //System.out.println(" I EXPLORED EVERYHTING APPARENTLY");
                //System.out.println(explored);
                //System.out.println(walls);
            }
            boolean allTheSame=true;
            for(int i=0;i<values.size();i++){
                if(values.get(i)!=result)allTheSame=false;
            }
            if(allTheSame){
               // System.out.println("ALL THE SAME AAAAAA");
                ArrayList<Integer> Reversevalues = new ArrayList<Integer>();
                for (int i=0;i<avaliableMoves.length;i++){
                    Reversevalues.add(new ReverseTreeNode(avaliableMoves[i],deepClone(explored),deepClone(walls),xy.clone(),rot,eyeRange,result,0).getValue(depth));
                }
                int Reverseresult = max(Reversevalues);
                //System.out.println(Reversevalues);
                return avaliableMoves[Reversevalues.indexOf(Reverseresult)];

            }
            else return avaliableMoves[values.indexOf(result)];

    }
    private HashMap<Integer,ArrayList<Integer>> deepClone(HashMap<Integer,ArrayList<Integer>> maptoCopy){
        Gson gson = new Gson();
        String jsonString = gson.toJson(maptoCopy);
        Type type = new TypeToken<HashMap<Integer, ArrayList<Integer>>>(){}.getType();
        HashMap<Integer,ArrayList<Integer>> cloned = gson.fromJson(jsonString,type);
        return cloned;
    }

}
