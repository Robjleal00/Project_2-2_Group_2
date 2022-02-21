package Entities;

import Enums.EntityType;
import Enums.Moves;

public class Entity {
    public EntityType getType(){
        return EntityType.INTRUDER;
    }
    public Moves getMove(){
        return Moves.WALK;
    }


}
