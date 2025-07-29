package pr2mapAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    int x, y;
    int gCost, hCost, penalty;
    List<Node> neighbors;
    boolean isObstacle;


    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.gCost = Integer.MAX_VALUE;
        this.hCost = 0;
        this.penalty = 0;
        this.isObstacle = false;
        this.neighbors = new ArrayList<Node>();
    }

    public Node(int x, int y, boolean isObstacle) {
        this.x = x;
        this.y = y;
        this.gCost = Integer.MAX_VALUE;
        this.hCost = 0;
        this.penalty = 0;
        this.neighbors = new ArrayList<Node>();
        this.isObstacle = isObstacle;
    }

    public int fCost() {
        return gCost + hCost + penalty;
    }

    public void penalize(int penalty) {
        this.penalty += penalty;
    }

    public void addNeighbor(Node neighbor) {
        this.neighbors.add(neighbor);
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }

    public void setGCost(int gCost) {
        this.gCost = gCost;
    }

    public void setHCost(int hCost) {
        this.hCost = hCost;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
