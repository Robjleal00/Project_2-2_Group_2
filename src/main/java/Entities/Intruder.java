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
    private int prevX;
    private int prevY;
    private Rotations currentRotation;
    private final EntityType type;
    private final GameController gm;
    private final Strategy st;
    private final Variables vr;
    private Rotations globalRotation;
    private boolean walked;
    public boolean caught;


    //TODO: Compare this to Explorer class
    public Intruder(EntityType type, GameController gm, Strategy st, Variables vr) {
        this.x = 0;
        this.y = 0;
        this.prevX = 0;
        this.prevY = 0;
        this.currentRotation = Rotations.FORWARD;
        this.type = type;
        this.gm = gm;
        this.st = st;
        this.vr = vr;
        this.walked = true;
        this.caught = false;
        //true false initially when looking for goal and not avoiding guards
        st.setBooleans(true, false);
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public Moves getMove() {
        if(caught){
            return Moves.STUCK;
        }

        String[][] vision = gm.giveVision(this);

        int[] xy = {x,y};
        System.out.println("XY: " + xy[0] + ", " + xy[1]);
        System.out.println("Current Local Rotation before move: " + currentRotation.toString());



        return  st.decideOnMoveIntruder(vision, xy, currentRotation, vr, gm, this);
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