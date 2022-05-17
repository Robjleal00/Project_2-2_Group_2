package Chasing;

// We will define a set of cells within our grid
public class Cell {

    //coordinates
    public int i, j;

    // creating the parent call for the path
    public Cell parent;

    // creating the heuristic cost of the current cell
    public int heuristicCost;

    // Final cost
    public int finalCost; // G + H width
    // g(n) is the cost of the path from the start node n to the end node (goal node)
    // h(n) is heuristic that estimates the cost of the cheapest path from n to the goal node

    public Cell(int i, int j) {
        this.i = i;
        this.j = j;

    }

    @Override
    public String toString() {
        return "[]";

    }



}
