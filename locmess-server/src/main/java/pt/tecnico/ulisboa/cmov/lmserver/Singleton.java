package pt.tecnico.ulisboa.cmov.lmserver;

import pt.tecnico.ulisboa.cmov.lmserver.types.Account;
import pt.tecnico.ulisboa.cmov.lmserver.types.Location;
import pt.tecnico.ulisboa.cmov.lmserver.types.LoginToken;
import pt.tecnico.ulisboa.cmov.lmserver.types.Message;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static pt.tecnico.ulisboa.cmov.lmserver.utils.CryptoUtils.getSalt;
import static pt.tecnico.ulisboa.cmov.lmserver.utils.HashUtils.hashInText;

// File Name: Singleton.java
public class Singleton {

    private static Singleton singleton = new Singleton();

    private List<Account> accounts;
    private List<Message> messages;
    private List<Location> locations;
    private List<String> profileKeys;
    private List<LoginToken> tokens;

    private String locationsHash;
    private String profileKeysHash;


    private Singleton() {
        accounts = new ArrayList<>();
        messages = new ArrayList<>();
        locations = new ArrayList<>();
        profileKeys = new ArrayList<>();
        tokens = new ArrayList<>();

        try {
            locationsHash = hashInText(locations, null);
            profileKeysHash = hashInText(profileKeys, null);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /* Static 'instance' method */
    public static Singleton getInstance() {
        return singleton;
    }

    //Accounts
    public boolean usernameExists(String username) {
        if (getAccount(username) != null) {
            return true;
        }
        return false;
    }

    public Account getAccount(String username) {
        for (Account a : accounts)
            if (a.getUsername().equals(username)) {
                return a;
            }
        return null;
    }

    public byte[] getAccountSalt(String username){
        return getAccount(username).getSalt();
    }

    public void createAccount(String username, String password) throws IOException, NoSuchAlgorithmException {
        byte[] salt = getSalt();
        String hashedPassword = hashInText(password, salt);
        Account account = new Account(username, hashedPassword, salt);
        accounts.add(account);
        System.out.println("LOG: Account '" + username + "' created.");
    }

    public int login(String username, String password) throws IOException, NoSuchAlgorithmException {

        byte[] salt = getAccountSalt(username);

        if(salt == null){
            System.out.println("LOG: Login '" +username+ "' failed. Reason: Wrong password.");
            return -2;
        }
        String hashedPassword = hashInText(password, salt);

        if(getAccount(username).getHashedPassword().equals(hashedPassword)){
            LoginToken token = new LoginToken(username);
            tokens.add(token);
            System.out.println("LOG: Session " + token.getSessionID() + " created for user " + username + ".");
            return token.getSessionID();
        }
        System.out.println("LOG: Login '" +username+ "' failed. Reason: Wrong password.");
        return -1;
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


    //Login tokens

    public List<LoginToken> getTokens() {
        return tokens;
    }

    public LoginToken getToken(int id) {
        for (LoginToken token: tokens) {
            if(token.getSessionID() ==  id)
                return token;
        }
        return null;
    }

    public LoginToken getToken(String username) {
        for (LoginToken token: tokens) {
            if(token.getUsername().equals(username))
                return token;
        }
        return null;
    }

    public boolean tokenExists(int id){
        if(getToken(id) != null)
            return true;
        return false;
    }

    public boolean tokenExists(String username){
        if(getToken(username) != null)
            return true;
        return false;
    }

    public void removeToken(LoginToken token){
        tokens.remove(token);
    }
}