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
        return false;
        /*
        Position pos = new Position(xy[0], xy[1]);
        ArrayList<Position> neighbouringCells = getWalkableNeighbors(pos);
        for(int i = 0; i < neighbouringCells.size(); i++)
        {
            for(int j = i; j < neighbouringCells.size(); j++)
            {
                if(!pathExists(neighbouringCells.get(i), neighbouringCells.get(j)))
                {
                    return true;
                }
            }
        }
        return false;

         */

    }
    //TODO: verify if this shit makes sense top to bottom
    //gonna change it now to arraylists
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

    private boolean pathExists(Position start, Position target)
    {
        BreadthFirstSearch bfs = new BreadthFirstSearch();
        return bfs.pathExists(start, target);
    }

    class BreadthFirstSearch {
        private Queue<Position> queue;
        private ArrayList<Position> visited;
        private Position start;

        public BreadthFirstSearch()
        {
            this.visited = new ArrayList<>();
            this.queue = new LinkedList<>();
        }

        boolean pathExists(Position start, Position target)
        {
            this.start = start;
            this.queue = new LinkedList<>();
            this.visited = new ArrayList<>();
            queue.add(start);


            while(!queue.isEmpty())
            {
                Position currentPos = queue.poll();
                if(currentPos.getX() == target.getX() && currentPos.getY() == target.getY())
                {
                    return true;
                }
                visited.add(currentPos);
                //small change, taking it out of for loop
                ArrayList<Position> neighbours = getWalkableNeighbors(currentPos);

                for(Position neighbour : neighbours)
                {
                    if(neighbour.getX() == target.getX() && neighbour.getY() == target.getY())
                    {
                        return true;
                    }
                    addUnvisitedPosition(neighbour);
                }
            }
            return false;
        }



        private void addUnvisitedPosition(Position pos)
        {
            if(!isVisited(pos) && !(pos.getX() == start.getX() && pos.getY() == start.getY() ))
            {
                queue.add(pos);
                visited.add(pos);
            }
        }
        boolean isVisited(Position pos)
        {
            return visited.contains(pos);
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

    //TODO: REAL SHIT
    private ArrayList<Position> exploredCells;
    private ArrayList<Position> wallCells;
    private Position nextBest;
    private ArrayList<Position> unexploredNeighbours;
    private ArrayList<Position> exploredNeighbours;

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
        //Marking Step:
        if(isBlockingPath(xy))
        {
            //DUMB SHIT
            Position pos = new Position(xy[0], xy[1]);
            if(!checkArray(exploredCells, pos))
            {
                exploredCells.add(pos);
            }
        }
        else
        {
            Position currentPosition = new Position(xy[0], xy[1]);
            if(visitedCells.isEmpty())
            {
                visitedCells.add(currentPosition);
            }
            else
            {
                if(!checkArray(visitedCells, currentPosition))
                {
                    visitedCells.add(currentPosition);
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

        //Navigation step
        if(hasUnexploredNeighbors(xy))
        {
            //TODO: Update ^ method, make it check it's neighbours with a counter
            //Make it look in it's four directions, get the bestUnexplored, make it walk to it
            //actually then it wouldn't make sense to use a counter, it would still need the bestUnexplored
            //ACTUALLY NO, I KNOW HOW TO DO THIS
            //THIS IS WHERE THE EXPLORED HASHMAP COMES IN
            //ALRIGHT BABY, WE NEED A NEW METHOD WITH THE ROTATION INCLUDED
            //CHECK IT'S FOUR NEIGHBOURS, WE CAN USE VISITEDCELLS AND EXPLOREDCELLS
            //HELL I GOT A PLAN
            //CREATE AN EXPLOREDCELLS ARRAYLIST
            //FUCK THE HASHMAPS
            //THEY'RE DUMB AS SHIT;
            System.out.println("LET'S DO THIS BABY");

            Position bestUnexplored = getBestUnexplored();
            int xDiff = bestUnexplored.getX() - xy[0];
            int yDiff = bestUnexplored.getY() - xy[1];
            lastPosition = new Position(xy[0], xy[1]);
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
                            if(stuck(xy))
                            {
                                System.out.println("We're stuck lads");
                                lastMove = evadeWall(gm, intruder);
                                return lastMove;
                            }
                            if(!isVisited(xy,vr,rot)) {
                                System.out.println("Cringe");
                                lastMove = Moves.WALK;
                                return Moves.WALK;
                            }
                            else
                            {
                                return Moves.STUCK;
                            }
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
                            if(stuck(xy))
                            {
                                System.out.println("We're stuck lads");
                                lastMove = evadeWall(gm, intruder);
                                return lastMove;
                            }
                            if(!isVisited(xy,vr,rot)) {
                                System.out.println("Cringe");
                                lastMove = Moves.WALK;
                                return Moves.WALK;
                            }
                            else
                            {
                                return Moves.STUCK;
                            }
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
                            if(stuck(xy))
                            {
                                System.out.println("We're stuck lads");
                                lastMove = evadeWall(gm, intruder);
                                return lastMove;
                            }
                            if(!isVisited(xy,vr,rot)) {

                                System.out.println("Cringe");
                                lastMove = Moves.WALK;
                                return Moves.WALK;
                            }
                            else
                            {
                                lastMove = Moves.WALK;
                                return Moves.WALK;
                            }
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
                            if(stuck(xy))
                            {
                                System.out.println("We're stuck lads");
                                lastMove = evadeWall(gm, intruder);
                                return lastMove;
                            }
                            if(!isVisited(xy,vr,rot)) {
                                System.out.println("Cringe");
                                lastMove = Moves.WALK;
                                return Moves.WALK;
                            }
                            else
                            {
                                return Moves.STUCK;
                            }
                        }
                        case BACK -> {lastMove = Moves.TURN_AROUND;
                            return Moves.TURN_AROUND;}
                    }
                }
            }
        }
        else
        {
            System.out.println("IT EXPLORED ALL ITS NEIGHBOURS");
            if(!exploredNeighbours.isEmpty())
            {
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
        }
        return null;
    }

    //If it crashes into a wall, just make it cheat a move.
    public Moves evadeWall(GameController gm, Intruder intruder)
    {
        //TODO: getDirection seems to only return walk
        //return gm.getDirection(intruder);
        Moves nextMove = gm.getDirection(intruder);
        Moves bestMove = gm.getNextBestMove(intruder);
        System.out.println("Direction move: "+nextMove);
        System.out.println("Best move: "+bestMove);
        //return nextMove;
        return bestMove;
    }


    public boolean stuck(int[] xy)
    {

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

    //Whenever the intruder wants to walk it first has to make sure the cell it's going to isn't visited
    //POTENTIALLY ERROR CAUSING, just seems like it idk
    public boolean isVisited(int[] xy, Variables vr, Rotations rot)
    {
        if(rot == Rotations.FORWARD)
        {
            return checkArray(visitedCells, new Position(xy[0], xy[1]+1));
        }
        else if(rot == Rotations.BACK)
        {
            return checkArray(visitedCells, new Position(xy[0], xy[1]-1));
        }
        else if(rot == Rotations.LEFT)
        {
            return checkArray(visitedCells, new Position(xy[0]+1, xy[1]));
        }
        else if(rot == Rotations.RIGHT)
        {
            return checkArray(visitedCells, new Position(xy[0]-1, xy[1]-1));
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
                                if (explored.containsKey(currentX + j)) {
                                    if (!explored.get(currentX + j).contains(currentY + i)) {
                                        explored.get(currentX + j).add(currentY + i);
                                        exploredCells.add(new Position(currentX + j, currentY + i));
                                    }

                                } else {
                                    explored.put(currentX + j, new ArrayList<>());
                                    explored.get(currentX + j).add(currentY + i);
                                    exploredCells.add(new Position(currentX + j, currentY + i));
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
        //HOW TO APPROACH THIS?
        //to check if it has unexploredNeighbors it would have to access a hashmap that DOESNT EXIST
        //so we'll need to have it look around itself
        //WE NEED A SEEN ARRAYLIST BABY --> exploredCells
        //I'M ADDING AN ADD TO UPDATEEXPLORATION
        //I AM JOSE MOURINHO
        //DUDE FUCK IT, LET'S ADD A WALL ARRAYLIST
        //WHY THE FUCK DID I EVEN TRY HASHMAPS HUH?
        //wait would this work?
        //Since updateExploration now adds the walls and cells it sees
        //it could also "know" what it has not seen, so if one of those things has not been seen yet, it
        //would then make the next move a rotation, to check it out?
        // fuck it refocus Kaiwei
        // du muss lo kucken op et unexplored Neighbours huet
        // dat bedeit du kuckst elo op eppes net existeiert, an wann et net existeiert
        // Ah mee, kannst du net einfach kucken op die positioun, uewen, lenks, riets, ennen net deel vun den drei arraylists sinn?
        //Dat giff vlait klappen
        //TODO: verify^
        boolean ret = false;
        ArrayList<Position> check = getSurroundings(new Position(xy[0], xy[1]));
        //The cell has to be in either explored, visited or walls
        //else it's unexplored
        int count = 0;
        int previousCount = 0;
        //clear unexploredNeighbours first
        //TODO: problem?^
        unexploredNeighbours.clear();
        exploredNeighbours.clear();
        for(int i = 0; i < check.size(); i++)
        {
            //PRAY THAT COUNT NEVER GOES HIGHER THAN 4
            //ELSE THAT MEANS THERE'S OVERLAP BETWEEN THE ARRAYS
            for(int j = 0; j < exploredCells.size(); j++)
            {
                if(isSame(check.get(i), exploredCells.get(j)))
                {
                    count++;
                    exploredNeighbours.add(check.get(i));
                }
            }
            for(int j = 0 ; j < visitedCells.size(); j++)
            {
                if(isSame(check.get(i), visitedCells.get(j)))
                {
                    count++;
                }
            }
            for(int j = 0; j < wallCells.size(); j++)
            {
                if(isSame(check.get(i), wallCells.get(j)))
                {
                    count++;
                }
            }
            //if previousCount didn't increase, that means it didn't exist in any array
            // --> add this specific position to unexploredneigh
            if(previousCount != count)
            {
                unexploredNeighbours.add(check.get(i));
            }
            previousCount = count;
        }
        if(count == 4)
        {
            return false;
        }
        else
        {
            return true;
        }
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
        //TODO: NULL ERROR POSSIBLE

        int count = 0;
        int bestPositionIndex = 0;
        for(int i = 0; i < unexploredNeighbours.size(); i++)
        {
            ArrayList<Position> check = getSurroundings(unexploredNeighbours.get(i));
            for(int k = 0; k < check.size(); k++)
            {
                int currentCount = 0;
                for(int j = 0 ; j < visitedCells.size(); j++)
                {
                    if(isSame(unexploredNeighbours.get(i), visitedCells.get(j)))
                    {
                        currentCount++;
                    }
                }
                for(int j = 0; j < wallCells.size(); j++)
                {
                    if(isSame(unexploredNeighbours.get(i), wallCells.get(j)))
                    {
                        currentCount++;
                    }
                }
                if(currentCount > count)
                {
                    count = currentCount;
                    bestPositionIndex = i;
                }
            }

        }
        Position bestPosition = unexploredNeighbours.get(bestPositionIndex);
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
}

