package com.hercules.werbung;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hercules.werbung.jirarestapi.JiraRestClient;
import com.hercules.werbung.jirarestapi.com.JiraListener;
import com.hercules.werbung.jirarestapi.json.User;
import com.pakhee.common.CryptLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * A login screen that offers login via user/password.
 */
public class LoginActivity extends Activity {

    private final String AVATAR_SIZE = "48x48";
    public static final String PREF_USER_NAME = "username";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_ENCODED_AVATAR = "encoded_avatar";
    // Singleton of the Jira REST API lib
    private JiraRestClient mJiraRestClient;

    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mErrorView;

    // Shared Preferences
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        setTitle(R.string.title_activity_login);

        mUserView = (EditText) findViewById(R.id.user);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mErrorView = (TextView) findViewById(R.id.login_error);

        // Login Button and Click handler
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // Init the Jira REST API singleton
        mJiraRestClient = JiraRestClient.getInstance(this);
        mJiraRestClient.setBaseURL(getString(R.string.base_url));
    }


    /**
     * Attempts to log in the account specified by the login form.
     * If there are form errors (missing fields), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUserView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edittext));
        mPasswordView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edittext));
        mErrorView.setVisibility(View.GONE);

        // Store values at the time of the login attempt.
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setBackground(ContextCompat.getDrawable(this, R.drawable.invalid_rounded_edittext));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid user
        if (TextUtils.isEmpty(user)) {
            mUserView.setBackground(ContextCompat.getDrawable(this, R.drawable.invalid_rounded_edittext));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mErrorView.setVisibility(View.VISIBLE);
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mJiraRestClient.connectUser(user, password, new JiraListener<User>() {
                @Override
                public void onResponse(User jiraObject) {
                    finish();
                    savePreferences(jiraObject);
                    startActivity(new Intent(LoginActivity.this, NavigationDrawerActivity.class));
                }

                @Override
                public void onErrorResponse(Exception e) {
                    showProgress(false);
                    showErrorDialog(R.string.invalid_pwd_title, R.string.invalid_pwd_message);
                    mPasswordView.setText("");
                    mPasswordView.requestFocus();
                }
            });
        }
    }

    /**
     * Save the user's data to the local preferences
     */
    private void savePreferences(final User user) {
        final SharedPreferences.Editor ed = mPrefs.edit();

        // download the avatar image
        Uri avatarUri = user.getAvatarURI(AVATAR_SIZE);
        if (avatarUri != null) {
            ImageRequest request = new ImageRequest(avatarUri.toString(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            // Save the avatar bitmap to the preferences
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] b = baos.toByteArray();
                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                            ed.putString(PREF_ENCODED_AVATAR, encodedImage);
                            ed.apply();
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            mJiraRestClient.addToRequestQueue(request);
        }

        ed.putString(PREF_USER_NAME, user.getDisplayName());
        ed.putString(PREF_EMAIL, user.getJSONObject().optString("emailAddress"));

        ed.apply();
    }


    /**
     * Show the error alert dialog
     *
     * @param titleId   Ressource id of the title to display
     * @param messageId Message id to display
     */
    private void showErrorDialog(final int titleId, final int messageId) {
        String title = getString(titleId);
        String message = getString(messageId);
        showErrorDialog(title, message);
    }

    /**
     * Show the error alert dialog
     *
     * @param title   the title to display
     * @param message the message to display
     */
    private void showErrorDialog(final String title, final String message) {
        DialogFragment dlg = new DialogFragment() {

            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Dialog dialog;
                // inflate the dialog view
                View dialogView = LoginActivity.this.getLayoutInflater().inflate(R.layout.dialog_layout, null);
                // set the dialog title
                TextView titleView = (TextView) dialogView.findViewById(R.id.title);
                titleView.setText(title);
                // set the dialog message
                TextView messageView = (TextView) dialogView.findViewById(R.id.message);
                messageView.setText(message);

                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setView(dialogView);

                // Create the AlertDialog object and return it
                dialog = builder.create();

                // set the dialog button
                Button button = (Button) dialogView.findViewById(R.id.btn_ok);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }
                });

                return dialog;
            }
        };

        dlg.show(getFragmentManager(), null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // fade-in the progress spinner.

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanResult != null) {  // the result code of the QR-Scan request
            if (resultCode == RESULT_OK) {
                boolean readable = true; // error flag
                String qr_text = scanResult.getContents();
                String output = null;

                // try to encrypt the message
                try {
                    CryptLib _crypt = new CryptLib();
                    String key = getString(R.string.aes_key);
                    String iv = getString(R.string.aes_iv);
                    output = _crypt.decrypt(qr_text, key, iv);
                } catch (NoSuchAlgorithmException
                        | NoSuchPaddingException
                        | IllegalBlockSizeException
                        | BadPaddingException
                        | InvalidAlgorithmParameterException
                        | InvalidKeyException
                        | UnsupportedEncodingException e) {
                    readable = false;
                }

                if (output != null) {
                    try {
                        JSONObject json = new JSONObject(output);

                        // At least the user is expected to appear in the json
                        // if not, we set the error flag readable to false
                        if (json.has("user")) {
                            mUserView.setText(json.getString("user"));

                            if (json.has("pass")) {
                                mPasswordView.setText(json.getString("pass"));

                            }

                        } else {
                            readable = false;
                        }

                    } catch (JSONException e) {
                        readable = false;
                    }
                }

                if (!readable) {
                    Toast.makeText(this, R.string.error_interpretation_qr, Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                // Do nothing if the request has been cancelled
            }
        }
    }
}

