package pt.ulisboa.tecnico.cmov.locmess.activities.outbox;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.ToolbarActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.login.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Policy;
import pt.ulisboa.tecnico.cmov.locmess.model.types.SecureMessage;
import pt.ulisboa.tecnico.cmov.locmess.model.types.TimeWindow;
import pt.ulisboa.tecnico.cmov.locmess.services.WifiMessageReceiver;
import pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.messages.SendSecureMessageTask;

public class PostMessageActivity extends ToolbarActivity implements
        SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    private static final int POLICY_ACTIVITY = 1;
    Button createButton;
    ImageButton locationButton;
    ImageButton policyButton;
    ImageButton scheduleButton;
    Switch deliveryModeSwitch;

    EditText contentEditText;
    EditText titleEditText;

    boolean isViewMode = false;

    Location location;
    TimeWindow timeWindow;
    Policy policy;

    int positionInList; //used when editing a message

    boolean isCentralized = true;

    public static final String TAG = "msgsender";
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private static SimWifiP2pSocketServer mSrvSocket = null;
    private WifiMessageReceiver mReceiver;
    private SimWifiP2pSocket mCliSocket;

    private ArrayList<String> ipList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_post_message);
        if (!isViewMode) {
            setupToolbar("LocMess - Post Message");
        } else {
            setupToolbar("LocMess - View Outbox Message");
        }
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

    /**
     * changes buttons background according to the PostMessageActivity state
     */
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

    private boolean isPostMessageReady() {
        return timeWindow.isTimeWindowSet() &&
                (!isCentralized || (location != null)) &&
                policy != null;
    }

    protected void setupButtons() {
        deliveryModeSwitch = (Switch) findViewById(R.id.switch_delivery_mode);
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

                if (titleEditText.getText().length() == 0) {
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
                if (isCentralized) {
                    if (!isViewMode) {
                        Log.d("MSG", "add message");

                        //send message to server
                        SendSecureMessageTask task = new SendSecureMessageTask(PostMessageActivity.this);
                        task.execute(new SecureMessage(message, application.getOwnPrivateKey()));
                        try {
                            Boolean result = task.get();
                            if (result == null){
                                Toast.makeText(PostMessageActivity.this, "Can't reach server, no actions done.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(result)
                                application.addLocation(location);
                            else{
                                if (application.forceLoginFlag){
                                    Intent i = new Intent(PostMessageActivity.this, LoginActivity.class);
                                    application.forceLoginFlag = false;
                                    Toast.makeText(PostMessageActivity.this, "This session was invalid. Logging into new session.", Toast.LENGTH_LONG).show();
                                    startActivity(i);
                                }
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    finish();
                } else {

                    //Encontra ip´s de todos os devices
                    mManager.requestGroupInfo(mChannel, PostMessageActivity.this);


                    for (String ip : ipList) {
                        new OutgoingCommTask().executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, ip);  //liga-se a todos os ip´s

                        //new SendCommTask().executeOnExecutor(
                        //  AsyncTask.THREAD_POOL_EXECUTOR,message. );  //Envia msg para o ip respectivo
                    }
                    finish();
                }
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

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        // compile list of network members
        StringBuilder peersStr = new StringBuilder();
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "" + deviceName + " (" +
                    ((device == null) ? "??" : device.getVirtIp()) + ")\n";
            peersStr.append(devstr);
        }

        // display list of network members
        new android.app.AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Network")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();

        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
        }

        // display list of devices in range
        new android.app.AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Range")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }


    public static class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(10001);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {

                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        publishProgress(st);
                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //mTextOutput.append(values[0] + "\n");
        }
    }

    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            //mTextOutput.setText("Connecting...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mCliSocket = new SimWifiP2pSocket(params[0], 10001);
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }


    public class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... msg) {

            try {
                mCliSocket.getOutputStream().write((msg[0] + "\n").getBytes());
                BufferedReader sockIn = new BufferedReader(
                        new InputStreamReader(mCliSocket.getInputStream()));
                sockIn.readLine();
                mCliSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

	/*
     * Listeners associated to Termite
	 */


}
