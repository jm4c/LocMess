package pt.ulisboa.tecnico.cmov.locmess;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import pt.ulisboa.tecnico.cmov.locmess.services.ServerMessageReceiverService;
import pt.ulisboa.tecnico.cmov.locmess.services.ProfileKeyManagerService;

/**
 * This is a Custom Application Class that extends the class Application, which is a Singleton.
 * This custom class allows the developers to access the main application objects from every activity.
 */

public class LocMessApplication extends Application {


    private final static String SERVER_URL = "http://192.168.1.7:38864";

    private final static int TYPE_PROFILE = 1;
    private final static int TYPE_INBOX_MESSAGES = 2;
    private final static int TYPE_OUTBOX_CENTRALIZED_MESSAGES = 3;
    private final static int TYPE_OUTBOX_DECENTRALIZED_MESSAGES = 4;
    private final static int TYPE_QUEUE_PROFILE_ACTIONS = 5;


    public boolean forceLoginFlag = false;


    private Profile profile;
    private List<Message> inboxMessages;
    private List<Message> outboxCentralizedMessages;
    private List<Message> outboxDecentralizedMessages;

    private Queue<ProfileKeyAction> queueKeyActions;

    private LocationsContainer locationsContainer;
    private AvailableKeysContainer availableKeysContainer;

    private LatLng currentLocation;

    public LocMessApplication() {
        clearPersonalData();
        this.locationsContainer = new LocationsContainer();
        this.availableKeysContainer = new AvailableKeysContainer();
    }

    public void clearPersonalData() {
        this.profile = null;
        this.inboxMessages = null;
        this.outboxCentralizedMessages = null;
        this.outboxDecentralizedMessages = null;
        this.queueKeyActions = null;
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
        if(profile == null){
            profile = (Profile) load(TYPE_PROFILE);
            if(profile == null){
                profile = new Profile();
            }
            setProfile(profile);
        }
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        save(profile, TYPE_PROFILE);
    }

    public List<ProfileKeypair> getProfileKeypairs() {
        return getProfile().getProfileKeypairs();
    }

    public Queue<ProfileKeyAction> getQueueKeyActions() {
        if(queueKeyActions == null){
            queueKeyActions = (Queue<ProfileKeyAction>) load(TYPE_QUEUE_PROFILE_ACTIONS);
            if(queueKeyActions == null){
                queueKeyActions = new LinkedList<>();
            }
            setProfile(profile);
        }
        return queueKeyActions;
    }

    public void setQueueKeyActions(Queue<ProfileKeyAction> queueKeyActions) {
        this.queueKeyActions = queueKeyActions;
        save(queueKeyActions, TYPE_QUEUE_PROFILE_ACTIONS);
    }

    public void setKeyPairs(List<ProfileKeypair> keypairs) {
        getProfile().setProfileKeypairs(keypairs);
        setProfile(getProfile());
    }

    public void addKeyAction(String keyName, Boolean isActionAdding) {
        getQueueKeyActions().add(new ProfileKeyAction<>(keyName, isActionAdding));
        if (!isServiceRunning(ProfileKeyManagerService.class))
            startService(new Intent(this, ProfileKeyManagerService.class));
    }

    public List<String> listProfileKeys() {
        List<String> keyNames = new ArrayList<>();
        for (ProfileKeypair keypair : getProfile().getProfileKeypairs()) {
            keyNames.add(keypair.getKey());
        }
        return keyNames;
    }

    

    public List<String> listProfileValues() {
        List<String> keyValues = new ArrayList<>();

        for (ProfileKeypair keyvalue : getProfileKeypairs()) {
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
        boolean result = getInboxMessages().add(message);
        setInboxMessages(getInboxMessages());
        return result;
    }

    public boolean removeInboxMessage(Message message) {
        return inboxMessages.remove(message);
    }

    public List<Message> getInboxMessages() {
        if(inboxMessages == null){
            inboxMessages = (List<Message>) load(TYPE_INBOX_MESSAGES);
            if(inboxMessages == null){
                inboxMessages = new ArrayList<>();
            }
            setInboxMessages(inboxMessages);
        }
        return inboxMessages;
    }

    public void setInboxMessages(List<Message> inboxMessages) {
        this.inboxMessages = inboxMessages;
        save(inboxMessages, TYPE_INBOX_MESSAGES);
    }

    //Outbox Messages


    public List<Message> getOutboxCentralizedMessages() {
        if(outboxCentralizedMessages == null){
            outboxCentralizedMessages = (List<Message>) load(TYPE_OUTBOX_CENTRALIZED_MESSAGES);
            if(outboxCentralizedMessages == null){
                outboxCentralizedMessages = new ArrayList<>();
            }
            setOutboxCentralizedMessages(outboxCentralizedMessages);
        }
        return outboxCentralizedMessages;
    }


    public void setOutboxCentralizedMessages(List<Message> outboxCentralizedMessages) {
        this.outboxCentralizedMessages = outboxCentralizedMessages;
        save(outboxCentralizedMessages, TYPE_OUTBOX_CENTRALIZED_MESSAGES);
    }

    public List<Message> getOutboxDecentralizedMessages() {
        if(outboxDecentralizedMessages == null){
            outboxDecentralizedMessages = (List<Message>) load(TYPE_OUTBOX_DECENTRALIZED_MESSAGES);
            if(outboxDecentralizedMessages == null){
                outboxDecentralizedMessages = new ArrayList<>();
            }
            setOutboxDecentralizedMessages(outboxDecentralizedMessages);
        }
        return outboxDecentralizedMessages;
    }

    public void setOutboxDecentralizedMessages(List<Message> outboxDecentralizedMessages) {
        this.outboxDecentralizedMessages = outboxDecentralizedMessages;
        save(outboxDecentralizedMessages, TYPE_OUTBOX_DECENTRALIZED_MESSAGES);
    }

    public void addOutboxMessage(Message message) {
        List<Message> outboxMessages;
        if (message.isCentralized()) {
            outboxMessages = getOutboxCentralizedMessages();
            outboxMessages.add(message);
            setOutboxCentralizedMessages(outboxMessages);
        } else {
            outboxMessages = getOutboxDecentralizedMessages();
            outboxMessages.add(message);
            setOutboxDecentralizedMessages(outboxMessages);
        }
    }

    public void removeOutboxMessage(String title, Boolean isCentralized) {
        List<Message> outboxMessages;
        if (isCentralized) {
            outboxMessages = getOutboxCentralizedMessages();
            for (Message msg : outboxMessages) {
                if (msg.getTitle().equals(title)) {
                    outboxMessages.remove(msg);
                    setOutboxCentralizedMessages(outboxMessages);
                }
            }
        } else {
            outboxMessages = getOutboxDecentralizedMessages();
            for (Message msg : outboxMessages) {
                if (msg.getTitle().equals(title)) {
                    outboxMessages.remove(msg);
                    setOutboxDecentralizedMessages(outboxMessages);
                }
            }
        }
    }


    public List<String> getAvailableKeys(List<ProfileKeypair> profileKeypairs) {

        List<String> keys = new ArrayList<>();
        for (ProfileKeypair keypair : profileKeypairs) {
            keys.add(keypair.getKey());
        }

        List<String> availableKeys = availableKeysContainer.getKeys();

        //removes keys already used from auto-complete
        for (String key : keys) {
            if (availableKeys.contains(key)) {
                availableKeys.remove(key);
            }
        }
        return availableKeys;
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

        Intent serviceIntent = new Intent(this, ServerMessageReceiverService.class);
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

    public void cancelAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent serviceIntent = new Intent(this, ServerMessageReceiverService.class);
        PendingIntent servicePendingIntent =
                PendingIntent.getService(this,
                        999, // integer constant used to identify the service
                        serviceIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);  // FLAG to avoid creating a second service if there's already one running

        alarmManager.cancel(servicePendingIntent);
    }

    public void save(Object object, int type) {
        String filename = getFilename(type);
        if(filename == null)
            return;
        try {
            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            Log.d("DATA", "Wrote " + filename);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object load(int type) {
        String filename = getFilename(type);

        if(filename == null)
            return null;
        try {
            FileInputStream fis = openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object object = is.readObject();
            Log.d("DATA", "Read " + filename);

            is.close();
            fis.close();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFilename(int type) {
        String username = getSharedPreferences("LocMess", MODE_PRIVATE).getString("username", null);
        if(username == null)
            return null;
        String filename = "";
        switch (type) {
            case TYPE_PROFILE:
                filename = username + "_profile.dat";
                break;
            case TYPE_INBOX_MESSAGES:
                filename = username + "_inbox_messages.dat";
                break;
            case TYPE_OUTBOX_CENTRALIZED_MESSAGES:
                filename = username + "_outbox_centralized_messages.dat";
                break;
            case TYPE_OUTBOX_DECENTRALIZED_MESSAGES:
                filename = username + "_outbox_decentralized_messages.dat";
                break;
            case TYPE_QUEUE_PROFILE_ACTIONS:
                filename = username + "_queue_profile_actions.dat";
                break;
        }
        return filename;
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
