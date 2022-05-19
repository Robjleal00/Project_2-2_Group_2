package Patrolling;

public class CoordinateTransformer {
    private int fixX;
    private int fixY;
    public CoordinateTransformer(int fixX, int fixY){
        this.fixX=fixX;
        this.fixY=fixY;
    }
    public int[] transform(int[]xy){
        int fixedX=xy[0]-fixX;
        int fixedY= (xy[1]-fixY)*-1;
        // mindMap[((integer - highestYTotal) * -1) + 2][i - lowestXTotal + 2] = " ";
        int[] fixed={fixedX,fixedY};
        return fixed;
    }
}
