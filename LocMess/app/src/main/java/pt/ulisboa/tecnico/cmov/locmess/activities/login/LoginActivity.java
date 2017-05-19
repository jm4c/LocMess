package pt.ulisboa.tecnico.cmov.locmess.activities.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.locmess.LocMessApplication;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.accounts.LoginTask;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private Button loginButton;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private String username;
    private String password;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.btn_login);
        usernameEditText = (EditText) findViewById(R.id.input_name);
        passwordEditText = (EditText) findViewById(R.id.input_password);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                validateFields();
            }
        });

        TextView signupLink = (TextView) findViewById(R.id.link_signup);
        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        LocMessApplication application = (LocMessApplication) getApplication().getApplicationContext();
        if (application.getSharedPreferences("LocMess", MODE_PRIVATE).contains("username") &&
                application.getSharedPreferences("LocMess", MODE_PRIVATE).contains("password")) {
            username = application.getSharedPreferences("LocMess", MODE_PRIVATE).getString("username", null);
            password = application.getSharedPreferences("LocMess", MODE_PRIVATE).getString("password", null);
            login(username, password);
        }
        super.onResume();
    }

    public void login(String username, String password) {
        LoginTask task = new LoginTask(this, username, password);
        task.execute();
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }



    public void validateFields() {
        if (passwordEditText.getText().length() < 4) {
            passwordEditText.setError("larger than 4 characters");
            return;
        } else {
            passwordEditText.setError(null);
        }

        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();

        login(username, password);
    }


}
