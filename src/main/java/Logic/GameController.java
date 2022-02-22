package Logic;

import Entities.Entity;
import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;

import java.util.ArrayList;
import java.util.HashMap;


public class GameController {
    private String[][] map;
    private int mapLength;
    private int mapHeight;
    private boolean isRunning;
    private int eyeRange;
    private ArrayList<Entity> entities;
    private HashMap<Entity, int[]> locations;
    private HashMap<Entity, Rotations> entityRotationsHashMap;
    private HashMap<Entity, Moves> queuedMoves;
    private ArrayList<Integer> allUnseenTiles;

    public GameController(int height, int length, int eyeRange) {
        allUnseenTiles=new ArrayList<>();
        this.mapLength = length;
        this.mapHeight = height;
        this.map = makeMap(height, length);
        makeBorders(length, height);
        isRunning = true;
        this.eyeRange = eyeRange;
        entities = new ArrayList<Entity>();
        locations = new HashMap<Entity, int[]>();
        entityRotationsHashMap = new HashMap<Entity, Rotations>();
        queuedMoves = new HashMap<Entity, Moves>();

    }
    public GameController(){};

    public void init() throws InterruptedException {
        int turns =0;
        while (isRunning) {//gameloop
            for (Entity e : entities) {
                Moves currentMove = e.getMove();
                queuedMoves.put(e, currentMove);
            }
            for (Entity e : entities) {
                executeMove(e, queuedMoves.get(e));
            }
            for(Entity e:entities){
                Moves move = queuedMoves.get(e);
                switch(move){
                    case WALK -> e.walk();
                    case TURN_AROUND -> e.turnAround();
                    case TURN_RIGHT -> e.turnRight();
                    case TURN_LEFT -> e.turnLeft();
                }
            }
            printMap();
            turns++;
            checkWin();
            System.out.println(allUnseenTiles.toString());
           // Thread.sleep(1000);
        }
        System.out.println("EXPLORATION DONE IN "+turns+" TURNS!");
    }

    private String[][] makeMap(int height, int length) {
        String[][] mappy = new String[height][length];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                mappy[i][j] = " ";
                allUnseenTiles.add(coordsToNumber(i,j));
            }
        }
        return mappy;
    }

    public void printMap() {
        printArray(map);
        print("------------------------");
    }

    public void printArray(String[][] thing) {
        int height = thing.length;
        int lenght = thing[0].length;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < lenght; j++) {
                if (j == lenght - 1) {
                    print(thing[i][j]);
                } else {
                    System.out.print(thing[i][j] + "-");
                }
            }
        }
    }

    public String[][] giveVision(Entity e) {
        Rotations rot = entityRotationsHashMap.get(e);
        String[][] vision = new String[eyeRange][3];
        int[] position = locations.get(e);
        boolean canSee[] = {true, true, true};
        switch (rot) {
            case UP -> {
                for (int i = 0; i < eyeRange; i++) {
                    for (int j = -1; j < 2; j++) {
                        int[] lookingAt = {position[0] - i, position[1] + j};
                        allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                        if (canSee[j + 1]) {

                            {
                                if (existsInBoard(lookingAt)) {
                                    String symbol = map[lookingAt[0]][lookingAt[1]];
                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (symbol == "W") {
                                        canSee[j + 1] = false;
                                    }
                                }
                            }
                        } else {
                            if (j != 0 && canSee[1]) {
                                if (j == -1 && (map[lookingAt[0] + 1][lookingAt[1]]) == "W") {
                                    vision[eyeRange - (i + 1)][j + 1] = "X";
                                } else {
                                    {
                                        if (existsInBoard(lookingAt)) {
                                            String symbol = map[lookingAt[0]][lookingAt[1]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (symbol == "W") {
                                                canSee[j + 1] = false;
                                            }
                                        }
                                    }

                                }
                            } else vision[eyeRange - (i + 1)][j + 1] = "X";
                        }
                    }
                }
            }
            case RIGHT -> {
                for (int i = 0; i < eyeRange; i++) {
                    for (int j = -1; j < 2; j++) {
                        int[] lookingAt = {position[0] + j, position[1] + i};
                        allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                        if (canSee[j + 1]) {
                            {
                                if (existsInBoard(lookingAt)) {
                                    String symbol = map[lookingAt[0]][lookingAt[1]];
                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (symbol == "W") {
                                        canSee[j + 1] = false;
                                    }
                                }
                            }
                        } else {
                            if (j != 0 && canSee[1]) {
                                if (j == -1 && (map[lookingAt[0] + 1][lookingAt[1]]) == "W") {
                                    vision[eyeRange - (i + 1)][j + 1] = "X";
                                } else {
                                    {
                                        if (existsInBoard(lookingAt)) {
                                            String symbol = map[lookingAt[0]][lookingAt[1]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (symbol == "W") {
                                                canSee[j + 1] = false;
                                            }
                                        }
                                    }
                                }
                            } else vision[eyeRange - (i + 1)][j + 1] = "X";
                        }
                    }
                }
            }
            case LEFT -> {
                for (int i = 0; i < eyeRange; i++) {
                    for (int j = -1; j < 2; j++) {
                        int[] lookingAt = {position[0] - j, position[1] - i};
                        allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                        if (canSee[j + 1]) {
                            {
                                if (existsInBoard(lookingAt)) {
                                    String symbol = map[lookingAt[0]][lookingAt[1]];
                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (symbol == "W") {
                                        canSee[j + 1] = false;
                                    }
                                }
                            }
                        } else {
                            if (j != 0 && canSee[1]) {
                                if (j == -1 && (map[lookingAt[0] + 1][lookingAt[1]]) == "W") {
                                    vision[eyeRange - (i + 1)][j + 1] = "X";
                                } else {
                                    {
                                        if (existsInBoard(lookingAt)) {
                                            String symbol = map[lookingAt[0]][lookingAt[1]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (symbol == "W") {
                                                canSee[j + 1] = false;
                                            }
                                        }
                                    }
                                }
                            } else vision[eyeRange - (i + 1)][j + 1] = "X";
                        }
                    }
                }
            }
            case DOWN -> {
                for (int i = 0; i < eyeRange; i++) {
                    for (int j = -1; j < 2; j++) {
                        int[] lookingAt = {position[0] + i, position[1] - j};
                        if (existsInBoard(lookingAt)) {
                            allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                            if (canSee[j + 1]) {
                                {
                                    String symbol = map[lookingAt[0]][lookingAt[1]];

                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (symbol == "W") {
                                        canSee[j + 1] = false;
                                    }

                                }
                            } else {
                                if (j != 0 && canSee[1]) {
                                    if (j == -1 && (map[lookingAt[0] + 1][lookingAt[1]]) == "W") {
                                        vision[eyeRange - (i + 1)][j + 1] = "X";
                                    } else {
                                        {
                                            String symbol = map[lookingAt[0]][lookingAt[1]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (symbol == "W") {
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
                    }
                }
            }

        }
        System.out.println(rot);
        return vision;
    }

    private void executeMove(Entity e, Moves m) {
        Rotations rotation = entityRotationsHashMap.get(e);
        switch (m) {
            case TURN_LEFT -> {
                switch (rotation) {
                    case DOWN -> {
                        entityRotationsHashMap.put(e, Rotations.RIGHT);
                    }
                    case LEFT -> {
                        entityRotationsHashMap.put(e, Rotations.DOWN);
                    }
                    case RIGHT -> {
                        entityRotationsHashMap.put(e, Rotations.UP);
                    }
                    case UP -> {
                        entityRotationsHashMap.put(e, Rotations.LEFT);
                    }
                }
            }
            case TURN_RIGHT -> {
                switch (rotation) {
                    case DOWN -> {
                        entityRotationsHashMap.put(e, Rotations.LEFT);
                    }
                    case LEFT -> {
                        entityRotationsHashMap.put(e, Rotations.UP);
                    }
                    case RIGHT -> {
                        entityRotationsHashMap.put(e, Rotations.DOWN);
                    }
                    case UP -> {
                        entityRotationsHashMap.put(e, Rotations.RIGHT);
                    }
                }
            }
            case TURN_AROUND -> {
                switch (rotation) {
                    case DOWN -> {
                        entityRotationsHashMap.put(e, Rotations.UP);
                    }
                    case LEFT -> {
                        entityRotationsHashMap.put(e, Rotations.RIGHT);
                    }
                    case RIGHT -> {
                        entityRotationsHashMap.put(e, Rotations.LEFT);
                    }
                    case UP -> {
                        entityRotationsHashMap.put(e, Rotations.DOWN);
                    }
                }
            }
            case WALK -> {
                int[] pos = locations.get(e);
                switch (rotation) {
                    case UP -> {
                        int[] targetlocation = {pos[0] - 1, pos[1]};
                        if (existsInBoard(targetlocation)) {
                            if (canBePutThere(targetlocation[0], targetlocation[1])) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                locations.put(e, targetlocation);
                            }
                        }
                    }
                    case DOWN -> {
                        int[] targetlocation = {pos[0] + 1, pos[1]};
                        if (existsInBoard(targetlocation)) {
                            if (canBePutThere(targetlocation[0], targetlocation[1])) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                locations.put(e, targetlocation);
                            }
                        }
                    }
                    case RIGHT -> {
                        int[] targetlocation = {pos[0], pos[1] + 1};
                        if (existsInBoard(targetlocation)) {
                            if (canBePutThere(targetlocation[0], targetlocation[1])) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                locations.put(e, targetlocation);
                            }
                        }
                    }
                    case LEFT -> {
                        int[] targetlocation = {pos[0], pos[1] - 1};
                        if (existsInBoard(targetlocation)) {
                            if (canBePutThere(targetlocation[0], targetlocation[1])) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                locations.put(e, targetlocation);
                            }
                        }
                    }
                }
            }
        }
    }


    public void addEntity(Entity e, int h, int l, Rotations rot) {
        entities.add(e);
        int[] yx = {h, l};
        locations.put(e, yx);
        entityRotationsHashMap.put(e, rot);
        putOnMap(symbol(e), h, l);
    }

    public void print(String s) {
        System.out.println(s);
    }

    public void print(int s) {
        System.out.println(s);
    }

    public void makeBorders(int length, int height) {
        for (int i = 0; i < length; i++) {
            putOnMap("W", 0, i);
            allUnseenTiles.remove((Object)coordsToNumber(0,i));
            putOnMap("W", height - 1, i);
            allUnseenTiles.remove((Object)coordsToNumber(height-1,i));
        }
        for (int j = 0; j < height; j++) {
            putOnMap("W", j, 0);
            allUnseenTiles.remove((Object)coordsToNumber(j,0));
            putOnMap("W", j, length - 1);
            allUnseenTiles.remove((Object)coordsToNumber(j,length-1));
        }
    }

    private void putOnMap(String s, int[] yx) {
        map[yx[0]][yx[1]] = s;
    }

    private void putOnMap(String s, int h, int l) {
        map[h][l] = s;
    }

    private void removeFromMap(int h, int l) {
        map[h][l] = " ";
    }

    private void removeFromMap(int[] yx) {
        map[yx[0]][yx[1]] = " ";
    }

    private String symbol(Entity e) {
        if (e.getType() == EntityType.EXPLORER) {
            return "E";
        }
        return "ERROR";
    }

    private boolean canBePutThere(int h, int l) {
        return map[h][l] == " ";
    }

    private boolean existsInBoard(int[] pos) {
        return (pos[0] > -1 && pos[0] < mapHeight && pos[1] > -1 && pos[1] < mapLength);
    }
    private int coordsToNumber(int h, int l){
        return ((h*mapLength)+l);
    }
    private int coordsToNumber(int []yx){
        return ((yx[0]*mapLength)+yx[1]);
    }
    private void checkWin(){
        if(allUnseenTiles.isEmpty()) isRunning=false;
    }



    /*
    public String symbol(int i){
        if (walls.contains(i)){
            return"W";
        }
        else return" ";
    }
    */


}
