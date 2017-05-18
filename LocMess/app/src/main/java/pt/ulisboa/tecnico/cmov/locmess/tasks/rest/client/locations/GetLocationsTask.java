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
import pt.ulisboa.tecnico.cmov.locmess.model.containers.LocationsContainer;

public class GetLocationsTask extends AsyncTask<Void, Void, LocationsContainer> {
    private LocMessApplication application;
    private Context context;
    private String locationsHash;
    private int sessionID;
    private ProgressDialog dialog;

    public GetLocationsTask(Context context) {
        application = (LocMessApplication) context.getApplicationContext();
        this.context = context;
        if (context instanceof Activity) {
            dialog = new ProgressDialog(context);
        }
    }


    @Override
    protected void onPreExecute() {
        if (dialog != null) {
            this.dialog.setMessage("Loading Locations...");
            this.dialog.show();
        }

        sessionID = application
                .getSharedPreferences("LocMess", Context.MODE_PRIVATE)
                .getInt("session", 0);

        locationsHash = application.getLocationsHash();

        super.onPreExecute();
    }

    @Override
    protected LocationsContainer doInBackground(Void... params) {
        // Setup url
        final String url = application.getServerURL() + "/location";

        // Populate header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("session", String.valueOf(sessionID));
        requestHeaders.add("hash", locationsHash);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(4000);

        try {
            ResponseEntity<LocationsContainer> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(requestHeaders), LocationsContainer.class);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED)
                application.forceLoginFlag = true;

            return response.getBody();

        } catch (Exception e) {
            Log.e("GetLocationsTask", e.getMessage(), e);

            return null;
        }
    }


    @Override
    protected void onPostExecute(LocationsContainer locationsContainer) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        if (locationsContainer != null) {
            application.setLocationsContainer(locationsContainer);
        }

        super.onPostExecute(locationsContainer);
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
