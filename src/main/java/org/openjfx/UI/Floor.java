package org.openjfx.UI;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Floor extends Box{

    PhongMaterial floorMaterial = new PhongMaterial();

    public Floor(double height, double width, double depth)
    {
        super(height, width, depth);

        setMaterialColor();
        this.setMaterial(floorMaterial);

        System.out.println("floorX:" +getTranslateX());
        System.out.println("floory:" +getTranslateY());
    }

    private void setMaterialColor()
    {
        this.floorMaterial.setDiffuseColor(Color.GRAY);
    }
}