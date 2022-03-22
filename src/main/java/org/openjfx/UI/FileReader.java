package org.openjfx.UI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

//This FileReader is only meant for me to get the values necessary to render walls and the targetArea
// feel free to modify as necessary
// -Kaiwei
public class FileReader {

    private Area area ;
    int height;
    int width;
    double scaling;
    int numberOfGuards;
    int numberOfIntruders;
    double baseSpeedIntruder;
    double sprintSpeedIntruder;
    double baseSpeedGuard;
    int gamemode;
    Area spawnArea;
    ArrayList<Area> walls = new ArrayList<>();
    Area targetArea;

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
                String fullLine = scan.nextLine();
                String[] parts = fullLine.split("=");
                //seperate string into header and value
                String id = parts[0];
                String value = parts[1];
                //trims lead and trailing spaces
                id = id.trim();
                value = value.trim();

                //System.out.println("Head: "+id+", Value: "+value);

                //Handles array values, not necessary I think
                //String[] locations = value.split(" ");
                switch (id) {
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
                        break;
                    case "targetArea":
                        //System.out.println("Value: "+value); //this just returns 0 (?)
                        targetArea = createArea(value);
                        //System.out.println(targetArea.toString());
                        break;
                    case "wall":
                        Area wall = createArea(value);
                        walls.add(wall);
                        break;
                    case "spawnAreaGuards":
                        Area spawnArea = createArea(value);
                        break;
                }
            }
        }
    }


    public Area getSpawnArea()
    {
        return spawnArea;
    }
    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public ArrayList<Area> getWalls() {
        return walls;
    }

    public Area getTargetArea() {
        return targetArea;
    }

    public Area createArea(String input)
    {
        String[] values = input.split(" ");
        //calls other constructor
        Area mapArea = new Area(Integer.valueOf(values[0]),Integer.valueOf(values[1]),Integer.valueOf(values[2]),Integer.valueOf(values[3]));
        System.out.println("new area " + mapArea.toString());
        return mapArea;
    }
}