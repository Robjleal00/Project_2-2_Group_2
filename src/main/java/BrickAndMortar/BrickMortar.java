package BrickAndMortar;

import Config.Variables;
import Entities.Intruder;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import Patrolling.Position;

import java.util.ArrayList;
import java.util.Arrays;

public class BrickMortar {

    private final Moves[] availableMoves = {Moves.WALK, Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND, Moves.USE_TELEPORTER};

    int unexplored = 0;
    int explored = 1;
    int visited = 2;
    int walls = 3;


    private final int mapLength;
    private final int mapHeight;

    private int [][]map;
    private int []xy;

    private boolean exploDone;
    private final double randomness = 0.2;
    private final Rotations rot;


    public BrickMortar(int[][] map, Rotations rot){
        this.map = map;
        this.exploDone = false;
        this.rot = rot;
        this.mapHeight = map.length;
        this.mapLength = map[0].length;
    }





    public Moves brickAndMortar(int[] xy, Rotations rot, GameController gm, Variables vr)
    {
        updateVision(rot, xy,vr);
        //Marking Step:
        //
        if(isBlockingPath(xy) == false)
        {
            map[xy[0]][xy[1]] = visited;
        }
        else
        {
            map[xy[0]][xy[1]] = explored;
        }

        //Navigation Step:
        //NOTE: IF AT LEAST ONE OF THE 4 SURROUNDING CELLS IS UNEXPLORED
        ArrayList<Position> unexploredNeighbours = returnUnexploredNeighbours(xy);
        if(unexploredNeighbours.size() != 0)
        {
            int[] bestUnexploredPos = bestUnexplored(unexploredNeighbours);
            int xDiff = bestUnexploredPos[0] - xy[0];
            int yDiff = bestUnexploredPos[1] - xy[1];

            //TODO: compare with Bianca and Husam's intruder to see if global rotation also applies here
            // I added this in here for now
            Rotations intruderRotation = gm.getIntRot();
            if(yDiff == 0)
            {
                //Move to the right
                if(xDiff > 0)
                {
                    //check if it has to rotate first before moving
                    switch(rot)
                    {
                        case LEFT -> {return Moves.TURN_AROUND;}
                        case RIGHT -> {return Moves.WALK;}
                        //TODO: Am I mixing up i and j? Is it xy or yx?
                        //Looking at Piotr's code from patroller, forward would mean it's facing down???
                        //compare starting line 266 in patroller
                        case FORWARD -> {return Moves.TURN_LEFT;}
                        case BACK -> {return Moves.TURN_RIGHT;}
                    }
                }
                else
                {
                    //Move left
                    switch(rot)
                    {
                        case LEFT -> {return Moves.WALK;}
                        case RIGHT -> {return Moves.TURN_AROUND;}
                        case FORWARD -> {return Moves.TURN_RIGHT;}
                        case BACK -> {return Moves.TURN_LEFT;}
                    }
                }
            }
            else if(xDiff == 0)
            {
                if(yDiff < 0)
                {
                    //GO UP (????)
                    switch(rot)
                    {
                        case LEFT -> {return Moves.TURN_RIGHT;}
                        case RIGHT -> {return Moves.TURN_LEFT;}
                        case FORWARD -> {return Moves.TURN_AROUND;}
                        case BACK -> {return Moves.WALK;}
                    }
                }
                else
                {
                    switch(rot)
                    {
                        case LEFT -> {return Moves.TURN_LEFT;}
                        case RIGHT -> {return Moves.TURN_RIGHT;}
                        case FORWARD -> {return Moves.WALK;}
                        case BACK -> {return Moves.TURN_AROUND;}
                    }
                }
            }
        }
        // NOTE ELSE IF: else if at least one of the four cells around is explored
        ArrayList<Position> exploredNeighbours = returnExploredNeighbours(xy);
        if(exploredNeighbours.size() != 0)
        {
            //Make it always head into the target direction
            //randomness makes it choose another cell to go to than the one in the direction of the target
            double chance = Math.random();
            if(chance > randomness)
            {
                //This is "cheating" since it just gets the targetPosition, but it doesn't plot an immediate path to it
                //so this is "fine"
                int[] targetPosition = gm.brickAndMortarDirection();
                int xDiff = targetPosition[0] - xy[0];
                int yDiff = targetPosition[1] - xy[1];

                Rotations intruderRotation = gm.getIntRot();
                if(yDiff == 0)
                {
                    //Move to the right
                    if(xDiff > 0)
                    {
                        //check if it has to rotate first before moving
                        switch(rot)
                        {
                            case LEFT -> {return Moves.TURN_AROUND;}
                            case RIGHT -> {return Moves.WALK;}
                            //TODO: Am I mixing up i and j? Is it xy or yx?
                            //Looking at Piotr's code from patroller, forward would mean it's facing down???
                            //compare starting line 266 in patroller
                            case FORWARD -> {return Moves.TURN_LEFT;}
                            case BACK -> {return Moves.TURN_RIGHT;}
                        }
                    }
                    else
                    {
                        //Move left
                        switch(rot)
                        {
                            case LEFT -> {return Moves.WALK;}
                            case RIGHT -> {return Moves.TURN_AROUND;}
                            case FORWARD -> {return Moves.TURN_RIGHT;}
                            case BACK -> {return Moves.TURN_LEFT;}
                        }
                    }
                }
                else if(xDiff == 0)
                {
                    if(yDiff < 0)
                    {
                        //GO UP (????)
                        switch(rot)
                        {
                            case LEFT -> {return Moves.TURN_RIGHT;}
                            case RIGHT -> {return Moves.TURN_LEFT;}
                            case FORWARD -> {return Moves.TURN_AROUND;}
                            case BACK -> {return Moves.WALK;}
                        }
                    }
                    else
                    {
                        switch(rot)
                        {
                            case LEFT -> {return Moves.TURN_LEFT;}
                            case RIGHT -> {return Moves.TURN_RIGHT;}
                            case FORWARD -> {return Moves.WALK;}
                            case BACK -> {return Moves.TURN_AROUND;}
                        }
                    }
                }
            }
            else
            {
                //else do an arbitrary move
                //Placeholder for now
                double randomMove = Math.random();
                if(randomMove < 0.25)
                {
                    return Moves.TURN_RIGHT;
                }
                if(randomMove < 0.5)
                {
                    return Moves.TURN_LEFT;
                }
                if(randomMove < 1)
                {
                    return Moves.WALK;
                }
            }


        }
        else
        {
            return Moves.STUCK;
        }

        return null;
    }


    public ArrayList<Position> returnUnexploredNeighbours(int[]xy){

            ArrayList<Position> unexploredNeighbours = new ArrayList<Position>();

            // check LEFT
            if(map[xy[0] - 1][xy[1]] == unexplored){
                unexploredNeighbours.add(new Position(xy[0] - 1, xy[1]));

            }
            // Check RIGHT
            else if(map[xy[0] + 1][xy[1]] == unexplored){
                unexploredNeighbours.add(new Position(xy[0] + 1, xy[1]));
            }
            // Check UP
            else if(map[xy[0]][xy[1] + 1] == unexplored){
                unexploredNeighbours.add(new Position(xy[0], xy[1] + 1));
            }
            // Check DOWN
            else if(map[xy[0]][xy[1] - 1] == unexplored){
                unexploredNeighbours.add(new Position(xy[0], xy[1] - 1));

            }
            return unexploredNeighbours;
        }

    public ArrayList<Position> returnExploredNeighbours(int[]xy){

        ArrayList<Position> exploredNeighbours = new ArrayList<Position>();

        // check LEFT
        if(map[xy[0] - 1][xy[1]] == unexplored){
            exploredNeighbours.add(new Position(xy[0] - 1, xy[1]));
        }
        // Check RIGHT
        else if(map[xy[0] + 1][xy[1]] == unexplored){
            exploredNeighbours.add(new Position(xy[0] + 1, xy[1]));
        }
        // Check UP
        else if(map[xy[0]][xy[1] + 1] == unexplored){
            exploredNeighbours.add(new Position(xy[0], xy[1] + 1));
        }
        // Check DOWN
        else if(map[xy[0]][xy[1] - 1] == unexplored){
            exploredNeighbours.add(new Position(xy[0], xy[1] - 1));
        }
        return exploredNeighbours;
    }


    public int[] bestUnexplored(ArrayList<Position> exploredNeighbours){
        int count = 0;
        // This will store the pos of the best unexplored neighbour
        int[] bestXY = new int[1];

        //check walls and visited
        for(Position explored: exploredNeighbours){
            int currentCount = 0;
            int[] currentXY = {explored.getX(), explored.getY()};

            // check LEFT
            if(map[xy[0] - 1][xy[1]] == walls || map[xy[0] - 1][xy[1]] == visited){
                currentCount++;
            }
            // Check RIGHT
            else if(map[xy[0] + 1][xy[1]] == unexplored || map[xy[0] + 1][xy[1]] == visited ){
                currentCount++;
            }
            // Check UP
            else if(map[xy[0]][xy[1] + 1] == unexplored || map[xy[0]][xy[1] + 1] == visited){
                currentCount++;
            }
            // Check DOWN
            else if(map[xy[0]][xy[1] - 1] == unexplored || (map[xy[0]][xy[1] - 1] == visited)){
                currentCount++;
            }
            if(currentCount > count){
                count = currentCount;
                bestXY[0] = currentXY[0];
                bestXY[1] = currentXY[1];
            }
        }
        return bestXY;
    }




    /**
     *
     * @param xy Position of the cell
     * @return
     */
    public boolean isBlockingPath(int[] xy)
    {
        return false;
    }

    //This would probably only be called when the intruder first spawns and when it uses a
    //teleporter, since it usually knows the way it came from, and can see to its left and right
    //Interesting research question how this performs when we cut down its vision to only straight
    /*
    public boolean checkUnexploredSurroundings(int[] xy)
    {
        getUnexploredNeighbours(xy);
        if(!unexploredNeighbours.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    */

    public int countWallsAndVisited(int[] xy)
    {
        int count = 0;
        // for every wall and visited cell add to count
        return count;
    }

    public void updateVision(Rotations rotation, int[] xy, Variables vr)
    {
        // O = Unexplored
        // 1 = Explored
        // 2 = Visited
        // 3 = Wall

        int x = xy[0];
        int y = xy[1];
        int range = vr.eyeRange();
        for (int i = -1; i < 2; i++) { // sideways
            for (int j = 0; j < range; j++) { // upfront
                int sX = range - 1 - j;
                int sY = i + 1;
                switch (rotation) {
                    case BACK -> {
                        int xM = x + j;
                        int yM = y - i;
                        if (inBounds(xM, yM)) {
                            //TODO: look into this:
                            //Copied over from Patroller which used sX/sY instead of xM, yM

                            //Don't set walls, explored and visited cells as explored
                            if (map[sX][sY]!= 3 && map[sX][sY]!= 2 && map[sX][sY]!= 1) {
                                map[xM][yM] = explored;
                            }
                        }
                    }
                    case FORWARD -> {
                        int xM = x - j;
                        int yM = y + i;
                        if (inBounds(xM, yM)) {
                            if (map[sX][sY]!= 3 && map[sX][sY]!= 2 && map[sX][sY]!= 1) {
                                map[xM][yM] = explored;
                            }
                        }
                    }
                    case RIGHT -> {
                        int xM = x + i;
                        int yM = y + j;
                        if (inBounds(xM, yM)) {
                            if (map[sX][sY]!= 3 && map[sX][sY]!= 2 && map[sX][sY]!= 1) {
                                map[xM][yM] = explored;
                            }
                        }
                    }
                    case LEFT -> {
                        int xM = x - i;
                        int yM = y - j;
                        if (inBounds(xM, yM)) {
                            if (map[sX][sY]!= 3 && map[sX][sY]!= 2 && map[sX][sY]!= 1) {
                                map[xM][yM] = explored;
                            }
                        }
                    }
                }
            }
        }
    }


    private boolean inBounds(int x, int y) {
        return (x > -1 && x < mapHeight && y > -1 && y < mapLength);
    }

}
