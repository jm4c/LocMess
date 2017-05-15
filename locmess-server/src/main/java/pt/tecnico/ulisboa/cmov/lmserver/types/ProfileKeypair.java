package pt.tecnico.ulisboa.cmov.lmserver.types;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


@XmlRootElement(name = "profile-key")
public class ProfileKeypair implements Serializable {
    private String key;
    private String value;

    public ProfileKeypair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ProfileKeypair(){
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
