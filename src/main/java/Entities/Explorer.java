package Entities;

import Config.Variables;
import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import Strategies.Strategy;

public class Explorer extends Entity { // example of an implemented entity
    private int x;
    private int y;
    private Rotations currentRotation;
    private final EntityType type;
    private final GameController gm;
    private final Strategy st;
    private final Variables vr;

    public Explorer(EntityType type, GameController gm, Strategy st, Variables vr) {
        this.currentRotation = Rotations.FORWARD;
        this.x = 0;
        this.y = 0;
        this.type = type;
        this.gm = gm;
        this.st = st;
        this.vr=vr;
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public Moves getMove() {//gets vision and passes on needed variables to strategy
        String[][] vision = gm.giveVision(this);
        int[] xy = {x, y};
        return st.decideOnMove(vision, xy, currentRotation,vr);
    }

    @Override
    public void turnLeft() {
        switch (currentRotation) {
            case BACK -> setCurrentRotation(Rotations.RIGHT);
            case LEFT -> setCurrentRotation(Rotations.BACK);
            case FORWARD -> setCurrentRotation(Rotations.LEFT);
            case RIGHT -> setCurrentRotation(Rotations.FORWARD);
        }
    }

    @Override
    public void walk(int d) {
        switch (currentRotation) {
            case FORWARD -> y+=d;
            case BACK -> y-=d;
            case RIGHT -> x+=d;
            case LEFT -> x-=d;
        }
    }


    @Override
    public void turnRight() {
        switch (currentRotation) {
            case FORWARD -> setCurrentRotation(Rotations.RIGHT);
            case RIGHT -> setCurrentRotation(Rotations.BACK);
            case LEFT -> setCurrentRotation(Rotations.FORWARD);
            case BACK -> setCurrentRotation(Rotations.LEFT);
        }
    }

    @Override
    public void turnAround() {
        switch (currentRotation) {
            case FORWARD -> setCurrentRotation(Rotations.BACK);
            case RIGHT -> setCurrentRotation(Rotations.LEFT);
            case LEFT -> setCurrentRotation(Rotations.RIGHT);
            case BACK -> setCurrentRotation(Rotations.FORWARD);
        }
    }
    @Override
    public void showMeWhatUSaw(){
        st.printMappings();
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void setPosition(int[] xy) {
        this.x=xy[0];
        this.y=xy[1];
    }

    public Rotations getCurrentRotation() {
        return currentRotation;
    }

    public void setCurrentRotation(Rotations currentRotation) {
        this.currentRotation = currentRotation;
    }
}
