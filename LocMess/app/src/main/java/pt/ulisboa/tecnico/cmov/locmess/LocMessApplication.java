package pt.ulisboa.tecnico.cmov.locmess;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import pt.ulisboa.tecnico.cmov.locmess.model.containers.AvailableKeysContainer;
import pt.ulisboa.tecnico.cmov.locmess.model.containers.LocationsContainer;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Profile;
import pt.ulisboa.tecnico.cmov.locmess.model.types.ProfileKeypair;
import pt.ulisboa.tecnico.cmov.locmess.services.MessageReceiverService;
import pt.ulisboa.tecnico.cmov.locmess.services.ProfileKeyManagerService;

/**
 * This is a Custom Application Class that extends the class Application, which is a Singleton.
 * This custom class allows the developers to access the main application objects from every activity.
 */

public class LocMessApplication extends Application {


    private String SERVER_URL = "http://192.168.1.7:38864";

    public boolean forceLoginFlag = false;


    private Profile profile;
    public Queue<ProfileKeyAction> queueKeyActions;
    private List<Message> inboxMessages;
    private List<Message> outboxCentralizedMessages;
    private List<Message> outboxDecentralizedMessages;

    private LocationsContainer locationsContainer;
    private AvailableKeysContainer availableKeysContainer;

    private LatLng currentLocation;

    public LocMessApplication() {
        //TODO if exists in storage, load it
        this.inboxMessages = new ArrayList<>();
        this.outboxCentralizedMessages = new ArrayList<>();
        this.outboxDecentralizedMessages = new ArrayList<>();
        this.queueKeyActions = new LinkedList<>();

        this.profile = new Profile();
        this.locationsContainer = new LocationsContainer();
        this.availableKeysContainer = new AvailableKeysContainer();

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

    public Profile getProfile() {
        return profile;
    }
    public List<ProfileKeypair> getProfileKeypairs() {
        return profile.getProfileKeypairs();
    }


    public void setKeyPairs(List<ProfileKeypair> keypairs) {
        profile.setProfileKeypairs(keypairs);
    }

    public void addKeyAction(String keyName, Boolean isActionAdding) {
        queueKeyActions.add(new ProfileKeyAction<>(keyName, isActionAdding));
        if(!isServiceRunning(ProfileKeyManagerService.class))
            startService(new Intent(this, ProfileKeyManagerService.class));
    }

    public  List<String> listProfileKeys() {
        List<String> keyNames = new ArrayList<>();
        for (ProfileKeypair keypair : profile.getProfileKeypairs()) {
            keyNames.add(keypair.getKey());
        }
        return keyNames;
    }

    public String getKeysHash() {
        return availableKeysContainer.getKeysHash();
    }



    public List<String> listProfileValues(){
        List<String> keyValues = new ArrayList<>();

        for(ProfileKeypair keyvalue : getProfileKeypairs()){
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


    public boolean addInboxMessage(Message message) {
        return inboxMessages.add(message);
    }

    public boolean removeInboxMessage(Message message) {
        return inboxMessages.remove(message);
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



    public List<String> getAvailableKeys() {
        List<String> availableKeys = availableKeysContainer.getKeys();

        //removes keys already used from auto-complete
        for (String key : listProfileKeys()) {
            if(availableKeys.contains(key)){
                availableKeys.remove(key);
            }
        }
        return null;
    }

    public AvailableKeysContainer getAvailableKeysContainer() {
        return availableKeysContainer;
    }



    public void setAvailableKeysContainer(AvailableKeysContainer availableKeysContainer) {
        this.availableKeysContainer = availableKeysContainer;
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

    public void startAlarmManager() {
        Log.d("AlarmManager", "inside");
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60; // 1 min in milliseconds

        Intent serviceIntent = new Intent(this, MessageReceiverService.class);
        PendingIntent servicePendingIntent =
                PendingIntent.getService(this,
                        999, // integer constant used to identify the service
                        serviceIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);  // FLAG to avoid creating a second service if there's already one running
        // there are other options like setInexactRepeating, check the docs
        alarmManager.setRepeating(
                AlarmManager.RTC,
                Calendar.getInstance().getTimeInMillis(),
                interval,
                servicePendingIntent
        );
    }

    public void cancelAlarmManager(){
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent serviceIntent = new Intent(this, MessageReceiverService.class);
        PendingIntent servicePendingIntent =
                PendingIntent.getService(this,
                        999, // integer constant used to identify the service
                        serviceIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);  // FLAG to avoid creating a second service if there's already one running

        alarmManager.cancel(servicePendingIntent);



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
