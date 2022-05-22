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
    private boolean explorationRun;


    public IntruderSt(){
        this.explored = new HashMap<>();
        this.walls = new HashMap<>();
        this.objects = new HashMap<>();
        this.constraints=new Constraints();
        this.visitedPoints=new ArrayList<>();
        this.atGoal = false;
        this.walked = true;
        this.explorationRun = false;

    }

    @Override
    public void setBooleans(boolean s, boolean c) {
        this.searching = true;
        this.chased = false;
    }

    @Override
    public Moves decideOnMoveIntruder(String[][] vision, int[] xy, Rotations rot, Variables vr, GameController gm, Intruder intruder){

        updateExploration(vision, xy, rot);
        visitedPoints.add(new Point(xy,new ArrayList<>()));

        Moves move = Moves.STUCK;
        if(searching){
            if(walked == false ){
                walked = true;
                move = Moves.WALK;
                int[] nextPoint = new int[2];

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
                        move = root.getMove();
                        if (move == Moves.TURN_AROUND) {
                            if (gm.getIntRot() == Rotations.LEFT)
                                gm.setGlobalRotationIntruder(Rotations.RIGHT);
                            if (gm.getIntRot() == Rotations.DOWN)
                                gm.setGlobalRotationIntruder(Rotations.UP);
                            if (gm.getIntRot() == Rotations.RIGHT)
                                gm.setGlobalRotationIntruder(Rotations.LEFT);
                            if (gm.getIntRot() == Rotations.UP)
                                gm.setGlobalRotationIntruder(Rotations.DOWN);
                        }
                        if (move == Moves.TURN_LEFT) {
                            if (gm.getIntRot() == Rotations.LEFT)
                                gm.setGlobalRotationIntruder(Rotations.DOWN);
                            if (gm.getIntRot() == Rotations.DOWN)
                                gm.setGlobalRotationIntruder(Rotations.RIGHT);
                            if (gm.getIntRot() == Rotations.RIGHT)
                                gm.setGlobalRotationIntruder(Rotations.UP);
                            if (gm.getIntRot() == Rotations.UP)
                                gm.setGlobalRotationIntruder(Rotations.LEFT);
                        }
                        if (move == Moves.TURN_RIGHT) {
                            if (gm.getIntRot() == Rotations.LEFT)
                                gm.setGlobalRotationIntruder(Rotations.UP);
                            if (gm.getIntRot() == Rotations.DOWN)
                                gm.setGlobalRotationIntruder(Rotations.LEFT);
                            if (gm.getIntRot() == Rotations.RIGHT)
                                gm.setGlobalRotationIntruder(Rotations.DOWN);
                            if (gm.getIntRot() == Rotations.UP)
                                gm.setGlobalRotationIntruder(Rotations.RIGHT);
                        }
                        if(move==Moves.STUCK){
                            move = root.tryPathfinding();
                            if(move==Moves.STUCK){
                                move = root.tryTeleporting();
                            }
                        }
                    } else{
                        move = gm.getDirection(intruder); //This needs to move
                        //check if it is looking at a point that it has already visited, and then if move Walk
                        //check if looking at an explored point, and then if the first point of the looking at is a visited point
                        /*int[] nextPoint = new int[2];

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
                        }*/

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
        return move;

    }

    public boolean stuck(int[]xy){
        Point p = visitedPoints.get(visitedPoints.size()-2);
        if(Arrays.equals(p.xy(), xy)){
            return true;
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


    /** Random Movement
     *
     */

    /*if(explorationRun = true) {
                            if (move == Moves.WALK) {
                                if (gm.getIntRot() == Rotations.UP) {
                                    for (Point p : visitedPoints) {
                                        nextXY[0] = xy[0];
                                        nextXY[1] = xy[1] + vr.walkSpeed();
                                        if (Arrays.equals(p.xy(), nextXY)) {
                                            Random rand = new Random();
                                            int randomNum = rand.nextInt(2) + 1;
                                            move = availableMoves[randomNum];
                                        }
                                    }
                                } else if (gm.getIntRot() == Rotations.DOWN) {
                                    for (Point p : visitedPoints) {
                                        nextXY[0] = xy[0];
                                        nextXY[1] = xy[1] - vr.walkSpeed();
                                        if (Arrays.equals(p.xy(), nextXY)) {
                                            Random rand = new Random();
                                            int randomNum = rand.nextInt(2) + 1;
                                            move = availableMoves[randomNum];
                                        }
                                    }
                                } else if (gm.getIntRot() == Rotations.RIGHT) {
                                    for (Point p : visitedPoints) {
                                        nextXY[0] = xy[0] + vr.walkSpeed();
                                        nextXY[1] = xy[1];
                                        if (Arrays.equals(p.xy(), nextXY)) {
                                            Random rand = new Random();
                                            int randomNum = rand.nextInt(2) + 1;
                                            move = availableMoves[randomNum];
                                        }
                                    }
                                } else if (gm.getIntRot() == Rotations.LEFT) {
                                    for (Point p : visitedPoints) {
                                        nextXY[0] = xy[0] - vr.walkSpeed();
                                        nextXY[1] = xy[1];
                                        if (Arrays.equals(p.xy(), nextXY)) {
                                            Random rand = new Random();
                                            int randomNum = rand.nextInt(2) + 1;
                                            move = availableMoves[randomNum];
                                        }
                                    }
                                }
                            }
                        }*/
    /**ASTAR
     */
    //The goal will be in the direction of the target, but not the target itself

    /*public LinkedList<int[]> AStarSearch(int h){ //H is the manhattan distance and g is the counter
        int g = 0;
        int f = g + h;
        LinkedList<int[]> set = new LinkedList<>();
        set.add(start);
        LinkedList<int[]> cameFrom = new LinkedList<>();
        while(!set.isEmpty()){
            //current = node in set having the lowest fScore
            int[] current = set.getLast();
            if(current == target){ //or if manhattan distance == 0
                cameFrom.add(current);
                atGoal = true;
                return cameFrom;
            }
            set.remove(current);
            int current_distance  = Integer.MAX_VALUE;
            int[] front1 = new int[2];
            int[] front2 = new int[2];
            int[] front3 = new int[2];
            int[] front4 = new int[2];
            front1[0] = current[0] - 1;
            front1[1] = current[1];
            front2[0] = current[0] + 1;
            front2[1] = current[1];
            front3[0] = current[0];
            front3[1] = current[1] - 1;
            front4[0] = current[0];
            front4[1] = current[1] + 1;
            int[] closest_tile = new int[2];
            if(manDist(current, target) < current_distance)
                current_distance = manDist(current,target);
            if(manDist(front1, target) < current_distance && !walls.get(front1[0]).contains(front1[1])) {
                current_distance = manDist(front1,target);
                closest_tile = front1;
            }
            if(manDist(front2, target) < current_distance && !walls.get(front2[0]).contains(front2[1])){
                current_distance = manDist(front2,target);
                closest_tile = front2;
            }
            if(manDist(front3, target) < current_distance && !walls.get(front3[0]).contains(front3[1])){
                current_distance = manDist(front3,target);
                closest_tile = front3;
            }
            if(manDist(front4, target) < current_distance && !walls.get(front4[0]).contains(front4[1])){
                current_distance = manDist(front4,target);
                closest_tile = front4;
            }
            g++;
            h = current_distance;
            f = g + h;
            cameFrom.add(current);
            current = closest_tile;
            set.add(current);
        }
        return cameFrom;
    }
    public int manDist(int[] start, int[] target) {
        int distance = Math.abs(target[1]-start[1]) + Math.abs(target[0]-start[0]);
        return distance;
    }
     */


    @Override
    public void printMappings() {
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
        GameController printer=new GameController();
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
        printer.printArray(mindMap);
        //FIRST ONE IN MATRIX IS Y
        // SECOND ONE IN MATRIX IS X
        // GOTTA MOVE X's + LOWEST X TO REACH 0 SAME FOR Y

    }


}