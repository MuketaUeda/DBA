package pr2mapAgent;

import java.util.LinkedList;

public class HeuristicHandler {
    private Node goal;
    private int basePenalty;
    private int maxRecentVisits;

    HeuristicHandler(Node goal, int basePenalty, int maxRecentVisits) {
        this.goal = goal;
        this.basePenalty = basePenalty;
        this.maxRecentVisits = maxRecentVisits;
    }

    public int manhattan(Node a) {
        return Math.abs(a.x - goal.x) + Math.abs(a.y - goal.y);
    }

    public int euclidean(Node a) {
        return (int) (Math.pow(Math.abs(a.x - goal.x), 2) + Math.pow(Math.abs(a.y - goal.y), 2));
    }

    public void penalizeDynamically(Node current, Node last, LinkedList<Node> lastVisitiedNodes) {
        int penalty = basePenalty;
        int position = 0;

        // Calculate penalty based on the position in recentVisits
        for (Node recentNode : lastVisitiedNodes) {
            if (recentNode.equals(current)) {
                penalty = basePenalty * (maxRecentVisits - position); // Dynamic penalty
                break;
            }
            position++;
        }
        last.penalize(penalty);
    }

    public int getMaxRecentVisits() {
        return maxRecentVisits;
    }

}
