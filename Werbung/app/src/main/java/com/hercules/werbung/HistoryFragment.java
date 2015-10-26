package com.hercules.werbung;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hercules.werbung.jirarestapi.JiraRestClient;
import com.hercules.werbung.jirarestapi.com.JiraListener;

import org.json.JSONObject;

/**
 * History fragment placeholder
 */
public class HistoryFragment extends Fragment {
    public static final String BASE_URL = "https://adqa-dev.atlassian.net";
    public static final String USERNAME = "topcoder";
    public static final String PASSWORD = "pass1234";

    // Singleton of the Jira REST API lib
    private JiraRestClient mJiraRestClient;


    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container,
                false);
        mJiraRestClient = JiraRestClient.getInstance(getActivity());
        mJiraRestClient.setBaseURL(BASE_URL);
        getUserIssues();
        return rootView;
    }

    private void getUserIssues(){
        mJiraRestClient.search(USERNAME, PASSWORD, "assignee=" + USERNAME, null, null, null, null, null, new JiraListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jiraObject) {
                Log.d("UserIssues",jiraObject.toString());

            }

            @Override
            public void onErrorResponse(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NavigationDrawerActivity) activity).onSectionAttached(2);
    }

}