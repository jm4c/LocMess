package pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.locations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.activities.ToolbarActivity;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Location;

/**
 * Created by joaod on 17-May-17.
 */
public class RemoveLocationTask extends AsyncTask<Location, Void, Boolean> {
    private LocMessApplication application;
    private int sessionID;
    private ProgressDialog dialog;

    public RemoveLocationTask(Context context) {

        this.application = (LocMessApplication) context.getApplicationContext();
        if (context instanceof Activity) {
            dialog = new ProgressDialog(context);
        }

    }


    @Override
    protected void onPreExecute() {
        if (dialog != null) {
            this.dialog.setMessage("Removing location...");
            this.dialog.show();
        }

        sessionID = application
                .getSharedPreferences("LocMess", Context.MODE_PRIVATE)
                .getInt("session", 0);


        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Location... params) {
        // Setup url
        final String url = application.getServerURL() + "/location";

        // Populate header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("session", String.valueOf(sessionID));
        requestHeaders.add("location", params[0].getName());


        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(4000);

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(requestHeaders), Boolean.class);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED)
                application.forceLoginFlag = true;
            return response.getBody();

        } catch (Exception e) {
            Log.e("RemoveLocationTask", e.getMessage(), e);

            return null;
        }

    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        super.onPostExecute(aBoolean);
    }


    @Override
    protected void onCancelled() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        Toast.makeText(application.getBaseContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
        super.onCancelled();
    }

}
