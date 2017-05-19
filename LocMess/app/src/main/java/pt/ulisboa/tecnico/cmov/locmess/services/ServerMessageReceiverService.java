package pt.ulisboa.tecnico.cmov.locmess.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.ToolbarActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.model.containers.LocationsContainer;
import pt.ulisboa.tecnico.cmov.locmess.model.containers.MessagesContainer;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Profile;
import pt.ulisboa.tecnico.cmov.locmess.receivers.NotificationReceiver;
import pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.locations.GetLocationsTask;
import pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.messages.GetMessageTask;


public class ServerMessageReceiverService extends Service {

    private final static int MIN_UNIQUE_ID = 2000;
    private final static int MAX_UNIQUE_ID = 3999;
    private static final String ACCEPTED_MESSAGE = "locmess.intent.action.RECEIVED_MESSAGE";
    private static final String DECLINED_MESSAGE = "locmess.intent.action.DECLINED_MESSAGE";

    private LocMessApplication application;
    private int sessionID;
    private static int uniqueID;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize variables
        application = (LocMessApplication) getApplicationContext();
        sessionID = application
                .getSharedPreferences("LocMess", MODE_PRIVATE)
                .getInt("session", 0);
        updateLocations();
        List<Location> currentLocations = getUserCurrentLocations();
        if (currentLocations != null) {
            getNewMessages(currentLocations);
        }
        stopSelf();
    }

    private void getNewMessages(List<Location> currentLocations) {
        MessagesContainer bigMessagesContainer = new MessagesContainer();
        for (Location location : currentLocations) {
            GetMessageTask task = new GetMessageTask(this, location, application.getProfile());
            task.execute();
            MessagesContainer messageContainer = null;
            try {
                messageContainer = task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (messageContainer != null)
                bigMessagesContainer.addMessageContainer(messageContainer);
        }

        if (!bigMessagesContainer.isEmpty()) {
            createNotifications(bigMessagesContainer);
        }
    }

    private void createNotifications(MessagesContainer messageContainer) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        for (Message message : messageContainer.getMessages()) {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this);

            Intent acceptIntent = new Intent(this, NotificationReceiver.class); //switch for broadcast receiver
            acceptIntent.putExtra("message", message);
            acceptIntent.putExtra("ID", uniqueID);
            acceptIntent.setAction(ACCEPTED_MESSAGE);
            PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, uniqueID, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent declineIntent = new Intent(this, NotificationReceiver.class); //switch for broadcast receiver
            acceptIntent.putExtra("ID", uniqueID);
            declineIntent.setAction(DECLINED_MESSAGE);
            PendingIntent declinePendingIntent = PendingIntent.getBroadcast(this, uniqueID + 10000, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            notification.setAutoCancel(true).setSmallIcon(R.drawable.ic_email_white_48dp)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("Received new message from " + message.getOwner())
                    .setContentTitle(message.getTitle())
                    .setContentText(message.getOwner())
                    .addAction(R.drawable.ic_done_white_24dp, "Accept", acceptPendingIntent)
                    .addAction(R.drawable.ic_close_white_24dp, "Decline", declinePendingIntent)
                    .setAutoCancel(true);

            notificationManager.notify(uniqueID, notification.build());
            uniqueID++;
            if (uniqueID >= MAX_UNIQUE_ID)
                uniqueID = MIN_UNIQUE_ID;

        }
    }

    private List<Location> getUserCurrentLocations() {
        double currentLatitude = 0;
        double currentLongitude = 0;
        if (application.getCurrentLocation() != null) {
            currentLatitude = application.getCurrentLocation().latitude;
            currentLongitude = application.getCurrentLocation().longitude;
        }

        List<Location> locations = new ArrayList<>();
        float[] distance = new float[2];

        for (Location location : application.getLocations()) {
            if (location.getSsidList() == null) { //if geo location compare to current geo location
                android.location.Location.distanceBetween(currentLatitude, currentLongitude, location.getLatitude(), location.getLongitude(), distance);
                if (distance[0] < location.getRadius()) {
                    locations.add(location);
                }
            } else { //if ssid check if a ssid in the list matches
                List<String> locationSSIDsInRange = location.getSsidList();
                locationSSIDsInRange.retainAll(getCurrentSsids());
                if(!locationSSIDsInRange.isEmpty()){
                    locations.add(location);
                }
            }
        }

        return locations;
    }


    private void updateLocations() {
        GetLocationsTask task = new GetLocationsTask(application);
        task.execute();
        LocationsContainer serverResult = null;
        try {
            serverResult = task.get();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (serverResult != null) {
            application.setLocationsContainer(serverResult);
        }
    }
    public List<String> getCurrentSsids() {
        List<String> ssids = new ArrayList<>();

        // get the wifi name
        final WifiManager wifiManager = (WifiManager) application.getSystemService(WIFI_SERVICE);

        for (ScanResult scanResult: wifiManager.getScanResults()) {
            ssids.add(scanResult.SSID);
        }

        return ssids;
    }


}
