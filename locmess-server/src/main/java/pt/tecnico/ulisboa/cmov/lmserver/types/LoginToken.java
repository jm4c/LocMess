package pt.tecnico.ulisboa.cmov.lmserver.types;

import pt.tecnico.ulisboa.cmov.lmserver.Singleton;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;


public class LoginToken implements Serializable{
    private static int counter;

    private int sessionID;

    private String username;

    private Timestamp expiration = Timestamp.valueOf(LocalDateTime.now().plusHours(2));

    public LoginToken(String username) {
        Singleton singleton = Singleton.getInstance();
        this.username = username;

        //remove token if it already exists token for user
        singleton.removeToken(singleton.getToken(username));

        do {
            counter++;
            //reset counter if about to overflow
            if(counter == Integer.MAX_VALUE) {
                counter = 1;
            }
        }while (singleton.tokenExists(counter));

        sessionID = counter;
    }

    public int getSessionID() {
        return sessionID;
    }

    public String getUsername() {
        return username;
    }

    public Timestamp getExpiration() {
        return expiration;
    }
}
