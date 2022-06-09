package Strategies;
import Config.Config;
import Config.Variables;
import Entities.Entity;
import Entities.Explorer;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import OptimalSearch.TreeNode;
import OptimalSearch.TreeRoot;
import PathMaking.Point;
import Patrolling.CoordinateTransformer;
import Patrolling.Patroller;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

import static java.util.Collections.max;
import static java.util.Collections.min;


public class BasicExplo extends Strategy { // no need to touch, basic explo
    private final HashMap<Integer, ArrayList<Integer>> explored;
    private final HashMap<Integer, ArrayList<Integer>> walls;
    private final HashMap<Integer, int[]> objects;
    private final HashMap<Integer, int[]> teleporterGoal;
    private final HashMap<int[], int[]> teleporterAll;
    private final Constraints constraints;
    private final ArrayList<Point> visitedPoints;
    boolean firstPhase;
    boolean patrolling;
    boolean chasing;
    boolean exploDone;
    boolean setup;
    private int[][] lastSeen;
    private Entity agent;
    private CoordinateTransformer coordT = null;
    private int lastUsedTeleporter;
    private boolean TELEPORTED = false;
    private boolean PATROLLING_SETUP_DEBUG;
    private ArrayList<Integer> intruderCoordinates;
    private String[][] patrollingMap;


    public BasicExplo() {
        this.explored = new HashMap<>();
        this.walls = new HashMap<>();
        this.objects = new HashMap<>();
        this.constraints = new Constraints();
        this.firstPhase = true;
        this.visitedPoints = new ArrayList<>();
        this.exploDone = false;
        this.setup=false;
        this.teleporterGoal = new HashMap<>();
        this.teleporterAll = new HashMap<>();

        Config c = new Config();
        PATROLLING_SETUP_DEBUG=c.PATROLLING_SETUP_DEBUG;
    }

    @Override
    public void setBooleans(boolean p, boolean c) {
        this.patrolling = p;
        this.chasing = c;
    }

    @Override
    public void setAgent(Entity a) {
        this.agent = a;
    }

    @Override
    public Moves decideOnMove(String[][] vision, int[] xy, Rotations rot, Variables vr) {
        if (TELEPORTED) {
            teleporterGoal.putIfAbsent(lastUsedTeleporter, xy);
            TELEPORTED = false;
        }
        Moves returner = Moves.STUCK;
        /*
        if(chasing)
        {
            int[] newxy = coordT.transform(xy);
            TreeRoot tr = new TreeRoot(deepClone(explored), deepClone(walls), newxy, rot, 5, constraints,vr,visitedPoints,objects);
            String[][] map = makeMap(tr.giveMappings());
            AStarChase astar = new AStarChase(map, newxy, intruderPosition);
            returner = astar.getMove(newxy, astar.getNextMoveCoordinate(), rot);
            checkVision(map, newxy, rot, vr);

            //TODO: want to avoid getting into the other statements, so I added a return here
            return returner;
        }
        */

        if (!exploDone) {
            /*
            int[] newxy = coordT.transform(xy);
            TreeRoot tr = new TreeRoot(deepClone(explored), deepClone(walls), newxy, rot, 5, constraints,vr,visitedPoints,objects);
            String[][] map = makeMap(tr.giveMappings());
            checkVision(map, xy, rot, vr);

             */

            updateExploration(vision, xy, rot);
            if (!explored(xy)) visitedPoints.add(new Point(xy, new ArrayList<>()));
            int eyeRange = vision.length;
            int check = eyeRange - 2;
            if (firstPhase) {
                if (!Objects.equals(vision[check][1], " ")) {
                    System.out.println("FOUND A WALL");
                    firstPhase = false;
                } else return Moves.WALK;
            }
        }
        if (!firstPhase && !exploDone) {
            TreeRoot root = new TreeRoot(deepClone(explored), deepClone(walls), xy.clone(), rot, 5, constraints, vr, visitedPoints, objects);
            returner = root.getMove();
            if (returner == Moves.STUCK) {
                returner = root.tryPathfinding();
                if (returner == Moves.STUCK) {
                    returner = root.tryTeleporting();
                    if (returner == Moves.STUCK) exploDone = true;
                }
            }
        }
        if (exploDone &&patrolling && !setup) {
            TreeRoot tr = new TreeRoot(deepClone(explored), deepClone(walls), xy.clone(), rot, 5, constraints, vr, visitedPoints, objects);
            // CT SETUP
            String[][] mindMap=tr.giveMappings();
            String[][] map = makeMap(mindMap);
            this.patrollingMap=map;
            int fixY = Integer.parseInt(mindMap[0][1]);
            int fixX = Integer.parseInt(mindMap[0][2]);
            CoordinateTransformer ct = new CoordinateTransformer(fixX, fixY);
            this.coordT = ct;
            agent.setCT(ct);
            int[] original = xy.clone();
            xy = ct.transform(xy);
            //if (rot.equals(Rotations.FORWARD) || rot.equals(Rotations.BACK)) {
            //    rot = rot.turnAround();
           // }
            // CT SETUP DONE

                // agent switch
            this.agent.nowPatrol(xy);

            lastSeen = makeLastSeenMap(map);
            for (Integer i : objects.keySet()) {
                if (teleporterGoal.containsKey(i)) {
                    int[] origin = ct.transform(objects.get(i));
                    int[] goal = ct.transform(teleporterGoal.get(i));
                    teleporterAll.put(origin, goal);
                }
            }
            setup=true;
            // SETTING UP PATROLLING FINISHED
            if(PATROLLING_SETUP_DEBUG){
                GameController printer=new GameController();
               // String save = patrollingMap[xy[0]][xy[1]];
              //  patrollingMap[xy[0]][xy[1]]="O";
                System.out.println("---------------------------------------");
                System.out.println("DEBUG OF PATROLLING SETUP");
                System.out.println("MY CURRENT POSITION IS X - "+xy[0]+" Y - "+xy[1]+" |");
                System.out.println("MY OLD POSITION IS X - "+original[0]+" Y - "+original[1]+" |");
                System.out.println("MY CURRENT ROTATION IS "+rot);
                printer.printArray(patrollingMap);
                System.out.println("---------------------------------------");
              //  patrollingMap[xy[0]][xy[1]]=save;
                System.exit(2);
            }
        }

        if(setup){

            Patroller patroller = new Patroller(xy,rot,vr,patrollingMap,teleporterAll,lastSeen);
            updateLastSeen(vision, rot, xy);
            returner = patroller.dfs(1);
            // NEVER CHANGES DIRECTION: ALWAYS FACING BACK

        }

        if (returner == Moves.USE_TELEPORTER) {
            for (Integer i : objects.keySet()) {
                int[] position = objects.get(i);
                if (itsNextToMe(position, xy)) {
                    lastUsedTeleporter = i;
                    break;
                }
            }
        }
        return returner;
    }
    private void updateLastSeen(String[][] vision,Rotations rot,int[]xy){
        int eyeRange = vision.length;
        int x = xy[0];
        int y = xy[1];
        for (int i = 0; i < eyeRange; i++) { //i= upfront
            for (int j = -1; j < 2; j++) { //j==sideways
                int h = eyeRange - (i + 1);
                int l = j + 1;
                final String lookingAt = vision[h][l];
                switch (rot) {
                    case FORWARD -> { //walls.get(currentX + j).add(currentY + i);
                        if(!lookingAt.contains("W") && !lookingAt.contains("X")){
                        lastSeen[x+i][y+j]=0;
                        }

                    }
                    case BACK -> {
                        if(!lookingAt.contains("W") && !lookingAt.contains("X")){
                            lastSeen[x-i][y+j]=0;
                        }

                    }
                    case LEFT -> {
                        if(!lookingAt.contains("W") && !lookingAt.contains("X")){
                            lastSeen[x+j][y-i]=0;
                        }

                    }
                    case RIGHT -> {
                        if(!lookingAt.contains("W") && !lookingAt.contains("X")){
                            lastSeen[x+j][y+i]=0;
                        }

                    }


                }
            }
        }
    }
    @Override
    public void teleported() {
        TELEPORTED = true;
    }

    boolean itsNextToMe(int[] pos, int[] xy) {
        if (xy[0] == pos[0]) {
            if (pos[1] == xy[1] + 1) return true;
            if (pos[1] == xy[1] - 1) return true;
        }
        if (xy[1] == pos[1]) {
            if (pos[0] == xy[0] + 1) return true;
            if (pos[0] == xy[0] - 1) return true;
        }
        return false;

    }

    public boolean explored(int[] xy) {
        for (Point p : visitedPoints) {
            if (Arrays.equals(p.xy(), xy)) return true;
        }
        return false;
    }

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
                        // THIS IS WHERE I  WOULD THINK THE CONDITIONS FOR CHECKING WHETHER WE SHOULD IMMEDIATELY CHASE OR NOT SHOULD GO

                        if (!Objects.equals(lookingAt, "I")) {
                            if (lookingAt.contains("E")) {
                                if (i != 0) {
                                    constraints.setMAX_Y(currentY + 1);
                                }
                            }
                            if (!Objects.equals(lookingAt, "X")) {
                                if (!Objects.equals(lookingAt, "W") && !lookingAt.contains("T")) {
                                    if (explored.containsKey(currentX + j)) {
                                        if (!explored.get(currentX + j).contains(currentY + i)) {
                                            explored.get(currentX + j).add(currentY + i);
                                        }

                                    } else {
                                        explored.put(currentX + j, new ArrayList<>());
                                        explored.get(currentX + j).add(currentY + i);
                                    }
                                } else {
                                    if (lookingAt.contains("T")) {
                                        String id = lookingAt.replace("T", "");
                                        int ide = Integer.valueOf(id);
                                        if (!objects.containsKey(ide)) {
                                            int[] pos_of_It = {currentX + j, currentY + i};
                                            objects.put(ide, pos_of_It);
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
                        // ELSE SAVES LOCATION OF INTRUDER
                        else {
                            // Remember his location and execute Astar
                            intruderCoordinates.add(currentX);
                            intruderCoordinates.add(currentY);

                        }

                    }
                    case BACK -> {
                        if (!Objects.equals(lookingAt, "I")) {
                            if (lookingAt.contains("E")) {
                                if (i != 0) {
                                    constraints.setMIN_Y(currentY - 1);
                                }
                            }
                            if (!Objects.equals(lookingAt, "X")) {
                                if (!Objects.equals(lookingAt, "W") && !lookingAt.contains("T")) {
                                    if (explored.containsKey(currentX - j)) {
                                        if (!explored.get(currentX - j).contains(currentY - i)) {
                                            explored.get(currentX - j).add(currentY - i);
                                        }

                                    } else {
                                        explored.put(currentX - j, new ArrayList<>());
                                        explored.get(currentX - j).add(currentY - i);
                                    }
                                } else {
                                    if (lookingAt.contains("T")) {
                                        String id = lookingAt.replace("T", "");
                                        int ide = Integer.valueOf(id);
                                        if (!objects.containsKey(ide)) {
                                            int[] pos_of_It = {currentX - j, currentY - i};
                                            objects.put(ide, pos_of_It);
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
                            if (lookingAt.contains("E")) {
                                if (i != 0) {
                                    constraints.setMIN_X(currentX - 1);
                                }
                            }
                            if (!Objects.equals(lookingAt, "X")) {
                                if (!Objects.equals(lookingAt, "W") && !lookingAt.contains("T")) {
                                    if (explored.containsKey(currentX - i)) {
                                        if (!explored.get(currentX - i).contains(currentY + j)) {
                                            explored.get(currentX - i).add(currentY + j);
                                        }

                                    } else {
                                        explored.put(currentX - i, new ArrayList<>());
                                        explored.get(currentX - i).add(currentY + j);
                                    }
                                } else {
                                    if (lookingAt.contains("T")) {
                                        String id = lookingAt.replace("T", "");
                                        int ide = Integer.valueOf(id);
                                        if (!objects.containsKey(ide)) {
                                            int[] pos_of_It = {currentX - i, currentY + j};
                                            objects.put(ide, pos_of_It);
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
                                if (!Objects.equals(lookingAt, "W") && !lookingAt.contains("T")) {
                                    if (explored.containsKey(currentX + i)) {
                                        if (!explored.get(currentX + i).contains(currentY - j)) {
                                            explored.get(currentX + i).add(currentY - j);
                                        }

                                    } else {
                                        explored.put(currentX + i, new ArrayList<>());
                                        explored.get(currentX + i).add(currentY - j);
                                    }
                                } else {
                                    if (lookingAt.contains("T")) {
                                        String id = lookingAt.replace("T", "");
                                        int ide = Integer.valueOf(id);
                                        if (!objects.containsKey(ide)) {
                                            int[] pos_of_It = {currentX + i, currentY - j};
                                            objects.put(ide, pos_of_It);
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

    //Checks vision for if there is an intruder in sight
    public void checkVision(String[][] map, int[] xy, Rotations rot, Variables vr) {
        int[] intruderPosition = new int[2];
        int range = vr.eyeRange();
        switch (rot) {
            case BACK -> {
                for (int i = xy[1] - 1; i < xy[1] + 1; i++) {
                    for (int j = xy[0]; j < xy[0] + range; j++) {
                        if (map[i][j].contains("I")) {
                            chasing = true;
                            intruderPosition[0] = i;
                            intruderPosition[1] = j;
                        }
                    }
                }
            }
            case FORWARD -> {
                for (int i = xy[1] - 1; i < xy[1] + 1; i++) {
                    for (int j = xy[0] - range; j < xy[0]; j++) {
                        chasing = true;
                        intruderPosition[0] = i;
                        intruderPosition[1] = j;
                    }
                }
            }
            case LEFT -> {
                for (int i = xy[1] - range; i < xy[1]; i++) {
                    for (int j = xy[0] - 1; j < xy[0] + 1; j++) {
                        chasing = true;
                        intruderPosition[0] = i;
                        intruderPosition[1] = j;
                    }
                }
            }
            case RIGHT -> {
                for (int i = xy[1]; i < xy[1] + range; i++) {
                    for (int j = xy[0] - 1; j < xy[0] + 1; j++) {
                        chasing = true;
                        intruderPosition[0] = i;
                        intruderPosition[1] = j;
                    }
                }
            }
        }
    }

    @Override
    public void printMappings() {
        Set<Integer> keySet = explored.keySet();
        Integer[] exploredX = keySet.toArray(new Integer[0]);
        int lowestXExplored = Integer.MAX_VALUE;
        int highestXExplored = Integer.MIN_VALUE;
        for (int val : exploredX) {
            if (val > highestXExplored) highestXExplored = val;
            if (val < lowestXExplored) lowestXExplored = val;
        }
        Set<Integer> wallkeySet = walls.keySet();
        Integer[] wallX = wallkeySet.toArray(new Integer[0]);
        int highestXWalls = Integer.MIN_VALUE;
        int lowestXWalls = Integer.MAX_VALUE;
        for (int val : wallX) {
            if (val < lowestXWalls) lowestXWalls = val;
            if (val > highestXWalls) highestXWalls = val;
        }
        int lowestYExplored = Integer.MAX_VALUE;
        int highestYExplored = Integer.MIN_VALUE;
        for (Integer x : exploredX) {
            int lowest = min(explored.get(x));
            int highest = max(explored.get(x));
            if (lowest < lowestYExplored) lowestYExplored = lowest;
            if (highest > highestYExplored) highestYExplored = highest;
        }
        int lowestYWalls = Integer.MAX_VALUE;
        int highestYWalls = Integer.MIN_VALUE;
        for (Integer x : wallX) {
            int lowest = min(walls.get(x));
            int highest = max(walls.get(x));
            if (lowest < lowestYWalls) lowestYWalls = lowest;
            if (highest > highestYWalls) highestYWalls = highest;
        }
        int lowestXTotal = Math.min(lowestXExplored, lowestXWalls);
        int lowestYTotal = Math.min(lowestYWalls, lowestYExplored);
        int highestXTotal = Math.max(highestXExplored, highestXWalls);
        int highestYTotal = Math.max(highestYWalls, highestYExplored);
        int spanX = highestXTotal - lowestXTotal;
        int spanY = highestYTotal - lowestYTotal;
        String[][] mindMap = new String[spanY + 5][spanX + 5];
        for (int i : exploredX) {
            ArrayList<Integer> array = explored.get(i);
            for (Integer integer : array) {
                mindMap[((integer - highestYTotal) * -1) + 2][i - lowestXTotal + 2] = " ";
            }
        }
        for (int i : wallX) {
            ArrayList<Integer> array = walls.get(i);
            for (Integer integer : array) {
                mindMap[((integer - highestYTotal) * -1) + 2][i - lowestXTotal + 2] = "W";
            }
        }
        GameController printer = new GameController();
        for (int i = 0; i <= spanY + 4; i++) {
            for (int j = 0; j <= spanX + 4; j++) {
                boolean connected = false;
                if (mindMap[i][j] == null) {
                    if (i > 0) {
                        if (Objects.equals(mindMap[i - 1][j], " ")) connected = true;
                    }
                    if (i < spanY) {
                        if (Objects.equals(mindMap[i + 1][j], " ")) connected = true;
                    }
                    if (j > 0) {
                        if (Objects.equals(mindMap[i][j - 1], " ")) connected = true;
                    }
                    if (j < spanX) {
                        if (Objects.equals(mindMap[i][j + 1], " ")) connected = true;
                    }
                    if (connected) {
                        mindMap[i][j] = "?";
                    } else mindMap[i][j] = "X";
                }

            }
        }
        mindMap[0][0] = "-3";
        printer.printArray(mindMap);
        //FIRST ONE IN MATRIX IS Y
        // SECOND ONE IN MATRIX IS X
        // GOTTA MOVE X's + LOWEST X TO REACH 0 SAME FOR Y


    }

    public HashMap<Integer, ArrayList<Integer>> createHashMap(String[][] mindMap) {
        HashMap<Integer, ArrayList<Integer>> hashMap = new HashMap();
        HashMap<Integer, ArrayList<Integer>> wallsMap = new HashMap();
        //Removes the X's at the edges of the mindMap to create a functional HM
        int x = 0;
        int y = 0;
        for (int i = 2; i < mindMap.length - 2; i++) {
            x = i - 2;
            wallsMap.put(x, new ArrayList<Integer>());
            hashMap.put(x, new ArrayList<Integer>());
            for (int j = 2; j < mindMap[0].length - 2; j++) {
                y = j - 2;
                if (mindMap[i][j].contains("W")) {
                    wallsMap.get(x).add(y);
                } else if (mindMap[i][j].contains(" ")) {
                    hashMap.get(x).add(y);
                }
            }
        }
        return hashMap;
    }

    //Creates an identical copy without the borders of the mindMap
    public String[][] makeMap(String[][] mindMap) {
        String[][] newMap = new String[mindMap.length - 4][mindMap[0].length - 4];
        int x = 0;
        int y = 0;

        System.out.println("NEW MAP:");
        for (int i = 2; i < mindMap.length - 2; i++) {
            x = i - 2;
            for (int j = 2; j < mindMap[0].length - 2; j++) {
                y = j - 2;
                newMap[x][y] = mindMap[i][j];
                // System.out.print(newMap[i][j]);
            }
        }
        return newMap;
    }

    /**
     * @param individualMap map which you get from makeMap()
     * @return
     */
    public int[][] makeLastSeenMap(String[][] individualMap) {
        lastSeen = new int[individualMap.length][individualMap[0].length];

        for (int i = 0; i < individualMap.length; i++) {
            for (int j = 0; j < individualMap[0].length; j++) {
                // Since a wall isn't really worth visiting, I just assigned an arbitrary negative number
                if (individualMap[i][j].contains("W")||individualMap[i][j].contains("X")) {
                    lastSeen[i][j] = -1;

                } else {
                    // Make the security guard wish to check out everything
                    lastSeen[i][j] = 1;

                }
            }
        }
        return lastSeen;
    }










}

