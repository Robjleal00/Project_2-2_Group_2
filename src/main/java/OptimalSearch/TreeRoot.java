package OptimalSearch;

import Config.Config;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import PathMaking.PathMaker;
import PathMaking.Point;
import Strategies.Constraints;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import Config.Variables;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.max;
import static java.util.Collections.min;

public class TreeRoot { //more stuff for basic explo
    private final HashMap<Integer,ArrayList<Integer>> explored;
    private final HashMap<Integer,ArrayList<Integer>> walls;
    private final HashMap<Integer,int[]>objects;
    private final Rotations rot;
    private final int depth;
    private final int[] xy;
    private final Moves[] avaliableMoves = {Moves.WALK, Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND};
    private final int eyeRange;
    boolean PATHMAKING=true;
    boolean DEBUG_DECISIONS;
    private Constraints constraints;
    private final Variables vr;
    private final ArrayList<Point> visitedPoints;

    public TreeRoot(HashMap explored, HashMap walls, int[] xy, Rotations rot, int depth, Constraints constraints, Variables vr, ArrayList<Point> visitedPoints,HashMap<Integer,int[]>objects) {
        this.explored = explored;
        this.walls = walls;
        this.xy = xy;
        this.rot = rot;
        this.depth = depth;
        this.eyeRange = vr.eyeRange();
        this.constraints=constraints;
        Config cf= new Config();
        this.DEBUG_DECISIONS = cf.DEBUG_DECISIONS;
        this.vr=vr;
        this.visitedPoints=visitedPoints;
        this.objects=objects;
    }

    public Moves getMove() {
        ArrayList<Double> values = new ArrayList<>();
        for (Moves avaliableMove : avaliableMoves) {
            values.add(new TreeNode(avaliableMove, deepClone(explored), deepClone(walls), xy.clone(), rot,constraints,vr).getValue(1,depth));
        }
        double result = max(values);
        if(DEBUG_DECISIONS) System.out.println(values);
        if(result==0){
            constraints.reset();
            values.clear();
            for (Moves avaliableMove : avaliableMoves) {
                values.add(new TreeNode(avaliableMove, deepClone(explored), deepClone(walls), xy.clone(), rot,constraints,vr).getValue(1,depth));
            }
        }
         result = max(values);
        if(PATHMAKING&&result==0){

            String[][]mindMap=giveMappings();
            if(hasPotential(mindMap)) {
                PathMaker pm = new PathMaker(explored, walls, mindMap, visitedPoints, vr, xy.clone(), rot);
                return pm.giveMove();
            }else{
                //System.out.println("NOTHING MORE TO EXPLORE HERE");
                if(!objects.isEmpty()){
                    //System.out.println("I KNOW A TELEPORTER THOUGH");
                    int numberOfTeleporters = objects.keySet().size();
                    int r;
                    if(numberOfTeleporters!=1) r = ThreadLocalRandom.current().nextInt(1,numberOfTeleporters+1);
                    else r=numberOfTeleporters;

                    int[] position = objects.get(r);
                    if(!itsNextToMe(position)) {
                        HashMap<Integer, ArrayList<Integer>> explorationPoints = giveExplorationPoints(position);
                        PathMaker pm = new PathMaker(explored, walls, explorationPoints, visitedPoints, vr, xy.clone(), rot);
                        return pm.giveMove();
                    }else {
                        //System.out.println("IM IN FRONT OF IT");
                        if(position[0]==xy[0]){
                            if(position[1]>xy[1]){
                                //System.out.println("I NEED TO BE LOOKING FORWARD");
                                switch(rot){
                                    case FORWARD -> {return Moves.USE_TELEPORTER;}
                                    case BACK -> {return Moves.TURN_AROUND;}
                                    case RIGHT -> {return Moves.TURN_LEFT;}
                                    case LEFT -> {return Moves.TURN_RIGHT;}
                                }
                            }
                            else {
                                //System.out.println("NEED TO BE LOOKING BACK");
                                switch(rot){
                                    case FORWARD -> {return Moves.TURN_AROUND;}
                                    case BACK -> {return Moves.USE_TELEPORTER;}
                                    case RIGHT -> {return Moves.TURN_RIGHT;}
                                    case LEFT -> {return Moves.TURN_LEFT;}
                                }
                            }
                        }
                        if(position[1]==xy[1]){
                            if(position[0]>xy[0]){
                                //System.out.println("I NEED TO BE LOOKING RIGHT");
                                switch(rot){
                                    case FORWARD -> {return Moves.TURN_RIGHT;}
                                    case BACK -> {return Moves.TURN_LEFT;}
                                    case RIGHT -> {return Moves.USE_TELEPORTER;}
                                    case LEFT -> {return Moves.TURN_AROUND;}
                                }
                            }
                            else {
                                //System.out.println("NEED TO BE LOOKING LEFT");
                                switch(rot){
                                    case FORWARD -> {return Moves.TURN_LEFT;}
                                    case BACK -> {return Moves.TURN_RIGHT;}
                                    case RIGHT -> {return Moves.TURN_AROUND;}
                                    case LEFT -> {return Moves.USE_TELEPORTER;}
                                }
                            }
                        }
                    }
                }
            }
        }
        return avaliableMoves[values.indexOf(result)];

    }
    boolean itsNextToMe(int[]pos){
        int[]mypos = xy;
        if(xy[0]==pos[0]){
            if(pos[1]==xy[1]+1)return true;
            if(pos[1]==xy[1]-1)return true;
        }
        if(xy[1]==pos[1]){
            if(pos[0]==xy[0]+1)return true;
            if(pos[0]==xy[0]-1)return true;
        }
        return false;

    }
    private HashMap<Integer,ArrayList<Integer>>giveExplorationPoints(int[] pos){
        ArrayList<Integer> one = new ArrayList<Integer>();
        ArrayList<Integer>two = new ArrayList<Integer>();
        one.add(pos[1]);
        two.add(pos[1]+1);
        two.add(pos[1]-1);
        HashMap<Integer,ArrayList<Integer>> mapping = new HashMap<>();
        mapping.put(pos[0],two);
        mapping.put(pos[0]-1,one);
        mapping.put(pos[0]+1,one);
        return mapping;
    }
    private boolean hasPotential(String[][]thing){
        int lenght = thing[0].length;
        for (String[] strings : thing) {
            for (int j = 0; j < lenght; j++) {
                if(strings[j].equals("?"))return true;
            }
        }
        return false;
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
        mindMap[0][2]=Integer.toString(lowestXTotal);
        return mindMap;
        //printer.printArray(mindMap);
        //FIRST ONE IN MATRIX IS Y
        // SECOND ONE IN MATRIX IS X
        // GOTTA MOVE X's + LOWEST X TO REACH 0 SAME FOR Y

    }

}
