package Entities;

import Config.Variables;
import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import Patrolling.CoordinateTransformer;
import Strategies.Strategy;

public class Guard extends Entity{
    private int x;
    private int y;
    private Rotations currentRotation;
    private final EntityType type;
    private final GameController gm;
    private final Strategy st;
    private final Variables vr;
    boolean patrolling=false;
    private CoordinateTransformer CT;

    public Guard(EntityType type, GameController gm, Strategy st, Variables vr){
        this.currentRotation = Rotations.FORWARD;
        this.x = 0;
        this.y = 0;
        this.type=type;
        this.gm=gm;
        st.setBooleans(true,false);
        st.setAgent(this);
        this.st=st;
        this.vr=vr;
        this.CT=null;

    }

    @Override
    public EntityType getType() {
        return this.type;
    }

    @Override
    public Moves getMove() {
        String[][] vision = gm.giveVision(this);
        int[] xy = {x, y};
        Moves move = st.decideOnMove(vision, xy, currentRotation,vr);
        System.out.println("GUARD MOVEEE: " + move.toString());
        return move;
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

        if (!patrolling) {
            switch (currentRotation) {
                case FORWARD -> y += d;
                case BACK -> y -= d;
                case RIGHT -> x += d;
                case LEFT -> x -= d;
            }
        }
        else{
            switch (currentRotation) {
                case FORWARD -> x -= d;
                case BACK -> x += d;
                case RIGHT -> y += d;
                case LEFT -> y -= d;
            }
        }
    }

    @Override
    public void showMeWhatUSaw() {
        st.printMappings();
    }

    @Override
    public void setPosition(int[] xy) {
        if(CT!=null){xy=CT.transform(xy);}
        this.x=xy[0];
        this.y=xy[1];
        st.teleported();
    }

    @Override
    public void setCT(CoordinateTransformer ct) {
        this.CT=ct;
    }

    @Override
    public void nowPatrol(int[] xy) {
        this.x=xy[0];
        this.y=xy[1];
        /*if (currentRotation.equals(Rotations.FORWARD) || currentRotation.equals(Rotations.BACK)) {
            currentRotation = currentRotation.turnAround();
        }
         */
        patrolling=true;
    }
}
