package pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.accounts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.activities.inbox.InboxActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.login.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.services.GPSTrackerService;

public class LoginTask extends AsyncTask<Void, Void, Integer> {

    private LoginActivity loginActivity;
    private ProgressDialog dialog;

    private static final int TIMEOUT_CODE = -3;

    private String username;
    private String password;

    public LoginTask(LoginActivity loginActivity, String username, String password) {
        this.loginActivity = loginActivity;
        this.username = username;
        this.password = password;
        dialog = new ProgressDialog(loginActivity);
    }


    @Override
    protected void onPreExecute() {
        if (dialog != null) {
            this.dialog.setMessage("Logging in...");
            this.dialog.show();
        }
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {

        // Setup url
        final String url = ((LocMessApplication) loginActivity.getApplicationContext()).getServerURL() + "/login";

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
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        if (aInteger > 0)
            onLoginSuccess(aInteger);
        else
            onLoginFailed(aInteger);
        super.onPostExecute(aInteger);
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(loginActivity.getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
        super.onCancelled();
    }

    public void onLoginSuccess(int sessionID) {

        LocMessApplication application = (LocMessApplication) loginActivity.getApplicationContext();

        //save to shared preferences
        SharedPreferences sharedPref = application.getSharedPreferences("LocMess", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putInt("session", sessionID);
        editor.apply();


//        start GPS service
        if (!application.isServiceRunning(GPSTrackerService.class))
            loginActivity.startService(new Intent(application, GPSTrackerService.class));

        application.startAlarmManager();

        Intent i = new Intent(loginActivity.getApplicationContext(), InboxActivity.class);
        loginActivity.startActivity(i);
        loginActivity.finish();
    }

    public void onLoginFailed(int code) {
        if (code == TIMEOUT_CODE)
            Toast.makeText(loginActivity.getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(loginActivity.getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();
    }
}
