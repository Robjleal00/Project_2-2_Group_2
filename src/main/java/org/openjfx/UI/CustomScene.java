package org.openjfx.UI;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;

public class CustomScene extends Scene{
    private static final double WIDTH = 1200;
    private static final double HEIGHT = 800;
    private Camera camera;

    public CustomScene(CustomGroup group)
    {
        super(group, WIDTH,HEIGHT);
        addCamera();
    }
    private void addCamera(){
        camera = new PerspectiveCamera();
        camera.setNearClip(0);
        camera.setFarClip(1000);
        camera.setTranslateX(-WIDTH/2);
        camera.setTranslateY(-HEIGHT/2);
        camera.setTranslateZ(750);
        setCamera(camera);
    }
}
