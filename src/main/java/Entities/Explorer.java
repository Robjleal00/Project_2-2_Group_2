package Entities;

import Config.Variables;
import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import Patrolling.CoordinateTransformer;
import Strategies.Strategy;

public class Explorer extends Entity { // example of an implemented entity
    private int x;
    private int y;
    private Rotations currentRotation;
    private final EntityType type;
    private final GameController gm;
    private final Strategy st;
    private final Variables vr;
    private CoordinateTransformer ct=null;

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
        currentRotation=currentRotation.turnLeft();
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
        currentRotation=currentRotation.turnRight();
    }

    @Override
    public void turnAround() {
        currentRotation=currentRotation.turnAround();
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
            this.x = xy[0];
            this.y = xy[1];
            st.teleported();

    }

    @Override
    public void setCT(CoordinateTransformer ct) {
        super.setCT(ct);
    }

    @Override
    public void nowPatrol(int[] xy) {
        super.nowPatrol(xy);
    }
}
