package org.openjfx.UI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author joel
 */
public class Area {
    protected int leftBoundary;
    protected int rightBoundary;
    protected int topBoundary;
    protected int bottomBoundary;
    protected Area mapArea;

    public Area(){
        leftBoundary=0;
        rightBoundary=1;
        topBoundary=0;
        bottomBoundary=1;
    }

    public Area(int x1, int y1, int x2, int y2){
        leftBoundary=Math.min(x1,x2);
        rightBoundary=Math.max(x1,x2);
        topBoundary=Math.max(y1,y2);
        bottomBoundary=Math.min(y1,y2);
    }



    //TODO: verify legitimacy
    //Going to create a constructor with string as param for the FileReader
    /*
    public Area createArea(String input)
    {
        String[] values = input.split(" ");
        //calls other constructor
        mapArea = new Area(Integer.valueOf(values[0]),Integer.valueOf(values[1]),Integer.valueOf(values[2]),Integer.valueOf(values[3]));
        System.out.println("new area " + mapArea.toString());
        return mapArea;
    }

    /*
        Check whether a point is in the target area
    */
    public boolean isHit(double x,double y){
        return (y>bottomBoundary)&(y<topBoundary)&(x>leftBoundary)&(x<rightBoundary);
    }

    /*
        Check whether something with a radius is in the target area
        STILL TO BE IMPLEMENTED
    */
    public boolean isHit(double x,double y,double radius){
        return false;
    }


    public int getLeftBoundary() {
        return leftBoundary;
    }

    public int getRightBoundary() {
        return rightBoundary;
    }

    public int getTopBoundary() {
        return topBoundary;
    }

    public int getBottomBoundary() {
        return bottomBoundary;
    }

    public String toString()
    {
       return leftBoundary+" "+rightBoundary+" "+topBoundary+" "+bottomBoundary;
    }
}