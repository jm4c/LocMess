package pt.tecnico.ulisboa.cmov.lmserver;

import pt.tecnico.ulisboa.cmov.lmserver.types.Account;
import pt.tecnico.ulisboa.cmov.lmserver.types.Location;
import pt.tecnico.ulisboa.cmov.lmserver.types.Message;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static pt.tecnico.ulisboa.cmov.lmserver.utils.HashUtils.hash;
import static pt.tecnico.ulisboa.cmov.lmserver.utils.HashUtils.hashInText;

// File Name: Singleton.java
public class Singleton {

    private static Singleton singleton = new Singleton();

    private List<Account> accounts;
    private List<Message> messages;
    private List<Location> locations;
    private List<String> profileKeys;

    private String locationsHash;
    private String profileKeysHash;


    private Singleton() {
        accounts = new ArrayList<>();
        messages = new ArrayList<>();
        locations = new ArrayList<>();
        profileKeys = new ArrayList<>();
    }

    /* Static 'instance' method */
    public static Singleton getInstance() {
        return singleton;
    }

    //Accounts
    public boolean usernameExists(String username) {
        for (Account a : accounts)
            if (a.getUsername().equals(username)) {
                System.out.println("LOG: Account '" + username + "' already exists.");
                return true;
            }
        return false;
    }

    public void createAccount(String username, String hashedPassword, byte[] salt){
        Account account = new Account(username, hashedPassword, salt);
        accounts.add(account);
        System.out.println("LOG: Account '" + username + "' created.");
    }

    //Messages


    //Locations
    public void addLocation(Location location) throws IOException, NoSuchAlgorithmException {
        locations.add(location);
        locationsHash = hashInText(locations, null);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public String getLocationsHash() {
        return locationsHash;
    }

    //Profile Keys
    public List<String> getProfileKeys() {
        return profileKeys;
    }

    public String getProfileKeysHash() {
        return profileKeysHash;
    }
}