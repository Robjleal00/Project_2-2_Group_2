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
    private Rotations globalRotation;
    private boolean walked;


    //TODO: Compare this to Explorer class
    public Intruder(EntityType type, GameController gm, Strategy st, Variables vr) {
        this.x = 0;
        this.y = 0;
        this.currentRotation = Rotations.FORWARD;
        this.type = type;
        this.gm = gm;
        this.st = st;
        this.vr = vr;
        this.walked = true;
        //true false initially when looking for goal and not avoiding guards
        st.setBooleans(true, false);
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public Moves getMove() {
        String[][] vision = gm.giveVision(this);

        int[] xy = {x,y};
        System.out.println("XY: " + xy[0] + ", " + xy[1]);
        System.out.println("Current Local Rotation before move: " + currentRotation.toString());


        if(walked == false ){
            walked = true;
            return Moves.WALK;
        }
        else{
            Moves move = gm.getDirection(this);

            System.out.println("MOVE CHOSEN: " + move.toString());
            if(move != Moves.WALK){
                walked = false;
            }
            return move;
        }
       // return gm.getDirection(this);

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

    public Rotations translate(Rotations direction, Rotations global) {

        switch (currentRotation) {
            case FORWARD -> {
                if (global == Rotations.UP) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.BACK;
                    }
                } else if (global == Rotations.DOWN) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.FORWARD;
                    }
                } else if (global == Rotations.RIGHT) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.RIGHT;
                    }
                } else if (global == Rotations.LEFT) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.LEFT;
                    }
                }
            }


            case RIGHT -> {
                if (global == Rotations.UP) {  //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.LEFT;
                    }
                } else if (global == Rotations.DOWN) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.RIGHT;
                    }
                } else if (global == Rotations.RIGHT) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.BACK;
                    }
                } else if (global == Rotations.LEFT) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.FORWARD;
                    }
                }

            }


            case LEFT -> {
                if (global == Rotations.UP) {
                    if (direction == Rotations.UP) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.RIGHT;
                    }
                } else if (global == Rotations.DOWN) {
                    if (direction == Rotations.UP) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.LEFT;
                    }
                } else if (global == Rotations.RIGHT) {
                    if (direction == Rotations.UP) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.FORWARD;
                    }
                } else if (global == Rotations.LEFT) {
                    if (direction == Rotations.UP) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.BACK;
                    }
                }

            }

            case BACK -> {
                if (global == Rotations.UP) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.FORWARD;
                    }
                } else if (global == Rotations.DOWN) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.BACK;
                    }
                } else if (global == Rotations.RIGHT) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.RIGHT;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.LEFT;
                    }
                } else if (global == Rotations.LEFT) { //DONE
                    if (direction == Rotations.UP) {
                        return Rotations.LEFT;
                    } else if (direction == Rotations.RIGHT) {
                        return Rotations.FORWARD;
                    } else if (direction == Rotations.LEFT) {
                        return Rotations.BACK;
                    } else if (direction == Rotations.DOWN) {
                        return Rotations.RIGHT;
                    }
                }
            }
        }

        System.out.println("DID NOT WORK Translate method");
        return Rotations.FORWARD;
    }
}
