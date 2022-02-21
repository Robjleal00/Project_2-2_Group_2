package Entities;

import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import Strategies.Strategy;

import java.util.ArrayList;

public class Explorer extends Entity {
    private int x;
    private int y;
    private Rotations currentRotation;
    private EntityType type;
    private GameController gm;
    private Strategy st;

    public Explorer(EntityType type, GameController gm, Strategy st){
        this.currentRotation=Rotations.FORWARD;
        this.x=0;
        this.y=0;
        this.type=type;
        this.gm=gm;
        this.st=st;
    }
    @Override
    public EntityType getType(){
        return type;
    }

    @Override
    public Moves getMove() {
        String[][] vision = gm.giveVision(this);
        int[] xy={x,y};
        gm.printArray(vision);
        return st.decideOnMove(vision,xy,currentRotation);
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
