package pt.ulisboa.tecnico.cmov.locmess;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import pt.ulisboa.tecnico.cmov.locmess.model.containers.LocationsContainer;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Policy;
import pt.ulisboa.tecnico.cmov.locmess.model.types.ProfileKeypair;
import pt.ulisboa.tecnico.cmov.locmess.model.types.TimeWindow;
import pt.ulisboa.tecnico.cmov.locmess.services.ProfileKeyManagerService;

import static pt.ulisboa.tecnico.cmov.locmess.utils.HashUtils.hashInText;

/**
 * This is a Custom Application Class that extends the class Application, which is a Singleton.
 * This custom class allows the developers to access the main application objects from every activity.
 */

public class LocMessApplication extends Application {

    private String SERVER_URL = "http://192.168.1.7:38864";
    public static final boolean LOGIN_ACTIVE_FLAG = true;

    public boolean forceLoginFlag = false;




    private List<ProfileKeypair> keypairs;
    private List<String> availableKeys;
    public Queue<ProfileKeyAction> queueKeyActions; //TODO profile keys handler (in order to be able to change profiles offline) inside a service
    private List<Message> inboxMessages;
    private List<Message> outboxCentralizedMessages;
    private List<Message> outboxDecentralizedMessages;

    private LocationsContainer locationsContainer;

    private LatLng currentLocation;
    private String keysHash;

    public LocMessApplication() {
        //TODO if exists in storage, load it
        this.keypairs = new ArrayList<>();
        this.availableKeys = new ArrayList<>();
        this.inboxMessages = new ArrayList<>();
        this.outboxCentralizedMessages = new ArrayList<>();
        this.outboxDecentralizedMessages = new ArrayList<>();

        this.locationsContainer = new LocationsContainer();

    }

    // Locations

    public List<Location> getLocations() {
        return locationsContainer.getLocations();
    }

    public LocationsContainer getLocationsContainer() {
        return locationsContainer;
    }

    public void setLocationsContainer(LocationsContainer locationsContainer) {
        this.locationsContainer = locationsContainer;
    }

    public void addLocation(Location location) {
        locationsContainer.addLocation(location);
    }

    public Location getLocation(int pos) {
        return locationsContainer.getLocation(pos);
    }

    public List<String> listLocations() {
        return locationsContainer.listLocations();
    }

    public String getLocationsHash() {
        return locationsContainer.getLocationsHash();
    }

    //Profile keypairs
    public List<ProfileKeypair> getProfileKeypairs() {
        return keypairs;
    }


    public void setKeyPairs(List<ProfileKeypair> keypairs) {
        this.keypairs = keypairs;
    }

    public void addKeyAction(String keyName, Boolean isActionAdding) {
        queueKeyActions.add(new ProfileKeyAction<>(keyName, isActionAdding));
        if(!isServiceRunning(ProfileKeyManagerService.class))
            startService(new Intent(this, ProfileKeyManagerService.class));
    }

    public  List<String> listProfileKeys() {
        List<String> keyNames = new ArrayList<>();
        for (ProfileKeypair keypair : keypairs) {
            keyNames.add(keypair.getKey());
        }
        return keyNames;
    }



    public List<String> listProfileValues(){
        List<String> keyValues = new ArrayList<>();

        for(ProfileKeypair keyvalue : keypairs){
            keyValues.add(keyvalue.getValue());
        }
        return keyValues;
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

    public final class ProfileKeyAction<String, Boolean> implements Map.Entry<String, Boolean> {


        private final String key;
        private Boolean action;
        public ProfileKeyAction(String key, Boolean action) {
            this.key = key;
            this.action = action;
        }

        @Override
        public String getKey() {
            return key;
        }


        @Override
        public Boolean getValue() {
            return action;
        }

        @Override
        public Boolean setValue(Boolean value) {
            Boolean old = this.action;
            this.action = value;
            return old;
        }
    }

}
