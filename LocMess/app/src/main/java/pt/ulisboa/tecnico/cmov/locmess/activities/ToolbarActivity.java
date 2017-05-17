package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.location.LocationActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.login.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.model.containers.AvailableKeysContainer;
import pt.ulisboa.tecnico.cmov.locmess.model.containers.LocationsContainer;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;
import pt.ulisboa.tecnico.cmov.locmess.activities.outbox.OutboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.profile.ProfileActivity;
import pt.ulisboa.tecnico.cmov.locmess.services.GPSTrackerService;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (LocMessApplication) getApplicationContext();

        setContentView(R.layout.activity_main_menu);
        setupToolbar("LocMess - Main Menu");

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
        Intent i;
        switch (which) {
            case MENU_INBOX:
                if (ToolbarActivity.this instanceof InboxActivity)
                    return;
                i = new Intent(ToolbarActivity.this, InboxActivity.class);
                break;
            case MENU_OUTBOX:
                if (ToolbarActivity.this instanceof OutboxActivity)
                    return;

                i = new Intent(ToolbarActivity.this, OutboxActivity.class);
                break;
            case MENU_LOCATIONS:
                if (ToolbarActivity.this instanceof LocationActivity)
                    return;
                GetLocationsTask getLocationsTask = new GetLocationsTask();
                getLocationsTask.execute();
                getLocationsTask.get();
                i = new Intent(ToolbarActivity.this, LocationActivity.class);
                break;
            case MENU_PROFILE:
                if (ToolbarActivity.this instanceof ProfileActivity)
                    return;
                GetAvailableKeysTask getAvailableKeysTask = new GetAvailableKeysTask();
                getAvailableKeysTask.execute();
                //getAvailableKeysTask.get(); // No need wait for the task to finish for now

                i = new Intent(ToolbarActivity.this, ProfileActivity.class);
                break;
            default: //LOGOUT
                stopService(new Intent(this, GPSTrackerService.class));

                SharedPreferences sharedPreferences = this.getSharedPreferences("LocMess", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // To avoid automatically login
                editor.remove("username");
                editor.remove("password");
                editor.remove("session");
                editor.apply();

                i = new Intent(ToolbarActivity.this, LoginActivity.class);
                break;
        }

        if (application.forceLoginFlag) {
            i = new Intent(ToolbarActivity.this, LoginActivity.class);
            application.forceLoginFlag = false;
        }

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public List<String> getMenu() {
        return Arrays.asList(menu);
    }




    /** HTTP methods Tasks ******************************************************************************************************/

    /**
     * Location methods
     */
    private class GetLocationsTask extends AsyncTask<Void, Void, LocationsContainer> {
        private String locationsHash;
        private int sessionID;
        private ProgressDialog dialog = new ProgressDialog(ToolbarActivity.this);


        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading Locations...");
            this.dialog.show();

            sessionID = application
                    .getSharedPreferences("LocMess", MODE_PRIVATE)
                    .getInt("session", 0);

            locationsHash = application.getLocationsHash();

            super.onPreExecute();
        }

        @Override
        protected LocationsContainer doInBackground(Void... params) {
            return application.getLocationsFromServer(sessionID, locationsHash);
        }


        @Override
        protected void onPostExecute(LocationsContainer locationsContainer) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (locationsContainer != null) {
                application.setLocationsContainer(locationsContainer);
            }

            super.onPostExecute(locationsContainer);
        }


        @Override
        protected void onCancelled() {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
            super.onCancelled();
        }

    }

    public class AddLocationTask extends AsyncTask<Location, Void, Boolean> {
        private int sessionID;
        private ProgressDialog dialog = new ProgressDialog(ToolbarActivity.this);


        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Adding new location...");
            this.dialog.show();

            sessionID = application
                    .getSharedPreferences("LocMess", MODE_PRIVATE)
                    .getInt("session", 0);


            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Location... params) {
            return application.postLocationToServer(sessionID, params[0]);
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(aBoolean);
        }


        @Override
        protected void onCancelled() {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
            super.onCancelled();
        }

    }

    public class RemoveLocationTask extends AsyncTask<Location, Void, Boolean> {
        private int sessionID;
        private ProgressDialog dialog = new ProgressDialog(ToolbarActivity.this);


        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Removing location...");
            this.dialog.show();

            sessionID = application
                    .getSharedPreferences("LocMess", MODE_PRIVATE)
                    .getInt("session", 0);


            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Location... params) {
            return application.deleteLocationFromServer(sessionID, params[0]);

        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(aBoolean);
        }


        @Override
        protected void onCancelled() {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
            super.onCancelled();
        }

    }


    /**
     * Profile key methods (remaining profile methods in ProfileKeyManagerService)
     */
    private class GetAvailableKeysTask extends AsyncTask<Void, Void, AvailableKeysContainer> {
        private String availableKeysHash;
        private int sessionID;
        private ProgressDialog dialog = new ProgressDialog(ToolbarActivity.this);


        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading Profile keys...");
            this.dialog.show();

            sessionID = application
                    .getSharedPreferences("LocMess", MODE_PRIVATE)
                    .getInt("session", 0);

            availableKeysHash = application.getAvailableKeysContainer().getKeysHash();

            super.onPreExecute();
        }

        @Override
        protected AvailableKeysContainer doInBackground(Void... params) {
            return application.getAvailableKeysFromServer(sessionID, availableKeysHash);


        }


        @Override
        protected void onPostExecute(AvailableKeysContainer availableKeysContainer) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (availableKeysContainer != null) {
                application.setAvailableKeysContainer(availableKeysContainer);
            }

            super.onPostExecute(availableKeysContainer);
        }


        @Override
        protected void onCancelled() {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
            super.onCancelled();
        }

    }


    /**
     * Messages methods
     */
    public class SendMessageTask extends AsyncTask<Message, Void, Boolean> {
        private int sessionID;
//        private ProgressDialog dialog = new ProgressDialog(ToolbarActivity.this);


        @Override
        protected void onPreExecute() {
//            this.dialog.setMessage("Adding new message...");
//            this.dialog.show();

            sessionID = application
                    .getSharedPreferences("LocMess", MODE_PRIVATE)
                    .getInt("session", 0);


            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Message... params) {
            return application.postMessageToServer(sessionID, params[0]);

        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
            super.onPostExecute(aBoolean);
        }


        @Override
        protected void onCancelled() {
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
            Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
            super.onCancelled();
        }

    }



}
