package org.openjfx.UI;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

public class CustomGroup extends Group{
    Rotate r;
    Transform transform = new Rotate();

    public void rotateX(int degree){
        r = new Rotate(degree, Rotate.X_AXIS);
        transform = transform.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().addAll(transform);
    }

    public void rotateY(int degree){
        r = new Rotate(degree, Rotate.Y_AXIS);
        transform = transform.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().addAll(transform);
    }

    public void rotateZ(int degree){
        r = new Rotate(degree, Rotate.Z_AXIS);
        transform = transform.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().addAll(transform);
    }
}

