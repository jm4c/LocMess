package pt.ulisboa.tecnico.cmov.locmess;

import android.app.Application;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.model.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.Message;
import pt.ulisboa.tecnico.cmov.locmess.model.Policy;
import pt.ulisboa.tecnico.cmov.locmess.model.TimeWindow;
import pt.ulisboa.tecnico.cmov.locmess.model.ProfileKeypair;
import pt.ulisboa.tecnico.cmov.locmess.outbox.OutboxActivity;

import static pt.ulisboa.tecnico.cmov.locmess.utils.HashUtils.hashInText;

/**
 * This is a Custom Application Class that extends the class Application, which is a Singleton.
 * This custom class allows the developers to access the main application objects from every activity.
 */

public class LocMessApplication extends Application {

    private String SERVER_URL = "http://192.168.1.7:38864";

    private List<ProfileKeypair> keypairs;
    private List<String> availableKeys;
    private List<Message> inboxMessages;
    private List<Message> outboxMessages;
    private List<Location> locations;
    private LatLng currentLocation;

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
    public List<ProfileKeypair> getProfileKeypairs() {
        return keypairs;
    }

    public void setKeypairs(List<ProfileKeypair> keypairs) {
        this.keypairs = keypairs;
    }

    public void addKeyPair(String keyName, String value){
        keypairs.add(new ProfileKeypair(keyName,value));
    }

    public void removeKeyPair(String keyname){ //TODO e possivel remover keyPair??
        for (ProfileKeypair keypair : keypairs) {
            if (keypair.getKey().equals(keyname)) {
                keypairs.remove(keypair);
            }
        }
    }

    public void listKeys(){ //TODO  Analogo ao problema das locations
        for (ProfileKeypair keypair : keypairs) {
            System.out.println(keypair.getKey() + ":" + keypair.getValue());
        }
    }
    //Inbox Messages

    public void addInboxMessage(String title, String content, String owner, Location location, TimeWindow timeWindow, boolean isCentralized, Policy policy){
            inboxMessages.add(new Message(title,content,owner,location,timeWindow,isCentralized,policy));
    }

    public void removeInboxMessage(String title,String owner){ //TODO podem existir mensagens com o mesmo titulo?? se sim ver o owner? ou o mesmo owner pode ter 2 msgs com o mesmo titulo?
        for(Message msg: inboxMessages){
           // if(msg.getTitle().equals(title) && (msg.getOwner().equals(owner)))
            if(msg.getTitle().equals(title)){
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



    public List<Message> getOutboxMessages() {
        return outboxMessages;
    }

    public void listOutMessages(){ // TODO Primeiro centralized messages e depois decentralized.
        List<Message> decentralizedMsgList = new ArrayList<>();
        for(Message msg : outboxMessages){
            if(msg.isCentralized()){
                System.out.println(msg.getTitle());
            }
            decentralizedMsgList.add(msg);
        }
        for(Message msg : decentralizedMsgList){
            System.out.println(msg.getTitle());
        }
    }

    public void removeOutMessage(String title){
        for( Message msg : outboxMessages){
            if(msg.getTitle().equals(title)){
                outboxMessages.remove(msg);
            }
        }
    }


    public void setOutboxMessages(List<Message> outboxMessages) {
        this.outboxMessages = outboxMessages;
    }

    public void updateOutMessage(){  // TODO o que e possivel de se alterar numa msg?(update)

    }

    //Available Keys (keys that might not used by the user but they exist in the server) ?
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
