package Patrolling;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;
import Strategies.Constraints;

import java.util.ArrayList;
import java.util.Collections;



public class Patroller {
    private final Moves[] availableMoves = {Moves.WALK, Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND, Moves.USE_TELEPORTER};
    private Constraints constraints;
    private Variables vr;
    private final int walkSpeed = vr.walkSpeed();

    //TODO: create method that checks if teleporter is part of availableMoves -- ASHA
    private boolean isTeleporterAvailable(Rotations rotation, int [] xy, String[][]Map){


    }
    //TODO: Teleporter is stored in BasicExplo, but the HM coordinates need to be transformed into the Array vals --ASHA
    private int maxDepth;
    //Node method
    public int dfsRecursive(int depth, int[] xy, int[][] lastSeen, Rotations rotation)
    {
        int maxValue = 0;
        if(depth != 0)
        {
            //Create an array list to keep track of all our values
            ArrayList<int> nodeValues = new ArrayList<int>();

            // Store the values of all possible moves and return the highest one
            for(Moves availableMove : availableMoves)
            {
                //Prior to that, we'll have to give the child node the new rotation of the agent
                //we will also need to give it the new xy coordinates of the agent
                Rotations newRotation = null;
                int[] newPosition = null;
                //TODO: change to switch statement  --KAI
                if(availableMove.equals(Moves.TURN_LEFT))
                {
                    newRotation = rotation.turnLeft();
                    newPosition = xy.clone();
                }
                else if(availableMove.equals(Moves.TURN_RIGHT))
                {
                    newRotation = rotation.turnRight();
                    newPosition = xy.clone();
                }
                else if(availableMove.equals(Moves.WALK) || availableMove.equals(Moves.USE_TELEPORTER))
                {
                    newRotation = rotation;
                    newPosition = walk(xy, newRotation);
                }
                else if(availableMove.equals(Moves.TURN_AROUND))
                {
                    newRotation = rotation.turnAround();
                    newPosition = xy.clone();
                }
                //TODO: Currently creates one map that it will give to all children, need to change setSeen appropriately  --KAI
                int[][] nodesLastSeenMap = setSeen(lastSeen, vr, newPosition, newRotation);

                maxValue += dfsRecursive(depth - 1, newPosition , lastSeen, newRotation);
                nodeValues.add(maxValue);
                return getValue(lastSeen, vr, newPosition, newRotation);
            }
            /*
            for(int i = 0; i < nodeValues.size(); i++)
            {
                if(maxValue < nodeValues.get(i))
                {
                    maxValue = nodeValues.get(i);
                }
            }
            return maxValue;

            */
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
    public Moves dfs(int[] xy)
    {
        //TODO: Have to loop through the children, creates 4 children at first --KAI
        //TODO: when you create children, give them a move and let them execute it themselves, less "super" code required
        // same concept as TreeRoot: getMove()
        //dfsRecurisve() = treenode
        return null;
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
    public int getValue(int[][] lastSeen, Variables vr, int[] xy, Rotations rotation)
    {
        int value = 0;
        int range = vr.eyeRange();
        switch(rotation){
            case BACK -> {
                for(int i = xy[0] - 1; i < xy[0] + 1; i++){
                    for(int j = xy[1]; j < xy[1] + range; j++){
                        value  += lastSeen[i][j];
                    }
                }
            }
            case FORWARD -> {
                for(int i = xy[0] - 1; i < xy[0] + 1; i++){
                    for(int j = xy[1] - range; j < xy[1] ; j++){
                        value  += lastSeen[i][j];
                    }

                }
            }
            case LEFT -> {
                for(int i = xy[0] - range; i < xy[0]; i++){
                    for(int j = xy[1] - 1; j < xy[1] + 1; j++){
                        value  += lastSeen[i][j];
                    }
                }
            }
            case RIGHT ->{
                for(int i = xy[0]; i < xy[0] + range; i++){
                    for(int j = xy[1] - 1; j < xy[1] + 1; j++){
                        value  += lastSeen[i][j];
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
    public int[][] setSeen(int[][] lastSeen, Variables vr, int []xy, Rotations rotation)
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
        //For every different node the agent will see something else, here it will set all seen squares to 0 for its
        //own version
        int range = vr.eyeRange();
        //Agent sees a block to its left and its right with a range of 5 units
        switch(rotation){
            case BACK -> {
                for(int i = xy[0] - 1; i < xy[0] + 1; i++){
                    for(int j = xy[1]; j < xy[1] + range; j++){
                        newLastSeen[i][j] = 0;
                    }
                }
            }
            case FORWARD -> {
                for(int i = xy[0] - 1; i < xy[0] + 1; i++){
                    for(int j = xy[1] - range; j < xy[1] ; j++){
                        newLastSeen[i][j] = 0;
                    }

                }
            }
            case LEFT -> {
                for(int i = xy[0] - range; i < xy[0]; i++){
                    for(int j = xy[1] - 1; j < xy[1] + 1; j++){
                        newLastSeen[i][j] = 0;
                    }
                }
            }
            case RIGHT ->{
                for(int i = xy[0]; i < xy[0] + range; i++){
                    for(int j = xy[1] - 1; j < xy[1] + 1; j++){
                        newLastSeen[i][j] = 0;
                    }
                }
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
            //TODO: Forward doesn't increase Y ANYMORE IT INCREASES X
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

}
