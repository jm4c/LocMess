package pt.tecnico.ulisboa.cmov.lmserver.types;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;

@XmlRootElement(name = "account")
public class Account implements Serializable{

    private String username;
    private String hashedPassword;
    private byte[] salt;
    private HashMap<String, String> profile;

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


    public HashMap<String, String> getProfile() {
        return profile;
    }

    public void setProfile(HashMap<String, String> profile) {
        this.profile = profile;
    }
}
