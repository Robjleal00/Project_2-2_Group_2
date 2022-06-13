package BrickAndMortar;

import Enums.Rotations;

public class BrickMortar {
    private int [][]map;
    private int []xy;

    private boolean exploDone;
    private final double randomness = 0.2;
    //private final Rotations rot;



    public BrickMortar(int [][]map){
      this.map = map;
      this.exploDone = false;
    }
}
