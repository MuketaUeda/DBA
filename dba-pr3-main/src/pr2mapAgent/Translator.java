package pr2mapAgent;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Translator extends Agent {
    @Override
    protected void setup() {
        // Add behavior to handle translations
        addBehaviour(new TranslationBehaviour(this));
    }

    public void startAgent(ContainerController mainContainer) {
        try {
            AgentController agent = mainContainer.createNewAgent("Translator", "pr2mapAgent.Translator", null);
            agent.start(); // Start the agent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String translate(String message) {
        // Simple translation logic
        if (message.startsWith("Bro") && message.endsWith("En Plan")) {
            // Agent to Santa translation
            return message.replace("Bro", "Rakas Joulupukki")
                    .replace("En Plan", "Kiitos");
        } else if (message.startsWith("Hyvää joulua") && message.endsWith("Nähdään pian")) {
            // Santa to Agent translation
            return message.replace("Hyvää joulua", "Bro")
                    .replace("Nähdään pian", "En Plan");
        }
        return message;
    }
}