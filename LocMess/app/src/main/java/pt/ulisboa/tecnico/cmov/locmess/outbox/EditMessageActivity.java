package pt.ulisboa.tecnico.cmov.locmess.outbox;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.model.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.TimeWindow;

public class EditMessageActivity extends PostMessageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadMessage();
    }

    private void loadMessage(){
        //TODO load message received
        titleEditText.setText("Random title");
        titleEditText.setEnabled(false);
        refreshButtons();
    }
}
