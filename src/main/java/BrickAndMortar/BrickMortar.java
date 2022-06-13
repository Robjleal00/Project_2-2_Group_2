package BrickAndMortar;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;

import java.util.ArrayList;
import java.util.Arrays;

public class BrickMortar {

    int unexplored = 0;
    int explored = 1;
    int visited = 2;
    int walls = 3;


    private final int mapLength;
    private final int mapHeight;

    private int [][]map;
    private int []xy;
    public ArrayList<int[]> unexploredNeighbours = new ArrayList<>();;

    private boolean exploDone;
    private final double randomness = 0.2;
    private final Rotations rot;



    public BrickMortar(int[][] map, Rotations rot){
        this.map = map;
        this.exploDone = false;
        this.rot = rot;
        this.mapHeight = map.length;
        this.mapLength = map[0].length;
    }


    public Moves brickAndMortar(int[] xy)
    {
        //Marking Step:
        //
        if(isBlockingPath(xy) == false)
        {
            map[xy[0]][xy[1]] = visited;
        }
        else
        {
            map[xy[0]][xy[1]] = explored;
        }

        //Navigation Step:
        //NOTE: IF AT LEAST ONE OF THE 4 SURROUNDING CELLS IS UNEXPLORED
        if(checkUnexploredSurroundings(xy))
        {
            //for each of the unexplored cells see how many walls
            //or visited cells are around it, then go to the cell with
            //most of them, which is most likely to be marked as
            //visited in the marking step
            for(int i = 0; i < unexploredNeighbours.size(); i++)
            {

            }
        }
    }

    /**
     *
     * @param xy Position of the cell
     * @return
     */
    public boolean isBlockingPath(int[] xy)
    {
        return null;
    }

    //This would probably only be called when the intruder first spawns and when it uses a
    //teleporter, since it usually knows the way it came from, and can see to its left and right
    //Interesting research question how this performs when we cut down its vision to only straight
    public boolean checkUnexploredSurroundings(int[] xy)
    {
        getUnexploredNeighbours(xy);
        if(!unexploredNeighbours.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public int countWallsAndVisited(int[] xy)
    {
        int count = 0;
        // for every wall and visited cell add to count
        return count;
    }

    public void getUnexploredNeighbours(int[] xy)
    {

        unexploredNeighbours.clear();
        //if up down left right is unexplored blah blah blah
        //unexploredNeighbours = new ArrayList<>();

    }
    public void simulateVision(Rotations rot, int[] xy, Variables vr)
    {
        int explored = 1;
        int walls = 3;
        // O = Unexplored
        // 1 = Explored
        // 2 = Visited
        // 3 = Wall


        int x = xy[0];
        int y = xy[1];
        int range = vr.eyeRange();
        boolean[] blocked = new boolean[3];
        // System.out.println(range);

        String[][] returner = new String[range][3];

        for (int j = 0; j < range; j++) { // upfront
            for (int i = -1; i < 2; i++) { // sideways
                int sX = range - 1 - j;
                int sY = i + 1;
                switch (rot) {
                    case BACK -> {
                        int xM = x + j;
                        int yM = y - i;
                        if (inBounds(xM, yM)) {
                            if (!blocked[1]) {
                                if(i==-1&&blocked[0]){
                                    if(inBounds(xM,y)){
                                        if (map[xM][y] == walls){
                                            returner[sX][sY]="X";
                                            continue;
                                        }
                                    }
                                }
                                String symbol = map[xM][yM];
                                returner[sX][sY] = symbol;
                                if (symbol.contains("W")) {
                                    blocked[i + 1] = true;
                                }
                            } else {
                                switch (i) {
                                    case -1 -> {
                                        if (blocked[0]) {
                                            returner[sX][sY] = "X";
                                        } else {
                                            String symbol = map[xM][yM];
                                            returner[sX][sY] = symbol;
                                            if (symbol.contains("W")) {
                                                blocked[i + 1] = true;
                                            }
                                        }
                                    }
                                    case 0 -> returner[sX][sY] = "X";
                                    case 1 -> {
                                        if (blocked[2]) {
                                            returner[sX][sY] = "X";
                                        } else {
                                            String symbol = map[xM][yM];
                                            returner[sX][sY] = symbol;
                                            if (symbol.contains("W")) {
                                                blocked[i + 1] = true;
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            returner[sX][sY] = "X";
                        }
                    }
                    case FORWARD -> {
                        int xM = x - j;
                        int yM = y + i;
                        if (inBounds(xM, yM)) {
                            if (!blocked[1]) {
                                if(i==-1&&blocked[0]){
                                    if(inBounds(xM,y)){
                                        if (map[xM][y].contains("W")){
                                            returner[sX][sY]="X";
                                            continue;
                                        }
                                    }
                                }
                                String symbol = map[xM][yM];
                                returner[sX][sY] = symbol;
                                if (symbol.contains("W")) {
                                    blocked[i + 1] = true;
                                }
                            } else {
                                switch (i) {
                                    case -1 -> {
                                        if (blocked[0]) {
                                            returner[sX][sY] = "X";
                                        } else {
                                            String symbol = map[xM][yM];
                                            returner[sX][sY] = symbol;
                                            if (symbol.contains("W")) {
                                                blocked[i + 1] = true;
                                            }
                                        }
                                    }
                                    case 0 -> returner[sX][sY] = "X";
                                    case 1 -> {
                                        if (blocked[2]) {
                                            returner[sX][sY] = "X";
                                        } else {
                                            String symbol = map[xM][yM];
                                            returner[sX][sY] = symbol;
                                            if (symbol.contains("W")) {
                                                blocked[i + 1] = true;
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            returner[sX][sY] = "X";
                        }
                    }
                    case RIGHT -> {
                        int xM = x + i;
                        int yM = y + j;
                        if (inBounds(xM, yM)) {
                            if (!blocked[1]) {
                                if(i==-1&&blocked[0]){
                                    if(inBounds(x,yM)){
                                        if (map[x][yM].contains("W")){
                                            returner[sX][sY]="X";
                                            continue;
                                        }
                                    }
                                }
                                String symbol = map[xM][yM];
                                returner[sX][sY] = symbol;
                                if (symbol.contains("W")) {
                                    blocked[i + 1] = true;
                                }
                            } else {
                                switch (i) {
                                    case -1 -> {
                                        if (blocked[0]) {
                                            returner[sX][sY] = "X";
                                        } else {
                                            String symbol = map[xM][yM];
                                            returner[sX][sY] = symbol;
                                            if (symbol.contains("W")) {
                                                blocked[i + 1] = true;
                                            }
                                        }
                                    }
                                    case 0 -> returner[sX][sY] = "X";
                                    case 1 -> {
                                        if (blocked[2]) {
                                            returner[sX][sY] = "X";
                                        } else {
                                            String symbol = map[xM][yM];
                                            returner[sX][sY] = symbol;
                                            if (symbol.contains("W")) {
                                                blocked[i + 1] = true;
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            returner[sX][sY] = "X";
                        }
                    }
                    case LEFT -> {
                        int xM = x - i;
                        int yM = y - j;
                        if (inBounds(xM, yM)) {
                            if (!blocked[1]) {
                                if(i==-1&&blocked[0]){
                                    if(inBounds(x,yM)){
                                        if (map[x][yM].contains("W")){
                                            returner[sX][sY]="X";
                                            continue;
                                        }
                                    }
                                }
                                String symbol = map[xM][yM];
                                returner[sX][sY] = symbol;
                                if (symbol.contains("W")) {
                                    blocked[i + 1] = true;
                                }
                            } else {
                                switch (i) {
                                    case -1 -> {
                                        if (blocked[0]) {
                                            returner[sX][sY] = "X";
                                        } else {
                                            String symbol = map[xM][yM];
                                            returner[sX][sY] = symbol;
                                            if (symbol.contains("W")) {
                                                blocked[i + 1] = true;
                                            }
                                        }
                                    }
                                    case 0 -> returner[sX][sY] = "X";
                                    case 1 -> {
                                        if (blocked[2]) {
                                            returner[sX][sY] = "X";
                                        } else {
                                            String symbol = map[xM][yM];
                                            returner[sX][sY] = symbol;
                                            if (symbol.contains("W")) {
                                                blocked[i + 1] = true;
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            returner[sX][sY] = "X";
                        }
                    }
                }
            }
        }
        if (DEBUG_VISION) {
            GameController printer = new GameController();
            printer.printArray(returner);
            System.out.println(Arrays.toString(blocked));
        }
        return returner;
    }


    private boolean inBounds(int x, int y) {
        return (x > -1 && x < mapHeight && y > -1 && y < mapLength);
    }

}
