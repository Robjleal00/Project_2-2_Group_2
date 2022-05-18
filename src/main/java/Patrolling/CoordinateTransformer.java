package Patrolling;

public class CoordinateTransformer {
    private int fixX;
    private int fixY;
    public CoordinateTransformer(int fixX, int fixY){
        this.fixX=fixX;
        this.fixY=fixY;
    }
    public int[] transform(int[]xy){
        int fixedX=xy[0]+fixX;
        int fixedY=(xy[1]*-1)+fixY;
        int[] fixed={fixedX,fixedY};
        return fixed;
    }
}
