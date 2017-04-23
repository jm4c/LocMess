package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.location.LocationActivity;
import pt.ulisboa.tecnico.cmov.locmess.login.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.outbox.OutboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.profile.ProfileActivity;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String activityTitle = "LocMess - Main Menu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(activityTitle);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent i;

        switch(item.getItemId()){
            case R.id.nav_inbox:
                i = new Intent(this, InboxActivity.class);
                break;
            case R.id.nav_outbox:
                i = new Intent(this, OutboxActivity.class);
                break;
            case R.id.nav_locations:
                i = new Intent(this, LocationActivity.class);
                Toast.makeText(this, "TODO", Toast.LENGTH_LONG);
                break;
            case R.id.nav_profile:
                i = new Intent(this, ProfileActivity.class);
                break;
            case R.id.nav_logout:
                i = new Intent(this, LoginActivity.class);
                break;
            default:
                i = null;
                return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        startActivity(i);

        return true;
    }
}