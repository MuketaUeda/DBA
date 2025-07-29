package pr2mapAgent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scout extends Agent {

    Environment env;
    int energy;
    int[] currentPos;
    int[] targetPos;

    private final List<PositionListener> listeners = new ArrayList<>(); // Observer list

    public Scout() {
        super();
    }

    @Override
    protected void setup() {
        // Retrieve startup arguments
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            energy = -2;
            targetPos = ((int[]) args[1]).clone();
            env = (Environment) args[2];
            setCurrentPos(((int[]) args[0]).clone());
            System.out.println("Agent started with argument: Start - " + currentPos[0] + ", " + currentPos[1] + ", End - " + targetPos[0] + ", " + targetPos[1]);

            PositionListener glm = (PositionListener) args[3];
            addPositionListener(glm);
        } else {
            System.out.println("No arguments provided.");
        }

        //start walking
        addBehaviour(new WalkBehaviour(this, env));
    }

    int getEnergy() {
        return energy;
    }

    int[] getCurrentPos() {
        return currentPos;
    }

    void setCurrentPos(int[] newPos) {
        energy++;
        int[] oldPos = currentPos;
        this.currentPos = newPos;
        notifyPositionListeners(oldPos, newPos);
    }

    int[] getTargetPos() {
        return targetPos;
    }

    void startAgent(Object[] args, ContainerController mainContainer) {
        try {
            // Step 3: Start your agent(s)
            String agentName = "scout"; // Name of your agent
            String agentClass = "pr2mapAgent.Scout"; // Fully qualified name of your agent class

            AgentController agent = mainContainer.createNewAgent(agentName, agentClass, args);
            agent.start(); // Start the agent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPositionListener(PositionListener listener) {
        listeners.add(listener);
    }

    // Entfernen eines Listeners
    public void removePositionListener(PositionListener listener) {
        listeners.remove(listener);
    }

    // Benachrichtigung der Listener
    private void notifyPositionListeners(int[] oldPos, int[] currentPos) {
        for (PositionListener listener : listeners) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listener.onPositionUpdated(oldPos, currentPos, Arrays.equals(currentPos, targetPos), energy);
        }
    }
}