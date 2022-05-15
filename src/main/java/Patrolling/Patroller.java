package Patrolling;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;

public class Patroller {
    private int maxDepth;
    public int dfsRecursive(int depth, int[] xy, Moves availableMoves, int[][] lastSeen, Rotations rotation)
    {
        int maxValue = 0;
        if(depth == 0)
        {
            //Reached the end of the DFS, return the value of current child back up so that the parent can decide
            //which has the highest value
            return maxValue;
        }
        else
        {
            //Return the highest value of all the children
            for(availableMoves : availableMoves)
            {
                Rotations newRotation = null;
                dfsRecursive(depth - 1, xy ,getChildrensMoves(xy, lastSeen, rotation), lastSeen, newRotation);
            }
        }
        return maxValue;
    }

    // public void dfs(int[] xy)

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
}
