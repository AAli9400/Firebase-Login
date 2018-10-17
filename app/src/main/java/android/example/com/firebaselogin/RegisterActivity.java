package android.example.com.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText mEmailEditText;
    private TextInputEditText mPasswordEditText;
    private TextInputEditText mConfirmPasswordEditText;
    private TextView mEmailErrorTextView;
    private TextView mPasswordErrorTextView;
    private TextView mConfirmPasswordErrorTextView;
    private ProgressBar mProgressBar;

    private String mEmail;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mEmailEditText = findViewById(R.id.email);
        mPasswordEditText = findViewById(R.id.password);
        mConfirmPasswordEditText = findViewById(R.id.confirm_password);

        mEmailErrorTextView = findViewById(R.id.email_error_text_view);
        mPasswordErrorTextView = findViewById(R.id.password_error_text_view);
        mConfirmPasswordErrorTextView = findViewById(R.id.confirm_password_error_text_view);

        mProgressBar = findViewById(R.id.progressBar);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to LoginActivity
                navigateToLoginActivity();
            }
        });

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show the progress bar
                mProgressBar.setVisibility(View.VISIBLE);

                //clear any error on mEmailErrorTextView, mPasswordErrorTextView
                //and mConfirmPasswordErrorTextView if any
                mEmailErrorTextView.setText(null);
                mPasswordErrorTextView.setText(null);
                mConfirmPasswordErrorTextView.setText(null);

                //register
                register();
            }
        });
    }

    private void register() {
        if (ValidatePassword()) {
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //hide the progress bar
                    mProgressBar.setVisibility(View.INVISIBLE);

                    if (task.isSuccessful()) {
                        //sign up success
                        //navigate to HomeActivity
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        startActivity(intent);

                        //finish this activity
                        RegisterActivity.this.finish();
                    } else {
                        //if entered account details has a problem or no internet connection
                        try {
                            Exception e = task.getException();
                            if (e != null) {
                                throw e;
                            }
                        } catch (FirebaseAuthUserCollisionException e) {
                            //email already in use
                            mEmailErrorTextView.setText(getString(R.string.used_email));
                        } catch (FirebaseNetworkException e) {
                            //if no internet connection
                            Toast.makeText(RegisterActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
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

        Editable confirmPasswordEditable = mConfirmPasswordEditText.getEditableText();
        if (confirmPasswordEditable != null) {
            String confirmPassword = confirmPasswordEditable.toString();
            if (confirmPassword.length() == 0) {
                mConfirmPasswordErrorTextView.setText(getString(R.string.please_confirm_password));
                return false;
            }

            if (!confirmPassword.equals(mPassword)) {
                mConfirmPasswordErrorTextView.setText(getString(R.string.password_mismatch));
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle up button
        if (item.getItemId() == android.R.id.home) {
            navigateToLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //handle back button
        navigateToLoginActivity();
    }

    private void navigateToLoginActivity() {
        //navigate to LoginActivity
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);

        //finish this activity
        RegisterActivity.this.finish();
    }
}

