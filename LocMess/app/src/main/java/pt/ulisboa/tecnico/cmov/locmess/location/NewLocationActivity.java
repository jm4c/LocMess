package pt.ulisboa.tecnico.cmov.locmess.location;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Text;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.ToolbarActivity;
import pt.ulisboa.tecnico.cmov.locmess.R;


public class NewLocationActivity extends ToolbarActivity {

    private static final int MAP_ACTIVITY = 0;

    private EditText nameEditText;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private EditText radiusEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        setupToolbar("LocMess - New Location");

        nameEditText = (EditText) findViewById(R.id.input_name);
        latitudeEditText = (EditText) findViewById(R.id.input_latitude);
        longitudeEditText = (EditText) findViewById(R.id.input_longitude);
        radiusEditText = (EditText) findViewById(R.id.input_radius);

        Button addButton = (Button) findViewById(R.id.addButton);
        Button pickMapButton = (Button) findViewById(R.id.pickOnMap);


        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //validate fields

                boolean abort = false;
                if (nameEditText.getText().length() == 0) {
                    nameEditText.setError("Can't be null");
                    abort = true;
                }
                if (latitudeEditText.getText().length() == 0) {
                    latitudeEditText.setError("Can't be null");
                    abort = true;
                }
                if (longitudeEditText.getText().length() == 0) {
                    longitudeEditText.setError("Can't be null");
                    abort = true;
                }
                if (abort) return;

                Double latitude = Double.valueOf(latitudeEditText.getText().toString());
                Double longitude = Double.valueOf(latitudeEditText.getText().toString());

                if (latitude < -90 || latitude > 90) {
                    latitudeEditText.setError("Value outside range: -90 to 90");
                    abort = true;
                }
                if (longitude < -180 || latitude > 180) {
                    latitudeEditText.setError("Value outside range: -180 to 180");
                    abort = true;
                }

                if (abort) return;

                if (radiusEditText.getText().length() == 0)
                    radiusEditText.setText("100");
                ((LocMessApplication) getApplicationContext()).addLocation(nameEditText.getText().toString(),
                        Double.valueOf(latitudeEditText.getText().toString()),
                        Double.valueOf(longitudeEditText.getText().toString()),
                        Integer.parseInt(radiusEditText.getText().toString()));
                finish();
            }
        });

        pickMapButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

                Editable radiusText = radiusEditText.getText();

                if (radiusText.length() > 0)
                    intent.putExtra("radius", Integer.valueOf(radiusText.toString()));

                startActivityForResult(intent, MAP_ACTIVITY);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            latitudeEditText.setText(String.valueOf(data.getDoubleExtra("latitude", 0)));
            longitudeEditText.setText(String.valueOf(data.getDoubleExtra("longitude", 0)));
        }
    }
}
