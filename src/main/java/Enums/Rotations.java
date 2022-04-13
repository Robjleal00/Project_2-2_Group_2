package Enums;

public enum Rotations {
    FORWARD,
    BACK,
    RIGHT,
    LEFT,
    UP,
    DOWN;

    public Rotations turnLeft(){
        switch (this) {
            case BACK -> {
                return Rotations.RIGHT;
            }
            case LEFT -> {
                return Rotations.BACK;
            }
            case FORWARD -> {
                return Rotations.LEFT;
            }
            case RIGHT -> {
                return Rotations.FORWARD;
            }
        }
        return this;
    }
    public Rotations turnRight(){
        switch (this) {
            case FORWARD -> {
                return(Rotations.RIGHT);
            }
            case RIGHT -> {
                return(Rotations.BACK);
            }
            case LEFT -> {
                return(Rotations.FORWARD);
            }
            case BACK -> {
                return(Rotations.LEFT);
            }
        }
        return this;
    }
    public Rotations turnAround(){
        switch (this) {
            case FORWARD -> {
                return(Rotations.BACK);
            }
            case RIGHT -> {
                return(Rotations.LEFT);
            }
            case LEFT -> {
                return(Rotations.RIGHT);
            }
            case BACK -> {
                return(Rotations.FORWARD);
            }
        }
        return this;
    }



}
