package pt.ulisboa.tecnico.cmov.locmess.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.activities.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NotificationReceiver extends BroadcastReceiver {
    LocMessApplication application;

    @Override
    public void onReceive(Context context, Intent intent) {

        application = (LocMessApplication) context.getApplicationContext();
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        int notificationID = intent.getIntExtra("ID", 0);
        nm.cancel(notificationID);

        Message message = (Message) intent.getSerializableExtra("message");

        if(message != null){
            Log.d("NotificationReceiver", "Added new message to inbox");
            application.addInboxMessage(message);
        }

    }
}
