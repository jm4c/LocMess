package pt.ulisboa.tecnico.cmov.locmess.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.model.types.SecureMessage;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        int notificationID = intent.getIntExtra("ID", 0);
        nm.cancel(notificationID);

        SecureMessage secureMessage = (SecureMessage) intent.getSerializableExtra("message");

        if(secureMessage != null){
            LocMessApplication application = (LocMessApplication) context.getApplicationContext();
            Log.d("NotificationReceiver", "Added new message to inbox");
            application.addInboxSecureMessage(secureMessage);
            application.addInboxMessage(secureMessage.getMessage());
        }

    }
}
