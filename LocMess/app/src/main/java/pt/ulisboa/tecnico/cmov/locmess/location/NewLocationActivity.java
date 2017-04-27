package pt.ulisboa.tecnico.cmov.locmess.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.R;


public class NewLocationActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);


        final EditText nameLayout = (EditText) findViewById(R.id.input_name);
        final EditText latitudeLayout = (EditText) findViewById(R.id.input_latitude);
        final EditText longitudeLayout = (EditText) findViewById(R.id.input_longitude);
        final EditText radiusLayout = (EditText) findViewById(R.id.input_radius);

        Button addbutton = (Button) findViewById(R.id.addButton);
        Button pickMap = (Button) findViewById(R.id.pickOnMap);


        addbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                ((LocMessApplication) getApplicationContext()).addLocation(nameLayout.getText().toString(),
                        Double.valueOf(latitudeLayout.getText().toString()),
                        Double.valueOf(longitudeLayout.getText().toString()),
                        Integer.parseInt(radiusLayout.getText().toString()));//TODO buscar os valores das caixas.
                startActivity(intent);
            }
        });

        pickMap.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

}
