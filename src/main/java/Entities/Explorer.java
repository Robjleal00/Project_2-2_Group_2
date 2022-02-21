package Entities;

import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import Strategies.Strategy;

import java.util.ArrayList;

public class Explorer extends Entity {
    private int h;
    private int l;
    private Rotations currentRotation;
    private EntityType type;
    private GameController gm;
    private Strategy st;

    public Explorer(EntityType type, GameController gm, Strategy st){
        this.currentRotation=Rotations.FORWARD;
        this.h=0;
        this.l=0;
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
        gm.printArray(vision);
        return st.decideOnMove(vision);
    }
}
