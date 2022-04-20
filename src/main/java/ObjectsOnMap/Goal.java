package ObjectsOnMap;

public class Goal extends ObjectOnMap{
    private int id;
    private int[] yx;
    private double goalProb = 1;
    public Goal(int id,int x,int y){
        int [] xd = {x,y};
        this.yx=xd;
        this.id=id;
    }
    @Override
    public String getSymbol(){
        //add T but its making it look weird
        return "V"+String.valueOf(id);
    }
    @Override
    public int[] getXy(){
        return yx;
    }
}
