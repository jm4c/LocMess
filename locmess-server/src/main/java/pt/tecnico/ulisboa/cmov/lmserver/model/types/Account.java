package pt.tecnico.ulisboa.cmov.lmserver.model.types;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.security.PublicKey;

@XmlRootElement(name = "account")
public class Account implements Serializable{

    private String username;
    private String hashedPassword;
    private byte[] serializedCurrentPublicKey;
    private byte[] salt;

    public Account(String username, String hashedPassword, byte[] salt) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public byte[] getSalt() {
        return salt;
    }



    //SECURITY
    public byte[] getSerializedCurrentPublicKey() {
        return serializedCurrentPublicKey;
    }

    public void setSerializedCurrentPublicKey(byte[] serializedCurrentPublicKey) {
        this.serializedCurrentPublicKey = serializedCurrentPublicKey;
    }
}
