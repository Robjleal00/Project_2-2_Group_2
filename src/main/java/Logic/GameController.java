package Logic;

import Config.Config;
import Config.Variables;
import Entities.Entity;
import Entities.Intruder;
import Enums.EntityType;
import Enums.GameMode;
import Enums.Moves;
import Enums.Rotations;
import ObjectsOnMap.Goal;
import ObjectsOnMap.ObjectOnMap;
import ObjectsOnMap.Teleporter;
import javafx.application.Platform;
import org.openjfx.UI.MainApp;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameController { // self explanatory
    private final int MAX_TURNS = 1000;
    public boolean PRINTVISION;
    public boolean GUI;
    private String[][] map;
    private int[][] pheromonesMap;
    private int mapLength;
    private int mapHeight;
    private boolean isRunning;
    private int eyeRange;
    private int pheromonesDuration;
    private ArrayList<Entity> entities;
    private ArrayList<ObjectOnMap> objects;
    private HashMap<Entity, int[]> entityLocations;
    private HashMap<ObjectOnMap, int[]> objectsLocations;
    private HashMap<Entity, Rotations> entityRotationsHashMap;
    private HashMap<Entity, Integer> moveMap;
    private Map<Entity, Moves> queuedMoves;
    private HashMap<Entity, Pose> entityInitialPoses;
    private ArrayList<Integer> allUnseenTiles;
    private boolean PRINTMAPPINGS;
    private boolean DEBUG_EPXLO;
    private int maxExploNum;
    private int walkSpeed;
    private MainApp graphicsUpdater;
    private Rotations globalRotation;
    private Rotations intruderGlobalRotation;
    private GameMode gameMode = GameMode.EXPLORATION;

    /*
    Use this to construct with graphics.
     */
    public GameController(int height, int length, MainApp graphics) {
        moveMap = new HashMap<>();
        allUnseenTiles = new ArrayList<>();
        this.mapLength = length;
        this.mapHeight = height;
        this.map = makeMap(height, length);
        this.pheromonesMap = new int[length][height];
        makeBorders(length, height);
        isRunning = true;
        entities = new ArrayList<>();
        objects = new ArrayList<>();
        entityLocations = new HashMap<>();
        objectsLocations = new HashMap<>();
        entityRotationsHashMap = new HashMap<>();
        Config con = new Config();
        PRINTMAPPINGS = con.PRINTMIND;
        PRINTVISION = con.PRINTVISION;
        this.GUI = true;
        DEBUG_EPXLO = con.DEBUG_EXPLO;
        this.maxExploNum = allUnseenTiles.size();
        this.graphicsUpdater = graphics;
        entityInitialPoses = new HashMap<>();
    }

    /*
    Use this to construct without graphics
     */
    public GameController(int height, int length) {
        moveMap = new HashMap<>();
        allUnseenTiles = new ArrayList<>();
        this.mapLength = length;
        this.mapHeight = height;
        this.map = makeMap(height, length);
        this.pheromonesMap = new int[length][height];
        makeBorders(length, height);
        isRunning = true;
        entities = new ArrayList<>();
        objects = new ArrayList<>();
        entityLocations = new HashMap<>();
        objectsLocations = new HashMap<>();
        entityRotationsHashMap = new HashMap<>();
        queuedMoves = new HashMap<>();
        Config con = new Config();
        PRINTMAPPINGS = con.PRINTMIND;
        PRINTVISION = con.PRINTVISION;
        this.GUI = false;
        DEBUG_EPXLO = con.DEBUG_EXPLO;
        entityInitialPoses = new HashMap<>();
    }

    /*
    Use this if u wanna make a printer or sth
    GM has functions for printing arrays easy so using a basic one as a printer is quite handy
     */
    public GameController() {
    }

    // this prints anything u give it really
    public static <T> void print(T t) {
        System.out.println(t);
    }

    public void setGameMode(GameMode m) {
        this.gameMode = m;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void addVars(Variables vr) {
        this.walkSpeed = vr.walkSpeed();
        this.eyeRange = vr.eyeRange();
        this.pheromonesDuration = vr.pDur();
    }

    public void init() throws InterruptedException {
        this.maxExploNum = allUnseenTiles.size();
        boolean wasBroken = false;
        int turns = 0;
        if (GUI) {
            Platform.runLater(() -> graphicsUpdater.update(map));
        } else {
            print("------------------------");
            printMap();
            print("------------------------");
        }
        while (isRunning) {//gameloop
            boolean allBroken = false;
            shiftPheromones();
            // This cute little line get all the moves from the agents, effectively executing everything except turning
            entities.stream().collect(Collectors.toMap(Function.identity(), Entity::getMove, (o1, o2) -> o1, ConcurrentHashMap::new)).forEach((k, v) -> moveMap.put(k, executeMove(k, v)));
            // this checks if all of them are stuck
            if (moveMap.entrySet().stream().allMatch(e -> e.getValue() == -999)) allBroken = true;
            // And this executes all walking (possibly to be removed later)
            moveMap.entrySet().stream().filter(e -> e.getValue() > 0).forEach(e -> e.getKey().walk(e.getValue()));
            moveMap.clear();
            if (GUI) {
                Platform.runLater(() -> graphicsUpdater.update(map));
            } else {
                print("------------------------");
                printMap();
            }
            if (PRINTMAPPINGS) {
                for (Entity e : entities) {
                    e.showMeWhatUSaw();
                }
            }
            if (DEBUG_EPXLO) {
                System.out.println(allUnseenTiles.toString());
            }
            if (allBroken) {
                isRunning = false;
                wasBroken = true;
            }
            turns++;
            checkWin(turns);
            Thread.sleep(200);
        }
        if (wasBroken) {
            System.out.println("EXPLORATION WAS CANCELLED DUE TO ALL AGENTS GETTING STUCK ");
            int currentProgress = maxExploNum - allUnseenTiles.size();
            System.out.println(" THEY EXPLORED ABOUT " + (int) (currentProgress * 100 / maxExploNum) + "% IN " + turns + " TURNS");
        }
        if (PRINTMAPPINGS) {
            for (Entity e : entities) {
                e.showMeWhatUSaw();
            }
        }
        int currentProgress = maxExploNum - allUnseenTiles.size();
        System.out.println(" THEY EXPLORED ABOUT " + (int) (currentProgress * 100 / maxExploNum) + "% IN " + turns + " TURNS");
        System.out.println("EXPLORATION DONE IN " + turns + " TURNS!");
        printMap();
    }

    private String[][] makeMap(int height, int length) {
        String[][] mappy = new String[length][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                mappy[j][i] = " ";
                allUnseenTiles.add(coordsToNumber(i, j));
            }
        }
        return mappy;
    }

    public boolean noWallsOnTheWay(int[] pos, int[] target, Rotations rot, Entity e) {
        switch (rot) {
            case LEFT -> {
                int length = pos[0] - target[0];
                for (int i = 1; i <= length; i++) {
                    int[] nextTarget = {pos[0] - i, pos[1]};
                    if (!canBePutThere(nextTarget, e)) return false;
                }
                return true;
            }
            case RIGHT -> {
                int length = target[0] - pos[0];
                for (int i = 1; i <= length; i++) {
                    int[] nextTarget = {pos[0] + i, pos[1]};
                    if (!canBePutThere(nextTarget, e)) return false;
                }
                return true;
            }
            case DOWN -> {
                int length = target[1] - pos[1];
                for (int i = 1; i <= length; i++) {
                    int[] nextTarget = {pos[0], pos[1] + i};
                    if (!canBePutThere(nextTarget, e)) return false;
                }
                return true;
            }
            case UP -> {
                int length = pos[1] - target[1];
                for (int i = 1; i <= length; i++) {
                    int[] nextTarget = {pos[0], pos[1] - i};
                    if (!canBePutThere(nextTarget, e)) return false;
                }
                return true;
            }
        }
        return false;
    }

    public void printMap() {
        printArray(map);
    }

    public void printIntArray(int[][] thing) {
        Arrays.stream(thing).forEach(this::printIntRow);
    }

    public void printIntRow(int[] row) {
        String[] array = Arrays.stream(row).mapToObj(String::valueOf).toArray(String[]::new);
        printRow(array);
    }

    public void printArray(String[][] thing) {
        Arrays.stream(thing).forEach(this::printRow);
    }

    public void printRow(String[] row) {
        print(Arrays.stream(row).toList().stream().collect(Collectors.joining("-")));
    }

    /*
    returns vision ? idk seems self explanatory
    vision is piece of map, gives it with respect to
     entitys location rotation walls in the way and all that
     */
    public String[][] giveVision(Entity e) {
        Rotations rot = entityRotationsHashMap.get(e);
        String[][] vision = new String[eyeRange][3];
        int[] position = entityLocations.get(e);
        boolean[] canSee = {true, true, true};
        switch (rot) {
            case UP -> {
                for (int i = 0; i < eyeRange; i++) {
                    for (int j = -1; j < 2; j++) {
                        int[] lookingAt = {position[0] + j, position[1] - i};
                        if (canSee[j + 1]) {
                            if (existsInBoard(lookingAt)) {
                                String symbol = map[lookingAt[1]][lookingAt[0]];
                                vision[eyeRange - (i + 1)][j + 1] = symbol;
                                if (Objects.equals(symbol, "W")) {
                                    canSee[j + 1] = false;
                                }
                            }
                        } else {
                            if (j != 0 && canSee[1]) {
                                int[] pos_of_it = {lookingAt[0] + 1, lookingAt[1]};
                                if (existsInBoard(pos_of_it) && j == -1 && Objects.equals(map[pos_of_it[1]][pos_of_it[0]], "W")) {
                                    vision[eyeRange - (i + 1)][j + 1] = "X";
                                } else {
                                    {
                                        if (existsInBoard(lookingAt)) {
                                            String symbol = map[lookingAt[1]][lookingAt[0]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (Objects.equals(symbol, "W")) {
                                                canSee[j + 1] = false;
                                            }
                                        }
                                    }

                                }
                            } else vision[eyeRange - (i + 1)][j + 1] = "X";
                        }
                        if (!vision[eyeRange - (i + 1)][j + 1].contains("X")) {
                            if (!vision[eyeRange - (i + 1)][j + 1].contains("W")) {
                                allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                            }
                        }
                    }
                }
            }
            case RIGHT -> {
                for (int i = 0; i < eyeRange; i++) {
                    for (int j = -1; j < 2; j++) {
                        int[] lookingAt = {position[0] + i, position[1] + j};
                        if (canSee[j + 1]) {
                            {
                                if (existsInBoard(lookingAt)) {
                                    String symbol = map[lookingAt[1]][lookingAt[0]];
                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (Objects.equals(symbol, "W")) {
                                        canSee[j + 1] = false;
                                    }
                                }
                            }
                        } else {
                            if (j != 0 && canSee[1]) {
                                int[] pos_of_it = {lookingAt[0], lookingAt[1] + 1};
                                if (existsInBoard(pos_of_it) && j == -1 && Objects.equals(map[pos_of_it[1]][pos_of_it[0]], "W")) {
                                    vision[eyeRange - (i + 1)][j + 1] = "X";
                                } else {
                                    {
                                        if (existsInBoard(lookingAt)) {
                                            String symbol = map[lookingAt[1]][lookingAt[0]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (Objects.equals(symbol, "W")) {
                                                canSee[j + 1] = false;
                                            }
                                        }
                                    }
                                }
                            } else vision[eyeRange - (i + 1)][j + 1] = "X";
                        }
                        if (!vision[eyeRange - (i + 1)][j + 1].contains("X")) {
                            if (!vision[eyeRange - (i + 1)][j + 1].contains("W")) {
                                allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                            }
                        }
                    }
                }
            }
            case LEFT -> {
                for (int i = 0; i < eyeRange; i++) {
                    for (int j = -1; j < 2; j++) {
                        int[] lookingAt = {position[0] - i, position[1] - j};
                        if (canSee[j + 1]) {
                            {
                                if (existsInBoard(lookingAt)) {
                                    String symbol = map[lookingAt[1]][lookingAt[0]];
                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (Objects.equals(symbol, "W")) {
                                        canSee[j + 1] = false;
                                    }
                                }
                            }
                        } else {
                            if (j != 0 && canSee[1]) {
                                int[] pos_of_it = {lookingAt[0], lookingAt[1] - 1};
                                if (existsInBoard(pos_of_it) && j == -1 && Objects.equals(map[pos_of_it[1]][pos_of_it[0]], "W")) {
                                    vision[eyeRange - (i + 1)][j + 1] = "X";
                                } else {
                                    {
                                        if (existsInBoard(lookingAt)) {
                                            String symbol = map[lookingAt[1]][lookingAt[0]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (Objects.equals(symbol, "W")) {
                                                canSee[j + 1] = false;
                                            }
                                        }
                                    }
                                }
                            } else vision[eyeRange - (i + 1)][j + 1] = "X";
                        }
                        if (!vision[eyeRange - (i + 1)][j + 1].contains("X")) {
                            if (!vision[eyeRange - (i + 1)][j + 1].contains("W")) {
                                allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                            }
                        }
                    }
                }
            }
            case DOWN -> {
                for (int i = 0; i < eyeRange; i++) {
                    for (int j = -1; j < 2; j++) {
                        int[] lookingAt = {position[0] - j, position[1] + i};
                        if (existsInBoard(lookingAt)) {
                            if (canSee[j + 1]) {
                                {
                                    String symbol = map[lookingAt[1]][lookingAt[0]];
                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (Objects.equals(symbol, "W")) {
                                        canSee[j + 1] = false;
                                    }
                                }
                            } else {
                                if (j != 0 && canSee[1]) {
                                    int[] pos_of_it = {lookingAt[0] - 1, lookingAt[1]};
                                    if (existsInBoard(pos_of_it) && j == -1 && Objects.equals(map[pos_of_it[1]][pos_of_it[0]], "W")) {
                                        vision[eyeRange - (i + 1)][j + 1] = "X";
                                    } else {
                                        {
                                            String symbol = map[lookingAt[1]][lookingAt[0]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (Objects.equals(symbol, "W")) {
                                                canSee[j + 1] = false;
                                            }
                                        }
                                    }
                                } else {
                                    vision[eyeRange - (i + 1)][j + 1] = "X";
                                }
                            }
                        } else {
                            vision[eyeRange - (i + 1)][j + 1] = "X";
                        }
                        if (!vision[eyeRange - (i + 1)][j + 1].contains("X")) {
                            if (!vision[eyeRange - (i + 1)][j + 1].contains("W")) {
                                allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                            }
                        }
                    }
                }
            }

        }
        if (PRINTVISION) printArray(vision);
        return vision;
    }

    private void updateAgentDisplay(Entity e) {
        int[] xy = entityLocations.get(e);
        removeFromMap(xy);
        putOnMap(symbol(e), xy);
    }

    //handy for checking if two positions are equal
    private boolean equal(int[] a, int[] b) {
        return (a[0] == b[0] && a[1] == b[1]);
    }

    private Teleporter findTeleporter(int[] pos) {
        int[] first = {pos[0], pos[1] + 1};
        int[] second = {pos[0], pos[1] - 1};
        int[] third = {pos[0] + 1, pos[1]};
        int[] fourth = {pos[0] - 1, pos[1]};
        ArrayList<int[]> poss = new ArrayList<>();
        poss.add(first);
        poss.add(second);
        poss.add(third);
        poss.add(fourth);
        for (ObjectOnMap ob : objects) {
            int[] position = objectsLocations.get(ob);
            for (int[] p : poss) {
                if (equal(p, position) && ob instanceof Teleporter) return (Teleporter) ob;
            }
        }
        return null;
    }


    private void pheromoneSide(int[] pos, Rotations rot) {

        int x = pos[1];
        int y = pos[0];
        for (int i = -1; i < 2; i++) {
            switch (rot) { // the fucking coordinates are inverted
                case LEFT:
                case RIGHT:
                    int x2 = x + i;
                    pheromonesMap[x2][y] = pheromonesDuration;

                    continue;
                case DOWN:
                case UP:
                    int y2 = y + i;
                    pheromonesMap[x][y2] = pheromonesDuration;
                    continue;
            }
        }

    }


    private int executeMove(Entity e, Moves m) {
        Rotations rotation = entityRotationsHashMap.get(e);
        int[] pos = entityLocations.get(e);
        switch (m) {
            case P_TURN_AROUND -> {
                pheromoneSide(pos, rotation);
                return executeMove(e, Moves.TURN_AROUND);
            }
            case P_TURN_LEFT -> {
                pheromoneSide(pos, rotation);
                return executeMove(e, Moves.TURN_LEFT);
            }
            case P_TURN_RIGHT -> {
                pheromoneSide(pos, rotation);
                return executeMove(e, Moves.TURN_RIGHT);
            }
            case P_WALK -> {
                pheromoneSide(pos, rotation);
                return executeMove(e, Moves.WALK);
            }
            case P_USE_TELEPORTER -> {
                pheromoneSide(pos, rotation);
                return executeMove(e, Moves.USE_TELEPORTER);
            }
            case USE_TELEPORTER -> {
                //System.out.println("HE WANTS TO USE IT");
                Teleporter tp = findTeleporter(pos);

                if (tp == null) return -1;
                else {
                    int[] target = tp.getTarget();
                    if (canBePutThere(target, e)) {
                        e.setPosition(entityInitialPoses.get(e).newPosition(target));
                        removeFromMap(pos);
                        putOnMap(symbol(e), target);
                        putOnMap("M1", pos);
                        entityLocations.put(e, target);
                        return 0;
                    } else return -1;
                }
            }
            case STUCK -> {
                return -999;
            }
            case TURN_LEFT -> {
                switch (rotation) {
                    case DOWN -> {
                        entityRotationsHashMap.put(e, Rotations.RIGHT);
                        updateAgentDisplay(e);
                        e.turnLeft();
                        return 0;
                    }
                    case LEFT -> {
                        entityRotationsHashMap.put(e, Rotations.DOWN);
                        updateAgentDisplay(e);
                        e.turnLeft();
                        return 0;

                    }
                    case RIGHT -> {
                        entityRotationsHashMap.put(e, Rotations.UP);
                        updateAgentDisplay(e);
                        e.turnLeft();
                        return 0;
                    }
                    case UP -> {
                        entityRotationsHashMap.put(e, Rotations.LEFT);
                        updateAgentDisplay(e);
                        e.turnLeft();
                        return 0;
                    }
                }
            }
            case TURN_RIGHT -> {
                switch (rotation) {
                    case DOWN -> {
                        entityRotationsHashMap.put(e, Rotations.LEFT);
                        updateAgentDisplay(e);
                        e.turnRight();
                        return 0;
                    }
                    case LEFT -> {
                        entityRotationsHashMap.put(e, Rotations.UP);
                        updateAgentDisplay(e);
                        e.turnRight();
                        return 0;
                    }
                    case RIGHT -> {
                        entityRotationsHashMap.put(e, Rotations.DOWN);
                        updateAgentDisplay(e);
                        e.turnRight();
                        return 0;
                    }
                    case UP -> {
                        entityRotationsHashMap.put(e, Rotations.RIGHT);
                        updateAgentDisplay(e);
                        e.turnRight();
                        return 0;
                    }
                }
            }
            case TURN_AROUND -> {
                switch (rotation) {
                    case DOWN -> {
                        entityRotationsHashMap.put(e, Rotations.UP);
                        updateAgentDisplay(e);
                        e.turnAround();
                        return 0;
                    }
                    case LEFT -> {
                        entityRotationsHashMap.put(e, Rotations.RIGHT);
                        updateAgentDisplay(e);
                        e.turnAround();
                        return 0;
                    }
                    case RIGHT -> {
                        entityRotationsHashMap.put(e, Rotations.LEFT);
                        updateAgentDisplay(e);
                        e.turnAround();
                        return 0;
                    }
                    case UP -> {
                        entityRotationsHashMap.put(e, Rotations.DOWN);
                        updateAgentDisplay(e);
                        e.turnAround();
                        return 0;
                    }
                }
            }
            case WALK -> {
                switch (rotation) {
                    case UP -> {
                        int[] targetlocation = {pos[0], pos[1] - walkSpeed};
                        if (canBePutThere(targetlocation, e) && noWallsOnTheWay(pos, targetlocation, rotation, e)) {
                            putOnMap(symbol(e), targetlocation);
                            removeFromMap(pos);
                            entityLocations.put(e, targetlocation);
                            return walkSpeed;
                        } else {
                            if (walkSpeed > 1) {
                                for (int i = walkSpeed; i > 0; i--) {
                                    int[] nexttargetlocation = {pos[0], pos[1] - i};
                                    if (existsInBoard(nexttargetlocation)) {
                                        if (canBePutThere(nexttargetlocation, e) && noWallsOnTheWay(pos, nexttargetlocation, rotation, e)) {
                                            putOnMap(symbol(e), nexttargetlocation);
                                            removeFromMap(pos);
                                            entityLocations.put(e, nexttargetlocation);
                                            return i;
                                        }
                                    }

                                }
                            } else return -1;
                        }
                        return -1;
                    }
                    case DOWN -> {
                        int[] targetlocation = {pos[0], pos[1] + walkSpeed};
                        if (canBePutThere(targetlocation, e) && noWallsOnTheWay(pos, targetlocation, rotation, e)) {
                            putOnMap(symbol(e), targetlocation);
                            removeFromMap(pos);
                            entityLocations.put(e, targetlocation);
                            return walkSpeed;
                        } else {
                            if (walkSpeed > 1) {
                                for (int i = walkSpeed; i > 0; i--) {
                                    int[] nexttargetlocation = {pos[0], pos[1] + i};
                                    if (existsInBoard(nexttargetlocation)) {
                                        if (canBePutThere(nexttargetlocation, e) && noWallsOnTheWay(pos, nexttargetlocation, rotation, e)) {
                                            putOnMap(symbol(e), nexttargetlocation);
                                            removeFromMap(pos);
                                            entityLocations.put(e, nexttargetlocation);
                                            return i;
                                        }
                                    }

                                }
                            } else return -1;
                        }
                        return -1;
                    }
                    case RIGHT -> {
                        int[] targetlocation = {pos[0] + walkSpeed, pos[1]};
                        if (canBePutThere(targetlocation, e) && noWallsOnTheWay(pos, targetlocation, rotation, e)) {
                            putOnMap(symbol(e), targetlocation);
                            removeFromMap(pos);
                            entityLocations.put(e, targetlocation);
                            return walkSpeed;
                        } else {
                            if (walkSpeed > 1) {
                                for (int i = walkSpeed; i > 0; i--) {
                                    int[] nexttargetlocation = {pos[0] + i, pos[1]};
                                    if (existsInBoard(nexttargetlocation)) {
                                        if (canBePutThere(nexttargetlocation, e) && noWallsOnTheWay(pos, nexttargetlocation, rotation, e)) {
                                            putOnMap(symbol(e), nexttargetlocation);
                                            removeFromMap(pos);
                                            entityLocations.put(e, nexttargetlocation);
                                            return i;
                                        }
                                    }

                                }
                            } else return -1;
                        }
                        return -1;
                    }
                    case LEFT -> {
                        int[] targetlocation = {pos[0] - walkSpeed, pos[1]};
                        if (canBePutThere(targetlocation, e) && noWallsOnTheWay(pos, targetlocation, rotation, e)) {
                            putOnMap(symbol(e), targetlocation);
                            removeFromMap(pos);
                            entityLocations.put(e, targetlocation);
                            return walkSpeed;
                        } else {
                            if (walkSpeed > 1) {
                                for (int i = walkSpeed; i > 0; i--) {
                                    int[] nexttargetlocation = {pos[0] - i, pos[1]};
                                    if (existsInBoard(nexttargetlocation)) {
                                        if (canBePutThere(nexttargetlocation, e) && noWallsOnTheWay(pos, nexttargetlocation, rotation, e)) {
                                            putOnMap(symbol(e), nexttargetlocation);
                                            removeFromMap(pos);
                                            entityLocations.put(e, nexttargetlocation);
                                            return i;
                                        }
                                    }

                                }
                            } else return -1;
                        }
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    public void addEntity(Entity e, int h, int l, Rotations rot) {
        entities.add(e);
        if (e instanceof Intruder) {
            this.intruderGlobalRotation = rot;
        } else {
            this.globalRotation = rot;
        }
        int[] yx = {h, l};
        entityLocations.put(e, yx);
        entityRotationsHashMap.put(e, rot);
        putOnMap(symbol(e), h, l);
        entityInitialPoses.put(e, new Pose(rot, yx));
    }


    public void setGlobalRotationIntruder(Rotations rot) {
        this.intruderGlobalRotation = rot;
    }

    public void addObject(ObjectOnMap o) {
        objects.add(o);
        int[] yx = o.getXy();
        objectsLocations.put(o, yx);
        putObjectOnMap(o);
    }

    public void putObjectOnMap(ObjectOnMap o) {
        String symbol = o.getSymbol();
        putOnMap(o.getSymbol(), o.getXy());

    }

    public void makeBorders(int length, int height) {
        for (int i = 0; i < length; i++) {
            putOnMap("W", 0, i);
            allUnseenTiles.remove((Object) coordsToNumber(0, i));
            putOnMap("W", height - 1, i);
            allUnseenTiles.remove((Object) coordsToNumber(height - 1, i));
        }
        for (int j = 0; j < height; j++) {
            putOnMap("W", j, 0);
            allUnseenTiles.remove((Object) coordsToNumber(j, 0));
            putOnMap("W", j, length - 1);
            allUnseenTiles.remove((Object) coordsToNumber(j, length - 1));
        }
    }

    public void addWall(int y0, int x0, int y1, int x1) {
        int ySpan = y1 - y0;
        int xSpan = x1 - x0;
        if (xSpan > 0) {//x goes up
            if (ySpan > 0) {//Y goes up
                for (int y = y0; y < y1; y++) {
                    for (int x = x0; x < x1; x++) {
                        putOnMap("W", x, y);
                        allUnseenTiles.remove((Object) coordsToNumber(x, y));
                    }
                }

            } else {//Y goes down
                for (int y = y0; y >= y1; y--) {
                    for (int x = x0; x <= x1; x++) {
                        putOnMap("W", x, y);
                        allUnseenTiles.remove((Object) coordsToNumber(x, y));
                    }
                }
            }
        } else {//x goes down
            if (ySpan > 0) {//Y goes up
                for (int y = y0; y <= y1; y++) {
                    for (int x = x0; x >= x1; x--) {
                        putOnMap("W", x, y);
                        allUnseenTiles.remove((Object) coordsToNumber(x, y));
                    }
                }
            } else {//Y goes down
                for (int y = y0; y >= y1; y--) {
                    for (int x = x0; x >= x1; x--) {
                        putOnMap("W", x, y);
                        allUnseenTiles.remove((Object) coordsToNumber(x, y));
                    }
                }
            }
        }
    }

    private void putOnMap(String s, int[] yx) {
        map[yx[1]][yx[0]] = s;
    }

    private void putOnMap(String s, int h, int l) {
        map[l][h] = s;
    }

    private void removeFromMap(int h, int l) {
        map[l][h] = " ";
    }

    private void removeFromMap(int[] yx) {
        map[yx[1]][yx[0]] = " ";
    }

    private String symbol(Entity e) {
        Rotations rot = entityRotationsHashMap.get(e);
        String addition = "ERROR";
        switch (rot) {
            case DOWN -> addition = "d";
            case UP -> addition = "^";
            case LEFT -> addition = "<";
            case RIGHT -> addition = ">";
        }
        if (e.getType() == EntityType.EXPLORER) {
            return "E" + addition;
        }
        if (e.getType() == EntityType.GUARD) {
            return "G" + addition;
        }
        if (e.getType() == EntityType.INTRUDER) {
            return "I" + addition;
        }
        return "ERROR";
    }

    private boolean blockedByObstacles(String s) {
        // PUT ALL STUFF THAT BLOCK MOVEMENT HERE PLS
        return s.contains("I") || s.contains("G") || s.contains("E") || s.contains("W") ||s.contains("V1");
    }

    private boolean canBePutThere(int[] target, Entity e) {
        if (target[0] > -1 && target[0] < mapHeight && target[1] > -1 && target[1] < mapLength) {
            if (e.getType() == EntityType.INTRUDER) {
                if (Objects.equals(map[target[1]][target[0]], "V1"))
                    return true;
                else return !blockedByObstacles(map[target[1]][target[0]]);
            } else return !blockedByObstacles(map[target[1]][target[0]]);
        } else return false;
    }

    private boolean existsInBoard(int[] pos) {
        return (pos[0] > -1 && pos[0] < mapHeight && pos[1] > -1 && pos[1] < mapLength);
    }

    // all this usable for checking win condition
    private int coordsToNumber(int h, int l) {
        return (h + l * mapHeight);
    }

    private int coordsToNumber(int[] yx) {
        return ((yx[0]) + yx[1] * mapHeight);
    }

    private void checkWin(int turns) {
        switch (gameMode) {
            case EXPLORATION -> {
                if (allUnseenTiles.isEmpty()) isRunning = false;
                if (turns > MAX_TURNS) isRunning = false;
            }
            case PATROL_CHASE -> {
                //TODO intruder win/lose
            }
        }
    }

    public void shiftPheromones(){
        for ( int i=0;i<pheromonesMap.length;i++){
            for(int j = 0; j < pheromonesMap[0].length; j++){
                int val = pheromonesMap[i][j];
                if(val >0) {pheromonesMap[i][j]--;val--;}//map[i][j]=String.valueOf(pheromonesMap[i][j]);
                if(!blockedByObstacles(map[i][j])) {
                    if(val==0){
                        map[i][j]=" ";
                    }
                    else if(val>0) {
                        map[i][j] = String.valueOf(pheromonesMap[i][j]);
                    }
                }
            }
        }
    }

        public Moves getDirection(Entity intEntity){ //Rotation or move?
            //get position
            int[] targetLoc = new int[2];
            for(ObjectOnMap ob : objects){
                if(ob instanceof Goal)
                    targetLoc = ob.getXy(); break;
            }

            //get the current rotation of the intruder
            //From the rotation hashmap
            Rotations globRot = entityRotationsHashMap.get(intEntity);

            int[] intruderLoc = entityLocations.get(intEntity);

            //Stopping condition
            if(intruderLoc[0] == targetLoc[0] && intruderLoc[1] == targetLoc[1])
                isRunning = false;


            double tanTheta = (double)(targetLoc[1] - intruderLoc[1])/(targetLoc[0]-intruderLoc[0]);
            double angle = Math.atan(tanTheta);
            int degAngle = (int) Math.toDegrees(angle);

            System.out.println("Global rotation: " + globRot.toString());
            System.out.println("Global xy TARGET : " + targetLoc[0] + "," + targetLoc[1]);
            System.out.println("Global xy INTRUDER : " + intruderLoc[0] + "," + intruderLoc[1]);
            System.out.println("tanTheta: " + tanTheta);
            //System.out.println("Deg angle : " + degAngle);

            if(targetLoc[1] < intruderLoc[1] && targetLoc[0] < intruderLoc[0]) {
                degAngle = 90 + degAngle;
            }
            else if(degAngle < 0 && targetLoc[1] < intruderLoc[1]) {
                degAngle = degAngle + 360;
            }
            else if(targetLoc[1] > intruderLoc[1] && targetLoc[0] < intruderLoc[0]){
                degAngle = degAngle + 270;
            }
            else if(targetLoc[1] == intruderLoc[1] && targetLoc[0] < intruderLoc[0]){
                degAngle = degAngle + 180;
            }

            //Problem if it encounters a wall with the setGlobalRotation method
            System.out.println("Deg angle : " + degAngle);

            if(degAngle > 45 && degAngle < 135) {
                if(globRot == Rotations.UP) {return Moves.TURN_AROUND;}
                else if(globRot == Rotations.DOWN) {return Moves.WALK;}
                else if(globRot == Rotations.RIGHT){return Moves.TURN_RIGHT;}
                else {return Moves.TURN_LEFT;}
            }
            else if((degAngle <= 45 && degAngle >= 0) || (degAngle >= 315 && degAngle < 360)) {
                if(globRot == Rotations.UP) {return Moves.TURN_RIGHT;}
                else if(globRot == Rotations.DOWN) {return Moves.TURN_LEFT;}
                else if(globRot == Rotations.RIGHT) {return Moves.WALK;}
                else {return Moves.TURN_AROUND;}
            }
            else if(degAngle >225 && degAngle < 315){
                if(globRot == Rotations.UP)
                    return Moves.WALK;
                else if(globRot == Rotations.DOWN) {return Moves.TURN_AROUND;}
                else if(globRot == Rotations.RIGHT) {return Moves.TURN_LEFT;}
                else {return Moves.TURN_RIGHT;}
            }
            else{
                if(globRot == Rotations.UP) {return Moves.TURN_LEFT;}
                else if(globRot == Rotations.DOWN) {return Moves.TURN_RIGHT;}
                else if(globRot == Rotations.RIGHT) {return Moves.TURN_AROUND;}
                else {return Moves.WALK;}
            }

        }
        public Rotations getIntRot() {
            return intruderGlobalRotation;
        }

        public Moves getNextBestMove(Entity intEntity){
            Moves returner = Moves.STUCK;
            int[] targetLoc = new int[2];
            for(ObjectOnMap ob : objects){
                if(ob instanceof Goal)
                    targetLoc = ob.getXy(); break;
            }

            Rotations globalRot = entityRotationsHashMap.get(intEntity);

            int[] intruderLoc = entityLocations.get(intEntity);

            int ydif = targetLoc[1]-intruderLoc[1];
            int xdif = targetLoc[0] - intruderLoc[0];

            if (Math.abs(ydif) < Math.abs(xdif)) {
                if (ydif < 0) {
                    if(globalRot == Rotations.UP) {returner = Moves.WALK;}
                    else if(globalRot == Rotations.DOWN) {returner = Moves.TURN_AROUND;}
                    else if(globalRot == Rotations.RIGHT) {returner = Moves.TURN_LEFT;}
                    else {returner = Moves.TURN_RIGHT;}
                }
                else {
                    if(globalRot == Rotations.UP) {returner = Moves.TURN_AROUND;}
                    else if(globalRot == Rotations.DOWN) {returner = Moves.WALK;}
                    else if(globalRot == Rotations.RIGHT){returner = Moves.TURN_RIGHT;}
                    else {returner = Moves.TURN_LEFT;}
                }
            }
            else  {
                if (xdif<0) {
                    if(globalRot == Rotations.UP) {returner = Moves.TURN_LEFT;}
                    else if(globalRot == Rotations.DOWN) {returner = Moves.TURN_RIGHT;}
                    else if(globalRot == Rotations.RIGHT) {returner = Moves.TURN_AROUND;}
                    else {returner = Moves.WALK;}
                }
                else {
                    if(globalRot == Rotations.UP) {returner = Moves.TURN_RIGHT;}
                    else if(globalRot == Rotations.DOWN) {returner = Moves.TURN_LEFT;}
                    else if(globalRot == Rotations.RIGHT) {returner = Moves.WALK;}
                    else {returner = Moves.TURN_AROUND;}
                }
            }
            return returner;
        }
    }