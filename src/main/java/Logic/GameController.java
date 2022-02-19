package Logic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class GameController {
    private HashMap map;
    private ArrayList walls;
    private int mapLength;
    private int mapHeight;

    public GameController(int height, int length){
        this.walls=new ArrayList();
        this.map=makeMap(height,length);
        this.mapLength=length;
        this.mapHeight=height;
    }
    private HashMap makeMap(int height, int length){
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
        for(int i=1;i<=height;i++){
            for(int j=1;j<=length;j++){
                int number = ((i-1)*length)+j;
                map.put(number,new ArrayList<Integer>());
                if (number>length){  // can add top
                    map.get(number).add(number-length);
                }
                if(number<=length*(height-1)){  //can add bottom
                    map.get(number).add(number+length);
                }
                if(j!=length){  //can add right
                    map.get(number).add(number+1);
                }
                if(j!=1){  //can add left
                    map.get(number).add(number-1);
                }
            }
        }
        makeBorders(length,height);
        return map;
    }
    public void printMap(){

        for( int i=1; i<=mapHeight;i++){
            for(int j=1;j<=mapLength;j++){
                int number = ((i-1)*mapLength)+j;
                if (j==mapLength){
                    print(symbol(number));
                }
                else{

                    System.out.print(symbol(number)+"-");
                }
            }
        }
    }
    public void print(String s){
        System.out.println(s);
    }
    public void print(int s){
        System.out.println(s);
    }
    public void makeBorders(int length,int height){
        //get top and bot wall
        for(int i=1;i<=length;i++){
            walls.add(i);
            walls.add(i+((height-1)*length));
        }
        //left and right wall
        for(int j=1;j<=height;j++){
            walls.add(((j-1)*length)+1);
            walls.add(j*length);
        }

    }
    public String symbol(int i){
        if (walls.contains(i)){
            return"W";
        }
        else return" ";
    }

}
