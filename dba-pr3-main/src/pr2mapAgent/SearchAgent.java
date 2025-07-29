package pr2mapAgent;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import java.util.*;

public class SearchAgent extends Agent {
    private AID santaAID;
    private AID rudolphAID;
    private AID translatorAID;
    private String secretCode;
   // private Map reindeerLocations = new Map(filename);
    private List<int[]> collectedReindeerLocations = new ArrayList<>();
    private int[] santaLocation;

    @Override
    protected void setup() {
        // Find other agents
        findAgents();

        // Add initial behavior to volunteer for mission
        addBehaviour(new VolunteerForMissionBehaviour());
    }

    private void findAgents() {
        // Find Santa Claus Agent
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("santa-service");
        template.addServices(sd);

        try {
            DFAgentDescription[] results = DFService.search(this, template);
            if (results.length > 0) {
                santaAID = results[0].getName();
            }

            // Find Rudolph Agent
            sd.setType("rudolph-service");
            template.addServices(sd);
            results = DFService.search(this, template);
            if (results.length > 0) {
                rudolphAID = results[0].getName();
            }

            // Find Translator Agent
            sd.setType("translator-service");
            template.addServices(sd);
            results = DFService.search(this, template);
            if (results.length > 0) {
                translatorAID = results[0].getName();
            }
        } catch (Exception e) {
            System.err.println("Error finding agents: " + e.getMessage());
        }
    }

    // Behavior to volunteer for the mission
    private class VolunteerForMissionBehaviour extends jade.core.behaviours.Behaviour {
        private int step = 0;

        @Override
        public void action() {
            switch (step) {
                case 0:
                    // Propose to Santa
                    ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);
                    propose.addReceiver(santaAID);
                    propose.setContent("Bro I want to volunteer for the reindeer rescue mission En Plan");
                    send(propose);
                    step++;
                    break;
                case 1:
                    // Wait for Santa's response
                    ACLMessage reply = receive();
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                            // Extract secret code
                            secretCode = extractSecretCode(reply.getContent());
                            System.out.println("Mission accepted with secret code: " + secretCode);
                            step++;
                        } else {
                            // Mission rejected
                            System.out.println("Mission rejected by Santa");
                            step = 3; // End
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
                    // Communicate with Rudolph
                    ACLMessage rudolphMsg = new ACLMessage(ACLMessage.REQUEST);
                    rudolphMsg.addReceiver(rudolphAID);
                    rudolphMsg.setContent(secretCode + ":GET_LOCATIONS");
                    send(rudolphMsg);
                    step++;
                    break;
                default:
                    // Mission complete or failed
                    break;
            }
        }

        @Override
        public boolean done() {
            return step > 2;
        }
    }

    // Helper method to extract secret code
    private String extractSecretCode(String message) {
        String[] parts = message.split("Secret code: ");
        if (parts.length > 1) {
            return parts[1].split(" ")[0]; // return the first part after the "Secret code: "
        }
        return null; // return null if the message doesn't include the code
    }
}