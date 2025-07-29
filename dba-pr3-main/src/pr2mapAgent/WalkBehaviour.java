package pr2mapAgent;
import jade.core.behaviours.Behaviour;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class WalkBehaviour extends Behaviour {

    Scout scout;
    private boolean waitingForStep = false; // Flag to alternate turns
    int basePenalty = 10;
    int maxRecentVisits = 50;
    LinkedList<Node> lastVisitiedNodes;
    Environment env;
    Node current;
    Node target;
    HeuristicHandler hh;
    private boolean goalReached;

    public WalkBehaviour(Scout scout, Environment env) {
        this.goalReached = false;
        this.scout = scout;
        this.env = env;

        this.target = new Node(scout.getTargetPos()[0], scout.getTargetPos()[1]);
        this.hh = new HeuristicHandler(target, basePenalty, maxRecentVisits);

        this.current = new Node(scout.getCurrentPos()[0], scout.getCurrentPos()[1]);
        current.setGCost(0);
        current.setHCost(hh.manhattan(current));

        lastVisitiedNodes = new LinkedList<>();

    }

    @Override
    public void action() {
        if (!current.equals(target) && !waitingForStep) {
            scout.setCurrentPos(new int[]{current.x, current.y});
            scout.addBehaviour(new StepBehaviour(this));
            waitingForStep = true;
        } else if (current.equals(target) && !waitingForStep) {
            scout.setCurrentPos(new int[]{current.x, current.y});
            goalReached = true;
            waitingForStep = true;
        }
    }

    @Override
    public boolean done() {
        return goalReached;
    }

    public void notifyStepComplete() {
        this.waitingForStep = false;
    }
}
