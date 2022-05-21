package Strategies;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import ObjectsOnMap.Goal;

import java.awt.*;
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
    private final ArrayList<Point> visitedPoints;
    private boolean atGoal;
    private Goal target;
    private int[] start;
    private int[] direction;
    int moveXtimes;
    int moveYtimes;

    //for A* Search
    //private int fn; // gn + hn = total cost of path
    //private int gn; //cost of path between the first node and the current node
    //private int hn; // heuristic function


// baysian pathfinding
    //at every move it checks whether it can see the target and/or a guard and/or a wall

    public IntruderSt(int[] start, Goal target) {
        this.explored = new HashMap<>();
        this.walls = new HashMap<>();
        this.objects = new HashMap<>();
        this.constraints=new Constraints();
        this.visitedPoints=new ArrayList<>();
        this.atGoal = false;
        this.target = target;
        //this.direction = direction(start);
        //this.moveXtimes = Math.abs(direction[0]);
        //this.moveYtimes = Math.abs(direction[1]);
    }

    public IntruderSt(){
        this.explored = new HashMap<>();
        this.walls = new HashMap<>();
        this.objects = new HashMap<>();
        this.constraints=new Constraints();
        this.visitedPoints=new ArrayList<>();
        this.atGoal = false;

    }

    @Override
    public void setBooleans(boolean s, boolean c) {
        this.searching = true;
        this.chased = false;
    }

    @Override
    public Moves decideOnMove(String[][] vision, int[] xy, Rotations rot, Variables vr){


        //Always check what the intruder sees
        return Moves.STUCK;
    }


    //@Override
    /*public Moves decideOnMove(String[][] vision, int[] xy, Rotations rot, Variables vr) {
        Moves returner = Moves.STUCK;
        if(searching){
            //Rotate until facing the direction of the target
            //Move steps x and y of direction
            //check at each move if it encounters a wall
            //Save each visited point in visited points and explored
            //STOPPING CONDITIONS
            //CURRENT PROBLEM: Why does it spawn in the same position always, and understanding where point 0, 0 is
            //Do I have to get the absolute value? ** the target Y-coordinate is different from the intruder y -coor even if they are in the same height
            //ONCE IT SEES THE TARGET IT JUST GOES FOR IT
            updateExploration(vision, xy, rot);
            System.out.println("Rotation in decide on move: " + rot.toString());
            //System.out.println("moveXtimes: " + moveXtimes);
            //System.out.println("moveYtimes: " + moveYtimes);
            System.out.println("posX = " + xy[0] + ", posY = " + xy[1] + ", TX = " + target.getX() + ", TY = " + target.getY());
            if(infront(rot, xy)){
                returner = Moves.WALK;
                System.out.println("Walked because in front");
            }
            else{
                if(moveXtimes != 0){
                    returner = updateRotX(rot,direction[0]);
                    System.out.println("RETURNED: "+ returner.toString());
                    if (returner.equals(Moves.WALK))
                        moveXtimes--;
                }
                else if(moveXtimes == 0 && moveYtimes != 0){
                    returner = updateRotY(rot,direction[1]);
                    System.out.println("RETURNED: "+ returner.toString());
                    if (returner.equals(Moves.WALK))
                        moveYtimes--;
                }
                else {
                    direction(xy);
                    moveXtimes = Math.abs(direction[0]);
                    moveYtimes = Math.abs(direction[1]);
                    returner = decideOnMove(vision, xy, rot, vr);
                }
            }
        }
        return returner;
    }
    public int[] direction(int[] start){ //TO CHECK
        System.out.println("start: " + start[0] + ", " + start[1]);
        int[] t = target.getXy();
        System.out.println("target: " + t[0] + ", " + t[1]);
        int x = t[0]-start[0];
        int y = t[1]-start[1];
        int gcd = gcdAlg(x, y);
        System.out.println("GCD: " + gcd);
        int[] direction = new int[2];
        direction[0] = x/gcd;
        direction[1] = y/gcd;
        System.out.println("DIRECTION: X = " + direction[0] + " Y= " + direction[1]);
        return direction;
    }
    public int gcdAlg(int n1, int n2){
        if(n2 == 0)
            return n1;
        return gcdAlg(n2, n1%n2);
    }
    public boolean infront(Rotations rot, int[] pos){
        switch (rot){
            case FORWARD -> {
                if(target.getX() == pos[0] && target.getY() == pos[1]+1) {
                    moveYtimes--;
                    return true;
                }
                else return false;
            }
            case BACK -> {
                if(target.getX() == pos[0] && target.getY() == pos[1]-1) {
                    moveYtimes--;
                    return true;
                }
                else return false;
            }
            case RIGHT -> {
                if(target.getX() == pos[0]+1 && target.getY() == pos[1]) {
                    moveXtimes--;
                    return true;
                }
                else return false;
            }
            case LEFT -> {
                if(target.getX() == pos[0]-1 && target.getY() == pos[1]){
                    moveXtimes--;
                    return true;
                }
                else return false;
            }
        }
        return false;
    }
    public Moves updateRotY(Rotations rot, int direct){
        System.out.println("ENTERED ROT Y");
        switch (rot){
            case FORWARD -> {
                if(direct > 0)
                    return Moves.TURN_AROUND;
            }
            case BACK -> { //I THINK THIS IS THE OPPOSITE OF WHAT WE THINK BASED ON HOW THE MAP IS BEING PRINTED
                if(direct < 0)
                    return Moves.TURN_AROUND;
            }
            case RIGHT -> {
                if(direct < 0)
                    return Moves.TURN_LEFT;
                else if(direct > 0)
                    return Moves.TURN_RIGHT;
            }
            case LEFT -> {
                if(direct > 0)
                    return Moves.TURN_LEFT;
                else if(direct < 0)
                    return Moves.TURN_RIGHT;
            }
        }
        return Moves.WALK;
    }
    public Moves updateRotX(Rotations rot, int direct){
        System.out.println("ENTERED ROT X");
        System.out.println("Rotation in method: " + rot.toString());
        switch (rot){
            case BACK -> {
                if(direct > 0)
                    return Moves.TURN_LEFT;
                else if(direct < 0)
                    return Moves.TURN_RIGHT;
            }
            case FORWARD -> {
                if(direct > 0)
                    return Moves.TURN_RIGHT;
                else if(direct < 0)
                    return Moves.TURN_LEFT;
            }
        }
        return Moves.WALK;
    }*/


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