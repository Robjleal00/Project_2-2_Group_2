package Strategies;

import Config.Variables;
import Entities.Intruder;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import OptimalSearch.TreeRoot;
import PathMaking.Point;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.*;

import static java.util.Collections.max;
import static java.util.Collections.min;

public class IntruderSt extends Strategy{
    private final HashMap<Integer, ArrayList<Integer>> explored;
    private final HashMap<Integer, ArrayList<Integer>> walls;
    private final HashMap<Integer,int[]> objects;
    private boolean chased;
    private boolean searching;
    private final Constraints constraints;
    Rotations[] avaliableRotations = {Rotations.BACK, Rotations.RIGHT, Rotations.LEFT, Rotations.FORWARD};
    Moves[] availableMoves = {Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND};
    private final ArrayList<Point> visitedPoints;
    private boolean atGoal;
    private boolean walked;
    private int count = 0;
    private boolean onlyExplore;
    private boolean releaseMarkers;

    //THIS IS THE COOPERATIVE INTRUDER - not aware of its surroundings but communicates with other intruders
    public IntruderSt(){
        this.explored = new HashMap<>();
        this.walls = new HashMap<>();
        this.objects = new HashMap<>();
        this.constraints=new Constraints();
        this.visitedPoints=new ArrayList<>();
        this.atGoal = false;
        this.walked = true;
        this.onlyExplore = false;
        this.releaseMarkers = false;

    }

    @Override
    public void setBooleans(boolean s, boolean c) {
        this.searching = true;
        this.chased = false;
    }

    @Override
    public Moves decideOnMoveIntruder(String[][] vision, int[] xy, Rotations rot, Variables vr, GameController gm, Intruder intruder){

        //TODO: Check if intruder knows how to teleport
        updateExploration(vision, xy, rot);
        visitedPoints.add(new Point(xy,new ArrayList<>()));

        Moves move = Moves.STUCK;

        int eyeRange = vision.length; //CAN WE JUST USE VISION AS LOOKING AT ARRAY?

        if(!onlyExplore){
            for (int i = 0; i < eyeRange; i++) { //i= upfront
                for (int j = -1; j < 2; j++) { //j==sideways
                    int h = eyeRange - (i + 1);
                    int l = j + 1;
                    final String lookingAt = vision[h][l];

                    if(lookingAt.contains("V1")){
                        System.out.println("TARGET SPOTTED");
                        releaseMarkers = true;

                        if(j == 1){
                            walked = false;
                            return translateMove(Moves.TURN_RIGHT);
                        }
                        else if(j == 0){
                            walked = true;
                            return translateMove(Moves.WALK);
                        }
                        else{
                            walked = false;
                            return translateMove(Moves.TURN_LEFT);
                        }
                    }
                    if(lookingAt.contains("33")) {
                        System.out.println("Pheromone spotted");
                        gm.addVarsI(new Variables(1, 5, 1,20));
                        onlyExplore = true;
                    }
                }
            }
        }

        if(onlyExplore){
            TreeRoot root = new TreeRoot(deepClone(explored), deepClone(walls), xy.clone(), rot, 5, constraints,vr,visitedPoints,objects);
            return root.getMove(false);
        }

        if(searching){
            if(walked == false ){
                walked = true;
                move = Moves.WALK;
                int[] nextPoint = new int[2];
                //check if it is looking at a point that it has already visited, and then if move Walk
                //check if looking at an explored point, and then if the first point of the looking at is a visited point
                if (move == Moves.WALK){
                    if (rot == Rotations.FORWARD) {
                        if (explored.containsKey(xy[0]) && explored.get(xy[0]).contains(xy[1]+vr.walkSpeed())) {
                            nextPoint[0] = xy[0];
                            nextPoint[1] = xy[1] + vr.walkSpeed();
                            Point toCheck = new Point(nextPoint, new ArrayList<>());
                            for (int i = 0; i < visitedPoints.size(); i++) {
                                if (Arrays.equals(visitedPoints.get(i).xy(),toCheck.xy())) {
                                    move = gm.getNextBestMove(intruder);
                                    System.out.println("LOOKING AT VISITED");
                                    break;

                                }
                            }
                        }
                    }else if (rot == Rotations.RIGHT) {
                        if (explored.containsKey(xy[0]+vr.walkSpeed()) && explored.get(xy[0]+ vr.walkSpeed()).contains(xy[1])) {
                            nextPoint[0] = xy[0] + vr.walkSpeed();
                            nextPoint[1] = xy[1];
                            Point toCheck = new Point(nextPoint, new ArrayList<>());
                            for (int i = 0; i < visitedPoints.size(); i++) {
                                if (Arrays.equals(visitedPoints.get(i).xy(), toCheck.xy())) {
                                    move = gm.getNextBestMove(intruder);
                                    System.out.println("LOOKING AT VISITED");
                                    break;
                                }
                            }
                        }
                    }else if (rot == Rotations.LEFT) {
                        if (explored.containsKey(xy[0] - vr.walkSpeed()) && explored.get(xy[0] - vr.walkSpeed()).contains(xy[1])) {
                            nextPoint[0] = xy[0] - vr.walkSpeed();
                            nextPoint[1] = xy[1];
                            Point toCheck = new Point(nextPoint, new ArrayList<>());
                            for (int i = 0; i < visitedPoints.size(); i++) {
                                if (Arrays.equals(visitedPoints.get(i).xy(), toCheck.xy())) {
                                    move = gm.getNextBestMove(intruder);
                                    System.out.println("LOOKING AT VISITED");
                                    break;
                                }

                            }
                        }
                    }else if (rot == Rotations.BACK) {
                        if (explored.containsKey(xy[0]) && explored.get(xy[0]).contains(xy[1] - vr.walkSpeed())) {
                            nextPoint[0] = xy[0];
                            nextPoint[1] = xy[1] - vr.walkSpeed();
                            Point toCheck = new Point(nextPoint, new ArrayList<>());
                            for (int i = 0; i < visitedPoints.size(); i++) {
                                if (Arrays.equals(visitedPoints.get(i).xy(), toCheck.xy())) {
                                    move = gm.getNextBestMove(intruder);
                                    System.out.println("LOOKING AT VISITED");
                                    break;
                                }

                            }
                        }
                    }
                }

            }
            else{

                if (count > 1) {
                    if(stuck(xy)){
                        System.out.println("STUCKKKKK");
                        TreeRoot root = new TreeRoot(deepClone(explored), deepClone(walls), xy.clone(), rot, 5, constraints,vr,visitedPoints,objects);
                        move = root.getMove(false);

                        if(move==Moves.STUCK){
                            move = root.tryPathfinding();
                            if(move==Moves.STUCK){
                                move = root.tryTeleporting();
                            }
                        }
                    } else{
                        move = gm.getDirection(intruder); //This needs to move

                        System.out.println("MOVE CHOSEN: " + move.toString());
                        if(move != Moves.WALK){
                            walked = false;
                        }
                    }
                }else{
                    move = gm.getDirection(intruder);

                    System.out.println("MOVE CHOSEN: " + move.toString());
                    if(move != Moves.WALK){
                        walked = false;
                    }
                }
            }
        }

        count++;
        if(releaseMarkers){
            return translateMove(move);
        }
        return move;

    }

    public boolean stuck(int[]xy){
        Point p = visitedPoints.get(visitedPoints.size()-2);
        if(Arrays.equals(p.xy(), xy)){
            return true;
        }
        return false;
    }


    /**
     * Intruder releases marker when it sees the target
     * If this intruder gets captured its markers dissapear
     * Idea experiments : changing the amount of markers released
     *
     */


    public Moves translateMove(Moves move){
        Moves returner = Moves.STUCK;
        switch (move){
            case WALK -> returner = Moves.M_WALK;
            //case USE_TELEPORTER -> returner = Moves.M_USE_TELEPORTER;
            case TURN_LEFT -> returner = Moves.M_TURN_LEFT;
            case TURN_AROUND -> returner = Moves.M_TURN_AROUND;
            case TURN_RIGHT -> returner = Moves.M_TURN_RIGHT;
        }
        return returner;
    }

    private HashMap<Integer, ArrayList<Integer>> deepClone(HashMap<Integer, ArrayList<Integer>> maptoCopy) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(maptoCopy);
        Type type = new TypeToken<HashMap<Integer, ArrayList<Integer>>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }


    public void updateExploration(String[][] vision, int[] xy, Rotations rot) {
        int eyeRange = vision.length;
        int currentX = xy[0];
        int currentY = xy[1];

        for (int i = 0; i < eyeRange; i++) { //i= upfront
            for (int j = -1; j < 2; j++) { //j==sideways
                int h = eyeRange - (i + 1);
                int l = j + 1;
                final String lookingAt = vision[h][l];
                switch (rot) {
                    case FORWARD -> {
                        if(lookingAt.contains("E")){
                            if(i!=0){
                                constraints.setMAX_Y(currentY+1);
                            }
                        }
                        if (!Objects.equals(lookingAt, "X")) {
                            if (!Objects.equals(lookingAt, "W")&&!lookingAt.contains("T")) {
                                if (explored.containsKey(currentX + j)) {
                                    if (!explored.get(currentX + j).contains(currentY + i)) {
                                        explored.get(currentX + j).add(currentY + i);
                                    }

                                } else {
                                    explored.put(currentX + j, new ArrayList<>());
                                    explored.get(currentX + j).add(currentY + i);
                                }
                            } else {
                                if(lookingAt.contains("T")){
                                    String id = lookingAt.replace("T","");
                                    int ide = Integer.valueOf(id);
                                    if(!objects.containsKey(ide)){
                                        int [] pos_of_It = {currentX+j,currentY+i};
                                        objects.put(ide,pos_of_It);
                                    }
                                }
                                if (walls.containsKey(currentX + j)) {
                                    if (!walls.get(currentX + j).contains(currentY + i)) {
                                        walls.get(currentX + j).add(currentY + i);
                                    }

                                } else {
                                    walls.put(currentX + j, new ArrayList<>());
                                    walls.get(currentX + j).add(currentY + i);
                                }
                            }
                        }
                    }
                    case BACK -> {
                        if(lookingAt.contains("V")){
                            System.out.println("LOOKING AT GOAL");
                        }
                        if(lookingAt.contains("E")) {
                            if (i != 0) {
                                constraints.setMIN_Y(currentY - 1);
                            }
                        }
                        if (!Objects.equals(lookingAt, "X")) {
                            if (!Objects.equals(lookingAt, "W")&&!lookingAt.contains("T")) {
                                if (explored.containsKey(currentX - j)) {
                                    if (!explored.get(currentX - j).contains(currentY - i)) {
                                        explored.get(currentX - j).add(currentY - i);
                                    }

                                } else {
                                    explored.put(currentX - j, new ArrayList<>());
                                    explored.get(currentX - j).add(currentY - i);
                                }
                            } else {
                                if(lookingAt.contains("T")){
                                    String id = lookingAt.replace("T","");
                                    int ide = Integer.valueOf(id);
                                    if(!objects.containsKey(ide)){
                                        int [] pos_of_It = {currentX-j,currentY-i};
                                        objects.put(ide,pos_of_It);
                                    }
                                }
                                if (walls.containsKey(currentX - j)) {
                                    if (!walls.get(currentX - j).contains(currentY - i)) {
                                        walls.get(currentX - j).add(currentY - i);
                                    }

                                } else {
                                    walls.put(currentX - j, new ArrayList<>());
                                    walls.get(currentX - j).add(currentY - i);
                                }
                            }
                        }
                    }
                    case LEFT -> {
                        if(lookingAt.contains("E")) {
                            if (i != 0) {
                                constraints.setMIN_X(currentX - 1);
                            }
                        }
                        if (!Objects.equals(lookingAt, "X")) {
                            if (!Objects.equals(lookingAt, "W")&&!lookingAt.contains("T")) {
                                if (explored.containsKey(currentX - i)) {
                                    if (!explored.get(currentX - i).contains(currentY + j)) {
                                        explored.get(currentX - i).add(currentY + j);
                                    }

                                } else {
                                    explored.put(currentX - i, new ArrayList<>());
                                    explored.get(currentX - i).add(currentY + j);
                                }
                            } else {
                                if(lookingAt.contains("T")){
                                    String id = lookingAt.replace("T","");
                                    int ide = Integer.valueOf(id);
                                    if(!objects.containsKey(ide)){
                                        int [] pos_of_It = {currentX-i,currentY+j};
                                        objects.put(ide,pos_of_It);
                                    }
                                }
                                if (walls.containsKey(currentX - i)) {
                                    if (!walls.get(currentX - i).contains(currentY + j)) {
                                        walls.get(currentX - i).add(currentY + j);
                                    }

                                } else {
                                    walls.put(currentX - i, new ArrayList<>());
                                    walls.get(currentX - i).add(currentY + j);
                                }
                            }
                        }
                    }
                    case RIGHT -> {
                        if(lookingAt.contains("E")) {
                            if (i != 0) {
                                constraints.setMAX_X(currentX + 1);
                            }
                        }
                        if (!Objects.equals(lookingAt, "X")) {
                            if (!Objects.equals(lookingAt, "W")&&!lookingAt.contains("T")) {
                                if (explored.containsKey(currentX + i)) {
                                    if (!explored.get(currentX + i).contains(currentY - j)) {
                                        explored.get(currentX + i).add(currentY - j);
                                    }

                                } else {
                                    explored.put(currentX + i, new ArrayList<>());
                                    explored.get(currentX + i).add(currentY - j);
                                }
                            } else {
                                if(lookingAt.contains("T")){
                                    String id = lookingAt.replace("T","");
                                    int ide = Integer.valueOf(id);
                                    if(!objects.containsKey(ide)){
                                        int [] pos_of_It = {currentX+i,currentY-j};
                                        objects.put(ide,pos_of_It);
                                    }
                                }
                                if (walls.containsKey(currentX + i)) {
                                    if (!walls.get(currentX + i).contains(currentY - j)) {
                                        walls.get(currentX + i).add(currentY - j);
                                    }

                                } else {
                                    walls.put(currentX + i, new ArrayList<>());
                                    walls.get(currentX + i).add(currentY - j);
                                }
                            }
                        }
                    }

                }
            }
        }

    }
}
