package pt.ulisboa.tecnico.cmov.locmess.activities.outbox;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.locmess.activities.ToolbarActivity;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Policy;
import pt.ulisboa.tecnico.cmov.locmess.model.types.TimeWindow;
import pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.messages.SendMessageTask;

public class PostMessageActivity extends ToolbarActivity {

    private static final int POLICY_ACTIVITY = 1;
    private Button createButton;
    private ImageButton locationButton;
    private ImageButton policyButton;
    private ImageButton scheduleButton;

    EditText contentEditText;
    EditText titleEditText;

    Location location;
    TimeWindow timeWindow;
    Policy policy;

    boolean isEditMode = false;
    int positionInList; //used when editing a message

    boolean isCentralized = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        refreshButtons();
    }

    /** changes buttons background according to the PostMessageActivity state*/
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

        if (policy != null)
            policyButton.setBackgroundResource(R.drawable.background_icon_enabled_button);
        else
            policyButton.setBackgroundResource(R.drawable.background_icon_todo_button);

        if (isPostMessageReady()) {
            createButton.setEnabled(true);
            createButton.setBackgroundResource(R.drawable.background_icon_enabled_button);
        } else {
            createButton.setEnabled(false);
            createButton.setBackgroundResource(R.drawable.background_icon_disabled_button);
        }
    }

    private boolean isPostMessageReady(){
        return timeWindow.isTimeWindowSet() &&
                (!isCentralized || (location != null)) &&
                policy != null;
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

                final List<String> locations = application.listLocations();
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
                String message;

                if (policy == null)
                    message = "Policy not set.";
                else if (policy.isWhitelist())
                    message = "Policy: Whitelist";
                else
                    message = "Policy: Blacklist";

                Toast.makeText(PostMessageActivity.this,
                        message,
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                    + "From: " + PostMessageActivity.this.timeWindow.printFormattedStartTime() + "\n"
                                    + "To: " + PostMessageActivity.this.timeWindow.printFormattedEndTime(),
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
                Log.d("POST", "inside create");
                //validateFields
                if (!isPostMessageReady()) {
                    Log.d("POST", "field not valid");
                    return;
                }

                if(titleEditText.getText().length() == 0) {
                    titleEditText.setError("Title cannot be empty.");
                    return;
                }

                String owner = PostMessageActivity.this.getSharedPreferences("LocMess", MODE_PRIVATE).getString("username", "");

                Message message = new Message(
                        titleEditText.getText().toString(),
                        contentEditText.getText().toString(),
                        owner,
                        location,
                        timeWindow,
                        isCentralized,
                        policy);

                if(!isEditMode) {
                    Log.d("MSG", "add message");
                    application.addOutboxMessage(message);

                    //send message to server
                    SendMessageTask task = new SendMessageTask(PostMessageActivity.this);
                    task.execute(message);
                    try {
                        task.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }else {
                    //TODO remove or just keep for wifi id
                    Log.d("MSG", "replace message");
                    application.replaceOutboxMessage(message, positionInList);
                }

                finish();
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
