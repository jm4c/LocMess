package pt.ulisboa.tecnico.cmov.locmess.outbox;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.model.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.TestData;

import static pt.ulisboa.tecnico.cmov.locmess.model.TestData.getLocations;

public class NewMessageActivity extends AppCompatActivity {


    private EditText titleEditText;
    private EditText contentEditText;

    private boolean isCentralized;

    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_new_message);

        ImageButton locationButton = (ImageButton) findViewById(R.id.bt_location);
        ImageButton policyButton = (ImageButton) findViewById(R.id.bt_policy);
        ImageButton scheduleButton = (ImageButton) findViewById(R.id.bt_schedule);

        final Switch deliveryModeSwitch = (Switch) findViewById(R.id.switch_delivery_mode);
        Button createButton = (Button) findViewById(R.id.btn_create);

        titleEditText = (EditText) findViewById(R.id.tx_title);
        contentEditText= (EditText) findViewById(R.id.tx_content);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewMessageActivity.this);

                final List<String> locations = getLocations();
                CharSequence[] cs = locations.toArray(new CharSequence[locations.size()]);
                builder.setTitle(R.string.pick_location)
                        .setItems(cs, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                //TODO pick location
                                Toast.makeText(NewMessageActivity.this, "Location: " + locations.get(which), Toast.LENGTH_LONG).show();
                            }
                        });
                builder.create().show();
            }
        });
        policyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewMessageActivity.this, PolicyActivity.class);
//                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO onClick
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO onClick
            }
        });

        deliveryModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCentralized = b;
                if (b){
                    deliveryModeSwitch.setText(R.string.mode_server);
                }else
                    deliveryModeSwitch.setText(R.string.mode_wifi_direct);
            }
        });

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }

}
