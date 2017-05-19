package pt.tecnico.ulisboa.cmov.lmserver;

import pt.tecnico.ulisboa.cmov.lmserver.model.containers.AvailableKeysContainer;
import pt.tecnico.ulisboa.cmov.lmserver.model.containers.LocationsContainer;

import pt.tecnico.ulisboa.cmov.lmserver.model.containers.MessagesContainer;
import pt.tecnico.ulisboa.cmov.lmserver.model.types.*;


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
    private Map<Message, Set<Account>> messagesMap;
    private LocationsContainer locationsContainer;
    private AvailableKeysContainer availableKeysContainer;
    private Map<String, Set<Account>> profileKeysReferences;
    private List<LoginToken> tokens;


    private Singleton() {
        accounts = new ArrayList<>();
        messagesMap = new HashMap<>();
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
    public boolean addMessage(Message message) {
        messagesMap.put(message, new LinkedHashSet<>());
        return messagesMap.containsKey(message);
    }

    public Map<Message, Set<Account>> getMessagesMap() {
        return messagesMap;
    }

    public MessagesContainer getUserMessagesContainer(int sessionID, String location, Profile profile) {
        Account account = getAccount(getToken(sessionID).getUsername());
        MessagesContainer messagesContainer = new MessagesContainer();

        removeExpiredMessages();

        System.out.println("LOG: Looking for messages for '" + account.getUsername() + "' in '" + location + "'.");

        for (Message message : messagesMap.keySet()) {
            if (!message.getLocation().getName().equals(location)) {
                System.out.println("LOG: '" + account.getUsername() + "' not in the same location as the message.");
                continue;
            }
            if (message.getOwner().equals(account.getUsername()))
                continue;
            if (messagesMap.get(message).contains(account)) {
                System.out.println("LOG: '" + account.getUsername() + "' already received this message.");
                continue;
            }

            try {
                if (!isTimeWindowValid(message.getTimeWindow())) {
                    continue;
                }
            } catch (Exception e) {
                System.out.println("LOG: Expired message caught!");
                continue;
            }
            if (!message.getPolicy().matches(profile)) {
                System.out.println("LOG: '" + account.getUsername() + "' doesn't match policy.");

                continue;
            }

            messagesContainer.addMessage(message);
            messagesMap.get(message).add(account);

        }

        return messagesContainer;
    }

    public boolean removeMessage(int sessionID, String messageTitle) {
        Message message = getMessage(messageTitle, getToken(sessionID).getUsername());
        if (message != null) {
            // if return from removing in HashMap is not null
            if (messagesMap.remove(message) != null) {
                System.out.println("LOG: " + getToken(sessionID).getUsername() + " removed the message \"" + message.getTitle() + "\".");
                return true;
            }
        }
        return false;
    }

    private Message getMessage(String messageTitle, String owner) {
        for (Message message: messagesMap.keySet()) {
            if(message.getTitle().equals(messageTitle) && message.getOwner().equals(owner))
                return message;
        }
        return null;
    }

    private Boolean isTimeWindowValid(TimeWindow timeWindow) throws Exception {
        Calendar currentTime = Calendar.getInstance();
        Calendar startTimeWindow = Calendar.getInstance();
        startTimeWindow.set(timeWindow.getStartYear(),
                timeWindow.getStartMonth(),
                timeWindow.getStartDay(),
                timeWindow.getStartHour(),
                timeWindow.getStartMinute());

        Calendar endTimeWindow = Calendar.getInstance();
        endTimeWindow.set(timeWindow.getEndYear(),
                timeWindow.getEndMonth(),
                timeWindow.getEndDay(),
                timeWindow.getEndHour(),
                timeWindow.getEndMinute());
        if (currentTime.after(endTimeWindow))
            throw new Exception();
        return currentTime.after(startTimeWindow) && currentTime.before(endTimeWindow);
    }

    //Locations
    public boolean addLocation(Location location) throws IOException, NoSuchAlgorithmException {
        boolean result = locationsContainer.addLocation(location);
        locationsContainer.setLocationsHash(hashInText(locationsContainer.getLocations(), null));
        return result;

    }

    public boolean removeLocation(String locationName) throws IOException, NoSuchAlgorithmException {
        boolean result = false;
        //check if location is used in any message being broadcast
        if (isLocationInMessage()) {
            result = locationsContainer.removeLocation(locationName);
            locationsContainer.setLocationsHash(hashInText(profileKeysReferences, null));
        }
        return result;
    }

    private boolean isLocationInMessage() {
        removeExpiredMessages();
        return false;
    }

    private void removeExpiredMessages() {
        Calendar currentTime = Calendar.getInstance();
        List<Message> messagesToRemove = new ArrayList<>();
        for (Message message :
                messagesMap.keySet()) {
            TimeWindow timeWindow = message.getTimeWindow();
            Calendar endTimeWindow = Calendar.getInstance();
            endTimeWindow.set(timeWindow.getEndYear(),
                    timeWindow.getEndMonth(),
                    timeWindow.getEndDay(),
                    timeWindow.getEndHour(),
                    timeWindow.getEndMinute());

            if (currentTime.after(endTimeWindow))
                messagesToRemove.add(message);
        }

        for (Message message : messagesToRemove) {
            messagesMap.remove(message);
        }

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