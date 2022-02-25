package Strategies;

import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import OptimalSearch.TreeRoot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static java.util.Collections.max;
import static java.util.Collections.min;


public class BasicExplo extends Strategy {
    private final Moves[] moves = {Moves.WALK, Moves.TURN_AROUND, Moves.TURN_LEFT, Moves.TURN_RIGHT};
    private final HashMap<Integer, ArrayList<Integer>> explored;
    private final HashMap<Integer, ArrayList<Integer>> walls;

    public BasicExplo() {
        explored = new HashMap<Integer, ArrayList<Integer>>();
        walls = new HashMap<Integer, ArrayList<Integer>>();
    }

    @Override
    public Moves decideOnMove(String[][] vision, int[] xy, Rotations rot) {
        updateExploration(vision, xy, rot);
        //System.out.println(explored);
        //System.out.println(walls);
        TreeRoot root = new TreeRoot(deepClone(explored), deepClone(walls), xy.clone(), rot, 5, vision.length);
        Moves decision = root.getMove();
        return decision;
    }

    public int updateExploration(String[][] vision, int[] xy, Rotations rot) {
        int informationGain = 0;
        int eyeRange = vision.length;
        int currentX = xy[0];
        int currentY = xy[1];
        for (int i = 0; i < 5; i++) { //i= upfront
            for (int j = -1; j < 2; j++) { //j==sideways
                int h = eyeRange - (i + 1);
                int l = j + 1;
                switch (rot) {
                    case FORWARD -> {
                        if (vision[h][l] != "X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX + j)) {
                                    if (!explored.get(currentX + j).contains(currentY + i)) {
                                        explored.get(currentX + j).add(currentY + i);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX + j, new ArrayList<Integer>());
                                    explored.get(currentX + j).add(currentY + i);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX + j)) {
                                    if (!walls.get(currentX + j).contains(currentY + i)) {
                                        walls.get(currentX + j).add(currentY + i);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX + j, new ArrayList<Integer>());
                                    walls.get(currentX + j).add(currentY + i);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case BACK -> {
                        if (vision[h][l] != "X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX - j)) {
                                    if (!explored.get(currentX - j).contains(currentY - i)) {
                                        explored.get(currentX - j).add(currentY - i);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX - j, new ArrayList<Integer>());
                                    explored.get(currentX - j).add(currentY - i);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX - j)) {
                                    if (!walls.get(currentX - j).contains(currentY - i)) {
                                        walls.get(currentX - j).add(currentY - i);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX - j, new ArrayList<Integer>());
                                    walls.get(currentX - j).add(currentY - i);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case LEFT -> {
                        if (vision[h][l] != "X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX - i)) {
                                    if (!explored.get(currentX - i).contains(currentY + j)) {
                                        explored.get(currentX - i).add(currentY + j);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX - i, new ArrayList<Integer>());
                                    explored.get(currentX - i).add(currentY + j);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX - i)) {
                                    if (!walls.get(currentX - i).contains(currentY + j)) {
                                        walls.get(currentX - i).add(currentY + j);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX - i, new ArrayList<Integer>());
                                    walls.get(currentX - i).add(currentY + j);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case RIGHT -> {
                        if (vision[h][l] != "X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX + i)) {
                                    if (!explored.get(currentX + i).contains(currentY - j)) {
                                        explored.get(currentX + i).add(currentY - j);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX + i, new ArrayList<Integer>());
                                    explored.get(currentX + i).add(currentY - j);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX + i)) {
                                    if (!walls.get(currentX + i).contains(currentY - j)) {
                                        walls.get(currentX + i).add(currentY - j);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX + i, new ArrayList<Integer>());
                                    walls.get(currentX + i).add(currentY - j);
                                    informationGain++;
                                }
                            }
                        }
                    }


                }
            }
        }
        return informationGain;
    }

    private HashMap<Integer, ArrayList<Integer>> deepClone(HashMap<Integer, ArrayList<Integer>> maptoCopy) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(maptoCopy);
        Type type = new TypeToken<HashMap<Integer, ArrayList<Integer>>>() {
        }.getType();
        HashMap<Integer, ArrayList<Integer>> cloned = gson.fromJson(jsonString, type);
        return cloned;
    }

    @Override
    public void printMappings() {
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
        String[][]mindMap=new String[spanY+5][spanX+5];
        for(int i :exploredX){
            ArrayList<Integer> array=explored.get(i);
            for(int j=0;j<array.size();j++){
                mindMap[((array.get(j)-highestYTotal)*-1)+2][i-lowestXTotal+2]=" ";
            }
        }
        for(int i :wallX){
            ArrayList<Integer> array=walls.get(i);
            for(int j=0;j<array.size();j++){
                mindMap[((array.get(j)-highestYTotal)*-1)+2][i-lowestXTotal+2]="W";
            }
        }
        GameController printer=new GameController();
        for(int i=0;i<=spanY+4;i++){
            for(int j=0;j<=spanX+4;j++){
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
        mindMap[0][0]="-3";
        printer.printArray(mindMap);
        //FIRST ONE IN MATRIX IS Y
        // SECOND ONE IN MATRIX IS X
        // GOTTA MOVE X's + LOWEST X TO REACH 0 SAME FOR Y

    }

}

