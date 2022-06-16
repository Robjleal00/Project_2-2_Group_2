package Chasing;

import java.util.*;


public class AStarCoordinateTransform {

    public static String[][] transformIntoCell(HashMap<Integer, ArrayList<Integer>> map) {
        String[][] cellMap = new String[map.size()][2];
        Set entries = map.entrySet();
        Iterator entriesIterator = entries.iterator();

        int i = 0;
        while(entriesIterator.hasNext()){

            Map.Entry mapping = (Map.Entry) entriesIterator.next();

            cellMap[i][0] = (String) mapping.getKey();
            cellMap[i][1] = (String) mapping.getValue();

            i++;
        }
        return cellMap;
    }
}

/**
 * NOTES:
 * Ok so, I do not really understand what exactly we pass through as the map as I am just unfamiliar with the map structure.
 * However, I just passed in a hashmap (thinking it represents the entire map) but Cloud told me to use the mindmap as well.
 * This is where I am unsure about (in regard to the mindmap being necessary or not) so this needs to be reviewed and fixed/implemented.
 * It should be noted that the mindmap is a 2D array structure, which this coordinate transformation method does return a 2D array (String type).
 * It should also be noted that there is a method within the AStarChase class that converts a 2D String array into a Cell Object, therefore,
 * it is not necessary to include that in this class.
 */