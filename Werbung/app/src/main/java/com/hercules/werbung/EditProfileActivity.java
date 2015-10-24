package com.hercules.werbung;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Activity to edit the user's profile
 */
public class EditProfileActivity extends AppCompatActivity {
    // Shared Preferences
    private SharedPreferences mPrefs;
    private Button mSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        // get the shared preferences
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Button to save the profile
        mSaveBtn = (Button) findViewById(R.id.complete_profile_button);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save the profile modifications
                saveProfile();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Init the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Edit Profile");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the user data to the shared preferences
     */
    private void saveProfile() {
        String firstname = ((TextView) findViewById(R.id.firstName)).getText().toString();
        String lastname = ((TextView) findViewById(R.id.lastName)).getText().toString();
        String email = ((TextView) findViewById(R.id.email)).getText().toString();
        savePreferences(firstname, lastname, email);
        finish();
    }

    /**
     * Save the user's data to the local preferences
     */
    private void savePreferences(String firstname, String lastname, String email) {
        final SharedPreferences.Editor ed = mPrefs.edit();

        ed.putString(LoginActivity.PREF_USER_NAME, firstname + " " + lastname);
        ed.putString(LoginActivity.PREF_EMAIL, email);

        ed.commit();
    }

}

