package pt.ulisboa.tecnico.cmov.locmess.outbox;

import android.os.Bundle;

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
