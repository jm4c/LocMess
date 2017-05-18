package pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.profiles;

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
import pt.ulisboa.tecnico.cmov.locmess.model.containers.AvailableKeysContainer;

/**
 * Profile key methods (remaining profile methods in ProfileKeyManagerService)
 */
public class GetAvailableKeysTask extends AsyncTask<Void, Void, AvailableKeysContainer> {
    private LocMessApplication application;
    private String availableKeysHash;
    private int sessionID;
    private ProgressDialog dialog;

    public GetAvailableKeysTask(Context context) {
        this.application = (LocMessApplication) context.getApplicationContext();

        if (context instanceof Activity) {
            dialog = new ProgressDialog(context);
        }
    }


    @Override
    protected void onPreExecute() {
        if (dialog != null) {
            this.dialog.setMessage("Loading Profile keys...");
            this.dialog.show();
        }

        sessionID = application
                .getSharedPreferences("LocMess", Context.MODE_PRIVATE)
                .getInt("session", 0);

        availableKeysHash = application.getAvailableKeysContainer().getKeysHash();

        super.onPreExecute();
    }

    @Override
    protected AvailableKeysContainer doInBackground(Void... params) {
        // Setup url
        final String url = ((LocMessApplication) application.getApplicationContext()).getServerURL() + "/profilekey";

        // Populate header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("session", String.valueOf(sessionID));
        requestHeaders.add("hash", availableKeysHash);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(4000);

        try {
            ResponseEntity<AvailableKeysContainer> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(requestHeaders), AvailableKeysContainer.class);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED)
                application.forceLoginFlag = true;

            //convert from LinkedHashMap to list of strings
//                ObjectMapper mapper = new ObjectMapper();
//                List<String> availableKeys = mapper.convertValue(response.getBody(), new TypeReference<List<String>>() { });
            return response.getBody();

        } catch (Exception e) {
            Log.e("GetProfileKeysTask", e.getMessage(), e);

            return null;
        }


    }


    @Override
    protected void onPostExecute(AvailableKeysContainer availableKeysContainer) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        if (availableKeysContainer != null) {
            application.setAvailableKeysContainer(availableKeysContainer);
        }

        super.onPostExecute(availableKeysContainer);
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
