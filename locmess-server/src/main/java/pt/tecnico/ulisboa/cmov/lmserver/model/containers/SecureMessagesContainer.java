package pt.tecnico.ulisboa.cmov.lmserver.model.containers;


import com.fasterxml.jackson.annotation.JsonIgnore;
import pt.tecnico.ulisboa.cmov.lmserver.model.types.SecureMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class SecureMessagesContainer implements Serializable {

    private List<SecureMessage> secureMessages;

    public SecureMessagesContainer() {
        secureMessages = new ArrayList<>();
    }

    public SecureMessagesContainer(List<SecureMessage> secureMessages) {
        this.secureMessages = secureMessages;
    }

    public List<SecureMessage> getSecureMessages() {
        return secureMessages;
    }

    public void setSecureMessages(List<SecureMessage> secureMessages) {
        this.secureMessages = secureMessages;
    }

    public boolean addSecureMessage(SecureMessage secureMessage){
        return secureMessages.add(secureMessage);
    }

    public boolean addMessageContainer(SecureMessagesContainer secureMessagesContainer){
        return secureMessages.addAll(secureMessagesContainer.getSecureMessages());
    }

    @JsonIgnore
    public boolean isEmpty(){
        return secureMessages.isEmpty();
    }
}