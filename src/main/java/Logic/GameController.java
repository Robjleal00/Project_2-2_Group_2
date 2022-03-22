package Logic;

import Entities.Entity;
import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;

import java.util.ArrayList;
import java.util.HashMap;

import Config.*;
import ObjectsOnMap.ObjectOnMap;
import java.util.Objects;

public class GameController {
    private String[][] map;
    private int mapLength;
    private int mapHeight;
    private boolean isRunning;
    private int eyeRange;
    private ArrayList<Entity> entities;
    private ArrayList<ObjectOnMap> objects;
    private HashMap<Entity, int[]> entityLocations;
    private HashMap<ObjectOnMap, int[]> objectsLocations;
    private HashMap<Entity, Rotations> entityRotationsHashMap;
    private HashMap<Entity, Moves> queuedMoves;
    private ArrayList<Integer> allUnseenTiles;
    private boolean PRINTMAPPINGS ;
    public boolean PRINTVISION;
    public boolean GUI;
    private boolean DEBUG_EPXLO;
    private int maxExploNum;
    private int walkSpeed;

    public GameController(int height, int length) {
        allUnseenTiles = new ArrayList<>();
        this.mapLength = length;
        this.mapHeight = height;
        this.map = makeMap(height, length);
        makeBorders(length, height);
        isRunning = true;
        entities = new ArrayList<>();
        objects=new ArrayList<>();
        entityLocations = new HashMap<>();
        objectsLocations = new HashMap<>();
        entityRotationsHashMap = new HashMap<>();
        queuedMoves = new HashMap<>();
        Config con = new Config();
        PRINTMAPPINGS=con.PRINTMIND;
        PRINTVISION=con.PRINTVISION;
        GUI=con.GUI;
        DEBUG_EPXLO=con.DEBUG_EXPLO;
        this.maxExploNum=allUnseenTiles.size();
    }

    public GameController() {
    }
    public void addVars(Variables vr){
        this.walkSpeed=vr.walkSpeed();
        this.eyeRange=vr.eyeRange();
    }

    public void init() throws InterruptedException {
        boolean wasBroken=false;
        int turns = 0;
        while (isRunning) {//gameloop
            boolean allBroken=false;
            if(GUI){

            }
            else{
                print("------------------------");
                printMap();
            }
            for (Entity e : entities) {
                Moves currentMove = e.getMove();
                queuedMoves.put(e, currentMove);
            }
            Moves lastmove=Moves.STUCK;
            for (Entity e : entities) {
                int tester=executeMove(e, queuedMoves.get(e));
                if (tester!=-1) {
                    Moves move = queuedMoves.get(e);
                    switch (move) {
                        case WALK -> {
                            e.walk(tester);
                            lastmove=move;
                        }
                        case TURN_AROUND ->{e.turnAround();lastmove=move;}
                        case TURN_RIGHT -> {e.turnRight();lastmove=move;}
                        case TURN_LEFT -> {e.turnLeft();lastmove=move;}
                        case STUCK -> {
                           if(lastmove==Moves.STUCK){
                                   lastmove=move;
                                  allBroken=true;

                           }
                        }
                    }
                }
            }
            if(PRINTMAPPINGS){
                for (Entity e : entities) {
                    e.showMeWhatUSaw();
                }
            }

            if(DEBUG_EPXLO){
                System.out.println(allUnseenTiles.toString());
            }
            if(allBroken){
                isRunning=false;
                wasBroken=true;
            }
            if(GUI){

            }
            else{
                printMap();
                print("------------------------");
            }
            turns++;
            checkWin();
            Thread.sleep(100);
        }
        if(wasBroken){
            System.out.println("EXPLORATION WAS CANCELLED DUE TO ALL AGENTS GETTING STUCK ");
            int currentProgress=maxExploNum-allUnseenTiles.size();
            System.out.println(" THEY EXPLORED ABOUT "+(int)(currentProgress*100/maxExploNum) +"% IN "+turns+" TURNS" );
        }
        System.out.println("EXPLORATION DONE IN " + turns + " TURNS!");
    }

    private String[][] makeMap(int height, int length) {
        String[][] mappy = new String[height][length];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                mappy[i][j] = " ";
                allUnseenTiles.add(coordsToNumber(i, j));
            }
        }
        return mappy;
    }
    public boolean noWallsOnTheWay(int[]pos,int[]target,Rotations rot){
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

    public void printMap() {
        printArray(map);
    }

    public void printArray(String[][] thing) {
        int lenght = thing[0].length;
        for (String[] strings : thing) {
            for (int j = 0; j < lenght; j++) {
                if (j == lenght - 1) {
                    print(strings[j]);
                } else {
                    System.out.print(strings[j] + "-");
                }
            }
        }
    }

    public String[][] giveVision(Entity e) {
        Rotations rot = entityRotationsHashMap.get(e);
        String[][] vision = new String[eyeRange][3];
        int[] position = entityLocations.get(e);
        boolean[] canSee = {true, true, true};
        switch (rot) {
            case UP -> {
                for (int i = 0; i < eyeRange; i++) {
                    for (int j = -1; j < 2; j++) {
                        int[] lookingAt = {position[0] - i, position[1] + j};
                        if(existsInBoard(lookingAt)) allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                        if (canSee[j + 1]) {
                                if (existsInBoard(lookingAt)) {
                                    String symbol = map[lookingAt[0]][lookingAt[1]];
                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (Objects.equals(symbol, "W")) {
                                        canSee[j + 1] = false;
                                    }
                                }
                        } else {
                            if (j != 0 && canSee[1]) {
                                int[] pos_of_it = {lookingAt[0], lookingAt[1] + 1};
                                if (existsInBoard(pos_of_it) && j == -1 && Objects.equals(map[pos_of_it[0]][pos_of_it[1]], "W")) {
                                    vision[eyeRange - (i + 1)][j + 1] = "X";
                                } else {
                                    {
                                        if (existsInBoard(lookingAt)) {
                                            String symbol = map[lookingAt[0]][lookingAt[1]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (Objects.equals(symbol, "W")) {
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
                        if(existsInBoard(lookingAt)) allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                        if (canSee[j + 1]) {
                            {
                                if (existsInBoard(lookingAt)) {
                                    String symbol = map[lookingAt[0]][lookingAt[1]];
                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (Objects.equals(symbol, "W")) {
                                        canSee[j + 1] = false;
                                    }
                                }
                            }
                        } else {
                            if (j != 0 && canSee[1]) {
                                int[] pos_of_it = {lookingAt[0] + 1, lookingAt[1]};
                                if (existsInBoard(pos_of_it) && j == -1 && Objects.equals(map[pos_of_it[0]][pos_of_it[1]], "W")) {
                                    vision[eyeRange - (i + 1)][j + 1] = "X";
                                } else {
                                    {
                                        if (existsInBoard(lookingAt)) {
                                            String symbol = map[lookingAt[0]][lookingAt[1]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (Objects.equals(symbol, "W")) {
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
                        if(existsInBoard(lookingAt))  allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                        if (canSee[j + 1]) {
                            {
                                if (existsInBoard(lookingAt)) {
                                    String symbol = map[lookingAt[0]][lookingAt[1]];
                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (Objects.equals(symbol, "W")) {
                                        canSee[j + 1] = false;
                                    }
                                }
                            }
                        } else {
                            if (j != 0 && canSee[1]) {
                                int[] pos_of_it = {lookingAt[0] - 1, lookingAt[1]};
                                if (existsInBoard(pos_of_it) && j == -1 && Objects.equals(map[pos_of_it[0]][pos_of_it[1]], "W")) {
                                    vision[eyeRange - (i + 1)][j + 1] = "X";
                                } else {
                                    {
                                        if (existsInBoard(lookingAt)) {
                                            String symbol = map[lookingAt[0]][lookingAt[1]];
                                            vision[eyeRange - (i + 1)][j + 1] = symbol;
                                            if (Objects.equals(symbol, "W")) {
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
                            if(existsInBoard(lookingAt))  allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                            if (canSee[j + 1]) {
                                {
                                    String symbol = map[lookingAt[0]][lookingAt[1]];
                                    vision[eyeRange - (i + 1)][j + 1] = symbol;
                                    if (Objects.equals(symbol, "W")) {
                                        canSee[j + 1] = false;
                                    }
                                }
                            } else {
                                if (j != 0 && canSee[1]) {
                                    int[] pos_of_it = {lookingAt[0], lookingAt[1] - 1};
                                    if (existsInBoard(pos_of_it) && j == -1 && Objects.equals(map[pos_of_it[0]][pos_of_it[1]], "W")) {
                                        vision[eyeRange - (i + 1)][j + 1] = "X";
                                    } else {
                                        {
                                            String symbol = map[lookingAt[0]][lookingAt[1]];
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
                    }
                }
            }

        }
        if(PRINTVISION)printArray(vision);
        return vision;
    }
    private void updateAgentDisplay(Entity e){
        int[] xy=entityLocations.get(e);
        removeFromMap(xy);
        putOnMap(symbol(e),xy);
    }


    private int executeMove(Entity e, Moves m) {
        Rotations rotation = entityRotationsHashMap.get(e);

        switch (m) {
            case STUCK -> {
                return 1;
            }
            case TURN_LEFT -> {
                switch (rotation) {
                    case DOWN -> {
                        entityRotationsHashMap.put(e, Rotations.RIGHT);
                        updateAgentDisplay(e);
                        return 1;
                    }
                    case LEFT -> {
                        entityRotationsHashMap.put(e, Rotations.DOWN);
                        updateAgentDisplay(e);
                        return 1;

                    }
                    case RIGHT -> {
                        entityRotationsHashMap.put(e, Rotations.UP);
                        updateAgentDisplay(e);
                        return 1;
                    }
                    case UP -> {
                        entityRotationsHashMap.put(e, Rotations.LEFT);
                        updateAgentDisplay(e);
                        return 1;
                    }
                }
            }
            case TURN_RIGHT -> {
                switch (rotation) {
                    case DOWN -> {
                        entityRotationsHashMap.put(e, Rotations.LEFT);
                        updateAgentDisplay(e);
                        return 1;
                    }
                    case LEFT -> {
                        entityRotationsHashMap.put(e, Rotations.UP);
                        updateAgentDisplay(e);
                        return 1;
                    }
                    case RIGHT -> {
                        entityRotationsHashMap.put(e, Rotations.DOWN);
                        updateAgentDisplay(e);
                        return 1;
                    }
                    case UP -> {
                        entityRotationsHashMap.put(e, Rotations.RIGHT);
                        updateAgentDisplay(e);
                        return 1;
                    }
                }
            }
            case TURN_AROUND -> {
                switch (rotation) {
                    case DOWN -> {
                        entityRotationsHashMap.put(e, Rotations.UP);
                        updateAgentDisplay(e);
                        return 1;
                    }
                    case LEFT -> {
                        entityRotationsHashMap.put(e, Rotations.RIGHT);
                        updateAgentDisplay(e);
                        return 1;
                    }
                    case RIGHT -> {
                        entityRotationsHashMap.put(e, Rotations.LEFT);
                        updateAgentDisplay(e);

                        return 1;
                    }
                    case UP -> {
                        entityRotationsHashMap.put(e, Rotations.DOWN);
                        updateAgentDisplay(e);
                        return 1;
                    }
                }
            }
            case WALK -> {
                int[] pos = entityLocations.get(e);
                switch (rotation) {
                    case UP -> {
                        int[] targetlocation = {pos[0] - walkSpeed, pos[1]};
                            if (canBePutThere(targetlocation) && noWallsOnTheWay(pos, targetlocation, rotation)) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                entityLocations.put(e, targetlocation);
                                return walkSpeed;
                            } else {
                                if (walkSpeed > 1) {
                                    for (int i = walkSpeed; i > 0; i--) {
                                        int[] nexttargetlocation = {pos[0] - i, pos[1]};
                                        if (existsInBoard(nexttargetlocation)) {
                                            if (canBePutThere(nexttargetlocation) && noWallsOnTheWay(pos, nexttargetlocation, rotation)) {
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
                        int[] targetlocation = {pos[0] + walkSpeed, pos[1]};
                            if (canBePutThere(targetlocation)&&noWallsOnTheWay(pos,targetlocation,rotation)) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                entityLocations.put(e, targetlocation);
                                return walkSpeed;
                            } else {
                                if(walkSpeed>1){
                                    for(int i=walkSpeed;i>0;i--){
                                        int[] nexttargetlocation = {pos[0] + i, pos[1]};
                                        if(existsInBoard(nexttargetlocation)){
                                            if(canBePutThere(nexttargetlocation)&&noWallsOnTheWay(pos,nexttargetlocation,rotation)){
                                                putOnMap(symbol(e), nexttargetlocation);
                                                removeFromMap(pos);
                                                entityLocations.put(e, nexttargetlocation);
                                                return i;
                                            }
                                        }

                                    }
                                }else return -1;
                            }
                        return -1;
                    }
                    case RIGHT -> {
                        int[] targetlocation = {pos[0], pos[1] + walkSpeed};
                            if (canBePutThere(targetlocation)&&noWallsOnTheWay(pos,targetlocation,rotation)) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                entityLocations.put(e, targetlocation);
                                return walkSpeed;
                             }else {
                                if(walkSpeed>1){
                                    for(int i=walkSpeed;i>0;i--){
                                        int[] nexttargetlocation = {pos[0] , pos[1]+i};
                                        if(existsInBoard(nexttargetlocation)){
                                            if(canBePutThere(nexttargetlocation)&&noWallsOnTheWay(pos,nexttargetlocation,rotation)){
                                                putOnMap(symbol(e), nexttargetlocation);
                                                removeFromMap(pos);
                                                entityLocations.put(e, nexttargetlocation);
                                                return i;
                                            }
                                        }

                                    }
                                }else return -1;
                            }
                       return -1;
                    }
                    case LEFT -> {
                        int[] targetlocation = {pos[0], pos[1] - walkSpeed};
                            if (canBePutThere(targetlocation)&&noWallsOnTheWay(pos,targetlocation,rotation)) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                entityLocations.put(e, targetlocation);
                                return walkSpeed;
                             }else {
                                if(walkSpeed>1){
                                    for(int i=walkSpeed;i>0;i--){
                                        int[] nexttargetlocation = {pos[0], pos[1]-i};
                                        if(existsInBoard(nexttargetlocation)){
                                            if(canBePutThere(nexttargetlocation)&&noWallsOnTheWay(pos,nexttargetlocation,rotation)){
                                                putOnMap(symbol(e), nexttargetlocation);
                                                removeFromMap(pos);
                                                entityLocations.put(e, nexttargetlocation);
                                                return i;
                                            }
                                        }

                                    }
                                }else return -1;
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
        int[] yx = {h, l};
        entityLocations.put(e, yx);
        entityRotationsHashMap.put(e, rot);
        putOnMap(symbol(e), h, l);
    }
    public void addObject(ObjectOnMap o) {
        objects.add(o);
        int[] yx = o.getXy();
        objectsLocations.put(o, yx);
        putObjectOnMap(o);
    }
    public void putObjectOnMap(ObjectOnMap o){
        String symbol=o.getSymbol();
        int[] firstCorner=o.getFirstCorner();
        int[] secondCorner = o.getSecondCorner();
        int yGrow=(secondCorner[0]-firstCorner[0]);
        int xGrow=(secondCorner[1]-firstCorner[1]);
        int yFix;
        int xFix;
        if(yGrow>0){yFix=1;}else yFix=-1;
        if(xGrow>0){xFix=1;}else xFix=-1;
        for(int i=0;i<=Math.abs(yGrow);i++){
            for(int j=0;j<=Math.abs(xGrow);j++){
                putOnMap(symbol,firstCorner[0]+i*yFix,firstCorner[1]+j*xFix);
            }
        }


    }

    public int getEntitiesSize()
    {
        return entities.size();
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
        Rotations rot = entityRotationsHashMap.get(e);
        String addition="ERROR";
        switch(rot){
            case DOWN ->addition="d";
            case UP -> addition="^";
            case LEFT -> addition="<";
            case RIGHT -> addition=">";
        }
        if (e.getType() == EntityType.EXPLORER) {
            return "E"+addition;
        }
        return "ERROR";
    }

    private boolean canBePutThere(int []target) {
        return Objects.equals(map[target[0]][target[1]], " ")&&(target[0] > -1 &&target[0] < mapHeight && target[1] > -1 && target[1] < mapLength);
    }

    private boolean existsInBoard(int[] pos) {
        return (pos[0] > -1 && pos[0] < mapHeight && pos[1] > -1 && pos[1] < mapLength);
    }

    private int coordsToNumber(int h, int l) {
        return ((h * mapLength) + l);
    }

    private int coordsToNumber(int[] yx) {
        return ((yx[0] * mapLength) + yx[1]);
    }

    private void checkWin() {
        if (allUnseenTiles.isEmpty()) isRunning = false;
    }
}
