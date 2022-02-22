package OptimalSearch;

import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;

import java.util.ArrayList;
import java.util.HashMap;

public class TreeNode {
    private Moves move;
    private HashMap<Integer, ArrayList> explored;
    private HashMap<Integer,ArrayList> walls;
    private int[] xy;
    private Rotations rot;
    private int eyeRange;
    public TreeNode(Moves move, HashMap explored, HashMap walls, int[] xy, Rotations rot,int eyeRange){
            this.move=move;
            this.rot=rot;
            this.explored=explored;
            this.walls=walls;
            this.xy=xy;
            this.eyeRange=eyeRange;
    }
    public int getValue(int depth){
        switch(move){
            case TURN_AROUND -> {rot=turnAround(rot);}
            case WALK -> {xy=walk(xy,rot,walls);}
            case TURN_RIGHT -> {rot=turnRight(rot);}
            case TURN_LEFT -> {rot=turnLeft(rot);}
        }
        String[][] vision = predictVision(xy,rot,walls,explored);
        GameController printer = new GameController();
        printer.printArray(vision);
        printer.print("---------------------");

        return 1;
    }
    private String[][] predictVision(int[]xy,Rotations rot,HashMap<Integer, ArrayList>walls,HashMap<Integer, ArrayList>explored){
        String[][] returner = new String[eyeRange][3];
        int currentX=xy[0];
        int currentY=xy[1];
        boolean canSee[]={true,true,true};
        for(int i=0;i<5;i++){ //i= upfront
            for(int j=-1;j<2;j++){ //j==sideways
                int h=eyeRange-(i+1);
                int l=j+1;
                boolean middleisNotWall=true;
                switch(rot){
                    case FORWARD -> {
                        if(j==-1) {
                            if(walls.containsKey(currentX)){
                                if (walls.get(currentX).contains(currentY + i)){
                                    middleisNotWall=false;
                                }
                            }
                        }
                        if (canSee[j + 1]) {
                            if (explored.containsKey(currentX + j)) {
                                if (!explored.get(currentX + j).contains(currentY + i)) {
                                    returner[h][l] = " ";
                                }
                            } else {
                                returner[h][l] = " ";
                            }
                            if (walls.containsKey(currentX + j)) {
                                if (walls.get(currentX + j).contains(currentY + i)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        }else if(canSee[1]&&middleisNotWall){
                            if (explored.containsKey(currentX + j)) {
                                if (!explored.get(currentX + j).contains(currentY + i)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX + j)) {
                                if (walls.get(currentX + j).contains(currentY + i)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        }else{
                            returner[h][l]="X";
                        }

                    }
                    case BACK -> {
                        if(j==-1) {
                            if(walls.containsKey(currentX)){
                                if (walls.get(currentX).contains(currentY - i)){
                                    middleisNotWall=false;
                                }
                            }
                        }
                        if (canSee[j + 1]) {
                            if (explored.containsKey(currentX - j)) {
                                if (!explored.get(currentX - j).contains(currentY - i)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX - j)) {
                                if (walls.get(currentX - j).contains(currentY - i)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        }else if(canSee[1]&&middleisNotWall){
                            if (explored.containsKey(currentX - j)) {
                                if (!explored.get(currentX - j).contains(currentY - i)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX - j)) {
                                if (walls.get(currentX - j).contains(currentY - i)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        }else{
                            returner[h][l]="X";
                        }

                    }
                    case LEFT -> {
                        if(j==-1) {
                            if(walls.containsKey(currentX-i)){
                                if (walls.get(currentX -i).contains(currentY)){
                                    middleisNotWall=false;
                                }
                            }
                        }
                        if (canSee[j + 1]) {
                            if (explored.containsKey(currentX-i)) {
                                if (!explored.get(currentX -i).contains(currentY + j)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX -i)) {
                                if (walls.get(currentX -i).contains(currentY + j)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        }else if(canSee[1]&&middleisNotWall){
                            if (explored.containsKey(currentX -i)) {
                                if (!explored.get(currentX -i).contains(currentY + j)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX -i)) {
                                if (walls.get(currentX -i).contains(currentY + j)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        }else{
                            returner[h][l]="X";
                        }

                    }
                    case RIGHT -> {
                        if(j==-1) {
                            if(walls.containsKey(currentX+i)){
                                if (walls.get(currentX+i).contains(currentY)){
                                    middleisNotWall=false;
                                }
                            }
                        }
                        if (canSee[j + 1]) {
                            if (explored.containsKey(currentX+i)) {
                                if (!explored.get(currentX+i).contains(currentY - j)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX +i)) {
                                if (walls.get(currentX +i).contains(currentY - j)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        }else if(canSee[1]&&middleisNotWall){
                            if (explored.containsKey(currentX+i)) {
                                if (!explored.get(currentX +i).contains(currentY - j)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX +i)) {
                                if (walls.get(currentX +i).contains(currentY - j)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        }else{
                            returner[h][l]="X";
                        }
                    }
                }
            }
        }
        return returner;

    }

    public Rotations turnLeft(Rotations rot) {
        switch(rot){
            case BACK -> {
                return(Rotations.RIGHT);
            }
            case LEFT -> {
                return(Rotations.BACK);
            }
            case FORWARD -> {
                return(Rotations.LEFT);
            }
            case RIGHT -> {
                return(Rotations.FORWARD);
            }
            default -> {return Rotations.LEFT; }
        }
    }
    public int[] walk(int[]xy, Rotations rot,HashMap<Integer,ArrayList> walls) {
        int[] origin = {xy[0],xy[1]};
        switch (rot) {
            case FORWARD -> {
                xy[1]++;
            }
            case BACK -> {
                xy[1]--;

            }
            case RIGHT -> {
                xy[0]++;

            }
            case LEFT -> {
                xy[0]--;

            }

        }
        if (walls.containsKey(xy[0])){
            if(walls.get(xy[0]).contains(xy[1])){
                return origin;
            }
        }
        return xy;
    }
    public Rotations turnRight(Rotations rot) {
        switch(rot) {
            case FORWARD -> {
                return(Rotations.RIGHT);
            }
            case RIGHT -> {
                return(Rotations.BACK);
            }
            case LEFT -> {
                return(Rotations.FORWARD);
            }
            case BACK -> {
                return(Rotations.LEFT);
            }
            default -> {return Rotations.LEFT; }
        }
    }
    public Rotations turnAround(Rotations rot) {
        switch(rot){
            case FORWARD -> {
                return(Rotations.BACK);
            }
            case RIGHT -> {
                return(Rotations.LEFT);
            }
            case LEFT -> {
                return(Rotations.RIGHT);
            }
            case BACK -> {
                return(Rotations.FORWARD);
            }
            default -> {return Rotations.LEFT; }
        }
    }

}
