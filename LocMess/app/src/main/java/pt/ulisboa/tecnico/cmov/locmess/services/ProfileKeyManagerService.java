package pt.ulisboa.tecnico.cmov.locmess.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;


public class ProfileKeyManagerService extends Service {
    LocMessApplication application;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = (LocMessApplication) getApplicationContext();

        while (!application.queueKeyActions.isEmpty()) {
            LocMessApplication.ProfileKeyAction keyAction = application.queueKeyActions.peek();
            try {
                Boolean result;
                if ((Boolean) keyAction.getValue()) { // if true add to server
                    AddProfileKeyTask addTask = new AddProfileKeyTask();
                    addTask.execute((String) keyAction.getKey());
                    result = addTask.get();
                } else { //else remove from server
                    RemoveProfileKeyTask removeTask = new RemoveProfileKeyTask();
                    removeTask.execute((String) keyAction.getKey());
                    result = removeTask.get();
                }

                // if successful remove key/action from queue
                if (result)
                    application.queueKeyActions.poll();

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                // if problem with server, try again later
                return;
            }
        }
    }


    public class AddProfileKeyTask extends AsyncTask<String, Void, Boolean> {
        private int sessionID;


        @Override
        protected void onPreExecute() {

            sessionID = application
                    .getSharedPreferences("LocMess", MODE_PRIVATE)
                    .getInt("session", 0);


            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            // Setup url
            final String url = ((LocMessApplication) getApplicationContext()).getServerURL() + "/profilekey";

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

    public class RemoveProfileKeyTask extends AsyncTask<String, Void, Boolean> {
        private int sessionID;

        @Override
        protected void onPreExecute() {

            sessionID = application
                    .getSharedPreferences("LocMess", MODE_PRIVATE)
                    .getInt("session", 0);


            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            // Setup url
            final String url = ((LocMessApplication) getApplicationContext()).getServerURL() + "/profilekey";

            // Populate header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("session", String.valueOf(sessionID));
            requestHeaders.add("key", params[0]);

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
                Log.e("RemoveProfileKeyTask", e.getMessage(), e);

                return null;
            }

        }

    }
}
