package Strategies;

import Config.Variables;
import Entities.Explorer;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import OptimalSearch.TreeNode;
import OptimalSearch.TreeRoot;
import PathMaking.Point;
import Patrolling.CoordinateTransformer;
import Patrolling.Patroller;
import Patrolling.Position;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

import static java.util.Collections.max;
import static java.util.Collections.min;


public class BasicExplo extends Strategy { // no need to touch, basic explo
    private final HashMap<Integer, ArrayList<Integer>> explored;
    private final HashMap<Integer, ArrayList<Integer>> walls;
    private final HashMap<Integer,int[]> objects;
    private final HashMap<Integer,int[]> teleporterGoal;
    private final HashMap<int[],int[]> teleporterAll;
    private final Constraints constraints;
    private final ArrayList<Point> visitedPoints;
    private int[][] agentSeenMap;
    private int[][] lastSeen;
    boolean firstPhase;
    boolean patrolling;
    boolean chasing;
    boolean exploDone;
    private Explorer agent;
    private ArrayList<TreeNode> returnedMoves;
    private CoordinateTransformer coordT = null;
    private int lastUsedTeleporter;
    private boolean TELEPORTED=false;
    private ArrayList<Integer> intruderCoordinates;

    public BasicExplo() {
        this.explored = new HashMap<>();
        this.walls = new HashMap<>();
        this.objects = new HashMap<>();
        this.constraints=new Constraints();
        this.firstPhase=true;
        this.visitedPoints=new ArrayList<>();
        this.exploDone=false;
        this.teleporterGoal=new HashMap<>();
        this.teleporterAll=new HashMap<>();
    }
    @Override
    public void setBooleans(boolean p, boolean c){
        this.patrolling=p;
        this.chasing=c;
    }
    @Override
    public void setAgent(Explorer a){
        this.agent=a;
    }
    @Override
    public Moves decideOnMove(String[][] vision, int[] xy, Rotations rot, Variables vr) {
        if(TELEPORTED){
            teleporterGoal.putIfAbsent(lastUsedTeleporter,xy);
            TELEPORTED=false;
        }
        Moves returner = Moves.STUCK;
        if(!exploDone){
        updateExploration(vision, xy, rot);
        if(!explored(xy))visitedPoints.add(new Point(xy,new ArrayList<>()));
        int eyeRange=vision.length;
        int check = eyeRange-2;
        if(firstPhase) {
            if (!Objects.equals(vision[check][1], " ")) {
                System.out.println("FOUND A WALL");
                firstPhase = false;
            } else return Moves.WALK;
        }
        }
        if(!firstPhase&&!exploDone) {
            TreeRoot root = new TreeRoot(deepClone(explored), deepClone(walls), xy.clone(), rot, 5, constraints,vr,visitedPoints,objects);
            returner = root.getMove();
            if(returner==Moves.STUCK){
                returner = root.tryPathfinding();
                if(returner==Moves.STUCK){
                    returner = root.tryTeleporting();
                    if(returner==Moves.STUCK)exploDone=true;
                }
            }
        }
        if(exploDone && !patrolling) {
            patrolling=true;
            TreeRoot tr = new TreeRoot(deepClone(explored), deepClone(walls), xy.clone(), rot, 5, constraints,vr,visitedPoints,objects);
            String[][] map = makeMap(tr.giveMappings());
            String[][] secondMap=tr.giveMappings();
            int fixY=Integer.parseInt(secondMap[0][1]);
            int fixX=Integer.parseInt(secondMap[0][2]);
            CoordinateTransformer ct=new CoordinateTransformer(fixX, fixY);
            this.coordT=ct;
            agent.setCT(ct);
            //explorationPoints.get(j+fix+fixX).add(((i+fix)*-1)+fixY);
            xy=ct.transform(xy);

            //Attempt at debugging #1, invert rotation if agent is facing forward or backwards

            if(rot.equals(Rotations.FORWARD) || rot.equals(Rotations.BACK))
            {
                rot = rot.turnAround();
            }
             
            this.agent.nowPatrol(xy);
            System.out.println("EXPLO DONE");
            //String[][] agentPrivateMap = makeMap(map);
            lastSeen = makeLastSeenMap(map);
            for(Integer i:objects.keySet()){
                if(teleporterGoal.containsKey(i)){
                    int[] origin = ct.transform(objects.get(i));
                    int[] goal = ct.transform(teleporterGoal.get(i));
                    teleporterAll.put(origin,goal);
                }
            }
        }
        if(patrolling&&exploDone){
            int[] newxy = coordT.transform(xy);
            TreeRoot tr = new TreeRoot(deepClone(explored), deepClone(walls), newxy, rot, 5, constraints,vr,visitedPoints,objects);
            String[][] map = makeMap(tr.giveMappings());
            Patroller patroller = new Patroller(newxy,rot,vr,map,teleporterAll,lastSeen);
            /*
            TreeRoot tr = new TreeRoot(deepClone(explored), deepClone(walls), xy.clone(), rot, 5, constraints,vr,visitedPoints,objects);
            String[][] map = makeMap(tr.giveMappings());
            Patroller patroller = new Patroller(xy,rot,vr,map,teleporterAll,lastSeen);
            */

            returner = patroller.dfs(1);
        }

        if(returner==Moves.USE_TELEPORTER){
            for(Integer i:objects.keySet()){
                int [] position=objects.get(i);
                if(itsNextToMe(position,xy)){
                lastUsedTeleporter=i;
                break;
                }
            }
        }
        return returner;
    }
    @Override
    public void teleported(){
        TELEPORTED=true;
    }
    boolean itsNextToMe(int[]pos,int[]xy){
        if(xy[0]==pos[0]){
            if(pos[1]==xy[1]+1)return true;
            if(pos[1]==xy[1]-1)return true;
        }
        if(xy[1]==pos[1]){
            if(pos[0]==xy[0]+1)return true;
            if(pos[0]==xy[0]-1)return true;
        }
        return false;

    }
    public boolean explored(int[]xy){
        for(Point p : visitedPoints){
            if(Arrays.equals(p.xy(), xy))return true;
        }
        return false;
    }
    public void updateExploration(String[][] vision, int[] xy, Rotations rot) {
        int eyeRange = vision.length;
        int currentX = xy[0];
        int currentY = xy[1];
        System.out.println(objects);
        for (int i = 0; i < eyeRange; i++) { //i= upfront
            for (int j = -1; j < 2; j++) { //j==sideways
                int h = eyeRange - (i + 1);
                int l = j + 1;
                final String lookingAt = vision[h][l];
                switch (rot) {
                    case FORWARD -> {
                        // THIS IS WHERE I  WOULD THINK THE CONDITIONS FOR CHECKING WHETHER WE SHOULD IMMEDIATELY CHASE OR NOT SHOULD GO
                        if (!Objects.equals(lookingAt, "I")) {
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
                        } else {
                            // Remember his location and execute Astar
                            intruderCoordinates.add(currentX);
                            intruderCoordinates.add(currentY);

                        }

                    }
                    case BACK -> {
                        if (!Objects.equals(lookingAt, "I")) {
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
                        } else {
                            // Remember his location and execute AStar
                            intruderCoordinates.add(currentX);
                            intruderCoordinates.add(currentY);

                        }

                    }
                    case LEFT -> {
                        if (!Objects.equals(lookingAt, "I")) {
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
                        } else {
                            // remember the last location of the agent
                            // execute AStar
                            intruderCoordinates.add(currentX);
                            intruderCoordinates.add(currentY);
                        }

                    }
                    case RIGHT -> {
                        if (!Objects.equals(lookingAt, "I")) {
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
                        } else {
                            // remember last location of the agent
                            // execute the AStar algo
                            intruderCoordinates.add(currentX);
                            intruderCoordinates.add(currentY);
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

    public HashMap<Integer, ArrayList<Integer>> createHashMap(String[][] mindMap)
    {
        HashMap<Integer, ArrayList<Integer>> hashMap = new HashMap();
        HashMap<Integer, ArrayList<Integer>> wallsMap = new HashMap();
        //Removes the X's at the edges of the mindMap to create a functional HM
        int x = 0;
        int y = 0;
        for (int i = 2; i < mindMap.length-2;i++)
        {
            x = i-2;
            wallsMap.put(x,new ArrayList<Integer>());
            hashMap.put(x,new ArrayList<Integer>());
            for(int j = 2; j < mindMap[0].length-2;j++)
            {
                y = j-2;
                if(mindMap[i][j].contains("W"))
                {
                    wallsMap.get(x).add(y);
                }
                else if(mindMap[i][j].contains(" "))
                {
                    hashMap.get(x).add(y);
                }
            }
        }
        return hashMap;
    }

    //Creates an identical copy without the borders of the mindMap
    public String[][] makeMap(String[][] mindMap)
    {
        String[][] newMap = new String[mindMap.length-4][mindMap[0].length-4];
        int x = 0;
        int y = 0;

        System.out.println("NEW MAP:");
        for(int i = 2; i <mindMap.length-2;i++)
        {
            x = i-2;
            for(int j = 2; j < mindMap[0].length-2;j++)
            {
                y = j-2;
                newMap[x][y] = mindMap[i][j];
               // System.out.print(newMap[i][j]);
            }
        }
        return newMap;
    }

    /**
     *
     * @param individualMap map which you get from makeMap()
     * @return
     */
    public int[][] makeLastSeenMap(String[][] individualMap)
    {
        lastSeen = new int[individualMap.length][individualMap[0].length];

        for(int i = 0; i < individualMap.length; i++)
        {
            for(int j = 0; j < individualMap[0].length; j++)
            {
                // Since a wall isn't really worth visiting, I just assigned an arbitrary negative number
                if(individualMap[i][j].contains("W"))
                {
                    lastSeen[i][j] = 10;

                }
                else
                {
                    // Make the security guard wish to check out everything
                    lastSeen[i][j] = 1;

                }
            }
        }
        return lastSeen;
    }

    /**
     *  Question: Will this method be called once or reiteratively?
     * @return
     */
    public Moves getPatrolPath(int[] targetPosition, Rotations rotation, int[] agentPosition)
    {
        Moves nextMove = null;
        // HorizontalDifference < 0 : left
        //                      > 0 : right
        // VDiff < 0 : Up
        //       > 0 : Down
        int horizontalDifference  = targetPosition[0] - agentPosition[0];
        int verticalDifference = targetPosition[1] - agentPosition[1];
        switch(rotation){
            case LEFT -> {return Moves.TURN_AROUND;}
            case RIGHT -> {return Moves.WALK;}
            case FORWARD -> {return Moves.TURN_RIGHT;}
            case BACK ->{return Moves.TURN_LEFT;}
        }

        //move to right
        if(horizontalDifference > 0  ){
            if(verticalDifference > 0){//move up
                switch(rotation){
                    case BACK -> {return Moves.TURN_AROUND;}
                    case FORWARD -> {return Moves.WALK;}
                    case LEFT -> {return Moves.TURN_RIGHT;}
                    case RIGHT ->{return Moves.TURN_LEFT;}
                }
            }
            else if(verticalDifference < 0){ //move down
                switch(rotation){
                    case FORWARD -> {return Moves.TURN_AROUND;}
                    case BACK -> {return Moves.WALK;}
                    case RIGHT -> {return Moves.TURN_RIGHT;}
                    case LEFT ->{return Moves.TURN_LEFT;}
                }
            }
            else if(verticalDifference == 0){ //go right
                switch(rotation){
                    case LEFT -> {return Moves.TURN_AROUND;}
                    case RIGHT -> {return Moves.WALK;}
                    case FORWARD -> {return Moves.TURN_RIGHT;}
                    case BACK ->{return Moves.TURN_LEFT;}
                }
            }
        }
        //Move to left
        else if(horizontalDifference < 0 ){
            if(verticalDifference > 0){ //move up
                switch(rotation){
                    case BACK -> {return Moves.TURN_AROUND;}
                    case FORWARD -> {return Moves.WALK;}
                    case LEFT -> {return Moves.TURN_RIGHT;}
                    case RIGHT ->{return Moves.TURN_LEFT;}
                }
            }
            else if(verticalDifference < 0) { //move down
                switch (rotation) {
                    case FORWARD -> {return Moves.TURN_AROUND;}
                    case BACK -> {return Moves.WALK;}
                    case RIGHT -> {return Moves.TURN_RIGHT;}
                    case LEFT -> {return Moves.TURN_LEFT;}
                }
            }
            else if(verticalDifference == 0){ //go left
                switch(rotation){
                    case RIGHT-> {return Moves.TURN_AROUND;}
                    case LEFT -> {return Moves.WALK;}
                    case BACK -> {return Moves.TURN_RIGHT;}
                    case FORWARD ->{return Moves.TURN_LEFT;}
                }
            }
        }
        else if(horizontalDifference == 0){
            if(verticalDifference > 0){ //move down
                switch(rotation){
                    case FORWARD -> {return Moves.TURN_AROUND;}
                    case BACK -> {return Moves.WALK;}
                    case RIGHT -> {return Moves.TURN_RIGHT;}
                    case LEFT ->{return Moves.TURN_LEFT;}
                }
            }
            else if(verticalDifference < 0){ //move up
                switch (rotation) {
                    case FORWARD -> {return Moves.TURN_AROUND;}
                    case BACK -> {return Moves.WALK;}
                    case RIGHT -> {return Moves.TURN_RIGHT;}
                    case LEFT -> {return Moves.TURN_LEFT;}
                }
            }
        }
        return nextMove; //change this return
    }

    /**
     *
     * @param lastSeen
     * @param vr         represents how far the agent can see
     * @param xy         The agent's position on the int[][] map decomposed
     *
     *
     *              Question: since lastSeen is respective to each guard, does that mean this method type should be void
     *                    or return the lastSeen 2D Array with the modified values?
     */
    public void setSeen(int[][] lastSeen, Variables vr, int []xy,  Rotations rotation)
    {
        int range = vr.eyeRange();
        //Agent sees a block to its left and its right with a range of 5 units
        switch(rotation){
            case BACK -> {
                for(int i = xy[0] - 1; i < xy[0] + 1; i++){
                    for(int j = xy[1]; j < xy[1] + range; j++){
                        lastSeen[i][j] = 0;
                    }
                }
            }
            case FORWARD -> {
                for(int i = xy[0] - 1; i < xy[0] + 1; i++){
                    for(int j = xy[1] - range; j < xy[1] ; j++){
                        lastSeen[i][j] = 0;
                    }

                }
            }
            case LEFT -> {
                for(int i = xy[0] - range; i < xy[0]; i++){
                    for(int j = xy[1] - 1; j < xy[1] + 1; j++){
                        lastSeen[i][j] = 0;
                    }
                }
            }
            case RIGHT ->{
                for(int i = xy[0]; i < xy[0] + range; i++){
                    for(int j = xy[1] - 1; j < xy[1] + 1; j++){
                        lastSeen[i][j] = 0;
                    }
                }
            }
        }


    }


    public int[] getMaxSquare(int[][] lastSeen, int[]xy)
    {
        int maxValue = 0;
        int x = 0;
        int y = 0;
        for(int i = 0; i < lastSeen.length; i++)
        {
            for(int j = 0; j < lastSeen[0].length; j++)
            {
                if(lastSeen[i][j] > maxValue)
                {
                    maxValue = lastSeen[i][j];
                    x = i;
                    xy[0] = x;
                    y = j;
                    xy[1] = y;
                }
            }
        }
        return xy;
    }




    //--------------- Start of DFS ------------------

    public int depthFirstSearch(int depth)
    {
        int maxValue = 0;
        if(depth == 0)
        {
            return maxValue;
        }
        //If depth is not 0 we create more children and explore them
        else
        {
            depthFirstSearch(depth - 1);
        }
        return maxValue;
    }


}

