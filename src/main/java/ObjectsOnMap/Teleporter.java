package ObjectsOnMap;

public class Teleporter extends ObjectOnMap {
    private int id;
    private int[] yx;
    private int[] firstCorner;
    private int[] secondCorner;
    private Teleporter link;

    public Teleporter(int id,int y, int x,int y2,int x2){
    int [] xd = {y,x};
    int [] second = {y2,x2};
    this.firstCorner=xd;
    this.secondCorner=second;
    this.id=id;
    }
    @Override
    public String getSymbol(){
        //add T but its making it look weird
        return String.valueOf(id);
    }
    public int[] getXy(){
        return yx;
    }
    public void addLink(Teleporter t){
        if(link==null){
            this.link=t;
            t.addLink(this);
        }
    }
    @Override
    public int[] getFirstCorner() {
        return firstCorner;
    }
    @Override
    public int[] getSecondCorner() {
        return secondCorner;
    }
}
