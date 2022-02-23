package Strategies;

import Enums.Moves;
import Enums.Rotations;
import OptimalSearch.TreeRoot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


public class BasicExplo extends Strategy {
    private final Moves[] moves = {Moves.WALK, Moves.TURN_AROUND, Moves.TURN_LEFT, Moves.TURN_RIGHT};
    private final HashMap<Integer, ArrayList<Integer>> explored;
    private final HashMap<Integer, ArrayList<Integer>> walls;

    public BasicExplo() {
        explored = new HashMap<Integer, ArrayList<Integer>>();
        walls = new HashMap<Integer, ArrayList<Integer>>();
    }

    @Override
    public Moves decideOnMove(String[][] vision, int[] xy, Rotations rot) {
        updateExploration(vision, xy, rot);
        //System.out.println(explored);
        //System.out.println(walls);
        TreeRoot root = new TreeRoot(deepClone(explored), deepClone(walls), xy.clone(), rot, 8, vision.length);
        Moves decision = root.getMove();
        return decision;
    }

    public int updateExploration(String[][] vision, int[] xy, Rotations rot) {
        int informationGain = 0;
        int eyeRange = vision.length;
        int currentX = xy[0];
        int currentY = xy[1];
        for (int i = 0; i < 5; i++) { //i= upfront
            for (int j = -1; j < 2; j++) { //j==sideways
                int h = eyeRange - (i + 1);
                int l = j + 1;
                switch (rot) {
                    case FORWARD -> {
                        if (vision[h][l] != "X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX + j)) {
                                    if (!explored.get(currentX + j).contains(currentY + i)) {
                                        explored.get(currentX + j).add(currentY + i);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX + j, new ArrayList<Integer>());
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
                                    walls.put(currentX + j, new ArrayList<Integer>());
                                    walls.get(currentX + j).add(currentY + i);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case BACK -> {
                        if (vision[h][l] != "X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX - j)) {
                                    if (!explored.get(currentX - j).contains(currentY - i)) {
                                        explored.get(currentX - j).add(currentY - i);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX - j, new ArrayList<Integer>());
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
                                    walls.put(currentX - j, new ArrayList<Integer>());
                                    walls.get(currentX - j).add(currentY - i);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case LEFT -> {
                        if (vision[h][l] != "X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX - i)) {
                                    if (!explored.get(currentX - i).contains(currentY + j)) {
                                        explored.get(currentX - i).add(currentY + j);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX - i, new ArrayList<Integer>());
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
                                    walls.put(currentX - i, new ArrayList<Integer>());
                                    walls.get(currentX - i).add(currentY + j);
                                    informationGain++;
                                }
                            }
                        }
                    }
                    case RIGHT -> {
                        if (vision[h][l] != "X") {
                            if (vision[h][l] != "W") {
                                if (explored.containsKey(currentX + i)) {
                                    if (!explored.get(currentX + i).contains(currentY - j)) {
                                        explored.get(currentX + i).add(currentY - j);
                                        informationGain++;
                                    }

                                } else {
                                    explored.put(currentX + i, new ArrayList<Integer>());
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
                                    walls.put(currentX + i, new ArrayList<Integer>());
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
        HashMap<Integer, ArrayList<Integer>> cloned = gson.fromJson(jsonString, type);
        return cloned;
    }

    @Override
    public void printMappings() {
        System.out.println(explored);
        System.out.println(walls);
    }

}

