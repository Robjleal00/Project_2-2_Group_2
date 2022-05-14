package ObjectsOnMap;

public class Goal extends ObjectOnMap{
    private int id;
    private int[] yx;
    private int x;
    private int y;
    private double goalProb = 1;
    public Goal(int id,int x,int y){
        int [] xd = {x,y};
        this.x = x;
        this.y = y;
        this.yx=xd;
        this.id=id;
    }
    @Override
    public String getSymbol(){
        return "V"+String.valueOf(id);
    }
    @Override
    public int[] getXy(){
        return yx;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
