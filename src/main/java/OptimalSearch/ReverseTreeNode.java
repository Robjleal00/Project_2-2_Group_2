package OptimalSearch;

import Enums.Moves;
import Enums.Rotations;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static java.util.Collections.max;

public class ReverseTreeNode {
    private final Moves move;
    private final HashMap<Integer, ArrayList<Integer>> explored;
    private final HashMap<Integer, ArrayList<Integer>> walls;
    private int[] xy;
    private Rotations rot;
    private final int eyeRange;
    private final Moves[] avaliableMoves = {Moves.WALK, Moves.TURN_RIGHT, Moves.TURN_LEFT, Moves.TURN_AROUND};
    private final double target;
    private final double parentValue;

    public ReverseTreeNode(Moves move, HashMap explored, HashMap walls, int[] xy, Rotations rot, int eyeRange, double target, double parentValue) {
        this.move = move;
        this.rot = rot;
        this.explored = explored;
        this.walls = walls;
        this.xy = xy;
        this.eyeRange = eyeRange;
        this.target = target;
        this.parentValue = parentValue;
    }

    public int getValue(int depth,int maxDepth) {
        switch (move) {
            case TURN_AROUND -> rot = turnAround(rot);
            case WALK -> xy = walk(xy, rot, walls);
            case TURN_RIGHT -> rot = turnRight(rot);
            case TURN_LEFT -> rot = turnLeft(rot);
        }
        String[][] vision = predictVision(xy, rot, walls, explored);
        /*
        VISION TESTING
        GameController printer = new GameController();
        printer.printArray(vision);
        printer.print("---------------------");
        */
        int valued = updateExploration(vision, xy, rot);
        double value = valued/depth;
        value += parentValue;
        //if(depth==0)return Integer.MAX_VALUE;
        if (value == target) {
            return depth;
        } else {
              /*
            if (value == 1) {

                GameController printer = new GameController();
                printer.print(" PRINTING THE VISION AT POSITION THAT GAVE ME RESULT OF 1");
                printer.printArray(vision);
                printer.print(" AT POSITION");
                System.out.println(xy);
                printer.print(" LOOKING");
                System.out.println(rot);


            }
         */
            if (depth == maxDepth) {
                return Integer.MIN_VALUE;
            } else {
                ArrayList<Integer> values = new ArrayList<>();
                for (Moves avaliableMove : avaliableMoves) {
                    values.add(new ReverseTreeNode(avaliableMove, deepClone(explored), deepClone(walls), xy.clone(), rot, eyeRange, target, value).getValue(depth+1,maxDepth));
                }
                return max(values);
            }
        }
    }

    private String[][] predictVision(int[] xy, Rotations rot, HashMap<Integer, ArrayList<Integer>> walls, HashMap<Integer, ArrayList<Integer>> explored) {
        String[][] returner = new String[eyeRange][3];
        int currentX = xy[0];
        int currentY = xy[1];
        boolean[] canSee = {true, true, true};
        for (int i = 0; i < eyeRange; i++) { //i= upfront
            for (int j = -1; j < 2; j++) { //j==sideways
                int h = eyeRange - (i + 1);
                int l = j + 1;
                boolean middleisNotWall = true;
                switch (rot) {
                    case FORWARD -> {
                        if (j == -1) {
                            if (walls.containsKey(currentX)) {
                                if (walls.get(currentX).contains(currentY + i)) {
                                    middleisNotWall = false;
                                }
                            }
                        }
                        if (canSee[j + 1]) {
                            if (explored.containsKey(currentX + j)) {
                                if (!explored.get(currentX + j).contains(currentY + i)) {
                                    returner[h][l] = " ";
                                }
                            } else {
                                returner[h][l] = " ";
                            }
                            if (walls.containsKey(currentX + j)) {
                                if (walls.get(currentX + j).contains(currentY + i)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        } else if (canSee[1] && middleisNotWall) {
                            if (explored.containsKey(currentX + j)) {
                                if (!explored.get(currentX + j).contains(currentY + i)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX + j)) {
                                if (walls.get(currentX + j).contains(currentY + i)) {
                                    returner[h][l] = "W";

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        } else {
                            returner[h][l] = "X";
                        }

                    }
                    case BACK -> {
                        if (j == -1) {
                            if (walls.containsKey(currentX)) {
                                if (walls.get(currentX).contains(currentY - i)) {
                                    middleisNotWall = false;
                                }
                            }
                        }
                        if (canSee[j + 1]) {
                            if (explored.containsKey(currentX - j)) {
                                if (!explored.get(currentX - j).contains(currentY - i)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX - j)) {
                                if (walls.get(currentX - j).contains(currentY - i)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        } else if (canSee[1] && middleisNotWall) {
                            if (explored.containsKey(currentX - j)) {
                                if (!explored.get(currentX - j).contains(currentY - i)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX - j)) {
                                if (walls.get(currentX - j).contains(currentY - i)) {
                                    returner[h][l] = "W";

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        } else {
                            returner[h][l] = "X";
                        }

                    }
                    case LEFT -> {
                        if (j == -1) {
                            if (walls.containsKey(currentX - i)) {
                                if (walls.get(currentX - i).contains(currentY)) {
                                    middleisNotWall = false;
                                }
                            }
                        }
                        if (canSee[j + 1]) {
                            if (explored.containsKey(currentX - i)) {
                                if (!explored.get(currentX - i).contains(currentY + j)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX - i)) {
                                if (walls.get(currentX - i).contains(currentY + j)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        } else if (canSee[1] && middleisNotWall) {
                            if (explored.containsKey(currentX - i)) {
                                if (!explored.get(currentX - i).contains(currentY + j)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX - i)) {
                                if (walls.get(currentX - i).contains(currentY + j)) {
                                    returner[h][l] = "W";

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        } else {
                            returner[h][l] = "X";
                        }

                    }
                    case RIGHT -> {
                        if (j == -1) {
                            if (walls.containsKey(currentX + i)) {
                                if (walls.get(currentX + i).contains(currentY)) {
                                    middleisNotWall = false;
                                }
                            }
                        }
                        if (canSee[j + 1]) {
                            if (explored.containsKey(currentX + i)) {
                                if (!explored.get(currentX + i).contains(currentY - j)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX + i)) {
                                if (walls.get(currentX + i).contains(currentY - j)) {
                                    returner[h][l] = "W";
                                    canSee[j + 1] = false;

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        } else if (canSee[1] && middleisNotWall) {
                            if (explored.containsKey(currentX + i)) {
                                if (!explored.get(currentX + i).contains(currentY - j)) {
                                    returner[h][l] = " ";

                                }
                            } else {
                                returner[h][l] = " ";

                            }
                            if (walls.containsKey(currentX + i)) {
                                if (walls.get(currentX + i).contains(currentY - j)) {
                                    returner[h][l] = "W";

                                } else {
                                    returner[h][l] = " ";
                                }

                            } else {
                                returner[h][l] = " ";
                            }
                        } else {
                            returner[h][l] = "X";
                        }
                    }
                }
            }
        }
        return returner;

    }

    public Rotations turnLeft(Rotations rot) {
        switch (rot) {
            case BACK -> {
                return (Rotations.RIGHT);
            }
            case LEFT -> {
                return (Rotations.BACK);
            }
            case FORWARD -> {
                return (Rotations.LEFT);
            }
            case RIGHT -> {
                return (Rotations.FORWARD);
            }
            default -> {
                return Rotations.LEFT;
            }
        }
    }

    public int[] walk(int[] xy, Rotations rot, HashMap<Integer, ArrayList<Integer>> walls) {
        int[] origin = {xy[0], xy[1]};
        switch (rot) {
            case FORWARD -> xy[1]++;
            case BACK -> xy[1]--;
            case RIGHT -> xy[0]++;
            case LEFT -> xy[0]--;

        }
        if (walls.containsKey(xy[0])) {
            if (walls.get(xy[0]).contains(xy[1])) {
                return origin;
            }
        }
        return xy;
    }

    public Rotations turnRight(Rotations rot) {
        switch (rot) {
            case FORWARD -> {
                return (Rotations.RIGHT);
            }
            case RIGHT -> {
                return (Rotations.BACK);
            }
            case LEFT -> {
                return (Rotations.FORWARD);
            }
            case BACK -> {
                return (Rotations.LEFT);
            }
            default -> {
                return Rotations.LEFT;
            }
        }
    }

    public Rotations turnAround(Rotations rot) {
        switch (rot) {
            case FORWARD -> {
                return (Rotations.BACK);
            }
            case RIGHT -> {
                return (Rotations.LEFT);
            }
            case LEFT -> {
                return (Rotations.RIGHT);
            }
            case BACK -> {
                return (Rotations.FORWARD);
            }
            default -> {
                return Rotations.LEFT;
            }
        }
    }

    public int updateExploration(String[][] vision, int[] xy, Rotations rot) {
        int informationGain = 0;
        int eyeRange = vision.length;
        int currentX = xy[0];
        int currentY = xy[1];
        for (int i = 0; i < eyeRange; i++) { //i= upfront
            for (int j = -1; j < 2; j++) { //j==sideways
                int h = eyeRange - (i + 1);
                int l = j + 1;
                switch (rot) {
                    case FORWARD -> {
                        if (!Objects.equals(vision[h][l], "X")) {
                            if (!Objects.equals(vision[h][l], "W")) {
                                if (explored.containsKey(currentX + j)) {
                                    if (!explored.get(currentX + j).contains(currentY + i)) {
                                        explored.get(currentX + j).add(currentY + i);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX + j, new ArrayList<>());
                                    explored.get(currentX + j).add(currentY + i);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX + j)) {
                                    if (!walls.get(currentX + j).contains(currentY + i)) {
                                        walls.get(currentX + j).add(currentY + i);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX + j, new ArrayList<>());
                                    walls.get(currentX + j).add(currentY + i);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case BACK -> {
                        if (!Objects.equals(vision[h][l], "X")) {
                            if (!Objects.equals(vision[h][l], "W")) {
                                if (explored.containsKey(currentX - j)) {
                                    if (!explored.get(currentX - j).contains(currentY - i)) {
                                        explored.get(currentX - j).add(currentY - i);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX - j, new ArrayList<>());
                                    explored.get(currentX - j).add(currentY - i);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX - j)) {
                                    if (!walls.get(currentX - j).contains(currentY - i)) {
                                        walls.get(currentX - j).add(currentY - i);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX - j, new ArrayList<>());
                                    walls.get(currentX - j).add(currentY - i);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case LEFT -> {
                        if (!Objects.equals(vision[h][l], "X")) {
                            if (!Objects.equals(vision[h][l], "W")) {
                                if (explored.containsKey(currentX - i)) {
                                    if (!explored.get(currentX - i).contains(currentY + j)) {
                                        explored.get(currentX - i).add(currentY + j);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX - i, new ArrayList<>());
                                    explored.get(currentX - i).add(currentY + j);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX - i)) {
                                    if (!walls.get(currentX - i).contains(currentY + j)) {
                                        walls.get(currentX - i).add(currentY + j);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX - i, new ArrayList<>());
                                    walls.get(currentX - i).add(currentY + j);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case RIGHT -> {
                        if (!Objects.equals(vision[h][l], "X")) {
                            if (!Objects.equals(vision[h][l], "W")) {
                                if (explored.containsKey(currentX + i)) {
                                    if (!explored.get(currentX + i).contains(currentY - j)) {
                                        explored.get(currentX + i).add(currentY - j);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX + i, new ArrayList<>());
                                    explored.get(currentX + i).add(currentY - j);
                                    informationGain++;
                                }
                            } else {
                                if (walls.containsKey(currentX + i)) {
                                    if (!walls.get(currentX + i).contains(currentY - j)) {
                                        walls.get(currentX + i).add(currentY - j);
                                        informationGain++;
                                    }

                                } else {
                                    walls.put(currentX + i, new ArrayList<>());
                                    walls.get(currentX + i).add(currentY - j);
                                    informationGain++;
                                }
                            }
                        }
                    }


                }
            }
        }
        return informationGain;
    }

    private HashMap<Integer, ArrayList<Integer>> deepClone(HashMap<Integer, ArrayList<Integer>> maptoCopy) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(maptoCopy);
        Type type = new TypeToken<HashMap<Integer, ArrayList<Integer>>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }
}


