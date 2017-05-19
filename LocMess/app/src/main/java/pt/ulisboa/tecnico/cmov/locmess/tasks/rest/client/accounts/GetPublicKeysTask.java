package pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.accounts;

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

import java.security.PublicKey;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;

import static pt.ulisboa.tecnico.cmov.locmess.utils.CryptoUtils.deserialize;
import static pt.ulisboa.tecnico.cmov.locmess.utils.CryptoUtils.serialize;

// SECURITY
public class GetPublicKeysTask extends AsyncTask<Void, Void, HashMap<String, PublicKey>> {
    private LocMessApplication application;
    private int sessionID;

    public GetPublicKeysTask(Context context) {
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
    protected HashMap<String, PublicKey> doInBackground(Void... params) {

        // Setup url
        final String url = application.getServerURL() + "/publickey";

        // Populate header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("session", String.valueOf(sessionID));

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(4000);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(serialize(params[0]), requestHeaders), byte[].class);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED)
                application.forceLoginFlag = true;
            if (response.getBody() != null) {
                HashMap<String, byte[]> serializedPublicKeys = (HashMap<String, byte[]>) deserialize(response.getBody());
                HashMap<String, PublicKey> publicKeys = new HashMap<>();
                for (String owner : serializedPublicKeys.keySet()) {
                    publicKeys.put(owner, (PublicKey) deserialize(serializedPublicKeys.get(owner)));
                }
                return publicKeys;
            }else {
                return null;
            }

        } catch (Exception e) {
            Log.e("GetPublicKeysTask", e.getMessage(), e);

            return null;
        }

    }

}
