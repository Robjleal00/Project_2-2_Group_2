package Patrolling;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;
import ObjectsOnMap.Teleporter;
import PathMaking.Point;
import Strategies.Constraints;
import OptimalSearch.TreeRoot;

import java.util.ArrayList;
import static java.util.Collections.max;
import java.util.HashMap;

//import static com.sun.jndi.ldap.LdapSchemaCtx.deepClone;


public class Patroller {
    private final Moves[] availableMoves = {Moves.WALK, Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND, Moves.USE_TELEPORTER};
    private Constraints constraints;
    private Variables vr;
    private final int walkSpeed = vr.walkSpeed();
    private String[][] map;
    private final int[] xy;

    public Patroller(int []xy){
        this.xy = xy;
    }



    //TODO: Teleporter is stored in BasicExplo, but the HM coordinates need to be transformed into the Array vals --ASHA
    private boolean teleporterCanBeUsed(Rotations rotation, int [] xy, String[][] map, int []posTeleporter){

        if(xy[0]==posTeleporter[0]){
            if(posTeleporter[1]==xy[1]+1){
                if(rotation == Rotations.DOWN){
                    return true;
                }
            }else if(posTeleporter[1]==xy[1]-1){
                if(rotation == Rotations.UP){
                    return true;
                }
            }
        }
        if(xy[1]==posTeleporter[1]){
            if(posTeleporter[0]==xy[0]+1){
                if(rotation == Rotations.LEFT){
                    return true;
                }
            }
            if(posTeleporter[0]==xy[0]-1){
                if(rotation == Rotations.RIGHT){
                    return true;
                }
            }
        }
        return false;
    }


    private int maxDepth;
    //Node method
    public int dfsRecursive(int depth, int[] xy, int[][] lastSeen, Rotations rotation)
    {
        int maxValue = 0;
        if(depth != 0)
        {
            //Create an array list to keep track of all our values
            ArrayList<Integer> nodeValues = new ArrayList<Integer>();

            // Store the values of all possible moves and return the highest one
            for(Moves availableMove : availableMoves)
            {
                //Prior to that, we'll have to give the child node the new rotation of the agent
                //we will also need to give it the new xy coordinates of the agent
                Rotations newRotation = null;
                int[] newPosition = null;
                switch(availableMove)
                {
                    case TURN_AROUND -> {
                        newRotation = rotation.turnAround();
                        newPosition = xy.clone();
                    }
                    case WALK -> {
                        xy = walk(xy, rotation);
                        newPosition = walk(xy, newRotation);
                    }
                    case TURN_RIGHT -> {
                        newRotation = rotation.turnRight();
                        newPosition = xy.clone();
                    }
                    case TURN_LEFT -> {
                        newRotation = rotation.turnLeft();
                        newPosition = xy.clone();
                    }
                    case USE_TELEPORTER -> {
                        newRotation = rotation;
                        //TODO: add teleporter methods
                        Teleporter tp = new Teleporter(1,1,1,2,2);
                        newPosition = tp.getTarget();
                    }
                }
                //Finished to-do: Currently creates one map that it will give to all children, need to change setSeen appropriately  --KAI
                //^ Changed this so that it does the following:
                // 0. First increment the values of all squares of lastSeen by 1, before value calculation
                incrementLastSeen(lastSeen);
                // 1. First calculates the value of the current node, getValue also sets seen squares to 0
                maxValue += getValue(lastSeen, vr, newPosition, newRotation);
                // 2. Creates a new lastSeen map, passes it to the child node
                int[][] nodesLastSeenMap = createNewLastSeen(lastSeen, vr, newPosition, newRotation);

                // 3. Addition the child's value to the parent
                maxValue += dfsRecursive(depth - 1, newPosition , nodesLastSeenMap, newRotation);
                nodeValues.add(maxValue);

                //Decremented: Don't think it's necessary to have a return value in this clause
                // return getValue(lastSeen, vr, newPosition, newRotation);
            }
        }
        else
        {
            //Reached the end of the DFS, return the value of current child back up so that the parent can decide
            //which has the highest value
            return maxValue;
        }
        return maxValue;
    }

    //Root method
    public Moves dfs(int[] xy, Rotations rotation, int[][] lastSeen)
    {
        ArrayList<Integer> childValues = new ArrayList<Integer>();
        for(Moves availableMove : availableMoves)
        {
            // Annoying since a lot of moves won't have the teleporter, yet it still checks repeatedly
            Rotations newRotation = null;
            int[] newPosition = null;
            switch (availableMove) {
                case TURN_AROUND -> {
                    newRotation = rotation.turnAround();
                    newPosition = xy.clone();
                }
                case WALK -> {
                    xy = walk(xy, rotation);
                    newPosition = walk(xy, newRotation);
                }
                case TURN_RIGHT -> {
                    newRotation = rotation.turnRight();
                    newPosition = xy.clone();
                }
                case TURN_LEFT -> {
                    newRotation = rotation.turnLeft();
                    newPosition = xy.clone();
                }
                case USE_TELEPORTER -> {
                    /*
                    if (teleporterCanBeUsed(rotation, xy, map, tp)) {
                        newRotation = rotation;
                        //TODO: add teleporter methods
                        Teleporter tp = new Teleporter(1, 1, 1, 2, 2);
                        newPosition = tp.getTarget();
                    } else {
                        return Moves.STUCK;
                    } */
                }
            }

            childValues.add(dfsRecursive(4, newPosition , lastSeen, newRotation));
        }
        int maxValue= max(childValues);
        int index = childValues.indexOf(maxValue);
        // TODO: Won't index > 4? So it'll return an out of bounds exception for this
        return availableMoves[index];
        //TODO: when you create children, give them a move and let them execute it themselves, less "super" code required
        // ^ Not sure how to implement this, but I believe my implementation should suffice for now
    }

    /**
     *      Since we need to know what possible moves the children can make we'll have to do the calculations ourselves,
     *      I assume since Cloud said we can't reuse any code and all of ours will be separate
     * @param xy
     * @return
     */
    public Moves getChildrensMoves(int[] xy, int[][] lastSeen, Rotations rotation)
    {
        return null;
    }

    //Returns the SUM of all seen squares, MUST be called before setSeen
    // Additionally now it also sets all seen square values to 0
    public int getValue(int[][] lastSeen, Variables vr, int[] xy, Rotations rotation)
    {
        int value = 0;
        int range = vr.eyeRange();
        switch(rotation){
            case BACK -> {
                for(int i = xy[0] - 1; i < xy[0] + 1; i++){
                    for(int j = xy[1]; j < xy[1] + range; j++){
                        value  += lastSeen[i][j];
                        lastSeen[i][j] = 0;
                    }
                }
            }
            case FORWARD -> {
                for(int i = xy[0] - 1; i < xy[0] + 1; i++){
                    for(int j = xy[1] - range; j < xy[1] ; j++){
                        value  += lastSeen[i][j];
                        lastSeen[i][j] = 0;
                    }
                }
            }
            case LEFT -> {
                for(int i = xy[0] - range; i < xy[0]; i++){
                    for(int j = xy[1] - 1; j < xy[1] + 1; j++){
                        value  += lastSeen[i][j];
                        lastSeen[i][j] = 0;
                    }
                }
            }
            case RIGHT ->{
                for(int i = xy[0]; i < xy[0] + range; i++){
                    for(int j = xy[1] - 1; j < xy[1] + 1; j++){
                        value  += lastSeen[i][j];
                        lastSeen[i][j] = 0;
                    }
                }
            }
        }
        return value;
    }

    /**
     *        Creates a new copy of lastSeen to:
     *          A) be used to get the value
     *          B) increment unseen squares in the next iteration for this specific child node
     * @param lastSeen
     * @param vr
     * @param xy
     * @param rotation
     * @return
     */
    public int[][] createNewLastSeen(int[][] lastSeen, Variables vr, int []xy, Rotations rotation)
    {
        int[][] newLastSeen = new int[lastSeen.length][lastSeen[0].length];

        //Copy values of the lastSeen array
        for(int i = 0; i < lastSeen.length; i++)
        {
            for(int j = 0; j < lastSeen[0].length; j++)
            {
                newLastSeen[i][j] = lastSeen[i][j];
            }
        }
        return newLastSeen;
    }


    // Copied from TreeNode/TreeRoot

    //TODO: Remake howMuchCanIWalk and noWallsInTheWay -- ASHA
    public int[] walk(int[] xy, Rotations rot) {
        int[] origin = xy.clone();
        switch (rot) {
            case FORWARD -> { //y increase
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[1] += howMuch;
            }
            case BACK -> { //y decrease
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[1] -= howMuch;
            }

            case RIGHT -> { //x increase
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[0] += howMuch;
            }

            case LEFT -> { //x decrease
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[0] -= howMuch;

            }
        }
        return xy;

    }

    private int howMuchCanIWalk(int[]pos,Rotations rot){
        switch(rot){
            case LEFT -> {//x decrease
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0]-i,pos[1]};
                    //if(noWallsInTheWay(pos,targetCell,rot))
                        return i;
                }

            }
            case RIGHT -> {//x increase
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0]+i,pos[1]};
                    //if(noWallsInTheWay(pos,targetCell,rot))
                        return i;
                }
            }
            //REMINDER: Forward doesn't increase Y ANYMORE IT INCREASES X
            case FORWARD ->{//y increase
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0],pos[1]+i};
                    //if(noWallsInTheWay(pos,targetCell,rot))
                        return i;
                }
            }
            case BACK -> {//y decrease
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0],pos[1]-i};
                    //if(noWallsInTheWay(pos,targetCell,rot))
                        return i;
                }
            }
        }
        return 0;
    }

    /* Needs to be modified to account for walls
    private boolean noWallsInTheWay(int[]pos,int[]target,Rotations rot){
        switch(rot){
            case FORWARD -> {//y increase
                int distance = target[1]-pos[1];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0])){
                        if(walls.get(pos[0]).contains(pos[1]+i)){
                            return false;
                        }
                    }

                }
            }
            case BACK -> {//y decrease
                int distance = pos[1]-target[1];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0])){
                        if(walls.get(pos[0]).contains(pos[1]-i)){
                            return false;
                        }
                    }

                }
            }
            case RIGHT -> {//x increase
                int distance = target[0]-pos[0];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0]+i)){
                        if(walls.get(pos[0]+i).contains(pos[1])){
                            return false;
                        }
                    }

                }
            }
            case LEFT -> {//x decrease
                int distance = pos[0]-target[0];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0]-i)){
                        if(walls.get(pos[0]-i).contains(pos[1])){
                            return false;
                        }
                    }

                }
            }

        }
        return true;
    }
    */

    public void incrementLastSeen(int[][] lastSeen)
    {
        for(int i = 0; i < lastSeen.length; i++)
        {
            for(int j = 0; j < lastSeen[0]. length; j++)
            {
                lastSeen[i][j]++;
            }
        }
    }

}
