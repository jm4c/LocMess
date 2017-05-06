package pt.ulisboa.tecnico.cmov.locmess.outbox;

import android.os.Bundle;
import android.util.Log;

import pt.ulisboa.tecnico.cmov.locmess.model.Message;

public class EditMessageActivity extends PostMessageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadMessage();
    }

    private void loadMessage(){
        Message message =  (Message) getIntent().getSerializableExtra("message");

        assert message != null;
        Log.w("EDIT", message.getTitle());
        titleEditText.setText(message.getTitle());
        titleEditText.setEnabled(false);
        contentEditText.setText(message.getContent());
        location = message.getLocation();
        policy = message.getPolicy();
        timeWindow = message.getTimeWindow();
        isCentralized = message.isCentralized();

        positionInList = getIntent().getIntExtra("position", -1);
        isEditMode = true;

        refreshButtons();
    }
}
