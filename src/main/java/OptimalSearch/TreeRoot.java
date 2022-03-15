package OptimalSearch;

import Enums.Moves;
import Enums.Rotations;
import Strategies.Constraints;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
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
    boolean TESTING=true;
    private Constraints constraints;

    public TreeRoot(HashMap explored, HashMap walls, int[] xy, Rotations rot, int depth, int eyeRange,Constraints constraints) {
        this.explored = explored;
        this.walls = walls;
        this.xy = xy;
        this.rot = rot;
        this.depth = depth;
        this.eyeRange = eyeRange;
        this.constraints=constraints;
    }

    public Moves getMove() {
        ArrayList<Integer> values = new ArrayList<>();
        for (Moves avaliableMove : avaliableMoves) {
            values.add(new TreeNode(avaliableMove, deepClone(explored), deepClone(walls), xy.clone(), rot, eyeRange,constraints).getValue(depth));
        }
        int result = max(values);
        // System.out.println(result);
        if(result==0){
            constraints.reset();
            values.clear();
            for (Moves avaliableMove : avaliableMoves) {
                values.add(new TreeNode(avaliableMove, deepClone(explored), deepClone(walls), xy.clone(), rot, eyeRange,constraints).getValue(depth));
            }
        }
        if(TESTING){
            String[][]mindMap=giveMappings();
            HashMap<Integer,ArrayList<Integer>> explorationPoints= new HashMap<>();
            int fix=Integer.parseInt(mindMap[0][0]);
            int totalY=Integer.parseInt(mindMap[0][1]);
            int totalX=Integer.parseInt(mindMap[0][2]);

            for(int i=0;i<mindMap.length;i++){
                for(int j=0;j<mindMap[0].length;j++){
                    if(Objects.equals(mindMap[i][j], "?")){
                        if(explorationPoints.containsKey(j+fix)){
                                explorationPoints.get(j+fix).add((i+fix)*-1);
                        }else{
                            explorationPoints.put(j+fix, new ArrayList<>());
                            explorationPoints.get(j+fix).add((i+fix)*-1);
                        }
                    }
                }
            }
            //System.out.println(explorationPoints);
            //Now explorationPoints has all question marks transformed back into agents mapping
            //gotta add some kind of path-finding for him to get the shortest path and which move to output to go follow that
        }
        boolean allTheSame = true;
        for (Integer value : values) {
            if (value != result) {
                allTheSame = false;
                break;
            }
        }
        if (allTheSame) {
            // System.out.println("ALL THE SAME AAAAAA");
            ArrayList<Integer> Reversevalues = new ArrayList<>();
            for (Moves avaliableMove : avaliableMoves) {
                Reversevalues.add(new ReverseTreeNode(avaliableMove, deepClone(explored), deepClone(walls), xy.clone(), rot, eyeRange, result, 0).getValue(depth));
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
        return gson.fromJson(jsonString, type);
    }
    public String[][] giveMappings() {
        Set<Integer> keySet= explored.keySet();
        Integer[] exploredX=keySet.toArray(new Integer[0]);
        int lowestXExplored= Integer.MAX_VALUE;
        int highestXExplored=Integer.MIN_VALUE;
        for (int val : exploredX) {
            if (val > highestXExplored) highestXExplored = val;
            if (val < lowestXExplored) lowestXExplored = val;
        }
        Set<Integer> wallkeySet= walls.keySet();
        Integer[] wallX=wallkeySet.toArray(new Integer[0]);
        int highestXWalls= Integer.MIN_VALUE;
        int lowestXWalls=Integer.MAX_VALUE;
        for (int val : wallX) {
            if (val < lowestXWalls) lowestXWalls = val;
            if (val > highestXWalls) highestXWalls = val;
        }
        int lowestYExplored=Integer.MAX_VALUE;
        int highestYExplored=Integer.MIN_VALUE;
        for (Integer x : exploredX) {
            int lowest = min(explored.get(x));
            int highest = max(explored.get(x));
            if (lowest < lowestYExplored) lowestYExplored = lowest;
            if (highest > highestYExplored) highestYExplored = highest;
        }
        int lowestYWalls=Integer.MAX_VALUE;
        int highestYWalls=Integer.MIN_VALUE;
        for (Integer x : wallX) {
            int lowest = min(walls.get(x));
            int highest = max(walls.get(x));
            if (lowest < lowestYWalls) lowestYWalls = lowest;
            if (highest > highestYWalls) highestYWalls = highest;
        }
        int lowestXTotal=Math.min(lowestXExplored,lowestXWalls);
        int lowestYTotal=Math.min(lowestYWalls,lowestYExplored);
        int highestXTotal=Math.max(highestXExplored,highestXWalls);
        int highestYTotal=Math.max(highestYWalls,highestYExplored);
        int spanX=highestXTotal-lowestXTotal;
        int spanY=highestYTotal-lowestYTotal;
        String[][]mindMap=new String[spanY+5][spanX+5];
        for(int i :exploredX){
            ArrayList<Integer> array=explored.get(i);
            for (Integer integer : array) {
                mindMap[((integer - highestYTotal) * -1) + 2][i - lowestXTotal + 2] = " ";
            }
        }
        for(int i :wallX){
            ArrayList<Integer> array=walls.get(i);
            for (Integer integer : array) {
                mindMap[((integer - highestYTotal) * -1) + 2][i - lowestXTotal + 2] = "W";
            }
        }
        for(int i=0;i<=spanY+4;i++){
            for(int j=0;j<=spanX+4;j++){
                boolean connected=false;
                if(mindMap[i][j]==null){
                    if(i>0){
                        if(Objects.equals(mindMap[i - 1][j], " "))connected=true;
                    }
                    if(i<spanY){
                        if(Objects.equals(mindMap[i + 1][j], " "))connected=true;
                    }
                    if(j>0){
                        if(Objects.equals(mindMap[i][j - 1], " "))connected=true;
                    }
                    if(j<spanX){
                        if(Objects.equals(mindMap[i][j + 1], " "))connected=true;
                    }
                    if(connected){
                        mindMap[i][j]="?";
                    } else mindMap[i][j]="X";
                }

            }
        }
        mindMap[0][0]="-3";
        mindMap[0][1]=Integer.toString(highestYTotal);
        mindMap[0][2]=Integer.toString(highestXTotal);
        return mindMap;
        //printer.printArray(mindMap);
        //FIRST ONE IN MATRIX IS Y
        // SECOND ONE IN MATRIX IS X
        // GOTTA MOVE X's + LOWEST X TO REACH 0 SAME FOR Y

    }

}
