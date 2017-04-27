package pt.ulisboa.tecnico.cmov.locmess;

import android.app.Application;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.model.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.Message;
import pt.ulisboa.tecnico.cmov.locmess.model.ProfileKeypair;
import pt.ulisboa.tecnico.cmov.locmess.outbox.OutboxActivity;

import static pt.ulisboa.tecnico.cmov.locmess.utils.HashUtils.hashInText;

/**
 * This is a Custom Application Class that extends the class Application, which is a Singleton.
 * This custom class allows the developers to access the main application objects from every activity.
 */

public class LocMessApplication extends Application {

    private List<ProfileKeypair> keypairs;
    private List<String> availableKeys;
    private List<Message> inboxMessages;
    private List<Message> outboxMessages;
    private List<Location> locations;

    private String keysHash;
    private String locationsHash;

    public LocMessApplication() {
        //TODO if exists in storage, load it
        this.keypairs = new ArrayList<>();
        this.availableKeys = new ArrayList<>();
        this.inboxMessages = new ArrayList<>();
        this.outboxMessages = new ArrayList<>();
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
    }

    public void addLocation(String name, Double longitude, Double latitude, int radius) {
        locations.add(new Location(name, longitude, latitude, radius));
    }

    public void removeLocationArray(String name) {
        for (Location loc : locations) {
            if (loc.getName().equals(name)) {
                locations.remove(loc);
            }
        }
    }

    public void listLocation() { /*TODO Tera que ser alterada dependendo como se quer mostrar a informacao*/
        for (Location loc : locations) {
            System.out.println(loc.getName());
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
    public List<ProfileKeypair> getKeypairs() {
        return keypairs;
    }

    public void setKeypairs(List<ProfileKeypair> keypairs) {
        this.keypairs = keypairs;
    }

    //Inbox Messages
    public List<Message> getInboxMessages() {
        return inboxMessages;
    }

    public void setInboxMessages(List<Message> inboxMessages) {
        this.inboxMessages = inboxMessages;
    }

    //Outbox Messages
    public List<Message> getOutboxMessages() {
        return outboxMessages;
    }

    public void setOutboxMessages(List<Message> outboxMessages) {
        this.outboxMessages = outboxMessages;
    }

    //Available Keys (keys that might not used by the user but they exist in the server)
    public List<String> getAvailableKeys() {
        return availableKeys;
    }


    public void setAvailableKeys(List<String> availableKeys) {
        this.availableKeys = availableKeys;
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
}
