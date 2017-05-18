package pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.accounts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
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
import pt.ulisboa.tecnico.cmov.locmess.activities.login.NewUserActivity;


public class CreateAccountTask extends AsyncTask<Void, Void, Boolean> {
    private NewUserActivity newUserActivity;
    private String username;
    private String password;
    private ProgressDialog dialog ;

    public CreateAccountTask(NewUserActivity newUserActivity) {
        this.newUserActivity = newUserActivity;
        dialog = new ProgressDialog(newUserActivity);
    }


    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Creating new account...");
        this.dialog.show();
        EditText passwordText = (EditText) newUserActivity.findViewById(R.id.input_password);
        EditText nameText = (EditText) newUserActivity.findViewById(R.id.input_name);

        username = nameText.getText().toString();
        password = passwordText.getText().toString();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {


        // Setup url
        final String url = ((LocMessApplication) newUserActivity.getApplicationContext()).getServerURL() + "/account";

        // Populate header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("username", username);
        requestHeaders.add("password", password);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(4000);

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(requestHeaders), boolean.class);
            return response.getBody();

        } catch (Exception e) {
            Log.e("NewUserActivity", e.getMessage(), e);
            return null;
        }

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (aBoolean == null)
            Toast.makeText(newUserActivity.getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
        else if (aBoolean)
            newUserActivity.onSignupSuccess();
        else
            newUserActivity.onSignupFailed();
        super.onPostExecute(aBoolean);
    }

}
