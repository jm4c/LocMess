package pt.ulisboa.tecnico.cmov.locmess.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.services.GPSTrackerService;

import static pt.ulisboa.tecnico.cmov.locmess.LocMessApplication.*;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int TIMEOUT_CODE = -3;

    private Button loginButton;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private String username;
    private String password;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSharedPreferences("LocMess", MODE_PRIVATE).contains("username") &&
                getSharedPreferences("LocMess", MODE_PRIVATE).contains("password")) {
            getSharedPreferences("LocMess", MODE_PRIVATE).getString("username", null);
            getSharedPreferences("LocMess", MODE_PRIVATE).getString("password", null);
            login();
        }


        loginButton = (Button) findViewById(R.id.btn_login);
        usernameEditText = (EditText) findViewById(R.id.input_name);
        passwordEditText = (EditText) findViewById(R.id.input_password);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                validateFields();
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

    @Override
    protected void onResume() {
        if (getSharedPreferences("LocMess", MODE_PRIVATE).contains("username") &&
                getSharedPreferences("LocMess", MODE_PRIVATE).contains("password")) {
            username = getSharedPreferences("LocMess", MODE_PRIVATE).getString("username", null);
            password = getSharedPreferences("LocMess", MODE_PRIVATE).getString("password", null);
            login();
        }
        super.onResume();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (LOGIN_ACTIVE_FLAG) { //in order to speed up debugging
            LoginTask task = new LoginTask();
            task.execute();
        } else {
            onLoginSuccess(0);
        }

    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(int sessionID) {
        loginButton.setEnabled(true);

        //save to shared preferences
        SharedPreferences sharedPref = this.getSharedPreferences("LocMess", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putInt("session", sessionID);
        editor.apply();

        //start GPS service
        if (!((LocMessApplication) getApplication()).isServiceRunning(GPSTrackerService.class))
            startService(new Intent(this, GPSTrackerService.class));

        Intent i = new Intent(getApplicationContext(), InboxActivity.class);
        startActivity(i);
        finish();
    }

    public void onLoginFailed(int code) {
        if (code == TIMEOUT_CODE)
            Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();

        loginButton.setEnabled(true);
    }

    public void validateFields() {
        if (passwordEditText.getText().length() < 4) {
            passwordEditText.setError("larger than 4 characters");
            return;
        } else {
            passwordEditText.setError(null);
        }

        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();

        login();
    }


    private class LoginTask extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Logging in...");
            this.dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {

            // Setup url
            final String url = ((LocMessApplication) getApplicationContext()).getServerURL() + "/login";

            // Populate header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("username", username);
            requestHeaders.add("password", password);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(4000);

            try {
                ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(requestHeaders), Integer.class);
                return response.getBody();

            } catch (Exception e) {
                Log.e("NewUserActivity", e.getMessage(), e);
                return TIMEOUT_CODE;
            }
        }

        @Override
        protected void onPostExecute(Integer aInteger) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (aInteger > 0)
                onLoginSuccess(aInteger);
            else
                onLoginFailed(aInteger);
            super.onPostExecute(aInteger);
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
            super.onCancelled();
        }
    }
}
