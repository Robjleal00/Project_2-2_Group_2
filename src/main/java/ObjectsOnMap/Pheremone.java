package ObjectsOnMap;

public class Pheremone extends ObjectOnMap {

    private int id;
    private int[] yx;

    public Pheremone(int id, int x, int y) {
        int[] xd = {x, y};
        this.id = id;
        this.yx = xd;
    }

    @Override
    public String getSymbol() {
        return "P" + String.valueOf(id);
    }

    @Override
    public int[] getXy() {
        return yx;
    }














}
