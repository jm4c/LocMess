package pt.ulisboa.tecnico.cmov.locmess.activities.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.tasks.rest.client.accounts.CreateAccountTask;


public class NewUserActivity extends AppCompatActivity {
    private static final String TAG = "NewUserActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        Button signupButton = (Button) findViewById(R.id.btn_signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        TextView loginLink = (TextView) findViewById(R.id.link_login);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validateFields()) {
            return;
        }

        Button signupButton = (Button) findViewById(R.id.btn_signup);
//        signupButton.setEnabled(false);

        CreateAccountTask task = new CreateAccountTask(this);

        task.execute();

    }


    public void onSignupSuccess() {
        Button signupButton = (Button) findViewById(R.id.btn_signup);
//        signupButton.setEnabled(true);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(),
                "Sign up failed.\n" +
                        "The username '" +
                        ((EditText) findViewById(R.id.input_name)).getText().toString() +
                        "' already exists."
                , Toast.LENGTH_LONG).show();

        Button signupButton = (Button) findViewById(R.id.btn_signup);
//        signupButton.setEnabled(true);
    }

    public boolean validateFields() {

        EditText nameText = (EditText) findViewById(R.id.input_name);
        EditText passwordText = (EditText) findViewById(R.id.input_password);
        EditText passwordTextConfirm = (EditText) findViewById(R.id.input_password_confirm);

        String name = nameText.getText().toString();
        String password = passwordText.getText().toString();
        String passwordConfirm = passwordTextConfirm.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            return false;
        } else {
            nameText.setError(null);
        }


        if (password.isEmpty() || password.length() < 4) {
            passwordText.setError("larger than 4 characters");
            return false;
        } else {
            passwordText.setError(null);
        }

        if (!passwordConfirm.equals(password)) {
            passwordTextConfirm.setError("passwords must be equal");
            return false;
        } else {
            passwordTextConfirm.setError(null);
        }


        return true;
    }


}
