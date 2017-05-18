package pt.ulisboa.tecnico.cmov.locmess.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.profiles.AddProfileKeyTask;
import pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.profiles.RemoveProfileKeyTask;


public class ProfileKeyManagerService extends Service {
    private LocMessApplication application;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = (LocMessApplication) getApplicationContext();

        while (!application.queueKeyActions.isEmpty()) {
            LocMessApplication.ProfileKeyAction keyAction = application.queueKeyActions.peek();
            try {
                Boolean result;
                if ((Boolean) keyAction.getValue()) { // if true add to server
                    AddProfileKeyTask addTask = new AddProfileKeyTask(this);
                    addTask.execute((String) keyAction.getKey());
                    result = addTask.get();
                } else { //else remove from server
                    RemoveProfileKeyTask removeTask = new RemoveProfileKeyTask(this);
                    removeTask.execute((String) keyAction.getKey());
                    result = removeTask.get();
                }

                // if successful remove key/action from queue
                if (result)
                    application.queueKeyActions.poll();
                else
                    stopSelf(); // if problem with server, try again later
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                stopSelf(); // if problem with server, try again later
            }
        }
        stopSelf(); //done for now, turn off service
    }


}
