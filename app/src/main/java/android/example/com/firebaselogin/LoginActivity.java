package android.example.com.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText mEmailEditText;
    private TextInputEditText mPasswordEditText;
    private TextView mEmailErrorTextView;
    private TextView mPasswordErrorTextView;
    private ProgressBar mProgressBar;

    private String mEmail;
    private String mPassword;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //navigate to HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);

            //finish this activity
            this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailEditText = findViewById(R.id.email);
        mPasswordEditText = findViewById(R.id.password);

        mEmailErrorTextView = findViewById(R.id.email_error_text_vew);
        mPasswordErrorTextView = findViewById(R.id.password_error_text_view);

        mProgressBar = findViewById(R.id.progressBar);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show the progress bar
                mProgressBar.setVisibility(View.VISIBLE);

                //clear any error on mEmailErrorTextView and mPasswordErrorTextView if any
                mEmailErrorTextView.setText(null);
                mPasswordErrorTextView.setText(null);

                //login
                login();
            }
        });

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

                //finish this activity
                LoginActivity.this.finish();
            }
        });
    }

    private void login() {
        if (ValidatePassword()) {
            mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //hide the progress bar
                    mProgressBar.setVisibility(View.INVISIBLE);

                    if (task.isSuccessful()) {
                        //sign in success
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);

                        //finish this activity
                        LoginActivity.this.finish();
                    } else {
                        //if entered account details has a problem or no internet connection
                        try {
                            Exception e = task.getException();
                            if (e != null) {
                                throw e;
                            }
                        } catch (FirebaseAuthInvalidUserException e) {
                            //email not found
                            mEmailErrorTextView.setText(getString(R.string.email_not_found));
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            //wrong password
                            mPasswordErrorTextView.setText(getString(R.string.wrong_password));
                        } catch (FirebaseNetworkException e) {
                            //if no internet connection
                            Toast.makeText(LoginActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
        } else mProgressBar.setVisibility(View.INVISIBLE);
    }

    private boolean ValidatePassword() {
        Editable emailEditable = mEmailEditText.getEditableText();
        if (emailEditable != null) {
            mEmail = emailEditable.toString();

            if (mEmail.length() == 0) {
                mEmailErrorTextView.setText(getString(R.string.email_required));
                return false;
            }

            //email pattern from https://stackoverflow.com/a/8204716/8650277
            final Pattern VALID_EMAIL_ADDRESS_REGEX =
                    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            final Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mEmail);
            if (!matcher.find()) {
                mEmailErrorTextView.setText(getString(R.string.email_error));
                return false;
            }
        } else return false;

        Editable passwordEditable = mPasswordEditText.getEditableText();
        if (passwordEditable != null) {
            mPassword = passwordEditable.toString();
            if (mPassword.length() < 8) {
                mPasswordErrorTextView.setText(getString(R.string.password_error));
                return false;
            }
        }

        return true;
    }
}
