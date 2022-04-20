package Strategies;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class IntruderSt extends Strategy{
    private final HashMap<Integer, ArrayList<Integer>> explored;
    private final HashMap<Integer, ArrayList<Integer>> walls;
    private final HashMap<Integer,int[]> objects;
    private boolean chased;
    private boolean searching;
    private final ArrayList<Point> visitedPoints;
    private boolean atGoal;



    public IntruderSt(HashMap<Integer, ArrayList<Integer>> explored, HashMap<Integer, ArrayList<Integer>> walls, HashMap<Integer, int[]> objects) {
        this.explored = explored;
        this.walls = walls;
        this.objects = objects;
        this.visitedPoints = new ArrayList<>();
        this.atGoal = false;
    }

    @Override
    public void setBooleans(boolean s, boolean c) {
        this.searching = true;
        this.chased = false;
    }
}
