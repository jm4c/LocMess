package pt.ulisboa.tecnico.cmov.locmess.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.services.GPSTrackerService;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private Button loginButton;
    private EditText usernameEditText;
    private EditText passwordEditText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.btn_login);
        usernameEditText =  (EditText) findViewById(R.id.input_name);
        passwordEditText = (EditText) findViewById(R.id.input_password);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                login();
            }
        });

        TextView signupLink = (TextView) findViewById(R.id.link_signup);
        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 500); //TODO wait for response from server
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);

        //save to shared preferences
        SharedPreferences sharedPref = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", usernameEditText.getText().toString());
        editor.putString("password", passwordEditText.getText().toString());
        editor.apply();

        //start GPS service
        startService(new Intent(this, GPSTrackerService.class));

        Intent i = new Intent(getApplicationContext(), InboxActivity.class);
        startActivity(i);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        if (passwordEditText.getText().length() < 4) {
            passwordEditText.setError("larger than 4 characters");
            return false;
        } else {
            passwordEditText.setError(null);
        }

        return true;
    }
}
