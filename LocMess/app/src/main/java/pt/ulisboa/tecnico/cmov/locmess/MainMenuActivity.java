package pt.ulisboa.tecnico.cmov.locmess;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.location.LocationActivity;
import pt.ulisboa.tecnico.cmov.locmess.login.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.model.Location;
import pt.ulisboa.tecnico.cmov.locmess.outbox.OutboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.outbox.PostMessageActivity;
import pt.ulisboa.tecnico.cmov.locmess.profile.ProfileActivity;

import static pt.ulisboa.tecnico.cmov.locmess.model.TestData.getLocations;

public class MainMenuActivity extends AppCompatActivity
        /*implements NavigationView.OnNavigationItemSelectedListener*/ {
    private static final String activityTitle = "LocMess - Main Menu";

    private static final int MENU_INBOX = 0;
    private static final int MENU_OUTBOX = 1;
    private static final int MENU_PROFILE = 2;
    private static final int MENU_LOCATIONS = 3;
    private static final int MENU_LOGOUT = 4;


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
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(activityTitle);
        setSupportActionBar(toolbar);
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
                        changeActivity(which);
                    }
                });
        builder.create().show();

        return super.onOptionsItemSelected(item);
    }

    private void changeActivity(int which) {
        Intent i;
        switch (which){
            case MENU_INBOX:
                i = new Intent(MainMenuActivity.this, InboxActivity.class);
                break;
            case MENU_OUTBOX:
                i = new Intent(MainMenuActivity.this, OutboxActivity.class);
                break;
            case MENU_LOCATIONS:
                i = new Intent(MainMenuActivity.this, LocationActivity.class);
                break;
            case MENU_PROFILE:
                i = new Intent(MainMenuActivity.this, ProfileActivity.class);
                break;
            default: //LOGOUT
                i = new Intent(MainMenuActivity.this, LoginActivity.class);
                //TODO remove credentials/login token
                break;
        }
        startActivity(i);
    }

    public List<String> getMenu(){
        return Arrays.asList(menu);
    }


}