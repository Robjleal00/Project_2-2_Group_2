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
    private int[] target;

    //for A* Search
    //private int fn; // gn + hn = total cost of path
    //private int gn; //cost of path between the first node and the current node
    //private int hn; // heuristic function


// baysian pathfinding
    //at every move it checks whether it can see the target and/or a guard and/or a wall

    public IntruderSt(HashMap<Integer, ArrayList<Integer>> explored, HashMap<Integer, ArrayList<Integer>> walls, HashMap<Integer, int[]> objects, int[] target) {
        this.explored = explored;
        this.walls = walls;
        this.objects = objects;
        this.visitedPoints = new ArrayList<>();
        this.atGoal = false;
        this.target = target;
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

            int current_distance  = Integer.MAX_VALUE;

            int[] front1 = new int[2];
            int[] front2 = new int[2];
            int[] front3 = new int[2];
            int[] front4 = new int[2];

            front1[0] = current[0] - 1;
            front1[1] = current[1];
            front2[0] = current[0] + 1;
            front2[1] = current[1];
            front3[0] = current[0];
            front3[1] = current[1] - 1;
            front4[0] = current[0];
            front4[1] = current[1] + 1;


            if(manDist(current, target) < current_distance)
                current_distance = manDist(current,target);

            if(manDist(front1, target) < current_distance && !walls.get(front1[0]).contains(front1[1]))
                current_distance = manDist(front1,target);


            if(manDist(front2, target) < current_distance && !walls.get(front2[0]).contains(front2[1]))
                current_distance = manDist(front2,target);


            if(manDist(front3, target) < current_distance && !walls.get(front3[0]).contains(front3[1]))
                current_distance = manDist(front3,target);


            if(manDist(front4, target) < current_distance && !walls.get(front4[0]).contains(front4[1]))
                current_distance = manDist(front4,target);





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

    public int manDist(int[] start, int[] target) {
        int distance = Math.abs(target[1]-start[1]) + Math.abs(target[0]-start[0]);
        return distance;
    }

    public void reconstructPath(int[] path, int[]current){

    }

    public void rotates(){

    }

    public void Score(){

    }


}
