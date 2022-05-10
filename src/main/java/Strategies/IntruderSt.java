package Strategies;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class IntruderSt extends Strategy{
    private final HashMap<Integer, ArrayList<Integer>> explored;
    private final HashMap<Integer, ArrayList<Integer>> walls;
    private final HashMap<Integer,int[]> objects;
    private boolean chased;
    private boolean searching;
    private final ArrayList<Point> visitedPoints;
    private boolean atGoal;

    //for A* Search
    //private int fn; // gn + hn = total cost of path
    //private int gn; //cost of path between the first node and the current node
    //private int hn; // heuristic function


// baysian pathfinding
    //at every move it checks whether it can see the target and/or a guard and/or a wall

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

    //Searching
    //after every move it rotates 90 degrees and checks it its facing the right direction as well
    @Override
    public Moves decideOnMove(String[][] vision, int[] xy, Rotations rot, Variables vr) {

        if(searching){


        }

        return Moves.WALK;
    }


    //The goal will be in the direction of the target, but not the target itself
    public void AStarSearch(int[] start, int[] tempTarget, ArrayList<Integer> h){
        LinkedList<int[]> set = new LinkedList<>();
        set.add(start);

        //The last entry in this list is the last node it came from
        //It is null until the first move
        LinkedList<int[]> cameFrom = new LinkedList<>();

        //index 0 in the set linked list corresponds to index 0 of the fscore
        ArrayList<Integer> gScore = new ArrayList<>();
        gScore.add(0, 0); //at the beginning g is 0

        ArrayList<Integer> fScore = new ArrayList<>();
        fScore.add(0, h.get(0));

        while(!set.isEmpty()){

            //current = node in set having the lowest fScore
            int[] current = set.getLast();
            if(current == tempTarget){
                //return the "reconstructPath" from cameFrom to current
            }

            set.remove(current);

            //for each neighbor of current
            /**
             * d(current, neighbor) (THIS COULD BE A METHOD THAT CONNECTS TO THE BAYSIAN)
             * is the weight of the edge from current to neighbor
             * tentative_gScore is the distance from start to the neighbor through current
             *
             * so tentative_gScore = gScore[current] + d(current,neighbor)
             * if(tentative_gScore < gScore[neighbor])
             *      record this
             *      cameFrom[neighbor] = current
             *      gScore[neighbor] = tentative_gScore
             *      fScore[neighbor] = tentative_gScore + h(neighbor)
             *      if(neighbor is not in set)
             *          set.add(neighbor)
             */

        }


    }

    public void reconstructPath(int[] path, int[]current){

    }

    public void rotates(){

    }

    public void Score(){

    }


}
