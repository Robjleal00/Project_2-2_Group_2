package Strategies;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;
//import PathMaking.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class RandomExplo extends Strategy{

    private final HashMap<Integer, ArrayList<Integer>> explored;
    private final HashMap<Integer, ArrayList<Integer>> walls;
    private final Constraints constraints;
    //private final ArrayList<Point> visitedPoints;
    boolean firstPhase;


    public RandomExplo() {
        explored = new HashMap<>();
        walls = new HashMap<>();
        constraints=new Constraints();
       // firstPhase=true;
        //visitedPoints=new ArrayList<>();

    }

    @Override
    public Moves decideOnMove(String[][] vision, int[] xy, Rotations rot, Variables vr){
        int eyeRange = vision.length;
        int check = eyeRange-2;
        Moves randomMove = Moves.STUCK;




        if (!Objects.equals(vision[check][1], " ")) {
            System.out.println("FOUND A WALL");
            randomMove = randomMove(3);


        }
        else
            randomMove = randomMove(4);



        switch (randomMove) {
            case TURN_RIGHT -> {
                rot = Rotations.RIGHT;
                break;
            }
            case TURN_LEFT -> {
                rot = Rotations.LEFT;
                break;
            }
            case TURN_AROUND -> {
                rot = Rotations.DOWN;
                break;
            }
            case WALK -> {
                rot = Rotations.FORWARD;
                break;
            }
        }


        System.out.println(rot.toString());
        //for(int[] xys : trackcoors){
        //  System.out.print("(" + xys[0] + "," + xys[1] + "),");
        //}
        System.out.println();
        updateExploration(vision, xy, rot);

        return randomMove;
    }

    public Moves randomMove(int bound){ //called when agent has wall in front of it
        int r = ThreadLocalRandom.current().nextInt(0,bound); //from 0 to 4
        Moves move = Moves.STUCK;
        switch(r){
            case 0 : move = Moves.TURN_RIGHT; break;
            case 1 : move = Moves.TURN_LEFT; break;
            case 2 : move = Moves.TURN_AROUND; break;
            case 3 : move =  Moves.WALK; break;
        }
        System.out.println("THIS IS THE RANDOM NUMBER: " + r);
        System.out.println("THIS IS THE MOVE: " + move.toString());
        return move;
    }

    public void updateExploration(String[][] vision, int[] xy, Rotations rot) {
        int eyeRange = vision.length;
        int currentX = xy[0];
        int currentY = xy[1];
        for (int i = 0; i < eyeRange; i++) { //i= upfront
            for (int j = -1; j < 2; j++) { //j==sideways
                int h = eyeRange - (i + 1);
                int l = j + 1;
                final String lookingAt = vision[h][l];
                switch (rot) {
                    case FORWARD -> {
                        if(lookingAt.contains("E")){
                            if(i!=0){
                                constraints.setMAX_Y(currentY+1);
                            }
                        }
                        if (!Objects.equals(lookingAt, "X")) {
                            if (!Objects.equals(lookingAt, "W")) {
                                if (explored.containsKey(currentX + j)) {
                                    if (!explored.get(currentX + j).contains(currentY + i)) {
                                        explored.get(currentX + j).add(currentY + i);
                                    }

                                } else {
                                    explored.put(currentX + j, new ArrayList<>());
                                    explored.get(currentX + j).add(currentY + i);
                                }
                            } else {
                                if (walls.containsKey(currentX + j)) {
                                    if (!walls.get(currentX + j).contains(currentY + i)) {
                                        walls.get(currentX + j).add(currentY + i);
                                    }

                                } else {
                                    walls.put(currentX + j, new ArrayList<>());
                                    walls.get(currentX + j).add(currentY + i);
                                }
                            }
                        }
                    }
                    case BACK -> {
                        if(lookingAt.contains("E")) {
                            if (i != 0) {
                                constraints.setMIN_Y(currentY - 1);
                            }
                        }
                        if (!Objects.equals(lookingAt, "X")) {
                            if (!Objects.equals(lookingAt, "W")) {
                                if (explored.containsKey(currentX - j)) {
                                    if (!explored.get(currentX - j).contains(currentY - i)) {
                                        explored.get(currentX - j).add(currentY - i);
                                    }

                                } else {
                                    explored.put(currentX - j, new ArrayList<>());
                                    explored.get(currentX - j).add(currentY - i);
                                }
                            } else {
                                if (walls.containsKey(currentX - j)) {
                                    if (!walls.get(currentX - j).contains(currentY - i)) {
                                        walls.get(currentX - j).add(currentY - i);
                                    }

                                } else {
                                    walls.put(currentX - j, new ArrayList<>());
                                    walls.get(currentX - j).add(currentY - i);
                                }
                            }
                        }
                    }
                    case LEFT -> {
                        if(lookingAt.contains("E")) {
                            if (i != 0) {
                                constraints.setMIN_X(currentX - 1);
                            }
                        }
                        if (!Objects.equals(lookingAt, "X")) {
                            if (!Objects.equals(lookingAt, "W")) {
                                if (explored.containsKey(currentX - i)) {
                                    if (!explored.get(currentX - i).contains(currentY + j)) {
                                        explored.get(currentX - i).add(currentY + j);
                                    }

                                } else {
                                    explored.put(currentX - i, new ArrayList<>());
                                    explored.get(currentX - i).add(currentY + j);
                                }
                            } else {
                                if (walls.containsKey(currentX - i)) {
                                    if (!walls.get(currentX - i).contains(currentY + j)) {
                                        walls.get(currentX - i).add(currentY + j);
                                    }

                                } else {
                                    walls.put(currentX - i, new ArrayList<>());
                                    walls.get(currentX - i).add(currentY + j);
                                }
                            }
                        }
                    }
                    case RIGHT -> {
                        if(lookingAt.contains("E")) {
                            if (i != 0) {
                                constraints.setMAX_X(currentX + 1);
                            }
                        }
                        if (!Objects.equals(lookingAt, "X")) {
                            if (!Objects.equals(lookingAt, "W")) {
                                if (explored.containsKey(currentX + i)) {
                                    if (!explored.get(currentX + i).contains(currentY - j)) {
                                        explored.get(currentX + i).add(currentY - j);
                                    }

                                } else {
                                    explored.put(currentX + i, new ArrayList<>());
                                    explored.get(currentX + i).add(currentY - j);
                                }
                            } else {
                                if (walls.containsKey(currentX + i)) {
                                    if (!walls.get(currentX + i).contains(currentY - j)) {
                                        walls.get(currentX + i).add(currentY - j);
                                    }

                                } else {
                                    walls.put(currentX + i, new ArrayList<>());
                                    walls.get(currentX + i).add(currentY - j);
                                }
                            }
                        }
                    }


                }
            }
        }
    }


}
