package BrickAndMortar;

import Config.Variables;
import Entities.Intruder;
import Enums.*;
import Logic.GameController;
import Patrolling.Position;
import Strategies.Constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//BEGIN V2:
import Config.Variables;
import Entities.Intruder;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import OptimalSearch.TreeRoot;
import PathMaking.Point;
import Strategies.Strategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.*;


public class BrickMortar extends Strategy {


    private int [][]map;
    private int []xy;

    private boolean exploDone;
    private final double randomness = 0.2;

    /*
    public Moves brickAndMortar(int[] xy, Rotations rot, GameController gm, Variables vr)
    {
        //Sets all cells that are in the intruder's vision to "explored"
        //Marking Step:

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

     */

/*
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


 */
    /*
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



     */


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




    /**
     *
     * BEGINNING OF NEW VERSION
     */

    private final HashMap<Integer, ArrayList<Integer>> explored;
    private final HashMap<Integer, ArrayList<Integer>> unexplored;
    private final HashMap<Integer, ArrayList<Integer>> visited;
    private final HashMap<Integer, ArrayList<Integer>> walls;
    private final HashMap<Integer,int[]> objects;
    private boolean chased;
    private boolean searching;
    private final Constraints constraints;
    private final Rotations[] availableRotations = {Rotations.BACK, Rotations.RIGHT, Rotations.LEFT, Rotations.FORWARD};
    private final Moves[] availableMoves = {Moves.WALK, Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND, Moves.USE_TELEPORTER};
    private boolean atGoal;
    private boolean walked;
    private int count = 0;
    private ArrayList<Position> visitedCells;
    private int mapHeight;
    private int mapLength;

    public BrickMortar()
    {
        this.explored = new HashMap<>();
        this.unexplored = new HashMap<>();
        this.visited = new HashMap<>();
        this.walls = new HashMap<>();
        this.objects = new HashMap<>();
        this.constraints=new Constraints();
        this.visitedCells = new ArrayList();
        this.atGoal = false;
        this.walked = true;


    }

    @Override
    public Moves decideOnMoveIntruder(String[][] vision, int[] xy, Rotations rot, Variables vr, GameController gm, Intruder intruder)
    {
        mapHeight = gm.getMapHeight();
        mapLength = gm.getMapLength();
        System.out.println("X: "+xy[0] +" Y: "+xy[1]);
        updateExploration(vision,xy,rot);

        //MARKING STEP:
        if(isBlockingPath(xy))
        {
            //Since we aren't "using" the unexplored HashMap, will this cause an overwrite error?
            explored.put(xy[0], new ArrayList<>());
            explored.get(xy[0]).add(xy[1]);
            System.out.println("MARKING STEP: EXPLORED SOMETHING");
        }
        else
        {
            //TODO: change from Position into int[] xy
            //TODO: this is whack
            Position currentPosition = new Position(xy[0],xy[1]);
            if(visitedCells.isEmpty())
            {
                visited.put(xy[0], new ArrayList<>());
                visited.get(xy[0]).add(xy[1]);
                visitedCells.add(currentPosition);
            }

            for(int i = 0; i < visitedCells.size(); i++)
            {
                int x = visitedCells.get(i).getX();
                int y = visitedCells.get(i).getY();

                if(xy[0] != x && xy[1] != y)
                {
                    visited.put(xy[0], new ArrayList<>());
                    visited.get(xy[0]).add(xy[1]);
                    visitedCells.add(currentPosition);
                    System.out.println("MARKING STEP: VISITED SOMETHING, SIZE = "+visitedCells.size());
                }
            }
        }

        //Navigation Step:
        //if one of the four cells around is "UNEXPLORED"
        // count it's unexplored neighbours, I assume we can do this by using a negative (!) statement, so count
        // the LACK of explored neighbours
        //V2: added inBounds to moves.walk statements RESULT: ends after 3 iterations
        //code: && inBounds(bestUnexploredPos[0], bestUnexploredPos[1])
        //V3: removing inBounds
        if(hasUnexploredNeighbours(xy, vr))
        {
            System.out.println("NAVIGATION STEP: CHECKING UNEXPLORED NEIGHBOURS");
            ArrayList<Position> unexploredNeighbours = getUnexploredNeighbours(xy, vr);
            int[] bestUnexploredPos = getBestUnexplored(unexploredNeighbours, vr);
            int xDiff = bestUnexploredPos[0] - xy[0];
            int yDiff = bestUnexploredPos[1] - xy[1];
            if(yDiff == 0)
            {
                //Move to the right
                if(xDiff > 0)
                {
                    //check if it has to rotate first before moving
                    switch(rot)
                    {
                        case LEFT -> {return Moves.TURN_AROUND;}
                        case RIGHT -> {
                            if(isFacingWall(xy, Rotations.RIGHT, vr, vision))
                            {
                                return evadeWall(gm, intruder);
                            }
                            if(!isVisited(xy,vr,rot)) {
                                return Moves.WALK;
                            }
                            else
                            {
                                //TODO: FIX LOOP STUCK POSSIBILITY
                                return Moves.STUCK;
                            }
                        }
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
                        case LEFT -> {
                            if(isFacingWall(xy, Rotations.LEFT, vr, vision))
                            {
                                return evadeWall(gm, intruder);
                            }
                            if(!isVisited(xy,vr,rot)) {
                                return Moves.WALK;
                            }
                            else
                            {
                                return Moves.STUCK;
                            }
                        }
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
                        case BACK -> {
                            if(isFacingWall(xy, Rotations.BACK, vr, vision))
                            {
                                return evadeWall(gm, intruder);
                            }
                            if(!isVisited(xy,vr,rot)) {
                                return Moves.WALK;
                            }
                            else
                            {
                                return Moves.STUCK;
                            }
                        }
                    }
                }
                else
                {
                    switch(rot)
                    {
                        case LEFT -> {return Moves.TURN_LEFT;}
                        case RIGHT -> {return Moves.TURN_RIGHT;}
                        //TODO: keeps going into case forward despite it having better explored/unexplored cells
                        //Is it because of bounds?
                        case FORWARD -> {
                            if(isFacingWall(xy, Rotations.FORWARD, vr, vision))
                            {
                                return evadeWall(gm, intruder);
                            }
                            if(!isVisited(xy,vr,rot)) {
                                System.out.println(isFacingWall(xy, Rotations.FORWARD, vr, vision));
                                System.out.println("Well it's not visited, so it's trying to walk");
                                return Moves.WALK;
                            }
                            else
                            {
                                return Moves.STUCK;
                            }
                        }
                        case BACK -> {return Moves.TURN_AROUND;}
                    }
                }
            }
        }
        //ELSE IF: at least one of the four cells around is explored
        ArrayList<Position> exploredNeighbours = getExploredNeighbours(xy);
        if(exploredNeighbours.size() != 0)
        {
            //Make it always head into the target direction
            //randomness makes it choose another cell to go to than the one in the direction of the target
            double chance = Math.random();
            if(chance > randomness)
            {
                return gm.getNextBestMove(intruder);
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
                if(randomMove < 0.75)
                {
                    return Moves.TURN_AROUND;
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

    //TODO: BIANCA'S SOLUTION
    //Solution 1: use updateExploration and just make it check for a wall
    //Solution 2: create a boolean walked like in intruder and the method stuck() in intruderst
    //V2: since it was giving me the opposite of what I wanted, I just changed the return values - REVERTED
    public boolean isFacingWall(int[] xy, Rotations rot, Variables vr, String[][] vision)
    {
        //V3
        /*
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
                        if(i == 1)
                        {
                            if(j == 0)
                            {
                                if(lookingAt.contains("W"));
                                {
                                    System.out.println("There's a wall in front of me");
                                    System.out.println("X = "+h+" and Y = "+l);
                                    System.out.println(lookingAt);
                                    System.out.println("INTRUDER LOCATION : X ="+xy[0]+" Y = "+xy[1]);
                                    return true;
                                }
                            }
                        }
                    }
                    case BACK -> {
                        if(i == 1)
                        {
                            if(j == 0)
                            {
                                if(lookingAt.contains("W"));
                                {
                                    System.out.println("There's a wall in front of me");
                                    System.out.println("X = "+h+" and Y = "+l);
                                    System.out.println(lookingAt);
                                    System.out.println("INTRUDER LOCATION : X ="+xy[0]+" Y = "+xy[1]);
                                    return true;
                                }
                            }
                        }
                    }
                    case LEFT -> {
                        if(i == 1)
                        {
                            if(j == 0)
                            {
                                if(lookingAt.contains("W"));
                                {
                                    System.out.println("There's a wall in front of me");
                                    System.out.println("X = "+h+" and Y = "+l);
                                    System.out.println(lookingAt);
                                    System.out.println("INTRUDER LOCATION : X ="+xy[0]+" Y = "+xy[1]);
                                    return true;
                                }
                            }
                        }
                    }
                    case RIGHT -> {
                        if(i == 1)
                        {
                            if(j == 0)
                            {
                                if(lookingAt.contains("W"));
                                {
                                    System.out.println("There's a wall in front of me");
                                    System.out.println("X = "+h+" and Y = "+l);
                                    System.out.println(lookingAt);
                                    System.out.println("INTRUDER LOCATION : X ="+xy[0]+" Y = "+xy[1]);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        */
        /* V2
        int eyeRange = vision.length;
        int currentX = xy[0];
        int currentY = xy[1];
                int h;  // h = 1
                int l; // set to 0
                //TODO: modify lookingAt
                switch (rot) {
                    case FORWARD -> {
                        //Working values for h/x and l/y are 3 & 2
                        // Don't ask me why it's these values
                        //THIS CHANGES DEPENDING ON THE STARTING ROTATION OF THE INTRUDER
                        h = 3;
                        l = 2;
                        String lookingAt = vision[h][l];
                        if(lookingAt.contains("W")){
                            System.out.println("There's a wall in front of me");
                            System.out.println("X = "+h+" and Y = "+l);
                            System.out.println(lookingAt);
                            System.out.println("INTRUDER LOCATION : X ="+xy[0]+" Y = "+xy[1]);
                            return true;
                        }
                    }
                    case BACK -> {
                        //TODO: DETERMINE VALUES
                        //THIS CHANGES DEPENDING ON THE STARTING ROTATION OF THE INTRUDER
                        h = 2;
                        l = 3;
                        String lookingAt = vision[h][l];
                        if(lookingAt.contains("W")){
                            System.out.println("There's a wall in front of me");
                            System.out.println(lookingAt);
                            System.out.println("X = "+h+" and Y = "+l);
                            System.out.println("INTRUDER LOCATION : X ="+xy[0]+" Y = "+xy[1]);
                            return true;
                        }
                    }
                    case LEFT -> {
                        //TODO: determine values
                        // THIS CHANGES DEPENDING ON THE STARTING ROTATION OF THE INTRUDER
                        h = 2;
                        l = 2;
                        String lookingAt = vision[h][l];
                        if(lookingAt.contains("W")){
                            System.out.println("There's a wall in front of me");
                            System.out.println("X = "+h+" and Y = "+l);
                            System.out.println(lookingAt);
                            System.out.println("INTRUDER LOCATION : X ="+xy[0]+" Y = "+xy[1]);
                            return true;
                        }
                    }
                    case RIGHT -> {
                        //Values for this one
                        //THIS CHANGES DEPENDING ON THE STARTING ROTATION OF THE INTRUDER
                        h = 4;
                        l = 3;
                        String lookingAt = vision[h][l];
                        if(lookingAt.contains("W")){
                            System.out.println("There's a wall in front of me");
                            System.out.println("X = "+h+" and Y = "+l);
                            System.out.println(lookingAt);
                            System.out.println("INTRUDER LOCATION : X ="+xy[0]+" Y = "+xy[1]);
                            return true;
                        }
                    }

               // }
            //}
        }

         */

        /*
        switch(rot)
        {
            //
            case FORWARD -> {
                if(walls.containsValue((xy[1] + 1) - vr.walkSpeed()))
                {
                    return true;
                }
                else {
                    return false;
                }
            }
            case BACK -> {
                if(walls.containsValue(xy[1] - 1))
                {
                    return true;
                }
                else {
                    return false;
                }
            }
            case LEFT -> {
                if(walls.containsKey(xy[0] - 1))
                {
                    return true;
                }
                else {
                    return false;
                }
            }
            case RIGHT -> {
                if(walls.containsKey(xy[0] + 1))
                {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
         */
        return false;
    }
    //If it crashes into a wall, just make it cheat a move.
    public Moves evadeWall(GameController gm, Intruder intruder)
    {
        //return gm.getDirection(intruder);
        Moves nextMove= gm.getNextBestMove(intruder);
        System.out.println(nextMove);
        return nextMove;
    }

    //Whenever the intruder wants to walk it first has to make sure the cell it's going to isn't visited
    //POTENTIALLY ERROR CAUSING, just seems like it idk
    public boolean isVisited(int[] xy, Variables vr, Rotations rot)
    {
        if (rot == Rotations.FORWARD) {
            if (explored.containsKey(xy[0]) && explored.get(xy[0]).contains(xy[1]+vr.walkSpeed())) {
                Position positionToCheck = new Position(xy[0], xy[1] + vr.walkSpeed());
                for (int i = 0; i < visitedCells.size(); i++) {
                    int x = positionToCheck.getX();
                    int y = positionToCheck.getY();

                    if(visitedCells.get(i).getX() == x && visitedCells.get(i).getY() == y)
                    {
                        return true;
                    }
                }
            }
        }else if (rot == Rotations.RIGHT) {
            if (explored.containsKey(xy[0]+vr.walkSpeed()) && explored.get(xy[0]+ vr.walkSpeed()).contains(xy[1])) {
                Position positionToCheck = new Position(xy[0], xy[1] + vr.walkSpeed());
                for (int i = 0; i < visitedCells.size(); i++) {
                    int x = positionToCheck.getX();
                    int y = positionToCheck.getY();

                    if(visitedCells.get(i).getX() == x && visitedCells.get(i).getY() == y)
                    {
                        return true;
                    }
                }
            }
        }else if (rot == Rotations.LEFT) {
            if (explored.containsKey(xy[0] - vr.walkSpeed()) && explored.get(xy[0] - vr.walkSpeed()).contains(xy[1])) {
                Position positionToCheck = new Position(xy[0], xy[1] + vr.walkSpeed());
                for (int i = 0; i < visitedCells.size(); i++) {
                    int x = positionToCheck.getX();
                    int y = positionToCheck.getY();

                    if(visitedCells.get(i).getX() == x && visitedCells.get(i).getY() == y)
                    {
                        return true;
                    }
                }
            }
        }else if (rot == Rotations.BACK) {
            if (explored.containsKey(xy[0]) && explored.get(xy[0]).contains(xy[1] - vr.walkSpeed())) {
                Position positionToCheck = new Position(xy[0], xy[1] + vr.walkSpeed());
                for (int i = 0; i < visitedCells.size(); i++) {
                    int x = positionToCheck.getX();
                    int y = positionToCheck.getY();

                    if(visitedCells.get(i).getX() == x && visitedCells.get(i).getY() == y)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public ArrayList<Position> getExploredNeighbours(int[] xy)
    {
        ArrayList<Position> exploredNeighbours = new ArrayList<Position>();
        if(explored.containsKey(xy[0] - 1)){
            exploredNeighbours.add(new Position(xy[0] - 1, xy[1]));
        }
        if(explored.containsKey(xy[0] + 1))
        {
            exploredNeighbours.add(new Position(xy[0] + 1, xy[1]));
        }
        if(explored.containsValue(xy[1] + 1))
        {
            exploredNeighbours.add(new Position(xy[0], xy[1]+1));
        }
        if(explored.containsValue(xy[1] - 1))
        {
            exploredNeighbours.add(new Position(xy[0], xy[1]-1));
        }
        return exploredNeighbours;
    }

    //Giving the same treatment, changing if conditions to POS -/+ vr
    public boolean hasUnexploredNeighbours(int[] xy, Variables vr)
    {
        ArrayList<Position> unexploredNeighbours = new ArrayList<Position>();
        boolean hasUnexploredNeighbour = false;
        //key is for up/down
        if(!explored.containsKey((xy[0] - 1 ) - vr.walkSpeed())){
            hasUnexploredNeighbour = true;
        }
        if(!explored.containsKey((xy[0] + 1) + vr.walkSpeed()))
        {
            hasUnexploredNeighbour = true;
        }
        if(!explored.containsValue((xy[1] + 1) + vr.walkSpeed()))
        {
            hasUnexploredNeighbour = true;
        }
        if(!explored.containsValue((xy[1] - 1) - vr.walkSpeed()))
        {
            hasUnexploredNeighbour = true;
        }
        return hasUnexploredNeighbour;
    }

    public ArrayList<Position> getUnexploredNeighbours(int[] xy, Variables vr)
    {
        ArrayList<Position> unexploredNeighbours = new ArrayList<>();
        if(!explored.containsKey((xy[0] - 1 ) - vr.walkSpeed())){
            unexploredNeighbours.add(new Position(xy[0] - 1, xy[1]));
        }
        if(!explored.containsKey((xy[0] + 1) + vr.walkSpeed()))
        {
            unexploredNeighbours.add(new Position(xy[0] + 1, xy[1]));
        }
        if(!explored.containsValue((xy[1] + 1) + vr.walkSpeed()))
        {
            unexploredNeighbours.add(new Position(xy[0], xy[1]+1));
        }
        if(!explored.containsValue((xy[1] - 1) - vr.walkSpeed()))
        {
            unexploredNeighbours.add(new Position(xy[0], xy[1]-1));
        }
        return unexploredNeighbours;
    }

    //CHANGING THIS MADE IT NO LONGER GO TO THE RIGHT
    //change 2: removing vr.walkspeed again    RESULT: GOES BACK TO THE RIGHT AGAIN
    public int[] getBestUnexplored(ArrayList<Position> unexploredNeighbours, Variables vr)
    {
        int count = 0;
        int[] bestXY = new int[2];

        for(Position unexplored : unexploredNeighbours)
        {
            int currentCount = 0;
            int[] currentXY =  {unexplored.getX(), unexplored.getY()};

            if(visited.containsKey((currentXY[0]-1)) || walls.containsKey((currentXY[0]-1)))
            {
                currentCount++;
            }
            if(visited.containsKey((currentXY[0]+1)) || walls.containsKey((currentXY[0]+1)))
            {
                currentCount++;
            }
            if(visited.containsValue((currentXY[1]+1)) || walls.containsValue((currentXY[0]+1)))
            {
                currentCount++;
            }
            if(visited.containsValue((currentXY[0]-1)) || walls.containsValue((currentXY[0]-1)))
            {
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

    //V2: Added else statement to if X statement RESULT: Doesn't change anything - REMOVED
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

    private boolean inBounds(int x, int y) {
        return (x > -1 && x < mapHeight && y > -1 && y < mapLength);
    }

    private HashMap<Integer, ArrayList<Integer>> deepClone(HashMap<Integer, ArrayList<Integer>> maptoCopy) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(maptoCopy);
        Type type = new TypeToken<HashMap<Integer, ArrayList<Integer>>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }
}

