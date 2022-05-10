package Entities;

import Enums.EntityType;
import Enums.Moves;
import Enums.Rotations;
import Logic.GameController;
import Strategies.Strategy;
import Config.Variables;

public class Intruder extends Entity{
    private int x;
    private int y;
    private Rotations currentRotation;
    private final EntityType type;
    private final GameController gm;
    private final Strategy st;
    private final Variables vr;




    public Intruder(EntityType type, GameController gm, Strategy st, Variables vr) {

        this.currentRotation = currentRotation;
        this.type = type;
        this.gm = gm;
        this.st = st;
        this.vr = vr;
        //true false inititally when looking for goal and not avoiding guards
        st.setBooleans(true, false);
    }
    @Override
    public EntityType getType() {
        return super.getType();
    }

    @Override
    public Moves getMove() {
        String[][] vision = gm.giveVision(this);
        int[] xy = {x,y};
        st.decideOnMove(vision,xy, currentRotation,vr);
        return super.getMove();
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
