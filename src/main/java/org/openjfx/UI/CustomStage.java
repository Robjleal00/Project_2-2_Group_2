package org.openjfx.UI;
import javafx.scene.Camera;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class CustomStage extends Stage{
    CustomGroup group;
    public CustomStage(CustomGroup group, CustomScene scene)
    {
        super();
        this.group = group;
        addEventHandlers();

        setTitle("Project 2-2 Group 02");
        setScene(scene);
        show();
    }

    private void addEventHandlers(){
        this.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch(event.getCode()){
                case C:
                    this.group.rotateX(-10);
                    break;
                case V:
                    this.group.rotateX(10);
                    break;
                case D:
                    this.group.rotateY(-10);
                    break;
                case F:
                    this.group.rotateY(10);
                    break;
                case E:
                    this.group.rotateZ(-10);
                    break;
                case R:
                    this.group.rotateZ(10);
                    break;
                case T:
                    moveLeft();
                    break;
                case Y:
                    moveRight();
                    break;
                case G:
                    moveUp();
                    break;
                case H:
                    moveDown();
                    break;
                case N:
                    zoomOut();
                    break;
                case B:
                    zoomIn();
                    break;
            }
        });
    }


    private void moveLeft(){
        Camera c = this.getScene().getCamera();
        c.setTranslateX(c.getTranslateX()-50);
    }
    private void moveRight(){
        Camera c = this.getScene().getCamera();
        c.setTranslateX(c.getTranslateX()+50);
    }
    private void moveUp(){
        Camera c = this.getScene().getCamera();
        c.setTranslateY(c.getTranslateY()-50);
    }
    private void moveDown(){
        Camera c = this.getScene().getCamera();
        c.setTranslateY(c.getTranslateY()+50);
    }
    private void zoomOut(){
        Camera c = this.getScene().getCamera();
        c.setTranslateZ(c.getTranslateZ()-50);
    }
    private void zoomIn(){
        Camera c = this.getScene().getCamera();
        c.setTranslateZ(c.getTranslateZ()+50);
        System.out.println(c.getTranslateZ());
    }
}

