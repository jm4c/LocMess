package pt.ulisboa.tecnico.cmov.locmess.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.io.Serializable;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by joaod on 17-May-17.
 */

public class MessageNotification implements Serializable {
    Message message;
    NotificationCompat.Builder notification;
    static int uniqueID;

    public MessageNotification(Message message) {
        this.message = message;
        uniqueID++;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public NotificationCompat.Builder getNotification() {
        return notification;
    }

    public void setNotification(NotificationCompat.Builder notification) {
        this.notification = notification;
    }

    public static int getUniqueID() {
        return uniqueID;
    }

    public static void setUniqueID(int uniqueID) {
        MessageNotification.uniqueID = uniqueID;
    }
}
