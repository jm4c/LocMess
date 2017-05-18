package pt.ulisboa.tecnico.cmov.locmess.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.activities.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;


public class NotificationReceiver extends BroadcastReceiver {
    LocMessApplication application;

    @Override
    public void onReceive(Context context, Intent intent) {

        application = (LocMessApplication) context.getApplicationContext();

        Message message = (Message) intent.getSerializableExtra("message");

        if(message != null){
            Log.d("NotificationReceiver", "Added new message to inbox");
            application.addInboxMessage(message);
            Intent i = new Intent(application, InboxActivity.class);
            application.startActivity(i);
        }

    }
}
