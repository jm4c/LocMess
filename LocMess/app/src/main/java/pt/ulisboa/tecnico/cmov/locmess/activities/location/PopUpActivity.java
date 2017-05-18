package pt.ulisboa.tecnico.cmov.locmess.activities.location;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
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

        Intent intent = new Intent(this, InboxActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Build the notification
        notification.setSmallIcon(R.drawable.ic_email_white_48dp);
        notification.setTicker("This is ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("here is the title");
        notification.setContentText("Main body dfsjkdfkjhdfkdf");
        notification.addAction(R.drawable.ic_done_white_24dp, "Accept", pendingIntent);
        notification.addAction(R.drawable.ic_close_white_24dp, "Decline", pendingIntent);



        notification.setContentIntent(pendingIntent);

        //builds notification and issues it

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());

    }
}


