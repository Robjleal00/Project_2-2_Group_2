package map;
import java.io.*;
import java.util.*;

public class fileReader {

    public static void readData() throws FileNotFoundException{
        File map = new File("mapData/testmap.txt");

        try (Scanner in = new Scanner(map)) {
            while(in.hasNextLine()) {
                System.out.println(in.nextLine());
            }
        }
    }


}
