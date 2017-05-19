package pt.ulisboa.tecnico.cmov.locmess.activities.outbox;

import android.os.Bundle;
import android.util.Log;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;

public class ViewOutboxMessageActivity extends PostMessageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isViewMode = true;
        super.onCreate(savedInstanceState);
        loadMessage();
    }

    private void loadMessage(){
        Message message =  (Message) getIntent().getSerializableExtra("message");

        assert message != null;
        Log.w("VIEW", message.getTitle());
        titleEditText.setText(message.getTitle());
        titleEditText.setEnabled(false);
        contentEditText.setText(message.getContent());
        contentEditText.setEnabled(false);

        location = message.getLocation();
        locationButton.setOnClickListener(null);
        locationButton.setBackgroundResource(R.drawable.background_icon_disabled_button);

        policy = message.getPolicy();
        policyButton.setOnClickListener(null);
        policyButton.setBackgroundResource(R.drawable.background_icon_disabled_button);

        timeWindow = message.getTimeWindow();
        scheduleButton.setOnClickListener(null);
        scheduleButton.setBackgroundResource(R.drawable.background_icon_disabled_button);

        isCentralized = message.isCentralized();

        positionInList = getIntent().getIntExtra("position", -1);
        isViewMode = true;

        createButton.setEnabled(false);
        createButton.setBackgroundResource(R.drawable.background_icon_disabled_button);

        deliveryModeSwitch.setEnabled(false);
    }
}
