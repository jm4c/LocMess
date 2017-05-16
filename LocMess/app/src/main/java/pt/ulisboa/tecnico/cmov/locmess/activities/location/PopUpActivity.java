package pt.ulisboa.tecnico.cmov.locmess.activities.location;

/**
 * Created by SONY on 10/05/2017.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.inbox.InboxActivity;

public class PopUpActivity extends Activity {

    private Button button;
    NotificationCompat.Builder notification;
    private static final int uniqueID = 454545;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        button = (Button) findViewById(R.id.msg_received);

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

    }

    public void showNotification(View view) {
        //Build the notification
        notification.setSmallIcon(R.drawable.ic_menu_white_48dp);
        notification.setTicker("This is ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("here is the title");
        notification.setContentText("Main body dfsjkdfkjhdfkdf");

        Intent intent = new Intent(this, InboxActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //builds notification and issues it

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());

    }
}


