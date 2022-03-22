package Logic;

import Enums.Rotations;

public class Pose {
    private Rotations rot;
    private int[]pos;
    public Pose(Rotations rot, int[] pos){
        this.pos=pos;
        this.rot=rot;
    }
    public int[] newPosition(int[] global){
        int[] rotatedPos={1,2};
        switch(rot){
            case UP -> {
               int rotatedX=global[0]-pos[0];
               int rotatedY=(global[1]-pos[1])*-1;
              int[] rotatedPose={rotatedX,rotatedY};
              rotatedPos=rotatedPose.clone();
            }
            case LEFT -> {
                int rotatedX=(global[1]-pos[1])*-1;
                int rotatedY=(global[0-pos[0]])*-1;
                int[] rotatedPose={rotatedX,rotatedY};
                rotatedPos=rotatedPose.clone();
            }
            case DOWN -> {
                int rotatedX=(global[0]-pos[0])*-1;
                int rotatedY=(global[1]-pos[1]);
                int[] rotatedPose={rotatedX,rotatedY};
                rotatedPos=rotatedPose.clone();
            }
            case RIGHT -> {
                int rotatedX=(global[1]-pos[1]);
                int rotatedY=(global[0-pos[0]]);
                int[] rotatedPose={rotatedX,rotatedY};
                rotatedPos=rotatedPose.clone();
            }
        }

        return rotatedPos;
    }

}
