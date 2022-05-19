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
import java.util.Objects;

//import static com.sun.jndi.ldap.LdapSchemaCtx.deepClone;


public class Patroller {
    private final Moves[] availableMoves = {Moves.WALK, Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND, Moves.USE_TELEPORTER};
    private Constraints constraints;
    private Variables vr;
    private final int walkSpeed ;
    private String[][] map;
    private final int[] xy;
    private final Rotations rot;
    private final HashMap<int[],int[]>teleporters;
    private final int[][] lastSeen;
    private final int mapLength;
    private final int mapHeight;

    public Patroller(int []xy,Rotations rot,Variables vr,String[][] map,HashMap<int[],int[]>teleporters,int[][]lastSeen){
        this.xy = xy;
        this.rot=rot;
        this.vr=vr;
        this.map=map;
        this.teleporters=teleporters;
        this.lastSeen=lastSeen;
        this.mapHeight=map.length;
        this.mapLength=map[0].length;
        this.walkSpeed=vr.walkSpeed();
    }



    //TODO: Teleporter is stored in BasicExplo, but the HM coordinates need to be transformed into the Array vals --ASHA
    //Node method
    private int dfsRecursive(int depth,Moves move,int[] xy,Rotations rot,int[][]lastSeen)
    {
        int ownValue;        //execute the move
        switch(move){
            case TURN_AROUND -> {
                rot=rot.turnAround();
            }
            case TURN_LEFT -> {
                rot=rot.turnLeft();
            }
            case TURN_RIGHT -> {
                rot=rot.turnRight();
            }
            case WALK -> {
                xy=walk(xy.clone(),rot);
            }
            case USE_TELEPORTER -> {
                int [] target = canTeleport(xy,rot);
                xy=target;
            }

        }
        ownValue=getValue(lastSeen,xy,rot);
        if(depth != 0)
        {
            int[][] deepclone=createNewLastSeen(lastSeen);
            ArrayList<Integer> childValues = new ArrayList<Integer>();
            for(Moves availableMove : availableMoves)
            {
                childValues.add(dfsRecursive(depth-1,availableMove,xy.clone(),rot,deepclone));
            }
            return ownValue+max(childValues);
        }
        else {
            //Reached the end of the DFS, return the value of current child back up so that the parent can decide
            //which has the highest value
            // return maxValue;
            return ownValue;
        }
    }

    //Root method
    public Moves dfs(int maxDepth)
    {
        incrementLastSeen(lastSeen);
        ArrayList<Integer> childValues = new ArrayList<Integer>();
        int[][] deepclone=createNewLastSeen(lastSeen);
        for(Moves availableMove : availableMoves)
        {
            childValues.add(dfsRecursive(maxDepth,availableMove,xy.clone(),rot,deepclone));
        }

        int maxValue= max(childValues);
        int index = childValues.indexOf(maxValue);
        // TODO: Won't index > 4? So it'll return an out of bounds exception for this
        return availableMoves[index];
        //TODO: when you create children, give them a move and let them execute it themselves, less "super" code required
        // ^ Not sure how to implement this, but I believe my implementation should suffice for now
    }

    //Returns the SUM of all seen squares, MUST be called before setSeen
    // Additionally now it also sets all seen square values to 0
    public int getValue(int[][] lastSeen, int[] xy, Rotations rotation)
    {
        int value = 0;
        int range = vr.eyeRange();
        switch(rotation){
            case BACK -> {
                for(int i = xy[1] - 1; i < xy[1] + 1; i++){
                    for(int j = xy[0]; j < xy[0] + range; j++){
                        value  += lastSeen[i][j];
                        lastSeen[i][j] = 0;
                    }
                }
            }
            case FORWARD -> {
                for(int i = xy[1] - 1; i < xy[1] + 1; i++){
                    for(int j = xy[0] - range; j < xy[0] ; j++){
                        value  += lastSeen[i][j];
                        lastSeen[i][j] = 0;
                    }
                }
            }
            case LEFT -> {
                for(int i = xy[1] - range; i < xy[1]; i++){
                    for(int j = xy[0] - 1; j < xy[0] + 1; j++){
                        value  += lastSeen[i][j];
                        lastSeen[i][j] = 0;
                    }
                }
            }
            case RIGHT ->{
                for(int i = xy[1]; i < xy[1] + range; i++){
                    for(int j = xy[0] - 1; j < xy[0] + 1; j++){
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
     * @return deepclone of lastSeen
     */
    private int[][] createNewLastSeen(int[][] lastSeen)
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

    private int [] canTeleport(int[]xy, Rotations rot){
        for(int[]xyT : teleporters.keySet()){
            switch (rot){
                case LEFT -> {
                    if(xyT[0]==xy[0]&&xyT[1]==xy[1]-1){
                        return teleporters.get(xyT);
                    }
                }
                case RIGHT -> {
                    if(xyT[0]==xy[0]&&xyT[1]==xy[1]+1){
                        return teleporters.get(xyT);
                    }
                }
                case FORWARD -> {
                    if(xyT[0]==xy[0]-1&&xyT[1]==xy[1]){
                        return teleporters.get(xyT);
                    }
                }
                case DOWN -> {
                    if(xyT[0]==xy[0]+1&&xyT[1]==xy[1]){
                        return teleporters.get(xyT);
                    }
                }
            }
        }
        int a =-1;
        int []b={a,a};
        return b;
    }


    // Copied from TreeNode/TreeRoot

    //TODO: Remake howMuchCanIWalk and noWallsInTheWay -- ASHA
    private int[] walk(int[] xy, Rotations rot) {
        switch (rot) {
            case FORWARD -> { //x decrease
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[0] -= howMuch;
            }
            case BACK -> { //x increase
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[0] += howMuch;
            }

            case RIGHT -> { //y increase
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[1] += howMuch;
            }

            case LEFT -> { //y decrease
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[1] -= howMuch;

            }
        }
        return xy;

    }

    private int howMuchCanIWalk(int[]pos,Rotations rot){
        switch(rot){
            case LEFT -> {//y decrease
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0],pos[1]-1};
                    if(noWallsOnTheWay(pos,targetCell,rot))
                        return i;
                }

            }
            case RIGHT -> {//y increase
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0],pos[1]+1};
                    if(noWallsOnTheWay(pos,targetCell,rot))
                        return i;
                }
            }
            //REMINDER: Forward doesn't increase Y ANYMORE IT INCREASES X
            case FORWARD ->{//x decrease
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0]-i,pos[1]};
                    if(noWallsOnTheWay(pos,targetCell,rot))
                        return i;
                }
            }
            case BACK -> {//x increase
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0]+i,pos[1]};
                    if(noWallsOnTheWay(pos,targetCell,rot))
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
    private boolean noWallsOnTheWay(int[]pos,int[]target,Rotations rot){
        switch(rot){
            case LEFT -> {
                int length=pos[1]-target[1];
                for(int i=1;i<=length;i++){
                    int[] nextTarget={pos[0],pos[1]-i};
                    if(!canBePutThere(nextTarget))return false;
                }
                return true;
            }
            case RIGHT -> {
                int length=target[1]-pos[1];
                for(int i=1;i<=length;i++){
                    int[] nextTarget={pos[0],pos[1]+i};
                    if(!canBePutThere(nextTarget))return false;
                }
                return true;
            }
            case DOWN -> {
                int length=target[0]-pos[0];
                for(int i=1;i<=length;i++){
                    int[] nextTarget={pos[0]+i,pos[1]};
                    if(!canBePutThere(nextTarget))return false;
                }
                return true;
            }
            case UP -> {
                int length=pos[0]-target[0];
                for(int i=1;i<=length;i++){
                    int[] nextTarget={pos[0]-i,pos[1]};
                    if(!canBePutThere(nextTarget))return false;
                }
                return true;
            }
        }
        return false;
    }
    private boolean canBePutThere(int []target) {
        if(target[0] > -1 &&target[0] < mapHeight && target[1] > -1 && target[1] < mapLength)return Objects.equals(map[target[1]][target[0]], " ");
        else return false;
    }
    private void incrementLastSeen(int[][] lastSeen)
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
