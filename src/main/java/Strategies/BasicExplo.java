package Strategies;

import Enums.Moves;
import Enums.Rotations;

import java.util.ArrayList;
import java.util.HashMap;

public class BasicExplo extends Strategy{
    private Moves[] moves = {Moves.WALK, Moves.TURN_AROUND,Moves.TURN_LEFT,Moves.TURN_RIGHT};
    private HashMap<Integer, ArrayList<Integer>> explored;
    public BasicExplo(){
        explored=new HashMap<Integer,ArrayList<Integer>>();
    }
    @Override
    public Moves decideOnMove(String[][] vision, int[]  xy, Rotations rot){
        double random = (Math.random()*4)-0.00000001;
        int decision = (int) random;
        return moves [decision];
    }
    public int updateExploration(String[][]vision,int[] xy, Rotations rot){
        boolean []canSee={true,true,true};
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
                        //if(vision[h][l]=="W"){
                         //   canSee[l]=false;
                        //}
                            if(explored.containsKey(currentX+j)){
                                if(!explored.get(currentX+j).contains(i)) {
                                    explored.get(currentX + j).add(i);
                                    informationGain++;
                                }

                            }else{
                                explored.put(currentX+j,new ArrayList<Integer>());
                                explored.get(currentX+j).add(i);
                            }

                        return 1;


                    }
                    case BACK -> {
                        return 1;
                    }
                    case LEFT -> {
                        return 1;
                    }
                    case RIGHT -> {
                        return 1;
                    }


                }
            }
        }
        return 1;}
}
