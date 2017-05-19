package pt.tecnico.ulisboa.cmov.lmserver.model.containers;


import com.fasterxml.jackson.annotation.JsonIgnore;
import pt.tecnico.ulisboa.cmov.lmserver.model.types.Message;

import java.io.Serializable;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;



public class MessagesContainer implements Serializable {

    private List<Message> messages;
    private List<String> hashes;
    private List<Signature> signatures;

    public MessagesContainer() {
        messages = new ArrayList<>();
        hashes = new ArrayList<>();
        signatures = new ArrayList<>();
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

    public boolean addMessageContainer(MessagesContainer messagesContainer){
        return messages.addAll(messagesContainer.getMessages());
    }

    @JsonIgnore
    public boolean isEmpty(){
        return messages.isEmpty();
    }


    //SECURITY
    public List<String> getHashes() {
        return hashes;
    }

    public void setHashes(List<String> hashes) {
        this.hashes = hashes;
    }

    public List<Signature> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<Signature> signatures) {
        this.signatures = signatures;
    }
}

