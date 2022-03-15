package Strategies;

public class Constraints {
    private int MAX_Y;
    private int MIN_Y;
    private int MAX_X;
    private int MIN_X;
    public Constraints(){
        MAX_Y=Integer.MAX_VALUE;
        MAX_X=Integer.MAX_VALUE;
        MIN_X=Integer.MIN_VALUE;
        MIN_Y=Integer.MIN_VALUE;
    }

    public void setMAX_Y(int MAX_Y) {
        this.MAX_Y = MAX_Y;
    }

    public void setMIN_Y(int MIN_Y) {
        this.MIN_Y = MIN_Y;
    }

    public void setMAX_X(int MAX_X) {
        this.MAX_X = MAX_X;
    }

    public void setMIN_X(int MIN_X) {
        this.MIN_X = MIN_X;
    }
    public void reset(){
        MAX_Y=Integer.MAX_VALUE;
        MAX_X=Integer.MAX_VALUE;
        MIN_X=Integer.MIN_VALUE;
        MIN_Y=Integer.MIN_VALUE;
    }
    public boolean isLegal(int[] pos){
        int x=pos[0];
        int y=pos[1];
        return (x>MIN_X&&x<MAX_X&&y>MIN_Y&&y<MAX_Y);
    }
}
