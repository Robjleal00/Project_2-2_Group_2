package Entities;

import Enums.EntityType;
import Enums.Moves;

public class Entity { // extend this to create entities for it all to work
    public EntityType getType() {
        return EntityType.INTRUDER;
    }

    public Moves getMove() {
        return Moves.WALK;
    }

    public void turnLeft() {
    }

    public void turnRight() {
    }

    public void turnAround() {
    }

    public void walk(int d) {
    }
    public void showMeWhatUSaw(){

    }
    public void setPosition(int[]xy){

    }


}
