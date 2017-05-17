package pt.ulisboa.tecnico.cmov.locmess.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;


public class NotificationReceiver extends BroadcastReceiver {
    LocMessApplication application;

    @Override
    public void onReceive(Context context, Intent intent) {

        application = (LocMessApplication) context.getApplicationContext();

        Message message = (Message) intent.getSerializableExtra("message");

        if(message != null){
            application.addInboxMessage(message);
        }

    }
}
