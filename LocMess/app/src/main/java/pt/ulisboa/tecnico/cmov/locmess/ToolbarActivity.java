package pt.ulisboa.tecnico.cmov.locmess;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.location.LocationActivity;
import pt.ulisboa.tecnico.cmov.locmess.login.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.outbox.OutboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.profile.ProfileActivity;

public class ToolbarActivity extends AppCompatActivity {

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
                        changeActivity(which);
                    }
                });
        builder.create().show();

        return super.onOptionsItemSelected(item);
    }

    protected void setupToolbar(String activityTitle){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(activityTitle);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    private void changeActivity(int which) {
        Intent i;
        switch (which){
            case MENU_INBOX:
                i = new Intent(ToolbarActivity.this, InboxActivity.class);
                break;
            case MENU_OUTBOX:
                i = new Intent(ToolbarActivity.this, OutboxActivity.class);
                break;
            case MENU_LOCATIONS:
                i = new Intent(ToolbarActivity.this, LocationActivity.class);
                break;
            case MENU_PROFILE:
                i = new Intent(ToolbarActivity.this, ProfileActivity.class);
                break;
            default: //LOGOUT
                i = new Intent(ToolbarActivity.this, LoginActivity.class);
                //TODO remove credentials/login token
                break;
        }
        startActivity(i);
    }

    public List<String> getMenu(){
        return Arrays.asList(menu);
    }


}