package pt.tecnico.ulisboa.cmov.lmserver.types;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by joaod on 10-Apr-17.
 */

@XmlRootElement(name = "profile-key")
public class ProfileKey implements Serializable {
    private String key;
    private int currentUsers;

    public ProfileKey(String key) {
        this.key = key;
        this.currentUsers = 0;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void incrementCurrentUsers(){
        currentUsers++;
    }

    public int getCurrentUsers() {
        return currentUsers;
    }
}
