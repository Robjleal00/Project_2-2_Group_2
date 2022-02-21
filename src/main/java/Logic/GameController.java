package Logic;

import Entities.Entity;
import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;

import java.util.ArrayList;
import java.util.HashMap;

public class GameController {
    private String [][]map;
    private int mapLength;
    private int mapHeight;
    private boolean isRunning;
    private int eyeRange;
    private ArrayList<Entity> entities;
    private HashMap<Entity,int[]> locations;
    private HashMap<Entity, Rotations> entityRotationsHashMap;
    private HashMap<Entity, Moves> queuedMoves;

    public GameController(int height, int length,int eyeRange){
        this.map=makeMap(height,length);
        this.mapLength=length;
        this.mapHeight=height;
       makeBorders(length,height);
       isRunning=true;
       this.eyeRange=eyeRange;
       entities= new ArrayList<Entity>();
       locations=new HashMap<Entity,int[]>();
       entityRotationsHashMap= new HashMap<Entity,Rotations>();
       queuedMoves = new HashMap<Entity,Moves>();

    }
    public void init() throws InterruptedException {
        while(isRunning){//gameloop
            for(Entity e : entities){
            Moves currentMove = e.getMove();
            queuedMoves.put(e,currentMove);
            }
            for(Entity e:entities){
                executeMove(e,queuedMoves.get(e));
            }
            printMap();
            Thread.sleep(200);
        }
    }
    private String[][] makeMap(int height, int length){
        String[][] mappy=new String[height][length];
        for(int i =0;i<height;i++){
            for(int j=0;j<length;j++){
               mappy[i][j]=" ";
            }
        }
        return mappy;
    }
    public void printMap(){
        printArray(map);
        print("------------------------");
    }
    public void printArray(String[][] thing){
        int height = thing.length;
        int lenght = thing[0].length;
        for(int i =0;i<height;i++){
            for(int j=0;j<lenght;j++){
                if(j==lenght-1){
                    print(thing[i][j]);
                }
                else {
                    System.out.print(thing[i][j]+"-");
                }
            }
        }
    }
    public String[][] giveVision(Entity e){
        Rotations rot = entityRotationsHashMap.get(e);
        String [][] vision = new String [eyeRange][3];
        int[] position = locations.get(e);
        boolean canSee[]={true,true,true};
        if(rot==Rotations.UP){
            for(int i=eyeRange-1;i!=-1;i--){
                for(int j=-1;j<2;j++){
                    if(canSee[j+1]){
                        int[] lookingAt={position[0]-(eyeRange-(i+1)),position[1]+j};{
                            if(existsInBoard(lookingAt)){
                                String symbol=map[lookingAt[0]][lookingAt[1]];
                                vision[i][j+1]= symbol;
                                if(symbol=="W"){
                                    canSee[j+1]=false;
                                }
                            }
                        }
                    }
                    else{
                        vision[i][j+1]= "X";
                    }
                }
            }
        }
        return vision;
    }
    private void executeMove(Entity e, Moves m){
        Rotations rotation = entityRotationsHashMap.get(e);
            switch(m){
                case TURN_LEFT ->{
                    switch(rotation){
                        case DOWN -> {
                            entityRotationsHashMap.put(e,Rotations.RIGHT);
                        }
                        case LEFT -> {
                            entityRotationsHashMap.put(e,Rotations.DOWN);
                        }
                        case RIGHT -> {
                            entityRotationsHashMap.put(e,Rotations.UP);
                        }
                        case UP -> {
                            entityRotationsHashMap.put(e,Rotations.LEFT);
                        }

                    }
                }
                case TURN_RIGHT -> {
                    switch(rotation){
                        case DOWN -> {
                            entityRotationsHashMap.put(e,Rotations.LEFT);
                        }
                        case LEFT -> {
                            entityRotationsHashMap.put(e,Rotations.UP);
                        }
                        case RIGHT -> {
                            entityRotationsHashMap.put(e,Rotations.DOWN);
                        }
                        case UP -> {
                            entityRotationsHashMap.put(e,Rotations.RIGHT);
                        }

                    }

                }
                case TURN_AROUND -> {
                    switch(rotation){
                        case DOWN -> {
                            entityRotationsHashMap.put(e,Rotations.UP);
                        }
                        case LEFT -> {
                            entityRotationsHashMap.put(e,Rotations.RIGHT);
                        }
                        case RIGHT -> {
                            entityRotationsHashMap.put(e,Rotations.LEFT);
                        }
                        case UP -> {
                            entityRotationsHashMap.put(e,Rotations.DOWN);
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
                                    locations.put(e,targetlocation);
                                }

                            }
                        }
                        case DOWN -> {
                            int[] targetlocation = {pos[0] + 1, pos[1]};
                            if (existsInBoard(targetlocation)) {
                                if (canBePutThere(targetlocation[0], targetlocation[1])) {
                                    putOnMap(symbol(e), targetlocation);
                                    removeFromMap(pos);
                                    locations.put(e,targetlocation);
                                }

                            }
                        }
                        case RIGHT -> {
                            int[] targetlocation = {pos[0], pos[1] + 1};
                            if (existsInBoard(targetlocation)) {
                                if (canBePutThere(targetlocation[0], targetlocation[1])) {
                                    putOnMap(symbol(e), targetlocation);
                                    removeFromMap(pos);
                                    locations.put(e,targetlocation);
                                }
                            }

                        }
                        case LEFT -> {
                            int[] targetlocation = {pos[0], pos[1] - 1};
                            if (existsInBoard(targetlocation)) {
                                if (canBePutThere(targetlocation[0], targetlocation[1])) {
                                    putOnMap(symbol(e), targetlocation);
                                    removeFromMap(pos);
                                    locations.put(e,targetlocation);
                                }

                            }

                        }
                    }

                }
            }
        }


    public void addEntity(Entity e, int h, int l,Rotations rot) {
        entities.add(e);
        int[] xyz = {h,l};
        locations.put(e,xyz);
        entityRotationsHashMap.put(e,rot);
        putOnMap(symbol(e),h,l);
    }
    private void print(String s){
        System.out.println(s);
    }
    private void print(int s){
        System.out.println(s);
    }
    public void makeBorders(int length,int height){
        for(int i=0;i<length;i++){
            putOnMap("W",0,i);
            putOnMap("W",height-1,i);
        }
        for(int j=0;j<height;j++){
            putOnMap("W",j,0);
            putOnMap("W",j,length-1);
        }
    }
    private void putOnMap(String s,int[]xyz){
        map[xyz[0]][xyz[1]]=s;
    }
    private void putOnMap(String s,int h,int l){
        map[h][l]=s;
    }
    private void removeFromMap(int h, int l){
        map[h][l]=" ";
    }
    private void removeFromMap(int []xyz){
        map[xyz[0]][xyz[1]]=" ";
    }
    private String symbol(Entity e){
        if(e.getType()== EntityType.EXPLORER){
        return "E";
        }
        return "ERROR";
    }
    private boolean canBePutThere(int h, int l){
        return map[h][l]==" ";
    }
    private boolean existsInBoard(int[] pos){
        return(pos[0]>-1 && pos[0]<mapHeight && pos[1]>-1 && pos[1]<mapLength);
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
