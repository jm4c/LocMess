package pt.tecnico.ulisboa.cmov.lmserver;

import pt.tecnico.ulisboa.cmov.lmserver.types.Account;
import pt.tecnico.ulisboa.cmov.lmserver.types.Location;
import pt.tecnico.ulisboa.cmov.lmserver.types.Message;

import java.util.ArrayList;
import java.util.List;

// File Name: Singleton.java
public class Singleton {

    private static Singleton singleton = new Singleton();

    private List<Account> accounts;
    private List<Message> messages;
    private List<Location> locations;
    private List<String> keys;


    private Singleton() {
        accounts = new ArrayList<>();
        messages = new ArrayList<>();
        locations = new ArrayList<>();
        keys = new ArrayList<>();
    }

    /* Static 'instance' method */
    public static Singleton getInstance() {
        return singleton;
    }


    public boolean usernameExists(String username) {
        for (Account a : accounts)
            if (a.getUsername().equals(username))
                return true;
        return false;
    }

    public void createAccount(String username, String hashedPassword, byte[] salt){
        Account account = new Account(username, hashedPassword, salt);
        accounts.add(account);
    }
}