package PathMaking;

import Config.Variables;
import Enums.Moves;
import Enums.Rotations;

import java.sql.Array;
import java.util.*;

public class PathMaker {
    private HashMap<Integer, ArrayList<Integer>> explored;
    private HashMap<Integer, ArrayList<Integer>> walls;
    private String[][] mapToExplore;
    private HashMap<Integer, ArrayList<Integer>> explorationPoints;
    private ArrayList<Point> visitedPoints;
    private Variables vr;
    private final int[] xy;
    private final int walkSpeed;
    Rotations[] avaliableRotations = {Rotations.BACK, Rotations.RIGHT, Rotations.LEFT, Rotations.FORWARD};
    private int[] properTarget;
    private final Rotations currentRotation;
    int depth;

    public PathMaker(HashMap<Integer, ArrayList<Integer>> explored, HashMap<Integer, ArrayList<Integer>> walls, String[][] map, ArrayList<Point> visitedPoints, Variables vr, int[] xy, Rotations rot) {
        this.explored = explored;
        this.walls = walls;
        this.mapToExplore = map;
        explorationPoints = new HashMap<>();
        this.visitedPoints = visitedPoints;
        this.vr = vr;
        this.xy = xy;
        this.walkSpeed = vr.walkSpeed();
        this.currentRotation = rot;
        depth=2;
    }

    public PathMaker(HashMap<Integer, ArrayList<Integer>> explored, HashMap<Integer, ArrayList<Integer>> walls, HashMap<Integer, ArrayList<Integer>> explorationPoints, ArrayList<Point> visitedPoints, Variables vr, int[] xy, Rotations rot) {
        this.explored = explored;
        this.walls = walls;
        this.explorationPoints = explorationPoints;
        this.visitedPoints = visitedPoints;
        this.vr = vr;
        this.xy = xy;
        this.walkSpeed = vr.walkSpeed();
        this.currentRotation = rot;
        this.mapToExplore=null;
        depth=1;
    }
    private void findPoints(){
        int fix=Integer.parseInt(mapToExplore[0][0])+1;
        int fixY=Integer.parseInt(mapToExplore[0][1]);
        int fixX=Integer.parseInt(mapToExplore[0][2]);
        for(int i=0;i<mapToExplore.length;i++){
            for(int j=0;j<mapToExplore[0].length;j++){
                if(Objects.equals(mapToExplore[i][j], "?")){
                    if(explorationPoints.containsKey(j+fix+fixX)){
                        explorationPoints.get(j+fix+fixX).add(((i+fix)*-1)+fixY);
                    }else{
                        explorationPoints.put(j+fix+fixX, new ArrayList<>());
                        explorationPoints.get(j+fix+fixX).add(((i+fix)*-1)+fixY);
                    }
                }
            }
        }
    }
    private ArrayList<Point> pointsOrdered(){
        ArrayList<Point> points = new ArrayList<>();
        Set keySet=explorationPoints.keySet();
        int distance=9999;
       Integer[] keys =  explorationPoints.keySet().toArray(new Integer[keySet.size()]);
       for(int k : keys){
           ArrayList<Integer> values = explorationPoints.get(k);
           for(int i : values){
               int[] coords = {k,i};
            int currentDistance = Math.abs(xy[0]-k)+Math.abs(xy[1]-i);
            if(currentDistance<distance){
                distance=currentDistance;
                points.add(0,new Point(coords,new ArrayList<>()));
            }
            else points.add(new Point(coords,new ArrayList<>()));
           }
       }
       return points;
    }
    private boolean checkIfPossible(int[]target){
        boolean overallResult=false;
        int distance=999;
        for(Point p :visitedPoints){
            PathMakingTreeRoot treeRoot = new PathMakingTreeRoot(explored,walls,p.xy().clone(), Rotations.FORWARD,depth,vr,target);
            boolean result =treeRoot.getMove();
            if(result){
                overallResult=true;
                int currentDistance = Math.abs(p.xy()[0]-target[0])+Math.abs(p.xy()[1]-target[1]);
                if(currentDistance<distance){
                    //this.properTarget=p.xy();
                    distance=currentDistance;
                }

            }
        }
        return overallResult;
    }
    private ArrayList<Point> reachablePoints(Point p,boolean t){
        int [] pos = p.xy();
        ArrayList<Point> history =  new ArrayList<Point>(p.path());
        if(t)history.add(p);
        ArrayList<Point> returner = new ArrayList<>();
        for(Rotations rot : avaliableRotations){
            int [] currentPosition = walk(pos.clone(),rot);
            Point pointer= new Point(currentPosition,history);
            returner.add(0,pointer);
        }

       // System.out.println(returner);
        return returner;
    }
    public int[] walk(int[] xy, Rotations rot) {
        switch (rot) {
            case FORWARD -> { //y increase
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[1] += howMuch;
            }
            case BACK -> { //y decrease
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[1] -= howMuch;
            }

            case RIGHT -> { //x increase
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[0] += howMuch;
            }

            case LEFT -> { //x decrease
                int howMuch = howMuchCanIWalk(xy, rot);
                xy[0] -= howMuch;

            }
        }
        return xy;
    }
    private int howMuchCanIWalk(int[]pos,Rotations rot){
        switch(rot){
            case LEFT -> {//x decrease
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0]-i,pos[1]};
                    if(noWallsInTheWay(pos,targetCell,rot))return i;
                }

            }
            case RIGHT -> {//x increase
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0]+i,pos[1]};
                    if(noWallsInTheWay(pos,targetCell,rot))return i;
                }
            }
            case FORWARD ->{//y increase
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0],pos[1]+i};
                    if(noWallsInTheWay(pos,targetCell,rot))return i;
                }
            }
            case BACK -> {//y decrease
                for(int i=walkSpeed;i>0;i--){
                    int[]targetCell={pos[0],pos[1]-i};
                    if(noWallsInTheWay(pos,targetCell,rot))return i;
                }
            }
        }
        return 0;
    }
    private boolean noWallsInTheWay(int[]pos,int[]target,Rotations rot){
        switch(rot){
            case FORWARD -> {//y increase
                int distance = target[1]-pos[1];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0])){
                        if(walls.get(pos[0]).contains(pos[1]+i)){
                            return false;
                        }
                    }

                }
            }
            case BACK -> {//y decrease
                int distance = pos[1]-target[1];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0])){
                        if(walls.get(pos[0]).contains(pos[1]-i)){
                            return false;
                        }
                    }

                }
            }
            case RIGHT -> {//x increase
                int distance = target[0]-pos[0];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0]+i)){
                        if(walls.get(pos[0]+i).contains(pos[1])){
                            return false;
                        }
                    }

                }
            }
            case LEFT -> {//x decrease
                int distance = pos[0]-target[0];
                for(int i=distance;i>0;i--){
                    if(walls.containsKey(pos[0]-i)){
                        if(walls.get(pos[0]-i).contains(pos[1])){
                            return false;
                        }
                    }

                }
            }

        }
        return true;
    }
    private Point closestPoint(){
        ArrayList<Point>points = pointsOrdered();
        Point closestPoint=null;
        while(true){
            if(points.size()!=0){
                closestPoint=points.get(0);
                boolean done = checkIfPossible(closestPoint.xy());
                if(done){
                    break;
                }
                else{
                    explorationPoints.get(closestPoint.xy()[0]).remove((Object)closestPoint.xy()[1]);
                    points=pointsOrdered();
                    closestPoint=null;
                }
            } else break;
        }
        return closestPoint;
    }
    private boolean equal(int[]a, int[]b){
        return(a[0]==b[0]&&a[1]==b[1]);
    }
    private boolean contains(ArrayList<int[]> array, int[] value){
            for(int[] val : array){
                if(val[0]==value[0]&&val[1]==value[1])return true;
            }
            return false;
    }
    public Moves directMove(int[]target){
        if(target==null)return Moves.TURN_AROUND;
        if(target[0]>xy[0]){//you want to move right
            switch(currentRotation){
                case LEFT -> {return Moves.TURN_AROUND;}
                case RIGHT -> {return Moves.WALK;}
                case FORWARD -> {return Moves.TURN_RIGHT;}
                case BACK ->{return Moves.TURN_LEFT;}
            }
        }
        if(target[0]<xy[0]){//you want to move left
            switch(currentRotation){
                case RIGHT-> {return Moves.TURN_AROUND;}
                case LEFT -> {return Moves.WALK;}
                case BACK -> {return Moves.TURN_RIGHT;}
                case FORWARD ->{return Moves.TURN_LEFT;}
            }
        }
        if(target[1]>xy[1]){//you want to move up
            switch(currentRotation){
                case BACK -> {return Moves.TURN_AROUND;}
                case FORWARD -> {return Moves.WALK;}
                case LEFT -> {return Moves.TURN_RIGHT;}
                case RIGHT ->{return Moves.TURN_LEFT;}
            }
        }
        if(target[1]<xy[1]){//you want to move down
            switch(currentRotation){
                case FORWARD -> {return Moves.TURN_AROUND;}
                case BACK -> {return Moves.WALK;}
                case RIGHT -> {return Moves.TURN_RIGHT;}
                case LEFT ->{return Moves.TURN_LEFT;}
            }
        }
        return Moves.STUCK;
    }
    public Moves giveMove(){
        if(mapToExplore!=null)findPoints();
        Point targetPoint = closestPoint();
        this.properTarget=targetPoint.xy();
        ArrayList<int[]> visited = new ArrayList();
        ArrayList<Point> currentWave=new ArrayList<>();
        ArrayList<Point> nextWave = new ArrayList<>();
        if(targetPoint==null)return Moves.STUCK;
        else{
            visited.add(xy);
           // System.out.println("X "+xy[0]);
           // System.out.println("Y "+xy[1]);
            ArrayList<Point> reach = reachablePoints(new Point(xy,new ArrayList<>()),false);
            for(Point p : reach){
                if(!contains(visited,p.xy())) {
                    nextWave.add(p);
                    visited.add(p.xy());
                }
            }
          // System.out.println(nextWave);
            while(!contains(visited,properTarget)){ // if it will be true then in nextWave
                currentWave=new ArrayList<>(nextWave);
                nextWave=new ArrayList<>();
                for(Point p :currentWave){
                    ArrayList<Point> reachable = reachablePoints(p,true);
                    for(Point p2 : reachable){
                        if(!contains(visited,p2.xy())){
                            visited.add(p2.xy());
                            nextWave.add(p2);
                        }
                    }
                }
                //System.out.println(nextWave);
              //  System.out.println("X ->"+properTarget[0]);
                //System.out.println("Y->" +properTarget[1]);
            }
            Point savedPoint=null;
            for(Point p:nextWave){
                if(equal(p.xy(),properTarget)){
                    savedPoint=p;
                    break;
                }
            }
           // System.out.println(properTarget[0]);
            //System.out.println(properTarget[1]);
           // System.out.println(savedPoint.path());
            int[] toGo=null;
            if(savedPoint.path().size()!=0) toGo=savedPoint.path().get(0).xy();
            if(savedPoint.path().size()==0)toGo=savedPoint.xy();
            return directMove(toGo);
        }
    }
}
