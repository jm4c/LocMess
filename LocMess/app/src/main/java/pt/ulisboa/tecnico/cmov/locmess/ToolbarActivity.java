package pt.ulisboa.tecnico.cmov.locmess;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.locmess.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.location.LocationActivity;
import pt.ulisboa.tecnico.cmov.locmess.login.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.login.NewUserActivity;
import pt.ulisboa.tecnico.cmov.locmess.model.Location;
import pt.ulisboa.tecnico.cmov.locmess.outbox.OutboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.profile.ProfileActivity;
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

        // if there is a task, wait for it to finish first (timeout = 4 secs)

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public List<String> getMenu() {
        return Arrays.asList(menu);
    }

    private class GetLocationsTask extends AsyncTask<Void, Void, List<Location>> {
        private String locationsHash;
        private int sessionID;
        private ProgressDialog dialog = new ProgressDialog(ToolbarActivity.this);


        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading Locations...");
            this.dialog.show();

            sessionID  = application
                    .getSharedPreferences("LocMess",MODE_PRIVATE)
                    .getInt("session", 0);

            locationsHash = application.getLocationsHash();

            super.onPreExecute();
        }

        @Override
        protected List<Location> doInBackground(Void... params) {

            // Setup url
            final String url = ((LocMessApplication) getApplicationContext()).getServerURL() + "/locations";

            // Populate header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("session", String.valueOf(sessionID));
            requestHeaders.add("hash", locationsHash);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(4000);

            try {
                ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(requestHeaders), List.class);
                return response.getBody();

            } catch (Exception e) {
                Log.e("NewUserActivity", e.getMessage(), e);

                return null;
            }

        }

        @Override
        protected void onPostExecute(List<Location> locations) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(locations != null){
                application.setLocations(locations);
                application.generateLocationsHash();
            }

            super.onPostExecute(locations);
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


}