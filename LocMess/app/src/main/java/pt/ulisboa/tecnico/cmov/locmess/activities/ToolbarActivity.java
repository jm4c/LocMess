package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.location.LocationActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.login.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.outbox.OutboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.profile.ProfileActivity;
import pt.ulisboa.tecnico.cmov.locmess.services.GPSTrackerService;
import pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.profiles.GetAvailableKeysTask;
import pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.locations.GetLocationsTask;

public class ToolbarActivity extends AppCompatActivity {

    private static final int MENU_INBOX = 0;
    private static final int MENU_OUTBOX = 1;
    private static final int MENU_LOCATIONS = 2;
    private static final int MENU_PROFILE = 3;
    private static final int MENU_LOGOUT = 4;
    public LocMessApplication application;


    private static final String[] menu = {
            "Inbox",
            "Outbox",
            "Locations",
            "Profile",
            "Logout"
    };
    private static boolean  loggingOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (LocMessApplication) getApplicationContext();
        setContentView(R.layout.activity_main_menu);
        setupToolbar("LocMess - Main Menu");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (loggingOut) {
            loggingOut = false;
            logout();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_buttons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final List<String> menu = getMenu();

        CharSequence[] cs = menu.toArray(new CharSequence[menu.size()]);
        builder.setTitle("Main Menu")
                .setItems(cs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            changeActivity(which);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder.create().show();

        return super.onOptionsItemSelected(item);
    }

    protected void setupToolbar(String activityTitle) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(activityTitle);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    private void changeActivity(int which) throws ExecutionException, InterruptedException {
        Intent intent;
        switch (which) {
            case MENU_INBOX:
                if (ToolbarActivity.this instanceof InboxActivity)
                    return;
                intent = new Intent(ToolbarActivity.this, InboxActivity.class);
                break;
            case MENU_OUTBOX:
                if (ToolbarActivity.this instanceof OutboxActivity)
                    return;

                intent = new Intent(ToolbarActivity.this, OutboxActivity.class);
                break;
            case MENU_LOCATIONS:
                if (ToolbarActivity.this instanceof LocationActivity)
                    return;
                GetLocationsTask getLocationsTask = new GetLocationsTask(this);
                getLocationsTask.execute();
                getLocationsTask.get();
                intent = new Intent(ToolbarActivity.this, LocationActivity.class);
                break;
            case MENU_PROFILE:
                if (ToolbarActivity.this instanceof ProfileActivity)
                    return;
                GetAvailableKeysTask getAvailableKeysTask = new GetAvailableKeysTask(this);
                getAvailableKeysTask.execute();
                getAvailableKeysTask.get();

                intent = new Intent(ToolbarActivity.this, ProfileActivity.class);
                break;
            default:
                loggingOut = true;
                intent = new Intent(ToolbarActivity.this, LoginActivity.class);
                finish();
                break;
        }

        if (application.forceLoginFlag) {
            intent = new Intent(ToolbarActivity.this, LoginActivity.class);
            application.forceLoginFlag = false;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void logout() {
        Log.d("LOGOUT", "Logging out from application.");

        stopService(new Intent(this, GPSTrackerService.class));
        application.cancelAlarmManager();

        SharedPreferences sharedPreferences = application.getSharedPreferences("LocMess", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // To avoid automatically login
        editor.remove("username");
        editor.remove("password");
        editor.remove("session");
        editor.apply();
        application.clearPersonalData();

        Intent intent = new Intent(ToolbarActivity.this, LoginActivity.class);
        startService(intent);
    }

    public List<String> getMenu() {
        return Arrays.asList(menu);
    }


}
