package org.openjfx.UI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

//This FileReader is only meant for me to get the values necessary to render walls and the targetArea
// feel free to modify as necessary
// -Kaiwei
public class FileReader {

    int height;
    int width;
    double scaling;
    int numberOfGuards;
    int numberOfIntruders;
    double baseSpeedIntruder;
    double sprintSpeedIntruder;
    double baseSpeedGuard;
    int gamemode;

    //high school level code, but I don't know how to do it else
    //TODO: convert these to Areas, and not arrays
    ArrayList<Integer[]> walls = new ArrayList<Integer[]>();
    Integer[] targetArea;

    public void readFile(String path)
    {
        Path file = Paths.get(path);
        try (Scanner scan = new Scanner(file)) {
            int lineCount = 1;//we need to handle an exception for this case
            while (scan.hasNextLine()) {
                readNextLine(scan.nextLine(), lineCount);
                lineCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Path invalid");
        }

    }

    public void readNextLine(String nextLine, int lineCount)
    {
        try (Scanner scan = new Scanner(nextLine))
        {
            if (scan.hasNext())
            {
                //seperate string into header and value
                String check = scan.next();
                String value = scan.next();
                //trims lead and trailing spaces
                check = check.trim();
                value = value.trim();
                System.out.println("Head: "+check+", Value: "+value);


                //Handles array values
                String[] locations = value.split(" ");
                switch (check) {
                    case "height":
                        height = Integer.parseInt(value);
                        break;
                    case "width":
                        width = Integer.parseInt(value);
                        break;
                    case "scaling":
                        scaling = Double.parseDouble(value);
                        break;
                    case "numGuards":
                        numberOfGuards = Integer.parseInt(value);
                        break;
                    case "numIntruders":
                        numberOfIntruders = Integer.parseInt(value);
                        break;
                    case "baseSpeedIntruder":
                        baseSpeedIntruder = Double.parseDouble(value);
                        break;
                    case "sprintSpeedIntruder":
                        sprintSpeedIntruder = Double.parseDouble(value);
                        break;
                    case "baseSpeedGuard":
                        baseSpeedGuard = Double.parseDouble(value);
                        break;
                    case "gameMode":
                        gamemode = Integer.parseInt(value);
                        /**
                         * didn't really get coordinate system, but the dimensions is 4 entries
                         * create wall of dimensions
                         * how to get the height and width, check 3rd and 4th dimensions.
                         */
                    case "targetArea":
                        //I don't think it's necessary for this to be a for loop, but what if they add more targetAreas?
                        for(int i = 0; i < 3;i++)
                        {
                            targetArea[i] = Integer.parseInt(locations[i]);
                        }
                        break;
                    case "wall":
                        Integer[] wall = {Integer.parseInt(locations[0]),Integer.parseInt(locations[1]),
                                Integer.parseInt(locations[2]),Integer.parseInt(locations[3])};
                        walls.add(wall);
                        break;
                }
            }
    }
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public ArrayList<Integer[]> getWalls() {
        return walls;
    }

    public Integer[] getTargetArea() {
        return targetArea;
    }
}
