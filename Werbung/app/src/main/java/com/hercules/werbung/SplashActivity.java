package com.hercules.werbung;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity that displays the splash screen
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        // Display the splash screen for 1.5s
        final Intent loginIntent = new Intent(this, LoginActivity.class);
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(loginIntent);
            }
        };
        timer.schedule(task, 1500);

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // No menu on splash screen
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // No menu implementation
        return true;
    }
}
