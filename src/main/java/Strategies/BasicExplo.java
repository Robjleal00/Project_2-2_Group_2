package Strategies;

import Enums.Moves;
import Enums.Rotations;
import OptimalSearch.TreeNode;
import OptimalSearch.TreeRoot;

import java.util.ArrayList;
import java.util.HashMap;

public class BasicExplo extends Strategy{
    private Moves[] moves = {Moves.WALK, Moves.TURN_AROUND,Moves.TURN_LEFT,Moves.TURN_RIGHT};
    private HashMap<Integer, ArrayList<Integer>> explored;
    private HashMap<Integer, ArrayList<Integer>> walls;
    public BasicExplo(){
        explored=new HashMap<Integer,ArrayList<Integer>>();
        walls=new HashMap<Integer,ArrayList<Integer>>();
    }
    @Override
    public Moves decideOnMove(String[][] vision, int[]  xy, Rotations rot){
        updateExploration(vision,xy,rot);
        //System.out.println(explored);
        //System.out.println(walls);
        TreeRoot root = new TreeRoot((HashMap<Integer,ArrayList>)explored.clone(),(HashMap<Integer,ArrayList>)walls.clone(),xy.clone(),rot,6,vision.length);
        Moves decision = root.getMove();
        return decision;
    }
    public int updateExploration(String[][]vision,int[] xy, Rotations rot){
        int informationGain=0;
        int eyeRange=vision.length;
        int currentX=xy[0];
        int currentY=xy[1];
        for(int i=0;i<5;i++){ //i= upfront
            for(int j=-1;j<2;j++){ //j==sideways
                int h=eyeRange-(i+1);
                int l=j+1;
                switch(rot){
                    case FORWARD -> {
                            if (vision[h][l]!="X") {
                                if (vision[h][l] != "W") {
                                    if (explored.containsKey(currentX + j)) {
                                        if (!explored.get(currentX + j).contains(currentY+i)) {
                                            explored.get(currentX + j).add(currentY+i);
                                            informationGain++;
                                        }

                                    } else {
                                        explored.put(currentX + j, new ArrayList<Integer>());
                                        explored.get(currentX + j).add(currentY+i);
                                        informationGain++;
                                    }
                                } else {
                                    if (walls.containsKey(currentX + j)) {
                                        if (!walls.get(currentX + j).contains(currentY+i)) {
                                            walls.get(currentX + j).add(currentY+i);
                                            informationGain++;
                                        }

                                    } else {
                                        walls.put(currentX + j, new ArrayList<Integer>());
                                        walls.get(currentX + j).add(currentY+i);
                                        informationGain++;
                                    }
                                }
                            }
                    }
                    case BACK -> {
                        if (vision[h][l]!="X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX - j)) {
                                    if (!explored.get(currentX - j).contains(currentY-i)) {
                                        explored.get(currentX - j).add(currentY-i);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX - j, new ArrayList<Integer>());
                                    explored.get(currentX - j).add(currentY-i);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX - j)) {
                                    if (!walls.get(currentX - j).contains(currentY-i)) {
                                        walls.get(currentX - j).add(currentY-i);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX - j, new ArrayList<Integer>());
                                    walls.get(currentX - j).add(currentY-i);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case LEFT -> {
                        if (vision[h][l]!="X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX-i)) {
                                    if (!explored.get(currentX-i).contains(currentY+j)) {
                                        explored.get(currentX-i).add(currentY+j);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX-i, new ArrayList<Integer>());
                                    explored.get(currentX-i).add(currentY+j);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX-i)) {
                                    if (!walls.get(currentX-i).contains(currentY+j)) {
                                        walls.get(currentX-i).add(currentY+j);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX-i, new ArrayList<Integer>());
                                    walls.get(currentX-i).add(currentY+j);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case RIGHT -> {
                        if (vision[h][l]!="X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX+i)) {
                                    if (!explored.get(currentX+i).contains(currentY-j)) {
                                        explored.get(currentX+i).add(currentY-j);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX+i, new ArrayList<Integer>());
                                    explored.get(currentX+i).add(currentY-j);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX+i)) {
                                    if (!walls.get(currentX+i).contains(currentY-j)) {
                                        walls.get(currentX+i).add(currentY-j);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX+i, new ArrayList<Integer>());
                                    walls.get(currentX+i).add(currentY-j);
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
}
