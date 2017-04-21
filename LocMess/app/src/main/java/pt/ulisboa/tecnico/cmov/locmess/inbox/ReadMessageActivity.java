package pt.ulisboa.tecnico.cmov.locmess.inbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.locmess.R;

import static pt.ulisboa.tecnico.cmov.locmess.model.Tags.BUNDLE_EXTRAS;
import static pt.ulisboa.tecnico.cmov.locmess.model.Tags.EXTRA_CONTENT;
import static pt.ulisboa.tecnico.cmov.locmess.model.Tags.EXTRA_OWNER;
import static pt.ulisboa.tecnico.cmov.locmess.model.Tags.EXTRA_TITLE;

public class ReadMessageActivity extends AppCompatActivity {
    private TextView titleView;
    private TextView ownerView;
    private TextView contentView;

    private String title;
    private String owner;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);

        Intent i = getIntent();
        Bundle extras = i.getExtras().getBundle(BUNDLE_EXTRAS);

        title = extras.getString(EXTRA_TITLE);
        owner = extras.getString(EXTRA_OWNER);
        content = extras.getString(EXTRA_CONTENT);

        titleView = (TextView) findViewById(R.id.title);
        ownerView = (TextView) findViewById(R.id.owner);
        contentView = (TextView) findViewById(R.id.content);

        titleView.setText(title);
        ownerView.setText(owner);
        contentView.setText(content);

    }
}
