package pr2mapAgent;

import jade.lang.acl.ACLMessage;

public class TranslationBehaviour extends jade.core.behaviours.CyclicBehaviour {
    private Translator translator;

    TranslationBehaviour(Translator translator) {
        this.translator = translator;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.blockingReceive();

        // Translate messages
        String translatedContent = translator.translate(msg.getContent());

        // Create a new message with translated content
        ACLMessage translatedMsg = new ACLMessage(msg.getPerformative());
        translatedMsg.setContent(translatedContent);
        translatedMsg.setSender(msg.getSender());
        translatedMsg.addReceiver(msg.getSender());

        myAgent.send(translatedMsg);
    }
}