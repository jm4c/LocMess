package pt.ulisboa.tecnico.cmov.locmess;

import android.app.Application;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.model.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.Message;
import pt.ulisboa.tecnico.cmov.locmess.model.Policy;
import pt.ulisboa.tecnico.cmov.locmess.model.ProfileKeypair;
import pt.ulisboa.tecnico.cmov.locmess.model.TimeWindow;

import static pt.ulisboa.tecnico.cmov.locmess.utils.HashUtils.hashInText;

/**
 * This is a Custom Application Class that extends the class Application, which is a Singleton.
 * This custom class allows the developers to access the main application objects from every activity.
 */

public class LocMessApplication extends Application {

    private String SERVER_URL = "http://194.168.1.81:38864";
    public static final boolean LOGIN_ACTIVE_FLAG = true;

    public boolean forceLoginFlag = false;




    private List<ProfileKeypair> keypairs;
    private List<String> availableKeys;
    private List<Message> inboxMessages;
    private List<Message> outboxCentralizedMessages;
    private List<Message> outboxDecentralizedMessages;

    private List<Location> locations;
    private LatLng currentLocation;

    private String keysHash;
    private String locationsHash;

    public LocMessApplication() {
        //TODO if exists in storage, load it
        this.keypairs = new ArrayList<>();
        this.availableKeys = new ArrayList<>();
        this.inboxMessages = new ArrayList<>();
        this.outboxCentralizedMessages = new ArrayList<>();
        this.outboxDecentralizedMessages = new ArrayList<>();

        this.locations = new ArrayList<>();
        try {
            keysHash = hashInText(availableKeys, null);
            locationsHash = hashInText(locations, null);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Locations


    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
        generateLocationsHash();
    }

    public void addLocation(Location location) {
        locations.add(location);
    }

    public Location getLocation(int pos) {
        return locations.get(pos);
    }

    public List<String> listLocations() {
        List<String> locationNames = new ArrayList<>();
        for (Location location : locations)
            locationNames.add(location.getName());
        return locationNames;
    }

    public void removeLocation(String name) {
        for (Location loc : locations) {
            if (loc.getName().equals(name)) {
                locations.remove(loc);
            }
        }
    }


    public String getLocationsHash() {
        return locationsHash;
    }

    public void generateLocationsHash() {
        try {
            this.locationsHash = hashInText(getLocations(), null);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //Profile keypairs
    public List<ProfileKeypair> getProfileKeypairs() {
        return keypairs;
    }

    public void setKeypairs(List<ProfileKeypair> keypairs) {
        this.keypairs = keypairs;
    }

    public void addKeyPair(String keyName, String value) {
        keypairs.add(new ProfileKeypair(keyName, value));
    }

    public void removeKeyPair(String keyname) {
        for (ProfileKeypair keypair : keypairs) {
            if (keypair.getKey().equals(keyname)) {
                keypairs.remove(keypair);
            }
        }
    }

    public  List<String> listKeyNames() {
        List<String> keyNames = new ArrayList<>();
        for (ProfileKeypair keypair : keypairs) {
            keyNames.add(keypair.getKey());
        }
        return keyNames;
    }

    public List<String> listKeyValues(){
        List<String> keyValues = new ArrayList<>();

        for(ProfileKeypair keyvalue : keypairs){
            keyValues.add(keyvalue.getValue());
        }
        return keyValues;
    }


    //Inbox Messages

    public void addInboxMessage(String title, String content, String owner, Location location, TimeWindow timeWindow, boolean isCentralized, Policy policy) {
        inboxMessages.add(new Message(title, content, owner, location, timeWindow, isCentralized, policy));
    }

    public void removeInboxMessage(String title, String owner) { //TODO podem existir mensagens com o mesmo titulo?? se sim ver o owner? ou o mesmo owner pode ter 2 msgs com o mesmo titulo?
        for (Message msg : inboxMessages) {
            // if(msg.getTitle().equals(title) && (msg.getOwner().equals(owner)))
            if (msg.getTitle().equals(title)) {
                inboxMessages.remove(msg);
            }
        }
    }

    public List<Message> getInboxMessages() {
        return inboxMessages;
    }

    public void setInboxMessages(List<Message> inboxMessages) {
        this.inboxMessages = inboxMessages;
    }


    //Outbox Messages


    public List<Message> getOutboxCentralizedMessages() {
        return outboxCentralizedMessages;
    }

    public void setOutboxCentralizedMessages(List<Message> outboxCentralizedMessages) {
        this.outboxCentralizedMessages = outboxCentralizedMessages;
    }

    public List<Message> getOutboxDecentralizedMessages() {
        return outboxDecentralizedMessages;
    }

    public void setOutboxDecentralizedMessages(List<Message> outboxDecentralizedMessages) {
        this.outboxDecentralizedMessages = outboxDecentralizedMessages;
    }

    public void addOutboxMessage(Message message){
        if(message.isCentralized()) {
            outboxCentralizedMessages.add(message);
        }else {
            outboxDecentralizedMessages.add(message);
        }
    }

    public void replaceOutboxMessage(Message message, int position) {
        if(message.isCentralized()) {
            outboxCentralizedMessages.set(position, message);
        }else {
            outboxDecentralizedMessages.set(position, message);
        }
    }


    public void removeOutboxMessage(String title, Boolean isCentralized) {
        if(isCentralized) {
            for (Message msg : outboxCentralizedMessages) {
                if (msg.getTitle().equals(title)) {
                    outboxCentralizedMessages.remove(msg);
                }
            }
        }else {
            for (Message msg : outboxDecentralizedMessages) {
                if (msg.getTitle().equals(title)) {
                    outboxDecentralizedMessages.remove(msg);
                }
            }
        }
    }


    //Available Keys (keys that might not used by the user but they exist in the server) ?
    public List<String> getAvailableKeys() {
        return availableKeys;
    }

    public void setAvailableKeys(List<String> availableKeys) {
        this.availableKeys = availableKeys;
        generateKeysHash();
    }


    public String getKeysHash() {
        return keysHash;
    }

    public void generateKeysHash() {
        try {
            this.keysHash = hashInText(getAvailableKeys(), null);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getServerURL() {
        return SERVER_URL;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
        Toast.makeText(this, currentLocation.toString(), Toast.LENGTH_LONG).show();
    }


}
