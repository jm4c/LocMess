package pt.ulisboa.tecnico.cmov.locmess.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.R;


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

        if (!validate()) {
            onSignupFailed();
            return;
        }

        Button signupButton = (Button) findViewById(R.id.btn_signup);
        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(NewUserActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        Button signupButton = (Button) findViewById(R.id.btn_signup);
        signupButton.setEnabled(true);
        //  setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();

        Button signupButton = (Button) findViewById(R.id.btn_signup);
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        EditText passwordText = (EditText) findViewById(R.id.input_password);
        EditText passwordTextConfirm = (EditText) findViewById(R.id.input_password_confirm);
        EditText nameText = (EditText) findViewById(R.id.input_name);

        String name = nameText.getText().toString();
        String pass = passwordTextConfirm.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }



        if (password.isEmpty() || password.length() < 4) {
            passwordText.setError("larger than 4 characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (!pass.equals(password)) {
            passwordTextConfirm.setError("passwords must be equal");
            valid = false;
        } else {
            passwordTextConfirm.setError(null);
        }


        return valid;
    }
}
