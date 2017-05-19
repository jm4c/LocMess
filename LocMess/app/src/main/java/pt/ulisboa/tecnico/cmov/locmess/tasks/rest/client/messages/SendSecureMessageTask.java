package pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.messages;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.model.types.SecureMessage;

/**
 * Messages methods
 */
public class SendSecureMessageTask extends AsyncTask<SecureMessage, Void, Boolean> {
    private LocMessApplication application;
    private int sessionID;
    private ProgressDialog dialog;


    public SendSecureMessageTask(Context context) {
        this.application = (LocMessApplication) context.getApplicationContext();

        if (context instanceof Activity) {
            dialog = new ProgressDialog(context);
        }
    }


    @Override
    protected void onPreExecute() {
        if (dialog != null) {
            this.dialog.setMessage("Adding new secure message...");
            this.dialog.show();
        }

        sessionID = application
                .getSharedPreferences("LocMess", Context.MODE_PRIVATE)
                .getInt("session", 0);


        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(SecureMessage... params) {
        // Setup url
        final String url = application.getServerURL() + "/securemessage";

        // Populate header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("session", String.valueOf(sessionID));

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(4000);

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params[0], requestHeaders), Boolean.class);

            return response.getBody();

        } catch (HttpClientErrorException e) {
            Log.e("SendSecureMessageTask", e.getMessage(), e);
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                application.forceLoginFlag = true;

            return false;
        } catch (Exception e) {
            Log.e("SendSecureMessageTask", e.getMessage(), e);

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
