package pr2mapAgent;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HandleVolunteerRequestBehaviour extends Behaviour {
    Santa santa;
    boolean finished = false;

    HandleVolunteerRequestBehaviour(Santa santa) {
        this.santa = santa;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.blockingReceive();
        int state = santa.getCommunicationState();

        switch (state) {
            // Search Request from Search Agent: AGREE (+ Secret Code) / REFUSE
            case 0 -> {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    ACLMessage reply = msg.createReply();

                    if (santa.isTrustworthyAgent()) {
                        // Agent is trustworthy
                        reply.setPerformative(ACLMessage.AGREE);
                        reply.setContent("Hyvää joulua Trustworthy agent accepted. Secret code: " + santa.getSecretCode() + ". Nähdään pian");
                    } else {
                        // Agent is not trustworthy
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("Hyvää joulua Not trustworthy. Mission denied. Nähdään pian");
                    }

                    santa.setCommunicationState(1);

                    myAgent.send(reply);

                } else {
                    System.out.println("Error in the conversation protocol in state " + state);
                    myAgent.doDelete();
                }
            }

            // Santa-Position Request from Search Agent
            case 1 -> {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    ACLMessage reply = msg.createReply();

                    reply.setPerformative(ACLMessage.INFORM);
                    int[] position = santa.getPosition();
                    reply.setContent("Hyvää joulua I am at position (" + position[0] + ", " + position[1] + "). Nähdään pian");

                    myAgent.send(reply);

                } else {
                    System.out.println("Error in the conversation protocol in state " + state);
                    myAgent.doDelete();
                }
            }

            //send 'HoHoHo' Msg to Agent if he reached Santas position
            case 2 -> {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    ACLMessage reply = msg.createReply();

                    reply.setPerformative(ACLMessage.INFORM);

                    int[] santaPosition = santa.getPosition();

                    String content = msg.getContent();
                    String agentPositionStr = content.substring(content.indexOf("position (") + 10, content.indexOf(")"));
                    String[] parts = agentPositionStr.split(",\\s*");
                    int[] agentPosition = {Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};

                    if (!(santaPosition[0] == agentPosition[0] && santaPosition[1] == agentPosition[1])) {
                        System.out.println("Error: Agent reached wrong position");
                        myAgent.doDelete();
                    }

                    reply.setContent("Hyvää joulua HoHoHo! Nähdään pian");


                    finished = true;
                    myAgent.send(reply);

                } else {
                    System.out.println("Error in the conversation protocol in state " + state);
                    myAgent.doDelete();
                }
            }
        }
    }

    @Override
    public boolean done() {
        return finished;
    }
}
