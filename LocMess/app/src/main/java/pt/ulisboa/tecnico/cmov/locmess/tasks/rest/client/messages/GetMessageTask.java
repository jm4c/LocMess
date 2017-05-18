package pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.messages;

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
import pt.ulisboa.tecnico.cmov.locmess.model.containers.MessagesContainer;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Profile;

/**
 * Created by joaod on 18-May-17.
 */
public class GetMessageTask extends AsyncTask<Void, Void, MessagesContainer> {
    private LocMessApplication application;
    private int sessionID;
    private Location location;
    private Profile profile;

    public GetMessageTask(Context context, Location location, Profile profile) {
        application = (LocMessApplication) context.getApplicationContext();
        this.location = location;
        this.profile = profile;
    }


    @Override
    protected void onPreExecute() {

        sessionID = application
                .getSharedPreferences("LocMess", Context.MODE_PRIVATE)
                .getInt("session", 0);


        super.onPreExecute();
    }

    @Override
    protected MessagesContainer doInBackground(Void... params) {

        // Setup url
        final String url = application.getServerURL() + "/message";

        // Populate header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("session", String.valueOf(sessionID));
        requestHeaders.add("location", location.getName());

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(10000);

        try {
            //using PUT since a value in the server will be changed and GET does not support body a request body
            ResponseEntity<MessagesContainer> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(profile, requestHeaders), MessagesContainer.class);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED)
                application.forceLoginFlag = true;

            return response.getBody();

        } catch (Exception e) {
            Log.e("GetMessagesFromServer", e.getMessage(), e);
            return null;
        }
    }

}

