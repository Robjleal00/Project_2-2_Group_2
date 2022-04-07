package Logic;

import Entities.Entity;
import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;

import java.util.*;

import Config.*;
import ObjectsOnMap.ObjectOnMap;
import ObjectsOnMap.Teleporter;
import javafx.application.Platform;
import org.openjfx.UI.MainApp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private  HashMap<Entity, Integer> moveMap;
    private Map<Entity, Moves> queuedMoves;
    private HashMap<Entity,Pose> entityInitialPoses;
    private ArrayList<Integer> allUnseenTiles;
    private boolean PRINTMAPPINGS ;
    public boolean PRINTVISION;
    public boolean GUI;
    private boolean DEBUG_EPXLO;
    private int maxExploNum;
    private int walkSpeed;
    private MainApp graphicsUpdater;
    private final int MAX_TURNS=1000;

    public GameController(int height, int length,MainApp graphics) {
        moveMap=new HashMap<>();
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
        Config con = new Config();
        PRINTMAPPINGS=con.PRINTMIND;
        PRINTVISION=con.PRINTVISION;
        this.GUI=true;
        DEBUG_EPXLO=con.DEBUG_EXPLO;
        this.maxExploNum=allUnseenTiles.size();
        this.graphicsUpdater=graphics;
        entityInitialPoses=new HashMap<>();
    }
    public GameController(int height, int length) {
        moveMap=new HashMap<>();
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
        this.GUI=false;
        DEBUG_EPXLO=con.DEBUG_EXPLO;
        entityInitialPoses=new HashMap<>();
    }
    public GameController() {
    }
    public ArrayList<Entity> getEntities(){
        return entities;
    }
    public void addVars(Variables vr){
        this.walkSpeed=vr.walkSpeed();
        this.eyeRange=vr.eyeRange();
    }
    public void init() throws InterruptedException {
        this.maxExploNum=allUnseenTiles.size();
        boolean wasBroken=false;
        int turns = 0;
        while (isRunning) {//gameloop
            boolean allBroken=false;
            if(GUI){
                Platform.runLater(() -> graphicsUpdater.update(map));
            }
            else{
                print("------------------------");
                printMap();
            }
            //.forEach((k,v)->v==Moves.WALK ? k.walk(executeMove(k,v)):k.nothing(executeMove(k,v)));
            entities.stream().collect(Collectors.toMap(Function.identity(),Entity ::getMove,(o1,o2)->o1,ConcurrentHashMap::new)).forEach((k,v)->moveMap.put(k,executeMove(k,v)));
            moveMap.entrySet().stream().filter(e->e.getValue()>0).forEach(e->e.getKey().walk(e.getValue()));
                           // .entrySet().stream()
                            //.filter(e->executeMove(e.getKey(),e.getValue())!=-1).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue))
                            //.entrySet().stream().filter(e->e.getValue()==Moves.WALK).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue))
                            //.forEach((k,v)->k.walk(executeMove(k,v)));
            System.out.println(queuedMoves);
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
            turns++;
            checkWin(turns);
             Thread.sleep(200);
        }
        if(wasBroken){
            System.out.println("EXPLORATION WAS CANCELLED DUE TO ALL AGENTS GETTING STUCK ");
            int currentProgress=maxExploNum-allUnseenTiles.size();
            System.out.println(" THEY EXPLORED ABOUT "+(int)(currentProgress*100/maxExploNum) +"% IN "+turns+" TURNS" );
        }
        if(PRINTMAPPINGS){
            for (Entity e : entities) {
                e.showMeWhatUSaw();
            }
        }
        int currentProgress=maxExploNum-allUnseenTiles.size();
        System.out.println(" THEY EXPLORED ABOUT "+(int)(currentProgress*100/maxExploNum) +"% IN "+turns+" TURNS" );
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
    public boolean noWallsOnTheWay(int[]pos,int[]target,Rotations rot){
        switch(rot){
            case LEFT -> {
                int length=pos[0]-target[0];
                for(int i=1;i<=length;i++){
                    int[] nextTarget={pos[0]-i,pos[1]};
                    if(!canBePutThere(nextTarget))return false;
                }
                return true;
            }
            case RIGHT -> {
                int length=target[0]-pos[0];
                for(int i=1;i<=length;i++){
                    int[] nextTarget={pos[0]+i,pos[1]};
                    if(!canBePutThere(nextTarget))return false;
                }
                return true;
            }
            case DOWN -> {
                int length=target[1]-pos[1];
                for(int i=1;i<=length;i++){
                    int[] nextTarget={pos[0],pos[1]+i};
                    if(!canBePutThere(nextTarget))return false;
                }
                return true;
            }
            case UP -> {
                int length=pos[1]-target[1];
                for(int i=1;i<=length;i++){
                    int[] nextTarget={pos[0],pos[1]-i};
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
        Arrays.stream(thing).forEach(this::printRow);
    }
    public void printRow(String[] row) {
        print(Arrays.stream(row).toList().stream().collect(Collectors.joining("-")));

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
                        int[] lookingAt = {position[0] +j, position[1] -i};
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
                                int[] pos_of_it = {lookingAt[0]+1, lookingAt[1]};
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
                        if(!vision[eyeRange - (i + 1)][j + 1].contains("X")){
                            if(!vision[eyeRange - (i + 1)][j + 1].contains("W")){
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
                                int[] pos_of_it = {lookingAt[0], lookingAt[1]+1};
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
                        if(!vision[eyeRange - (i + 1)][j + 1].contains("X")){
                            if(!vision[eyeRange - (i + 1)][j + 1].contains("W")){
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
                                int[] pos_of_it = {lookingAt[0] , lookingAt[1]-1};
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
                        if(!vision[eyeRange - (i + 1)][j + 1].contains("X")){
                            if(!vision[eyeRange - (i + 1)][j + 1].contains("W")){
                                allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                            }
                        }
                    }
                }
            }
            case DOWN -> {
                for (int i = 0; i < eyeRange; i++) {
                    for (int j = -1; j < 2; j++) {
                        int[] lookingAt = {position[0] -j, position[1] +i};
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
                                    int[] pos_of_it = {lookingAt[0]-1, lookingAt[1]};
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
                        if(!vision[eyeRange - (i + 1)][j + 1].contains("X")){
                            if(!vision[eyeRange - (i + 1)][j + 1].contains("W")){
                                allUnseenTiles.remove((Object) coordsToNumber(lookingAt));
                            }
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
    private boolean equal(int[]a, int[]b){
        return(a[0]==b[0]&&a[1]==b[1]);
    }
    private Teleporter findTeleporter(int[]pos){
       int [] first = {pos[0],pos[1]+1};
        int [] second = {pos[0],pos[1]-1};
        int [] third = {pos[0]+1,pos[1]};
        int [] fourth = {pos[0]-1,pos[1]};
        ArrayList<int[]>poss = new ArrayList<>();
        poss.add(first);
        poss.add(second);
        poss.add(third);
        poss.add(fourth);
        for(ObjectOnMap ob : objects){
            int[]position = objectsLocations.get(ob);
            for(int[] p :poss){
                if(equal(p,position)&&ob instanceof Teleporter)return (Teleporter) ob;
            }
        }
        return null;
    }
    private int executeMove(Entity e, Moves m) {
        Rotations rotation = entityRotationsHashMap.get(e);
        switch (m) {
            case USE_TELEPORTER -> {
                //System.out.println("HE WANTS TO USE IT");
                int[] pos =entityLocations.get(e);
                Teleporter tp = findTeleporter(pos);

                if(tp==null)return -1;
                else{
                    int[]target=tp.getTarget();
                    if(canBePutThere(target)) {
                        e.setPosition(entityInitialPoses.get(e).newPosition(target));
                        removeFromMap(pos);
                        putOnMap(symbol(e),target);
                        putOnMap("M1",pos);
                        entityLocations.put(e,target);
                        return 0;
                    }
                    else return -1;
                }
            }
            case STUCK -> {
                return 0;
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
                int[] pos = entityLocations.get(e);
                switch (rotation) {
                    case UP -> {
                        int[] targetlocation = {pos[0] , pos[1]- walkSpeed};
                            if (canBePutThere(targetlocation) && noWallsOnTheWay(pos, targetlocation, rotation)) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                entityLocations.put(e, targetlocation);
                                return walkSpeed;
                            } else {
                                if (walkSpeed > 1) {
                                    for (int i = walkSpeed; i > 0; i--) {
                                        int[] nexttargetlocation = {pos[0] , pos[1]- i};
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
                        int[] targetlocation = {pos[0] , pos[1]+ walkSpeed};
                            if (canBePutThere(targetlocation)&&noWallsOnTheWay(pos,targetlocation,rotation)) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                entityLocations.put(e, targetlocation);
                                return walkSpeed;
                            } else {
                                if(walkSpeed>1){
                                    for(int i=walkSpeed;i>0;i--){
                                        int[] nexttargetlocation = {pos[0] , pos[1]+ i};
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
                        int[] targetlocation = {pos[0]+ walkSpeed, pos[1] };
                            if (canBePutThere(targetlocation)&&noWallsOnTheWay(pos,targetlocation,rotation)) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                entityLocations.put(e, targetlocation);
                                return walkSpeed;
                             }else {
                                if(walkSpeed>1){
                                    for(int i=walkSpeed;i>0;i--){
                                        int[] nexttargetlocation = {pos[0]+i , pos[1]};
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
                        int[] targetlocation = {pos[0]- walkSpeed, pos[1] };
                            if (canBePutThere(targetlocation)&&noWallsOnTheWay(pos,targetlocation,rotation)) {
                                putOnMap(symbol(e), targetlocation);
                                removeFromMap(pos);
                                entityLocations.put(e, targetlocation);
                                return walkSpeed;
                             }else {
                                if(walkSpeed>1){
                                    for(int i=walkSpeed;i>0;i--){
                                        int[] nexttargetlocation = {pos[0]-i, pos[1]};
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
        entityInitialPoses.put(e,new Pose(rot,yx));
    }
    public void addObject(ObjectOnMap o) {
        objects.add(o);
        int[] yx = o.getXy();
        objectsLocations.put(o, yx);
        putObjectOnMap(o);
    }
    public void putObjectOnMap(ObjectOnMap o){
        String symbol=o.getSymbol();
        putOnMap(o.getSymbol(),o.getXy());

    }
    public int getEntitiesSize() {return entities.size();}
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
    public void addWall(int y0, int x0, int y1, int x1){
        int ySpan=y1-y0;
        int xSpan=x1-x0;
        if(xSpan>0){//x goes up
            if(ySpan>0){//Y goes up
                for(int y=y0;y<y1;y++){
                    for(int x=x0;x<x1;x++){
                        putOnMap("W",x,y);
                        allUnseenTiles.remove((Object) coordsToNumber(x,y));
                    }
                }

            }
            else{//Y goes down
                for(int y=y0;y>=y1;y--){
                    for(int x=x0;x<=x1;x++){
                        putOnMap("W",x,y);
                        allUnseenTiles.remove((Object) coordsToNumber(x,y));
                    }
                }
            }
        }
        else{//x goes down
            if(ySpan>0){//Y goes up
                for(int y=y0;y<=y1;y++){
                    for(int x=x0;x>=x1;x--){
                        putOnMap("W",x,y);
                        allUnseenTiles.remove((Object) coordsToNumber(x,y));
                    }
                }
            }
            else{//Y goes down
                for(int y=y0;y>=y1;y--){
                    for(int x=x0;x>=x1;x--){
                        putOnMap("W",x,y);
                        allUnseenTiles.remove((Object) coordsToNumber(x,y));
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
        if(target[0] > -1 &&target[0] < mapHeight && target[1] > -1 && target[1] < mapLength)return Objects.equals(map[target[1]][target[0]], " ");
        else return false;
    }
    private boolean existsInBoard(int[] pos) {
        return (pos[0] > -1 && pos[0] < mapHeight && pos[1] > -1 && pos[1] < mapLength);
    }
    private int coordsToNumber(int h, int l) {
        return (h + l*mapHeight);
    }
    private int coordsToNumber(int[] yx) {
        return ((yx[0] ) + yx[1]* mapHeight);
    }
    private void checkWin(int turns) {
        if (allUnseenTiles.isEmpty()) isRunning = false;
        if(turns>MAX_TURNS) isRunning=false;
    }
}
