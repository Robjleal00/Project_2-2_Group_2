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

    private final double randomness = 0.2;



    /**
     *
     * @param xy Position of the cell
     * @return
     */
    public boolean isBlockingPath(int[] xy)
    {
        //return false;

        Position pos = new Position(xy[0], xy[1]);
        ArrayList<Position> neighbouringCells = getWalkableNeighbors(pos);
        for(int i = 0; i < neighbouringCells.size(); i++)
        {
            for(int j = i; j < neighbouringCells.size(); j++)
            {
                if(!pathExists(neighbouringCells.get(i), neighbouringCells.get(j), pos))
                {
                    return true;
                }
            }
        }
        return false;



    }

    public ArrayList<Position> getWalkableNeighbors(Position pos)
    {
        ArrayList<Position> walkableNeighbors = new ArrayList<>();
        for(int i = -1; i < 1; i++)
        {
            for(int j = -1; j < 1; j++)
            {
                Position check = new Position(pos.getX()+i, pos.getY()+j);
                if(checkArray(exploredCells, check))
                {
                    walkableNeighbors.add(check);
                }
            }
        }
        return walkableNeighbors;
    }

    private boolean pathExists(Position start, Position target, Position trueStart)
    {
        BreadthFirstSearch bfs = new BreadthFirstSearch(trueStart);
        return bfs.pathExists(start, target);
    }

    class BreadthFirstSearch {
        private Queue<Position> queue;
        private ArrayList<Position> visited;
        private Position start;
        private Position trueStart;

        public BreadthFirstSearch(Position trueStart)
        {
            this.visited = new ArrayList<>();
            this.queue = new LinkedList<>();
            this.trueStart = trueStart;
        }

        boolean pathExists(Position start, Position target)
        {
            this.start = start;
            this.queue = new LinkedList<>();
            this.visited = new ArrayList<>();
            queue.add(start);

            //TODO: gets into an infinite loop here
            while(!queue.isEmpty())
            {
                Position currentPos = queue.poll();
                if(isSame(currentPos, target))
                {
                    return true;
                }
                visited.add(currentPos);
                //TODO: specifically here
                for(Position neighbour : getWalkableNeighbors(currentPos))
                {
                    if(isSame(neighbour, target))
                    {
                        return true;
                    }
                    addUnvisitedPosition(neighbour, currentPos);
                    //addUnvisitedPosition(neighbour);
                }
            }
            return false;
        }

        //V2
        public void addUnvisitedPosition(Position pos, Position current)
        {
            if(!isVisited(pos) && !(isSame(pos, current)))
            {
                queue.add(pos);
                visited.add(pos);
            }
        }


        private void addUnvisitedPosition(Position pos)
        {
            if(!isVisited(pos) && !(isSame(pos, trueStart)))
            {
                queue.add(pos);
                visited.add(pos);
            }
        }
        boolean isVisited(Position pos)
        {
            return checkArray(visited, pos);
        }
    }


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
    private boolean targetFound;
    private final Constraints constraints;
    private final Rotations[] availableRotations = {Rotations.BACK, Rotations.RIGHT, Rotations.LEFT, Rotations.FORWARD};
    private final Moves[] availableMoves = {Moves.WALK, Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND, Moves.USE_TELEPORTER};
    private boolean atGoal;
    private boolean walked;
    private int count = 0;
    private ArrayList<Position> visitedCells;
    private Moves lastMove;
    private Position lastPosition;
    private Position lastBest;


    private ArrayList<Position> exploredCells;
    private ArrayList<Position> wallCells;
    private Position nextBest;
    private ArrayList<Position> unexploredNeighbours;
    private ArrayList<Position> exploredNeighbours;
    private boolean direction = false;
    private boolean best = false;

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
        this.targetFound = false;
        this.exploredCells = new ArrayList<>();
        this.wallCells = new ArrayList<>();
        this.unexploredNeighbours = new ArrayList<>();
        this.exploredNeighbours = new ArrayList<>();


    }

    @Override
    public Moves decideOnMoveIntruder(String[][] vision, int[] xy, Rotations rot, Variables vr, GameController gm, Intruder intruder)
    {
        updateExploration(vision, xy, rot);
        lastPosition = new Position(xy[0], xy[1]);
        //Marking Step:
        if(!isBlockingPath(xy))
        {

            Position currentPosition = new Position(xy[0], xy[1]);
            if(visitedCells.isEmpty())
            {
                visitedCells.add(currentPosition);
                //System.out.println("Added something to visited. Cell X: "+ currentPosition.getX()+" Y:"+currentPosition.getY());
            }
            else
            {
                if(!checkArray(visitedCells, currentPosition))
                {
                    visitedCells.add(currentPosition);
                    //System.out.println("Added something to visited. Cell X: "+ currentPosition.getX()+" Y:"+currentPosition.getY());
                    if(checkArray(exploredCells, currentPosition))
                    {
                        //System.out.println("Removed something from explored");
                        exploredCells.remove(currentPosition);
                    }
                }
                /*
                boolean shouldSave = true;
                for(int i = 0; i < visitedCells.size(); i++)
                {
                    int x = visitedCells.get(i).getX();
                    int y = visitedCells.get(i).getY();

                    if(xy[0] == x && xy[1] == y)
                    {
                        shouldSave = false;
                    }
                }
                if(shouldSave == true){
                    visited.put(xy[0], new ArrayList<>());
                    visited.get(xy[0]).add(xy[1]);
                    visitedCells.add(currentPosition);
                    System.out.println("MARKING STEP: VISITED SOMETHING, SIZE = "+visitedCells.size());
                }

                 */
            }
        }
        else
        {
            Position pos = new Position(xy[0], xy[1]);
            if(!checkArray(exploredCells, pos))
            {
                exploredCells.add(pos);
            }
        }

        //Navigation step
        if(hasUnexploredNeighbors(xy)) {
            Position bestUnexplored = getBestUnexplored();
            lastBest = bestUnexplored;
            int xDiff = bestUnexplored.getX() - xy[0];
            int yDiff = bestUnexplored.getY() - xy[1];
            lastPosition = new Position(xy[0], xy[1]);
            System.out.println("Move origin Step 9");
            if (yDiff == 0) {
                //Move to the right
                if (xDiff > 0) {
                    switch (rot) {
                        case LEFT -> {
                            lastMove = Moves.TURN_AROUND;
                            return Moves.TURN_AROUND;
                        }
                        case RIGHT -> {
                            lastMove = Moves.WALK;
                            return Moves.WALK;
                        }
                        case FORWARD -> {
                            lastMove = Moves.TURN_LEFT;
                            return Moves.TURN_LEFT;
                        }
                        case BACK -> {
                            lastMove = Moves.TURN_RIGHT;
                            return Moves.TURN_RIGHT;
                        }
                    }
                    //check if it has to rotate first before moving
                }
                else {

                    switch (rot) {
                        case LEFT -> {
                            lastMove = Moves.WALK;
                            return Moves.WALK;

                        }
                        case RIGHT -> {
                            lastMove = Moves.TURN_AROUND;
                            return Moves.TURN_AROUND;
                        }
                        case FORWARD -> {
                            lastMove = Moves.TURN_RIGHT;
                            return Moves.TURN_RIGHT;
                        }
                        case BACK -> {
                            lastMove = Moves.TURN_LEFT;
                            return Moves.TURN_LEFT;
                        }
                    }
                }
            }
            else if(xDiff == 0)
            {
                if(yDiff < 0)
                {
                    switch (rot) {
                        case LEFT -> {lastMove = Moves.TURN_RIGHT;
                        return Moves.TURN_RIGHT;}
                        case RIGHT -> {lastMove = Moves.TURN_LEFT;
                        return Moves.TURN_LEFT;}
                        case FORWARD -> {lastMove = Moves.TURN_AROUND;
                        return Moves.TURN_AROUND;}
                        case BACK -> {
                            lastMove = Moves.WALK;
                            return Moves.WALK;
                        }
                    }

                }
                else
                {
                    switch (rot) {
                        case LEFT -> {lastMove = Moves.TURN_LEFT;
                        return Moves.TURN_LEFT;}
                        case RIGHT -> {lastMove = Moves.TURN_RIGHT;
                        return Moves.TURN_RIGHT;}
                        case FORWARD -> {
                            lastMove = Moves.WALK;
                            return Moves.WALK;
                        }
                        case BACK -> {lastMove = Moves.TURN_AROUND;
                        return Moves.TURN_AROUND;}
                    }
                }
            }
        }
        else
        {
            //TODO: make it go to the first explored according to paper
            if(!exploredNeighbours.isEmpty())
            {
                System.out.println("Explored neighbours size: "+exploredNeighbours.size());
                for(int i = 0; i < exploredNeighbours.size(); i++)
                {
                    if(!checkArray(visitedCells, exploredNeighbours.get(i)))
                    {
                        //GET IDEAL NEIGHBOUR SHOULD NET RETURN A VISITED CELL
                        Position bestExplored = getIdealNeighbour(xy);
                        lastBest = bestExplored;
                        int xDiff = bestExplored.getX() - xy[0];
                        int yDiff = bestExplored.getY() - xy[1];
                        Rotations desiredRot = rot;
                        if(yDiff == 0)
                        {
                            if(xDiff > 0)
                            {
                                desiredRot = Rotations.RIGHT;
                            }
                            else
                            {
                                desiredRot = Rotations.LEFT;
                            }
                        }
                        else if(xDiff == 0)
                        {
                            if(yDiff < 0)
                            {
                                desiredRot = Rotations.BACK;
                            }
                            else
                            {
                                desiredRot = Rotations.FORWARD;
                            }
                        }
                        //IF THE CELL IT WANTS TO GO TO IS VISITED
                        //GET SECOND BEST EXPLORED
                        if(isVisited(xy, vr, desiredRot))
                        {
                            System.out.println("IT'S GOING TO A VISITED CELL");
                            System.out.println("OLD Best X:"+bestExplored.getX()+" Y:"+bestExplored.getY());
                            bestExplored = getSecondBest(bestExplored);

                            System.out.println("New Best X:"+bestExplored.getX()+" Y:"+bestExplored.getY());
                        }
                        lastBest = bestExplored;
                        xDiff = bestExplored.getX() - xy[0];
                        yDiff = bestExplored.getY() - xy[1];


                        if(yDiff == 0)
                        {
                            //Move to the right
                            if(xDiff > 0)
                            {
                                //check if it has to rotate first before moving
                                switch(rot)
                                {
                                    case LEFT -> {
                                        lastMove = Moves.TURN_AROUND;
                                        return Moves.TURN_AROUND;
                                    }
                                    case RIGHT -> {
                                        lastMove = Moves.WALK;
                                        return Moves.WALK;
                                    }
                                    case FORWARD -> {
                                        lastMove = Moves.TURN_LEFT;
                                        return Moves.TURN_LEFT;}
                                    case BACK -> {
                                        lastMove = Moves.TURN_RIGHT;
                                        return Moves.TURN_RIGHT;
                                    }
                                }
                            }
                            else
                            {
                                //Move left
                                switch(rot)
                                {
                                    case LEFT -> {
                                        lastMove = Moves.WALK;
                                        return Moves.WALK;
                                    }
                                    case RIGHT -> {
                                        lastMove = Moves.TURN_AROUND;
                                        return Moves.TURN_AROUND;}
                                    case FORWARD -> {
                                        lastMove = Moves.TURN_RIGHT;
                                        return Moves.TURN_RIGHT;}
                                    case BACK -> {
                                        lastMove = Moves.TURN_LEFT;
                                        return Moves.TURN_LEFT;}
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
                                    case LEFT -> {lastMove = Moves.TURN_RIGHT;
                                        return Moves.TURN_RIGHT;}
                                    case RIGHT -> {lastMove = Moves.TURN_LEFT;
                                        return Moves.TURN_LEFT;}
                                    case FORWARD -> {lastMove = Moves.TURN_AROUND;
                                        return Moves.TURN_AROUND;}
                                    case BACK -> {
                                        lastMove = Moves.WALK;
                                        return Moves.WALK;
                                    }
                                }
                            }
                            else
                            {
                                switch(rot)
                                {
                                    case LEFT -> {lastMove = Moves.TURN_LEFT;
                                        return Moves.TURN_LEFT;}
                                    case RIGHT -> {lastMove = Moves.TURN_RIGHT;
                                        return Moves.TURN_RIGHT;}
                                    case FORWARD -> {
                                        lastMove = Moves.WALK;
                                        return Moves.WALK;
                                    }
                                    case BACK -> {lastMove = Moves.TURN_AROUND;
                                        return Moves.TURN_AROUND;}
                                }
                            }
                        }

                    }
                }
            }
        }
        return null;
    }

    //If it crashes into a wall, just make it cheat a move.
    public Moves evadeWall(GameController gm, Intruder intruder)
    {
        //return gm.getDirection(intruder);
        Moves nextMove = gm.getDirection(intruder);
        Moves bestMove = gm.getNextBestMove(intruder);
        System.out.println("Direction move: "+nextMove);
        System.out.println("Best move: "+bestMove);
        return nextMove;
        //return bestMove;
    }

/*
    public boolean stuck(int[] xy)
    {
        if(lastMove == null)
        {
            System.out.println("Last move doesn't exist");
            return false;
        }
        System.out.println("Last move does exist it was :"+lastMove);

        Position p = new Position(lastPosition.getX(), lastPosition.getY());
        if(lastMove.equals(Moves.WALK) && count > 2 && (p.getX() == xy[0] && p.getY() == xy[1]))
        {
            count = 0;
            return true;
        }
        if(visitedCells.size() < 3)
        {
            return false;
        }
        p = visitedCells.get(visitedCells.size()-3);
        if(p.getX() == xy[0] && p.getY() == xy[1])
        {
            return true;
        }

        return false;
    }
*/
    //Whenever the intruder wants to walk it first has to make sure the cell it's going to isn't visited
    //POTENTIALLY ERROR CAUSING, just seems like it idk
    public boolean isVisited(int[] xy, Variables vr, Rotations rot)
    {
        if(rot == Rotations.FORWARD)
        {
            Position pos = new Position(xy[0], xy[1]+1);
            if(checkArray(visitedCells, pos) == true || checkArray(wallCells, pos) == true)
            {
                System.out.println("Visited "+checkArray(visitedCells, pos));
                System.out.println("Walls "+checkArray(wallCells, pos));
                System.out.println("X: "+pos.getX()+" Y: "+pos.getY());
                return true;
            }
        }
        else if(rot == Rotations.BACK)
        {
            Position pos = new Position(xy[0], xy[1]-1);
            if(checkArray(visitedCells, pos) == true || checkArray(wallCells, pos) == true)
            {
                System.out.println("Visited "+checkArray(visitedCells, pos));
                System.out.println("Walls "+checkArray(wallCells, pos));
                System.out.println("X: "+pos.getX()+" Y: "+pos.getY());
                return true;
            }
        }
        else if(rot == Rotations.LEFT)
        {
            Position pos = new Position(xy[0]+1, xy[1]);
            if(checkArray(visitedCells,pos) == true || checkArray(wallCells,pos) == true)
            {
                System.out.println("Visited "+checkArray(visitedCells, pos));
                System.out.println("Walls "+checkArray(wallCells, pos));
                System.out.println("X: "+pos.getX()+" Y: "+pos.getY());
                return true;
            }
        }
        else if(rot == Rotations.RIGHT)
        {
            Position pos = new Position(xy[0]-1, xy[1]);
            if(checkArray(visitedCells,pos) == true || checkArray(wallCells,pos) == true)
            {
                System.out.println("Visited "+checkArray(visitedCells, pos));
                System.out.println("Walls "+checkArray(wallCells, pos));
                System.out.println("X: "+pos.getX()+" Y: "+pos.getY());
                return true;
            }
        }
        return false;
        /*
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

         */
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
                                if (checkArray(exploredCells, new Position(currentX + j, currentY + i))) {
                                    exploredCells.add(new Position(currentX + j, currentY + i));

                                }
                            }else {
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
                                        wallCells.add(new Position(currentX + j, currentY + i));
                                    }

                                } else {
                                    walls.put(currentX + j, new ArrayList<>());
                                    walls.get(currentX + j).add(currentY + i);
                                    wallCells.add(new Position(currentX + j, currentY + i));
                                }
                            }
                        }
                    }
                    case BACK -> {
                        if(lookingAt.contains("V") || lookingAt.contains("V1")){
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
                                        exploredCells.add(new Position(currentX - j, currentY - i));
                                    }

                                } else {
                                    explored.put(currentX - j, new ArrayList<>());
                                    explored.get(currentX - j).add(currentY - i);
                                    exploredCells.add(new Position(currentX - j, currentY - i));
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
                                        wallCells.add(new Position(currentX - j, currentY - i));
                                    }

                                } else {
                                    walls.put(currentX - j, new ArrayList<>());
                                    walls.get(currentX - j).add(currentY - i);
                                    wallCells.add(new Position(currentX - j, currentY - i));
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
                                        exploredCells.add(new Position(currentX - i, currentY + j));
                                    }

                                } else {
                                    explored.put(currentX - i, new ArrayList<>());
                                    explored.get(currentX - i).add(currentY + j);
                                    exploredCells.add(new Position(currentX - i, currentY + j));
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
                                        wallCells.add(new Position(currentX - i, currentY + j));
                                    }

                                } else {
                                    walls.put(currentX - i, new ArrayList<>());
                                    walls.get(currentX - i).add(currentY + j);
                                    wallCells.add(new Position(currentX - i, currentY + j));
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
                                        exploredCells.add(new Position(currentX + i, currentY - j));
                                    }

                                } else {
                                    explored.put(currentX + i, new ArrayList<>());
                                    explored.get(currentX + i).add(currentY - j);
                                    exploredCells.add(new Position(currentX + i, currentY - j));
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
                                        wallCells.add(new Position(currentX + i, currentY - j));
                                    }

                                } else {
                                    walls.put(currentX + i, new ArrayList<>());
                                    walls.get(currentX + i).add(currentY - j);
                                    wallCells.add(new Position(currentX + i, currentY - j));
                                }
                            }
                        }
                    }

                }
            }
        }

    }


    private HashMap<Integer, ArrayList<Integer>> deepClone(HashMap<Integer, ArrayList<Integer>> maptoCopy) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(maptoCopy);
        Type type = new TypeToken<HashMap<Integer, ArrayList<Integer>>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }





    public boolean hasUnexploredNeighbors(int[] xy)
    {
        ArrayList<Position> check = getSurroundings(new Position(xy[0], xy[1]));
        //The cell has to be in either explored, visited or walls
        //else it's unexplored
        int count = 4;
        int previousCount = 0;
        boolean ret = false;
        unexploredNeighbours.clear();
        exploredNeighbours.clear();
        for(int i = 0; i < check.size(); i++)
        {
            if(checkArray(exploredCells, check.get(i)) == true)
            {
                exploredNeighbours.add(check.get(i));
            }
            else if(checkArray(wallCells, check.get(i)) == true || checkArray(visitedCells, check.get(i)) == true)
            {
                count--;
            }
            else
            {
                unexploredNeighbours.add(check.get(i));
            }
        }
        if(!unexploredNeighbours.isEmpty())
        {
            return true;
        }
        return false;
    }

    public ArrayList<Position> getSurroundings(Position pos)
    {

        Position up = new Position(pos.getX(), pos.getY()-1);
        Position down = new Position(pos.getX(), pos.getY()+1);
        Position left = new Position(pos.getX()-1, pos.getY());
        Position right = new Position(pos.getX()+1, pos.getY());
        ArrayList<Position> check = new ArrayList<>();
        check.add(up);
        check.add(down);
        check.add(left);
        check.add(right);
        return check;
    }

    public Position getBestUnexplored()
    {
        //I assume it always tries to go into a wall, because a wall is usually surrounded by walls and X's
        //TODO: FIX THIS^

        int count = 0;
        int bestPositionIndex = 0;
        int second = 0;
        System.out.println("Unexplored neighbours: "+unexploredNeighbours.size());
        for(int i = 0; i < unexploredNeighbours.size(); i++)
        {
            if(checkArray(wallCells, unexploredNeighbours.get(i)) == true)
            {
                System.out.println("We skip this one");
            }
            else
            {
                ArrayList<Position> check = getSurroundings(unexploredNeighbours.get(i));
                for(int k = 0; k < check.size(); k++) {
                    int currentCount = 0;
                    for (int j = 0; j < visitedCells.size(); j++) {
                        if (isSame(unexploredNeighbours.get(i), visitedCells.get(j))) {
                            currentCount++;
                        }
                    }
                    for (int j = 0; j < wallCells.size(); j++) {
                        if (isSame(unexploredNeighbours.get(i), wallCells.get(j))) {
                            currentCount++;
                        }
                    }
                    if (currentCount > count) {
                        count = currentCount;
                        int help = 0;
                        help = bestPositionIndex;
                        bestPositionIndex = i;
                        second = help;
                    }
                }
            }

        }
        Position bestPosition = unexploredNeighbours.get(bestPositionIndex);

        if(lastBest != null)
        {
            if(isSame(bestPosition, lastBest))
            {
                bestPosition = unexploredNeighbours.get(second);
            }
        }
        return bestPosition;
    }

    public boolean isSame(Position first, Position second)
    {
        int x1 = first.getX();
        int y1 = first.getY();

        int x2 = second.getX();
        int y2 = second.getY();

        if(x1 == x2 && y1 == y2)
        {
            return true;
        }
        return false;
    }

    //checks for duplicates
    public boolean checkArray(ArrayList<Position> array, Position pos)
    {
        for(int i = 0; i < array.size(); i++)
        {
            if(isSame(array.get(i), pos))
            {
                return true;
            }
        }
        return false;

    }

    //GET LASTPOSITION
    //CHECK NEIGHBOURS
    //IF CURRENT NEIGHBOUR IS LASTPOSITION OR A WALL OR VISITED
    //GO TO THE NEXT NEIGHBOUR
    //RETURN IDEAL NEIGHBOUR
    public Position getIdealNeighbour(int[] xy)
    {
        ArrayList<Position> neighbours = getSurroundings(new Position(xy[0], xy[1]));
        /*
        for(Position neighbour : neighbours)
        {
            if(!checkArray(visitedCells, neighbour) && !check)
        }

         */
        Position idealNeighbour = null;
        for(Position neighbour : exploredNeighbours)
        {
            if(checkArray(exploredCells, neighbour))
            {
                //Move onto next neighbour if current one is visited or a wall
                if(checkArray(visitedCells, neighbour) == false && checkArray(wallCells, neighbour) == false)
                {
                    idealNeighbour = neighbour;
                    if(!isSame(neighbour, lastPosition))
                    {
                        idealNeighbour = neighbour;
                    }
                }
            }
        }
        return idealNeighbour;
    }

    public Position getSecondBest(Position firstOption)
    {
        Position secondBest = null;
        for(Position neighbour : exploredNeighbours)
        {
            if(checkArray(exploredCells, neighbour))
            {
                //Move onto next neighbour if current one is visited or a wall
                if(checkArray(visitedCells, neighbour) == false && checkArray(wallCells, neighbour) == false)
                {
                    if(!isSame(neighbour, firstOption))
                    {
                        secondBest = neighbour;
                    }
                }
            }
        }
        return secondBest;
    }
}
