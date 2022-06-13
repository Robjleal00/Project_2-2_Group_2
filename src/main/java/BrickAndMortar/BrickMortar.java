package BrickAndMortar;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;

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

        // Check LEFT
        if(map[xy[0] - 1][xy[1]] == unexplored){

        }
        // Check RIGHT
        else if(map[xy[0] + 1][xy[1]] == unexplored){

        }
        // Check UP
        else if(map[xy[0]][xy[1] + 1] == unexplored){

        }
        // Check DOWN
        else if(map[xy[0]][xy[1] - 1] == unexplored){

        }

        // NOTE ELSE IF: else if at least one of the four cells around is explored
        // Check LEFT
        if(map[xy[0] - 1][xy[1]] == explored){

        }
        // Check RIGHT
        else if(map[xy[0] + 1][xy[1]] == explored){

        }
        // Check UP
        else if(map[xy[0]][xy[1] + 1] == explored){

        }
        // Check DOWN
        else if(map[xy[0]][xy[1] - 1] == explored){

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
