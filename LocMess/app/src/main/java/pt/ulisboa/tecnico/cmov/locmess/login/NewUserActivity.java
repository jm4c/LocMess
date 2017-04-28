package pt.ulisboa.tecnico.cmov.locmess.login;

import android.app.ProgressDialog;
import android.net.Credentials;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.R;


public class NewUserActivity extends AppCompatActivity {
    private static final String TAG = "NewUserActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        Button signupButton = (Button) findViewById(R.id.btn_signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        TextView loginLink = (TextView) findViewById(R.id.link_login);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validateFields()) {
            onSignupFailed();
            return;
        }

        Button signupButton = (Button) findViewById(R.id.btn_signup);
        signupButton.setEnabled(false);

        CreateAccountTask task = new CreateAccountTask();

        task.execute();

    }


    public void onSignupSuccess() {
        Button signupButton = (Button) findViewById(R.id.btn_signup);
        signupButton.setEnabled(true);
        //  setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();

        Button signupButton = (Button) findViewById(R.id.btn_signup);
        signupButton.setEnabled(true);
    }

    public boolean validateFields() {

        EditText passwordText = (EditText) findViewById(R.id.input_password);
        EditText passwordTextConfirm = (EditText) findViewById(R.id.input_password_confirm);
        EditText nameText = (EditText) findViewById(R.id.input_name);

        String name = nameText.getText().toString();
        String passwordConfirm = passwordTextConfirm.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            return false;
        } else {
            nameText.setError(null);
        }


        if (password.isEmpty() || password.length() < 4) {
            passwordText.setError("larger than 4 characters");
            return false;
        } else {
            passwordText.setError(null);
        }

        if (!passwordConfirm.equals(password)) {
            passwordTextConfirm.setError("passwords must be equal");
            return false;
        } else {
            passwordTextConfirm.setError(null);
        }


        return true;
    }

    private class CreateAccountTask extends AsyncTask<Void, Void, Boolean> {
        final ProgressDialog progressDialog = new ProgressDialog(NewUserActivity.this);
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
        protected Boolean doInBackground(Void... params) {
            //show progress bar
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();

            // Setup url
            final String url = ((LocMessApplication) getApplicationContext()).getServerURL() + "/account/new/" + username;

            // Populate header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("username", username);
            requestHeaders.add("password", password);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            try {
                ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(requestHeaders), boolean.class);
                return response.getBody();

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            if (aBoolean)
                onSignupSuccess();
            else
                onSignupFailed();
            super.onPostExecute(aBoolean);
        }

    }
}
