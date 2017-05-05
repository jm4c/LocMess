package pt.ulisboa.tecnico.cmov.locmess.outbox;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.ToolbarActivity;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.model.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.Policy;
import pt.ulisboa.tecnico.cmov.locmess.model.TimeWindow;

import static pt.ulisboa.tecnico.cmov.locmess.model.TestData.getLocations;

public class PostMessageActivity extends ToolbarActivity {

    private static final int POLICY_ACTIVITY = 1;
    private LocMessApplication application;
    private Button createButton;
    private ImageButton locationButton;
    private ImageButton policyButton;
    private ImageButton scheduleButton;

    private Location location;
    private EditText contentEditText;
    EditText titleEditText;

    TimeWindow timeWindow;
    private Policy policy;

    private boolean isCentralized = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (LocMessApplication) getApplicationContext();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_post_message);
        setupToolbar("LocMess - Post Message");
        setupButtons();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            policy = (Policy) data.getSerializableExtra("policy");
        }
    }

    public void refreshButtons() {

        if (isCentralized) {
            if (location != null) {
                locationButton.setEnabled(true);
                locationButton.setBackgroundResource(R.drawable.background_icon_enabled_button);
            } else {
                locationButton.setEnabled(true);
                locationButton.setBackgroundResource(R.drawable.background_icon_todo_button);
            }
        } else {
            locationButton.setEnabled(false);
            locationButton.setBackgroundResource(R.drawable.background_icon_disabled_button);
        }

        if (timeWindow.isTimeWindowSet())
            scheduleButton.setBackgroundResource(R.drawable.background_icon_enabled_button);
        else
            scheduleButton.setBackgroundResource(R.drawable.background_icon_todo_button);

        //TODO policy

        if (timeWindow.isTimeWindowSet() && (isCentralized || (location != null) /*TODO && policy set && title/content not empty*/)) {
            createButton.setEnabled(true);
            createButton.setBackgroundResource(R.drawable.background_icon_enabled_button);
        } else {
            createButton.setEnabled(false);
            createButton.setBackgroundResource(R.drawable.background_icon_disabled_button);
        }
    }

    protected void setupButtons() {
        locationButton = (ImageButton) findViewById(R.id.bt_location);
        policyButton = (ImageButton) findViewById(R.id.bt_policy);
        scheduleButton = (ImageButton) findViewById(R.id.bt_schedule);


        final Switch deliveryModeSwitch = (Switch) findViewById(R.id.switch_delivery_mode);
        createButton = (Button) findViewById(R.id.btn_create);

        timeWindow = new TimeWindow();
        titleEditText = (EditText) findViewById(R.id.tx_title);
        contentEditText = (EditText) findViewById(R.id.tx_content);

        refreshButtons();


        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostMessageActivity.this);

                final List<String> locations = application.getLocationsNames();
                CharSequence[] cs = locations.toArray(new CharSequence[locations.size()]);
                builder.setTitle(R.string.pick_location)
                        .setItems(cs, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item

//                                location = new Location(locations.get(which), 0, 0, 0);
                                location = application.getLocation(which);
                                Toast.makeText(PostMessageActivity.this, "Location: " + location.getName(), Toast.LENGTH_LONG).show();
                                refreshButtons();
                            }
                        });
                builder.create().show();
            }
        });

        locationButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (location != null) {
                    Toast.makeText(PostMessageActivity.this,
                            "Location: " + location.getName() + "\n"
                                    + "Latitude: " + location.getLatitude() + "\n"
                                    + "Longitude: " + location.getLongitude() + "\n"
                                    + "Radius: " + location.getLatitude(),
                            Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    Toast.makeText(PostMessageActivity.this,
                            "Location not picked yet",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });

        policyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostMessageActivity.this, PolicyActivity.class);

                if (policy != null)
                    intent.putExtra("policy", policy);

                startActivityForResult(intent, POLICY_ACTIVITY);
            }
        });

        policyButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(PostMessageActivity.this,
                        "List Mode: TODO", //TODO
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO onClick
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getFragmentManager(), "dateStartPicker");
                refreshButtons();
            }
        });

        scheduleButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (timeWindow.isTimeWindowSet()) {
                    Toast.makeText(PostMessageActivity.this,
                            "Message Duration\n"
                                    + "From: " + PostMessageActivity.this.timeWindow.getFormattedStartTime() + "\n"
                                    + "To: " + PostMessageActivity.this.timeWindow.getFormattedEndTime(),
                            Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    Toast.makeText(PostMessageActivity.this,
                            "Time window not set yet",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO onClick
            }
        });

        deliveryModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCentralized = b;
                if (b)
                    deliveryModeSwitch.setText(R.string.mode_server);
                else
                    deliveryModeSwitch.setText(R.string.mode_wifi_direct);
                refreshButtons();
            }
        });
    }
}
