package Entities;

import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import Strategies.Strategy;

public class Explorer extends Entity {
    private int x;
    private int y;
    private Rotations currentRotation;
    private final EntityType type;
    private final GameController gm;
    private final Strategy st;

    public Explorer(EntityType type, GameController gm, Strategy st) {
        this.currentRotation = Rotations.FORWARD;
        this.x = 0;
        this.y = 0;
        this.type = type;
        this.gm = gm;
        this.st = st;
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public Moves getMove() {
        String[][] vision = gm.giveVision(this);
        int[] xy = {x, y};
       // gm.printArray(vision);
        return st.decideOnMove(vision, xy, currentRotation);
    }

    @Override
    public void turnLeft() {
        switch (currentRotation) {
            case BACK -> {
                setCurrentRotation(Rotations.RIGHT);
            }
            case LEFT -> {
                setCurrentRotation(Rotations.BACK);
            }
            case FORWARD -> {
                setCurrentRotation(Rotations.LEFT);
            }
            case RIGHT -> {
                setCurrentRotation(Rotations.FORWARD);
            }
        }
    }

    @Override
    public void walk() {
        switch (currentRotation) {
            case FORWARD -> y++;
            case BACK -> y--;
            case RIGHT -> x++;
            case LEFT -> x--;
        }
    }


    @Override
    public void turnRight() {
        switch (currentRotation) {
            case FORWARD -> {
                setCurrentRotation(Rotations.RIGHT);
            }
            case RIGHT -> {
                setCurrentRotation(Rotations.BACK);
            }
            case LEFT -> {
                setCurrentRotation(Rotations.FORWARD);
            }
            case BACK -> {
                setCurrentRotation(Rotations.LEFT);
            }
        }
    }

    @Override
    public void turnAround() {
        switch (currentRotation) {
            case FORWARD -> {
                setCurrentRotation(Rotations.BACK);
            }
            case RIGHT -> {
                setCurrentRotation(Rotations.LEFT);
            }
            case LEFT -> {
                setCurrentRotation(Rotations.RIGHT);
            }
            case BACK -> {
                setCurrentRotation(Rotations.FORWARD);
            }
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

    public Rotations getCurrentRotation() {
        return currentRotation;
    }

    public void setCurrentRotation(Rotations currentRotation) {
        this.currentRotation = currentRotation;
    }
}
