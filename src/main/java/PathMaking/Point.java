package PathMaking;

import java.util.ArrayList;

public record Point(int[] xy, ArrayList<Point> path){ // cute code for points, again records
    @Override
    public String toString() {
        return ("X:"+String.valueOf(xy[0])+"Y:"+String.valueOf(xy[1]));
    }
}
