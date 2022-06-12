package Patrolling;

import Config.Config;
import Config.Variables;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static java.util.Collections.max;



public class Patroller {
    private final Moves[] availableMoves = {Moves.WALK, Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND, Moves.USE_TELEPORTER};
    private final int walkSpeed;
    private final int[] xy;
    private final Rotations rot;
    private final HashMap<int[], int[]> teleporters;
    private final int[][] lastSeen;
    private final int mapLength;
    private final int mapHeight;
    boolean DEBUG_VISION;
    boolean DEBUG_LASTSEEN_ALL;
    boolean DEBUG_DECISION;
    boolean PRINT_ALL_MAPS;
    private final Variables vr;
    private final String[][] map;

    public Patroller(int[] xy, Rotations rot, Variables vr, String[][] map, HashMap<int[], int[]> teleporters, int[][] lastSeen) {
        this.xy = xy;
        this.rot = rot;
        this.vr = vr;
        this.map = map;
        this.teleporters = teleporters;
        this.lastSeen = lastSeen;
        this.mapHeight = map.length;
        this.mapLength = map[0].length;
        this.walkSpeed = vr.walkSpeed();
        Config c = new Config();
        this.DEBUG_DECISION = c.PATROLLING_DECISION;
        this.DEBUG_VISION = c.PATROLLING_VISION;
        this.PRINT_ALL_MAPS = c.PATROLLING_PRINT_ALL;
        this.DEBUG_LASTSEEN_ALL = c.PATROLLING_LASTSEEN;
        //System.out.println("HEIGHT + "+ mapHeight+"    LENGTH + "+mapLength);
    }


    //Node method
    private int dfsRecursive(int depth,int maxDepth, Moves move, int[] xy, Rotations rot, int[][] lastSeen) {
        int[][] myLastSeen=createNewLastSeen(lastSeen);
        incrementLastSeen(myLastSeen);
        int ownValue;
        switch (move) {
            case TURN_AROUND -> rot = rot.turnAround();
            case TURN_LEFT -> rot = rot.turnLeft();
            case TURN_RIGHT -> rot = rot.turnRight();
            case WALK -> xy = walk(xy.clone(), rot);
            case USE_TELEPORTER -> {
                xy = canTeleport(xy, rot);
                if(xy[0]==-1){
                    return -99999;
                }
            }

        }
        int weight = maxDepth-depth;
        String[][] vision = simulateVision(rot, xy, vr);
        // System.out.println("CURRENT MOVE +"+move);
        //GameController printer = new GameController();
        //printer.printArray(vision);

        ownValue = getValue(myLastSeen, xy, rot, vision);
        ownValue = ownValue /weight;
        //System.out.println("MOVE: "+move.toString()+" HAS VALUE: "+ownValue);
        if (depth != 0) {
            ArrayList<Integer> childValues = new ArrayList<>();

            for (Moves availableMove : availableMoves) {
                childValues.add(dfsRecursive(depth - 1,maxDepth, availableMove, xy.clone(), rot, myLastSeen));
            }
            return ownValue + max(childValues);
        } else {
            //Reached the end of the DFS, return the value of current child back up so that the parent can decide
            //which has the highest value
            // return maxValue;
            return ownValue;
        }
    }

    //Root method
    public Moves dfs(int maxDepth,boolean blockWalk) {
        incrementLastSeen(lastSeen);
        ArrayList<Integer> childValues = new ArrayList<>();
        int[][] deepclone = createNewLastSeen(lastSeen);
        for (Moves availableMove : availableMoves) {
            childValues.add(dfsRecursive(maxDepth-1,maxDepth, availableMove, xy.clone(), rot, deepclone));
        }
        if (blockWalk) {
                childValues.set(0,-99999);
        }
        if (DEBUG_DECISION) {
            System.out.println("_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
            System.out.println("DEBUG OF DECISIONS IN PATROLLING");
            System.out.println(childValues);
            System.out.println("_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
        }
        if (DEBUG_LASTSEEN_ALL) {
            GameController printer = new GameController();
            System.out.println("_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
            System.out.println("DEBUG OF LASTSEEN ARRAY IN PATROLLING");
            printer.printIntArray(lastSeen);
            System.out.println("_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
        }
        int maxValue = max(childValues);
        int index = childValues.indexOf(maxValue);
        //System.out.println(availableMoves[index].toString()+" with INDEX:"+index);
        return availableMoves[index];
        //TODO: when you create children, give them a move and let them execute it themselves, less "super" code required
        // ^ Not sure how to implement this, but I believe my implementation should suffice for now
    }

    //Returns the SUM of all seen squares, MUST be called before setSeen
    // Additionally now it also sets all seen square values to 0
    public int getValue(int[][] lastSeen, int[] xy, Rotations rotation, String[][] vision) {
        int x = xy[0];
        int y = xy[1];
        int value = 0;
        int range = vr.eyeRange();
        for (int i = -1; i < 2; i++) { // sideways
            for (int j = 0; j < range; j++) { // upfront
                int sX = range - 1 - j;
                int sY = i + 1;
                switch (rotation) {
                    case BACK -> {
                        int xM = x + j;
                        int yM = y - i;
                        if (inBounds(xM, yM)) {
                            int number = lastSeen[xM][yM];
                            String symbol = vision[sX][sY];
                            if (!symbol.contains("W")&&!symbol.contains("X")) {
                                value += number;
                                if(lastSeen[xM][yM]>0)lastSeen[xM][yM] = 0;
                            }
                        }
                    }
                    case FORWARD -> {
                        int xM = x - j;
                        int yM = y + i;
                        if (inBounds(xM, yM)) {
                            int number = lastSeen[xM][yM];
                            String symbol = vision[sX][sY];
                            if (!symbol.contains("W")&&!symbol.contains("X")) {
                                value += number;
                                if(lastSeen[xM][yM]>0)lastSeen[xM][yM] = 0;
                            }
                        }
                    }
                    case RIGHT -> {
                        int xM = x + i;
                        int yM = y + j;
                        if (inBounds(xM, yM)) {
                            int number = lastSeen[xM][yM];
                            String symbol = vision[sX][sY];
                            if (!symbol.contains("W")&&!symbol.contains("X")) {
                                value += number;
                                if(lastSeen[xM][yM]>0)lastSeen[xM][yM] = 0;
                            }
                        }
                    }
                    case LEFT -> {
                        int xM = x - i;
                        int yM = y - j;
                        if (inBounds(xM, yM)) {
                            int number = lastSeen[xM][yM];
                            String symbol = vision[sX][sY];
                            if (!symbol.contains("W")&&!symbol.contains("X")) {
                                value += number;
                                if(lastSeen[xM][yM]>0)lastSeen[xM][yM] = 0;
                            }
                        }
                    }
                }
            }
        }
        return value;
    }

    /**
     * Creates a new copy of lastSeen to:
     * A) be used to get the value
     * B) increment unseen squares in the next iteration for this specific child node
     *
     * @param lastSeen
     * @return deepclone of lastSeen
     */
    private int[][] createNewLastSeen(int[][] lastSeen) {
        int[][] newLastSeen = new int[lastSeen.length][lastSeen[0].length];

        //Copy values of the lastSeen array
        for (int i = 0; i < lastSeen.length; i++) {
            System.arraycopy(lastSeen[i], 0, newLastSeen[i], 0, lastSeen[0].length);
        }
        return newLastSeen;
    }

    private int[] canTeleport(int[] xy, Rotations rot) {
        for (int[] xyT : teleporters.keySet()) {
            switch (rot) {
                case LEFT -> {
                    if (xyT[0] == xy[0] && xyT[1] == xy[1] - 1) {
                        return teleporters.get(xyT);
                    }
                }
                case RIGHT -> {
                    if (xyT[0] == xy[0] && xyT[1] == xy[1] + 1) {
                        return teleporters.get(xyT);
                    }
                }
                case FORWARD -> {
                    if (xyT[0] == xy[0] - 1 && xyT[1] == xy[1]) {
                        return teleporters.get(xyT);
                    }
                }
                //TODO: CHANGED DOWN TO BACK
                case BACK -> {
                    if (xyT[0] == xy[0] + 1 && xyT[1] == xy[1]) {
                        return teleporters.get(xyT);
                    }
                }
            }
        }
        int b=-1;
        return new int[]{b,b};
    }

    private String[][] simulateVision(Rotations rot, int[] xy, Variables vr) {
        if (DEBUG_VISION) {
            GameController printer = new GameController();
            System.out.println("_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
            System.out.println("DEBUG OF VISION");
            System.out.println("MY CURRENT X is " + xy[0]);
            System.out.println("MY CURRENT Y is " + xy[1]);
            System.out.println("I AM LOOKING " + rot);
            System.out.println("PRINTING MY MAP");
            map[xy[0]][xy[1]] = "O";
            printer.printArray(map);
            map[xy[0]][xy[1]] = " ";
            System.out.println("_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
        }
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

    private void updateLastSeen(String[][] vision, Rotations rot, int[] xy) {
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
                        if (!lookingAt.contains("W") && !lookingAt.contains("X")) {
                            lastSeen[x + i][y + j] = 0;
                        }

                    }
                    case BACK -> {
                        if (!lookingAt.contains("W") && !lookingAt.contains("X")) {
                            lastSeen[x - i][y + j] = 0;
                        }

                    }
                    case LEFT -> {
                        if (!lookingAt.contains("W") && !lookingAt.contains("X")) {
                            lastSeen[x + j][y - i] = 0;
                        }

                    }
                    case RIGHT -> {
                        if (!lookingAt.contains("W") && !lookingAt.contains("X")) {
                            lastSeen[x + j][y + i] = 0;
                        }

                    }


                }
            }
        }
    }

    private int[] walk(int[] xy, Rotations rot) {
        switch (rot) {
            case FORWARD -> { //x decrease
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[0] -= howMuch;
            }
            case BACK -> { //x increase
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[0] += howMuch;
            }

            case RIGHT -> { //y increase
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[1] += howMuch;
            }

            case LEFT -> { //y decrease
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[1] -= howMuch;

            }
        }
        return xy;

    }

    private int howMuchCanIWalk(int[] pos, Rotations rot) {
        switch (rot) {
            case LEFT -> {//y decrease
                for (int i = walkSpeed; i > 0; i--) {
                    int[] targetCell = {pos[0], pos[1] - 1};
                    if (noWallsOnTheWay(pos, targetCell, rot))
                        return i;
                }

            }
            case RIGHT -> {//y increase
                for (int i = walkSpeed; i > 0; i--) {
                    int[] targetCell = {pos[0], pos[1] + 1};
                    if (noWallsOnTheWay(pos, targetCell, rot))
                        return i;
                }
            }
            //REMINDER: Forward doesn't increase Y ANYMORE IT INCREASES X
            case FORWARD -> {//x decrease
                for (int i = walkSpeed; i > 0; i--) {
                    int[] targetCell = {pos[0] - i, pos[1]};
                    if (noWallsOnTheWay(pos, targetCell, rot))
                        return i;
                }
            }
            case BACK -> {//x increase
                for (int i = walkSpeed; i > 0; i--) {
                    int[] targetCell = {pos[0] + i, pos[1]};
                    if (noWallsOnTheWay(pos, targetCell, rot))
                        return i;
                }
            }
        }
        return 0;
    }

    /* Needs to be modified to account for walls
    private boolean noWallsInTheWay(int[]pos,int[]target,Rotations rot){
        switch(rot){
            case FORWARD -> {//y increase
                int distance = target[1]-pos[1];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0])){
                        if(walls.get(pos[0]).contains(pos[1]+i)){
                            return false;
                        }
                    }

                }
            }
            case BACK -> {//y decrease
                int distance = pos[1]-target[1];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0])){
                        if(walls.get(pos[0]).contains(pos[1]-i)){
                            return false;
                        }
                    }

                }
            }
            case RIGHT -> {//x increase
                int distance = target[0]-pos[0];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0]+i)){
                        if(walls.get(pos[0]+i).contains(pos[1])){
                            return false;
                        }
                    }

                }
            }
            case LEFT -> {//x decrease
                int distance = pos[0]-target[0];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0]-i)){
                        if(walls.get(pos[0]-i).contains(pos[1])){
                            return false;
                        }
                    }

                }
            }

        }
        return true;
    }
    */
    private boolean noWallsOnTheWay(int[] pos, int[] target, Rotations rot) {
        switch (rot) {
            case LEFT -> {
                int length = pos[1] - target[1];
                for (int i = 1; i <= length; i++) {
                    int[] nextTarget = {pos[0], pos[1] - i};
                    if (!canBePutThere(nextTarget)) return false;
                }
                return true;
            }
            case RIGHT -> {
                int length = target[1] - pos[1];
                for (int i = 1; i <= length; i++) {
                    int[] nextTarget = {pos[0], pos[1] + i};
                    if (!canBePutThere(nextTarget)) return false;
                }
                return true;
            }
            //TODO: CHANGED BACK/DOWN, FORWARD/UP
            case BACK -> {
                int length = target[0] - pos[0];
                for (int i = 1; i <= length; i++) {
                    int[] nextTarget = {pos[0] + i, pos[1]};
                    if (!canBePutThere(nextTarget)) return false;
                }
                return true;
            }
            case FORWARD -> {
                int length = pos[0] - target[0];
                for (int i = 1; i <= length; i++) {
                    int[] nextTarget = {pos[0] - i, pos[1]};
                    if (!canBePutThere(nextTarget)) return false;
                }
                return true;
            }
        }
        return false;
    }

    private boolean canBePutThere(int[] target) {
        if (target[0] > -1 && target[0] < mapHeight && target[1] > -1 && target[1] < mapLength)
            return Objects.equals(map[target[0]][target[1]], " ");
        else return false;
    }

    private void incrementLastSeen(int[][] lastSeen) {
        for (int i = 0; i < lastSeen.length; i++) {
            for (int j = 0; j < lastSeen[0].length; j++) {
                if (lastSeen[i][j] != -999) lastSeen[i][j]++;
            }
        }
    }

}
