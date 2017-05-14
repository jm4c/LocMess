package pt.tecnico.ulisboa.cmov.lmserver;

import jdk.nashorn.internal.parser.Token;
import pt.tecnico.ulisboa.cmov.lmserver.types.Account;
import pt.tecnico.ulisboa.cmov.lmserver.types.Location;
import pt.tecnico.ulisboa.cmov.lmserver.types.LoginToken;
import pt.tecnico.ulisboa.cmov.lmserver.types.Message;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    public byte[] getAccountSalt(String username) {
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

        if (salt == null) {
            System.out.println("LOG: Login '" + username + "' failed. Reason: Wrong password.");
            return -2;
        }
        String hashedPassword = hashInText(password, salt);

        if (getAccount(username).getHashedPassword().equals(hashedPassword)) {
            LoginToken token = new LoginToken(username);
            tokens.add(token);
            System.out.println("LOG: Session " + token.getSessionID() + " created for user " + username + ".");
            return token.getSessionID();
        }
        System.out.println("LOG: Login '" + username + "' failed. Reason: Wrong password.");
        return -1;
    }

    //Messages


    //Locations
    public boolean addLocation(Location location) throws IOException, NoSuchAlgorithmException {
        boolean result = locations.add(location);
        locationsHash = hashInText(locations, null);
        return result;

    }

    public boolean removeLocation(String locationName) throws IOException, NoSuchAlgorithmException {
        Location location = getLocation(locationName);
        boolean result = locations.remove(location);
        profileKeysHash = hashInText(profileKeys, null);
        return result;
    }

    public Location getLocation(String name) {
        for (Location location : locations)
            if (location.getName().equals(name))
                return location;
        return null;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public String getLocationsHash() {
        return locationsHash;
    }

    public boolean locationExists(Location location) {
        if (getLocation(location.getName()) != null)
            return true;
        return false;
    }

    //Profile Keys
    public List<String> getProfileKeys() {
        return profileKeys;
    }

    public void addProfileKey(String profileKey) throws IOException, NoSuchAlgorithmException {
        profileKeys.add(profileKey);
        profileKeysHash = hashInText(profileKeys, null);
    }


    public String getProfileKeysHash() {
        return profileKeysHash;
    }

    public boolean profileKeyExists(String profileKey) {
        for (String key :
                getProfileKeys()) {
            if (profileKey.equals(key)) {
                return true;
            }
        }
        return false;
    }

    //Login tokens

    public List<LoginToken> getTokens() {
        return tokens;
    }

    public LoginToken getToken(int id) {
        for (LoginToken token : tokens) {
            if (token.getSessionID() == id)
                return token;
        }
        return null;
    }

    public LoginToken getToken(String username) {
        for (LoginToken token : tokens) {
            if (token.getUsername().equals(username))
                return token;
        }
        return null;
    }

    public boolean tokenExists(int id) {
        LoginToken token = getToken(id);
        return tokenExists(token);
    }

    public boolean tokenExists(String username) {
        LoginToken token = getToken(username);
        return tokenExists(token);
    }

    public boolean tokenExists(LoginToken token) {
        if (token != null)
            // if token expires, remove it from list
            if (token.getExpiration().before(Timestamp.valueOf(LocalDateTime.now()))) {
                System.out.println("Token expired. Removing token with session ID " + token.getSessionID());
                tokens.remove(token);
            } else
                return true;
        return false;
    }

    public void removeToken(LoginToken token) {
        tokens.remove(token);
    }
}