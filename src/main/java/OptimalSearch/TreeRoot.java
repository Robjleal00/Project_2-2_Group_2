package OptimalSearch;

import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static java.util.Collections.max;
import static java.util.Collections.min;

public class TreeRoot {
    private final HashMap<Integer,ArrayList<Integer>> explored;
    private final HashMap<Integer,ArrayList<Integer>> walls;
    private final Rotations rot;
    private final int depth;
    private final int[] xy;
    private final Moves[] avaliableMoves = {Moves.WALK, Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND};
    private final int eyeRange;

    public TreeRoot(HashMap explored, HashMap walls, int[] xy, Rotations rot, int depth, int eyeRange) {
        this.explored = explored;
        this.walls = walls;
        this.xy = xy;
        this.rot = rot;
        this.depth = depth;
        this.eyeRange = eyeRange;
    }

    public Moves getMove() {
        ArrayList<Integer> values = new ArrayList<Integer>();
        for (int i = 0; i < avaliableMoves.length; i++) {
            values.add(new TreeNode(avaliableMoves[i], deepClone(explored), deepClone(walls), xy.clone(), rot, eyeRange).getValue(depth));
        }
        int result = max(values);
        // System.out.println(result);
        if (result == 0) {
            String[][]mindMap=giveMappings();
            HashMap<Integer,ArrayList<Integer>> explorationPoints=new HashMap<Integer,ArrayList<Integer>>();
            for(int i=0;i<mindMap.length;i++){
                for(int j=0;j<mindMap[0].length;j++){
                    if(mindMap[i][j]=="?"){
                        if(explorationPoints.containsKey(i-2)){
                                explorationPoints.get(i-2).add(j-2);
                        }else{
                            explorationPoints.put(i-2,new ArrayList<Integer>());
                            explorationPoints.get(i-2).add(j-2);
                        }
                    }
                }
            }
            //Now explorationPoints has all Quesiton marks transformed back into agents mapping
            //gotta add some kind of A* for him to get the shortest path and which move to output to go follow that
        }
        boolean allTheSame = true;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) != result) allTheSame = false;
        }
        if (allTheSame) {
            // System.out.println("ALL THE SAME AAAAAA");
            ArrayList<Integer> Reversevalues = new ArrayList<Integer>();
            for (int i = 0; i < avaliableMoves.length; i++) {
                Reversevalues.add(new ReverseTreeNode(avaliableMoves[i], deepClone(explored), deepClone(walls), xy.clone(), rot, eyeRange, result, 0).getValue(depth));
            }
            int Reverseresult = max(Reversevalues);
            //System.out.println(Reversevalues);
            return avaliableMoves[Reversevalues.indexOf(Reverseresult)];

        } else return avaliableMoves[values.indexOf(result)];

    }

    private HashMap<Integer, ArrayList<Integer>> deepClone(HashMap<Integer, ArrayList<Integer>> maptoCopy) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(maptoCopy);
        Type type = new TypeToken<HashMap<Integer, ArrayList<Integer>>>() {
        }.getType();
        HashMap<Integer, ArrayList<Integer>> cloned = gson.fromJson(jsonString, type);
        return cloned;
    }
    public String[][] giveMappings() {
        Set<Integer> keySet= explored.keySet();
        Integer[] exploredX=keySet.toArray(new Integer[keySet.size()]);
        int lowestXExplored= Integer.MAX_VALUE;
        int highestXExplored=Integer.MIN_VALUE;
        for(int i=0;i<exploredX.length;i++){
            int val=exploredX[i];
            if(val>highestXExplored)highestXExplored=val;
            if(val<lowestXExplored)lowestXExplored=val;
        }
        Set<Integer> wallkeySet= walls.keySet();
        Integer[] wallX=wallkeySet.toArray(new Integer[wallkeySet.size()]);
        int highestXWalls= Integer.MIN_VALUE;
        int lowestXWalls=Integer.MAX_VALUE;
        for(int i=0;i<wallX.length;i++){
            int val=wallX[i];
            if(val<lowestXWalls)lowestXWalls=val;
            if(val>highestXWalls)highestXWalls=val;
        }
        int lowestYExplored=Integer.MAX_VALUE;
        int highestYExplored=Integer.MIN_VALUE;
        for(int i=0;i<exploredX.length;i++){
            int lowest=min(explored.get(exploredX[i]));
            int highest=max(explored.get(exploredX[i]));
            if (lowest<lowestYExplored)lowestYExplored=lowest;
            if(highest>highestYExplored)highestYExplored=highest;
        }
        int lowestYWalls=Integer.MAX_VALUE;
        int highestYWalls=Integer.MIN_VALUE;
        for(int i=0;i<wallX.length;i++){
            int lowest=min(walls.get(wallX[i]));
            int highest=max(walls.get(wallX[i]));
            if (lowest<lowestYWalls)lowestYWalls=lowest;
            if(highest>highestYWalls)highestYWalls=highest;
        }
        int lowestXTotal=Math.min(lowestXExplored,lowestXWalls);
        int lowestYTotal=Math.min(lowestYWalls,lowestYExplored);
        int highestXTotal=Math.max(highestXExplored,highestXWalls);
        int highestYTotal=Math.max(highestYWalls,highestYExplored);
        int spanX=highestXTotal-lowestXTotal;
        int spanY=highestYTotal-lowestYTotal;
        String[][]mindMap=new String[spanY+3][spanX+3];
        for(int i :exploredX){
            ArrayList<Integer> array=explored.get(i);
            for(int j=0;j<array.size();j++){
                mindMap[array.get(j)-lowestYTotal+1][i-lowestXTotal+1]=" ";
            }
        }
        for(int i :wallX){
            ArrayList<Integer> array=walls.get(i);
            for(int j=0;j<array.size();j++){
                mindMap[array.get(j)-lowestYTotal+1][i-lowestXTotal+1]="W";
            }
        }
       // GameController printer=new GameController();
        for(int i=0;i<=spanY+2;i++){
            for(int j=0;j<=spanX+2;j++){
                boolean connected=false;
                if(mindMap[i][j]==null){
                    if(i>0){
                        if(mindMap[i-1][j]==" ")connected=true;
                    }
                    if(i<spanY){
                        if(mindMap[i+1][j]==" ")connected=true;
                    }
                    if(j>0){
                        if(mindMap[i][j-1]==" ")connected=true;
                    }
                    if(j<spanX){
                        if(mindMap[i][j+1]==" ")connected=true;
                    }
                    if(connected){
                        mindMap[i][j]="?";
                    } else mindMap[i][j]="X";
                }

            }
        }
        return mindMap;
        //printer.printArray(mindMap);
        //FIRST ONE IN MATRIX IS Y
        // SECOND ONE IN MATRIX IS X
        // GOTTA MOVE X's + LOWEST X TO REACH 0 SAME FOR Y

    }

}
