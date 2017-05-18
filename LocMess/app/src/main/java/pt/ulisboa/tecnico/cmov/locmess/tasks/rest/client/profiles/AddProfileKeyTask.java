package pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.profiles;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.services.ProfileKeyManagerService;

/**
 * Created by joaod on 18-May-17.
 */
public class AddProfileKeyTask extends AsyncTask<String, Void, Boolean> {
    private LocMessApplication application;
    private int sessionID;

    public AddProfileKeyTask(Context context) {
        application = (LocMessApplication) context.getApplicationContext();
    }


    @Override
    protected void onPreExecute() {

        sessionID = application
                .getSharedPreferences("LocMess", Context.MODE_PRIVATE)
                .getInt("session", 0);


        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {

        // Setup url
        final String url = application.getServerURL() + "/profilekey";

        // Populate header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("session", String.valueOf(sessionID));

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(4000);

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params[0], requestHeaders), Boolean.class);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED)
                application.forceLoginFlag = true;
            return response.getBody();

        } catch (Exception e) {
            Log.e("AddProfileKeyTask", e.getMessage(), e);

            return null;
        }

    }

}
