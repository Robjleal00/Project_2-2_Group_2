package ObjectsOnMap;

public class Teleporter extends ObjectOnMap {
    private int id;
    private int[] yx;
    private Teleporter link;
    private int[] target;

    public Teleporter(int id,int x,int y, int x2, int y2){
    int [] xd = {x,y};
    int [] target = {x2,y2};
    this.yx=xd;
    this.target=target;
    this.id=id;
    }
    @Override
    public String getSymbol(){
        //add T but its making it look weird
        return "T"+String.valueOf(id);
    }
    @Override
    public int[] getXy(){
        return yx;
    }
    public int[]getTarget(){
        return target;
    }

}
