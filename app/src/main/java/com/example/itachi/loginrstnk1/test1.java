package com.example.itachi.loginrstnk1;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by itachi on 26/3/18.
 */

public class test1 extends AppCompatActivity  implements View.OnClickListener {

    private static final String TAG = "PasswordlessSignIn";
    private static final String KEY_PENDING_EMAIL = "key_pending_email";

    private FirebaseAuth mAuth;

    private Button mSendLinkButton;
    private Button mSignInButton;
    private Button mSignOutButton;

    private EditText mEmailField;
    private TextView mStatusText;

    private String mPendingEmail;
    private String mEmailLink;


    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordless);

        mAuth = FirebaseAuth.getInstance();

        mSendLinkButton = findViewById(R.id.passwordless_send_email_button);
        mSignInButton = findViewById(R.id.passwordless_sign_in_button);
        mSignOutButton = findViewById(R.id.sign_out_button);

        mEmailField = findViewById(R.id.field_email);
        mStatusText = findViewById(R.id.status);

        mSendLinkButton.setOnClickListener(this);
        mSignInButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);

        // Restore the "pending" email address
        if (savedInstanceState != null) {
            mPendingEmail = savedInstanceState.getString(KEY_PENDING_EMAIL, null);
            mEmailField.setText(KEY_PENDING_EMAIL);
        }

        // Check if the Intent that started the Activity contains an email sign-in link.
        checkIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(mAuth.getCurrentUser());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PENDING_EMAIL, mPendingEmail);
    }

    /**
     * Check to see if the Intent has an email link, and if so set up the UI accordingly.
     * This can be called from either onCreate or onNewIntent, depending on how the Activity
     * was launched.
     */
    private void checkIntent(@Nullable Intent intent) {
        if (intentHasEmailLink(intent)) {
            mEmailLink = intent.getData().toString();

            mStatusText.setText("status_link_found");
            mSendLinkButton.setEnabled(false);
            mSignInButton.setEnabled(true);
        } else {
            mStatusText.setText("status_email_not_sent");
            mSendLinkButton.setEnabled(true);
            mSignInButton.setEnabled(false);
        }
    }

    /**
     * Determine if the given Intent contains an email sign-in link.
     */
    private boolean intentHasEmailLink(@Nullable Intent intent) {
        if (intent != null && intent.getData() != null) {
            String intentData = intent.getData().toString();
            if (mAuth.isSignInWithEmailLink(intentData)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Send an email sign-in link to the specified email.
     */
    private void sendSignInLink(final String email) {

        ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(
                        getPackageName(),
                        false, /* install if not available? */
                        null   /* minimum app version */)
                .setHandleCodeInApp(true).setUrl("https://loginrstnk1.firebaseapp.com/__/auth/action?mode=<action>&oobCode=<code>")
                .build();

        hideKeyboard(mEmailField);
        showProgressDialog();

        mAuth.sendSignInLinkToEmail(email, settings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Link sent");
                            showSnackbar("Sign-in link sent!");

                            mPendingEmail = email;
                            mStatusText.setText("status_email_sent");
                        } else {
                            Exception e = task.getException();
                            Log.w(TAG, "Could not send link", e);
                            showSnackbar("Failed to send link.");

                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                mEmailField.setError("Invalid email address.");
                            }
                        }
                    }
                });
    }

    /**
     * Sign in using an email address and a link, the link is passed to the Activity
     * from the dynamic link contained in the email.
     */
    private void signInWithEmailLink(String email, String link) {
        Log.d(TAG, "signInWithLink:" + link);

        hideKeyboard(mEmailField);
        showProgressDialog();

        mAuth.signInWithEmailLink(email, link)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        mPendingEmail = null;

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmailLink:success");

                            mEmailField.setText(null);
                            updateUI(task.getResult().getUser());
                        } else {
                            Log.w(TAG, "signInWithEmailLink:failure", task.getException());
                            updateUI(null);

                            if (task.getException() instanceof FirebaseAuthActionCodeException) {
                                showSnackbar("Invalid or expired sign-in link.");
                            }
                        }
                    }
                });
    }

    private void onSendLinkClicked() {
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Email must not be empty.");
            return;
        }

        sendSignInLink(email);
    }

    private void onSignInClicked() {
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Email must not be empty.");
            return;
        }

        signInWithEmailLink(email, mEmailLink);
    }

    private void onSignOutClicked() {
        mAuth.signOut();

        updateUI(null);
        mStatusText.setText("status_email_not_sent");
    }

    private void updateUI(@Nullable FirebaseUser user) {
        if (user != null) {
//            mStatusText.setText(user.getEmail() + user.isEmailVerified());

            findViewById(R.id.passwordless_fields).setVisibility(View.GONE);
            findViewById(R.id.passwordless_buttons).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.passwordless_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.passwordless_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.passwordless_send_email_button:
                onSendLinkClicked();
                break;
            case R.id.passwordless_sign_in_button:
                onSignInClicked();
                break;
            case R.id.sign_out_button:
                onSignOutClicked();
                break;
        }
    }
}

