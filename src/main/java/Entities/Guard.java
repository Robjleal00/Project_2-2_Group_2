package Entities;

import Config.Variables;
import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import Strategies.Strategy;

public class Guard extends Entity{
    private int x;
    private int y;
    private Rotations currentRotation;
    private final EntityType type;
    private final GameController gm;
    private final Strategy st;
    private final Variables vr;

    public Guard(EntityType type, GameController gm, Strategy st, Variables vr){
        this.currentRotation = Rotations.FORWARD;
        this.x = 0;
        this.y = 0;
        this.type=type;
        this.gm=gm;
        st.setBooleans(true,false);
        this.st=st;
        this.vr=vr;

    }

    @Override
    public EntityType getType() {
        return this.type;
    }

    @Override
    public Moves getMove() {
        String[][] vision = gm.giveVision(this);
        int[] xy = {x, y};
        return st.decideOnMove(vision, xy, currentRotation,vr);
    }

    @Override
    public void turnLeft() {
        this.currentRotation=currentRotation.turnLeft();
    }

    @Override
    public void turnRight() {
        this.currentRotation=currentRotation.turnRight();
    }

    @Override
    public void turnAround() {
        this.currentRotation=currentRotation.turnAround();
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
    public void showMeWhatUSaw() {
        st.printMappings();
    }

    @Override
    public void setPosition(int[] xy) {
        this.x=xy[0];
        this.y=xy[1];
    }
}
