package com.hercules.werbung.myjirasampleapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hercules.werbung.jirarestapi.JiraRestClient;
import com.hercules.werbung.jirarestapi.com.JiraListener;

import org.json.JSONException;
import org.json.JSONObject;

public class GetIssueActivity extends AppCompatActivity {

    // Singleton of the Jira REST API lib
    private JiraRestClient mJiraRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_issue);

        // Get references to UI Elements
        final Button btnGet = (Button) findViewById(R.id.btnGet);
        final EditText txtKeyOrId = (EditText) findViewById(R.id.txtKeyOrId);
        final TextView txtIssue = (TextView) findViewById(R.id.txtIssue);
        txtIssue.setMovementMethod(new ScrollingMovementMethod());

        // Init the Jira REST API singleton
        mJiraRestClient = JiraRestClient.getInstance(this);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get username and password from the preferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GetIssueActivity.this);
                String username = prefs.getString("username", "");
                String password = prefs.getString("password", "");

                // Get the wanted Issue Id or key from the UI
                String keyOrId = txtKeyOrId.getText().toString();

                mJiraRestClient.getIssue(username,
                        password,
                        keyOrId,
                        null,
                        null,
                        new JiraListener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jiraObject) {
                                try {
                                    txtIssue.setText(jiraObject.toString(2));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onErrorResponse(Exception e) {
                                Toast.makeText(GetIssueActivity.this, R.string.retrieving_not_succ, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


}
