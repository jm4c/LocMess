package pt.ulisboa.tecnico.cmov.locmess.model.containers;

import java.io.Serializable;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;


public class MessagesContainer implements Serializable {

    private List<Message> messages;
    private final long whenReceived = System.currentTimeMillis();

    public MessagesContainer() {
    }

    public MessagesContainer(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean addMessage(Message message){
        return messages.add(message);
    }

    public long getWhenReceived() {
        return whenReceived;
    }
}
