package BrickAndMortar;

import Enums.Moves;
import Enums.Rotations;

public class BrickMortar {

    // O = Unexplored
    // 1 = Explored
    // 2 = Visited
    // 3 = Wall


    private int [][]map;
    private int []xy;

    private boolean exploDone;
    private final double randomness = 0.2;
    private final Rotations rot;



    public BrickMortar(int[][] map, Rotations rot){
        this.map = map;
        this.exploDone = false;
        this.rot = rot;
    }


    public Moves brickAndMortar()
    {

        return null;
    }


}
