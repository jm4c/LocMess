package pt.ulisboa.tecnico.cmov.locmess.login;


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


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final boolean LOGIN_ACTIVE_FLAG = true;
    private static final int TIMEOUT_CODE = -3;

    private Button loginButton;
    private EditText usernameEditText;
    private EditText passwordEditText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.btn_login);
        usernameEditText = (EditText) findViewById(R.id.input_name);
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
            return;
        }

        if (LOGIN_ACTIVE_FLAG) { //in order to speed up debugging
            LoginTask task = new LoginTask();
            task.execute();
        } else {
            onLoginSuccess(9999);
        }

//        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();
//
//
//        new android.os.Handler().post(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onLoginSuccess or onLoginFailed
//                        try {
//                            if (LOGIN_ACTIVE_FLAG) { //in order to speed up debugging
//                                LoginTask task = new LoginTask();
//                                task.execute();
//                                task.get(10, TimeUnit.SECONDS);
//                            } else {
//                                onLoginSuccess();
//                            }
//                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
//                            e.printStackTrace();
//                            Toast.makeText(LoginActivity.this, "Failed to connect with server.", Toast.LENGTH_SHORT);
//                        }
//                        progressDialog.dismiss();
//                    }
//                });
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(int sessionID) {
        loginButton.setEnabled(true);

        //save to shared preferences
        SharedPreferences sharedPref = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", usernameEditText.getText().toString());
        editor.putString("password", passwordEditText.getText().toString());
        editor.putInt("session", sessionID);
        editor.apply();

        //start GPS service
        startService(new Intent(this, GPSTrackerService.class));

        Intent i = new Intent(getApplicationContext(), InboxActivity.class);
        startActivity(i);
        finish();
    }

    public void onLoginFailed(int code) {
        if (code == TIMEOUT_CODE) {
            Toast.makeText(getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();

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


    private class LoginTask extends AsyncTask<Void, Void, Integer> {
        private String username;
        private String password;

        @Override
        protected void onPreExecute() {
            EditText passwordText = (EditText) findViewById(R.id.input_password);
            EditText nameText = (EditText) findViewById(R.id.input_name);

            username = nameText.getText().toString();
            password = passwordText.getText().toString();
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
            if (aInteger > 0)
                onLoginSuccess(aInteger);
            else
                onLoginFailed(aInteger);
            super.onPostExecute(aInteger);
        }

    }
}
