package Chasing;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarChase {

    // creating the costs for diagonal and vertical/horizontal moves
    public static final int DIAGONAL_COST = 14;
    public static final int V_H_COST = 10;

    //creating the cells for the grid we will make
    private Cell[][] grid;

    // Define a priority queue for open cells
    // Open set of cells will be the set of nodes that need to be evaluated
    // The cells with the lowest cost will go first in the priority queue
    private PriorityQueue<Cell> openCells;

    // We will define the set of closed cells as the set of nodes that have already been evaluated
    private boolean[][] closedCells;

    // the start cell
    private int startI, startJ;

    // the end cell
    private int endI, endJ;

    public AStarChase(String[][] map, int[] xy, int[] intruderPosition)
    {
        createScoreMap(map);
        startCell(xy[0],xy[1]);
        endCell(intruderPosition[0], intruderPosition[1]);
    }

    public void createScoreMap(String[][] map)
    {
        Cell[][] scoreMap = new Cell[map.length][map[0].length];
        for(int i = 0; i < map.length; i++)
        {
            for(int j = 0; j < map.length; j++)
            {
                if(map[i][j].contains("W"))
                {
                    addBlockOnCell(i,j);
                }
                else
                {
                    scoreMap[i][j] = new Cell(i,j);
                }
            }
        }
        process();
    }

    public AStarChase(int width, int height, int si, int sj, int ei, int ej, int[][] blocks) {
        grid = new Cell[width][height];
        closedCells = new boolean[width][height];
        openCells = new PriorityQueue<Cell>(Comparator.comparingInt((Cell c) -> c.finalCost));

        startCell(si, sj);
        endCell(ei, ej);

        // initial heuristic and cells
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = new Cell(i, j);
                grid[i][j].heuristicCost = Math.abs(i - endI) + Math.abs(j - endJ);
                grid[i][j].solution = false;
            }

        }
        grid[startI][startJ].finalCost = 0;

        // we put the blocks on the grid
        for (int i = 0; i < blocks.length; i++) {
            addBlockOnCell(blocks[i][0], blocks[i][1]);
        }
    }

    public void addBlockOnCell(int i, int j) {
        grid[i][j] = null;
    }

    public void startCell(int i, int j) {
        startI = i;
        startJ = j;
    }

    public void endCell(int i, int j) {
        endI = i;
        endJ = j;
    }


    public void updateCostIfNeeded(Cell current, Cell t, int cost) {
        if (t == null || closedCells[t.i][t.j]) {
            return;
        }
        int tFinalCost = t.heuristicCost + cost;
        boolean isOpen = openCells.contains(t);

        if (!isOpen || tFinalCost < t.finalCost) {
            t.finalCost = tFinalCost;
            t.parent = current;

            if (!isOpen) {
                openCells.add(t);
            }
        }
    }

    public void process() {
        // we add the start location to open list
        openCells.add(grid[startI][startJ]);
        Cell current;

        while (true) {
            current = openCells.poll();

            if (current == null) {
                break;
            }
            closedCells[current.i][current.j] = true;

            if (current.equals(grid[endI][endJ])) {
                return;
            }

            Cell t;

            if (current.i - 1 >= 0) {
                t = grid[current.i - 1][current.j];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST);

                if (current.j - 1 >= 0) {
                    t = grid[current.i - 1][current.j - 1];
                    updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST + 1000);
                }

                if (current.j + 1 < grid[0].length) {
                    t = grid[current.i - 1][current.j + 1];
                    updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST + 1000);
                }
            }
            if (current.j - 1 >= 0) {
                t = grid[current.i][current.j - 1];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST);
            }

            if (current.j + 1 < grid[0].length) {
                t = grid[current.i][current.j + 1];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST);
            }
            if (current.i + 1 < grid.length) {
                t = grid[current.i + 1][current.j];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST);

                if (current.j - 1 >= 0) {
                    t = grid[current.i + 1][current.j - 1];
                    updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST + 1000);
                }

                if (current.j + 1 < grid[0].length) {
                    t = grid[current.i + 1][current.j + 1];
                    updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST + 1000);
                }

            }


        }
    }



    public void display() {
        System.out.println("Grid :");

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (i == startI && j == startJ)
                    System.out.printf("SO "); // the source cell
                else if(i == endI && j == endJ)
                    System.out.printf("DE "); // Destination cell
                else if(grid[i][j] != null)
                    System.out.printf("%-3d", 0); // empty cells with padding
                else
                    System.out.printf("BL "); // block cell
            }

            System.out.println();
        }
        System.out.println();
    }

    public void displayScores() {
        System.out.println("\nScores for cells :");

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != null)
                    System.out.printf("%-3d", grid[i][j].finalCost);
                else
                    System.out.printf("BL ");
            }
            System.out.println();
        }

        System.out.println();
    }

    public void displaySolution() {
        if (closedCells[endI][endJ]) {
            // Here we will track back the path
            System.out.println("Path :");
            Cell current = grid[endI][endJ];
            System.out.println(current);
            grid[current.i][current.j].solution = true;

            while (current.parent != null && current.parent != grid[startI][startJ]) {
                System.out.print(" -> " + current.parent);
                grid[current.parent.i][current.parent.j].solution = true;
                current = current.parent;
            }

            System.out.println("\n");
            System.out.println(current);
            Cell nextMoveCoordinate;
            nextMoveCoordinate = current;
            System.out.println(nextMoveCoordinate);
            System.out.println("\n");

            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (i == startI && j == startJ)
                        System.out.printf("SO "); // the soure cell
                    else if (i == endI && j == endJ)
                        System.out.printf("DE "); // Destination cell
                    else if (grid[i][j] != null)
                        System.out.printf("%-3s", grid[i][j].solution ? "X" : "0");
                    else
                        System.out.printf("BL "); //block cell
                }

                System.out.println();
            }
            System.out.println();
        } else
            System.out.println("No possible path");

        }

        public static void main(String[] args){
            AStarChase aStar = new AStarChase(5, 5, 0, 0, 3, 2,
                        new int[][]{
                    {0, 4}, {2, 2}, {3, 1}, {3, 3}, {2, 1}, {2, 3}
                    }
            );
            aStar.display();
            aStar.process(); //Apply the A* algorithm
            aStar.displayScores(); // Display the scores on the grid
            aStar.displaySolution(); // Display the solution path

       }

}
