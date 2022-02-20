package org.openjfx.UI;

public class GuiMaker {
    double height;
    double width;
    double scale;
    private double depth = 20;


    public GuiMaker(double height, double width, double scale)
    {
        this.scale = scale;
        this.height = height/scale;
        this.width = width/scale;

    }
}
