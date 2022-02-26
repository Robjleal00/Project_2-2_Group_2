package map;

import java.io.*;
import java.util.*;

public class MapGenerator {

    double height;
    double width;
    double scale;

    public MapGenerator(double height, double width, double scale) {
        this.scale = scale;
        this.height = height/scale;
        this.width = width/scale;
    }

}
