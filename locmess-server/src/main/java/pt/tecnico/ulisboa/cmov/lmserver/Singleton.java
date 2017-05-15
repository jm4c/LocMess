package pt.tecnico.ulisboa.cmov.lmserver;

import pt.tecnico.ulisboa.cmov.lmserver.types.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static pt.tecnico.ulisboa.cmov.lmserver.utils.CryptoUtils.getSalt;
import static pt.tecnico.ulisboa.cmov.lmserver.utils.HashUtils.hashInText;

// File Name: Singleton.java
public class Singleton {

    private static Singleton singleton = new Singleton();

    private List<Account> accounts;
    private Map<Message,Set<Account>> messages;
    private LocationsContainer locationsContainer;
    private AvailableKeysContainer availableKeysContainer;
    private Map<String, Set<Account>> profileKeysReferences;
    private List<LoginToken> tokens;


    private Singleton() {
        accounts = new ArrayList<>();
        messages = new HashMap<>();
        locationsContainer = new LocationsContainer();
        availableKeysContainer = new AvailableKeysContainer();
        profileKeysReferences = new HashMap<>();
        tokens = new ArrayList<>();

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
    public void addMessage(Message message){
        messages.put(message, new LinkedHashSet<>());
    }


    //Locations
    public boolean addLocation(Location location) throws IOException, NoSuchAlgorithmException {
        boolean result = locationsContainer.addLocation(location);
        locationsContainer.setLocationsHash(hashInText(locationsContainer.getLocations(), null));
        return result;

    }

    public boolean removeLocation(String locationName) throws IOException, NoSuchAlgorithmException {
        boolean result = locationsContainer.removeLocation(locationName);
        locationsContainer.setLocationsHash(hashInText(profileKeysReferences, null));
        return result;
    }

    public Location getLocation(String name) {
        return locationsContainer.getLocation(name);
    }

    public LocationsContainer getLocationsContainer() {
        return locationsContainer;
    }

    public String getLocationsHash() {
        return locationsContainer.getLocationsHash();
    }

    public boolean locationExists(Location location) {
        if (getLocation(location.getName()) != null)
            return true;
        return false;
    }

    //Profile Keys
    public List<String> getProfileKeysReferences() {
        return new ArrayList<>(profileKeysReferences.keySet());
    }

    public boolean addProfileKey(String profileKey, int sessionID) throws IOException, NoSuchAlgorithmException {
        Account account = getAccountFromToken(sessionID);
        if (profileKeysReferences.containsKey(profileKey)) {
            profileKeysReferences.get(profileKey).add(account);
            System.out.println("LOG: Profile key '" + profileKey + "' reference count was incremented. Count: " + profileKeysReferences.get(profileKey).size());

            return false;
        } else {
            profileKeysReferences.put(profileKey, new LinkedHashSet<>());
            profileKeysReferences.get(profileKey).add(account);
            availableKeysContainer.addKey(profileKey);
            availableKeysContainer.setKeysHash(hashInText(profileKeysReferences, null));
            System.out.println("LOG: Profile key '" + profileKey + "' added successfully.");

            return true;
        }
    }

    private Account getAccountFromToken(int sessionID) {
        return getAccount(getToken(sessionID).getUsername());
    }

    public boolean removeProfileKey(String profileKey, int sessionID) throws IOException, NoSuchAlgorithmException {
        Account account = getAccountFromToken(sessionID);
        boolean result = profileKeysReferences.get(profileKey).remove(account);
        System.out.println("LOG: Profile key '" + profileKey + "' reference count was decremented. Count: " + profileKeysReferences.get(profileKey).size());

        if (profileKeysReferences.get(profileKey).size() <= 0) {
            profileKeysReferences.remove(profileKey);
            availableKeysContainer.removeKey(profileKey);
            availableKeysContainer.setKeysHash(hashInText(profileKeysReferences, null));
            return true;
        }

        return false;
    }


    public AvailableKeysContainer getAvailableKeysContainer() {
        return availableKeysContainer;
    }

    public String getProfileKeysHash() {
        return availableKeysContainer.getKeysHash();
    }

    public boolean profileKeyExists(String profileKey) {
        for (String key :
                getProfileKeysReferences()) {
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