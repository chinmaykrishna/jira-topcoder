package com.hercules.werbung.myjirasampleapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.hercules.werbung.jirarestapi.JiraRestClient;
import com.hercules.werbung.jirarestapi.com.JiraListener;
import com.hercules.werbung.jirarestapi.json.User;

/**
 * Created by Guillaume on 10/08/2015.
 */
public class LoginDialog extends DialogFragment {
    // the content view
    private View mContentView;

    /**
     * create a new dialog instance with parameters
     * @param retry true when create the dialog on login retry
     * @return the dialog
     */
    static LoginDialog newInstance(boolean retry) {
        LoginDialog loginDialog = new LoginDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putBoolean("retry", retry);
        loginDialog.setArguments(args);

        return loginDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // set the dialog view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mContentView = inflater.inflate(R.layout.dialog_signin, null);
        // get the dialog argument
        boolean retry = getArguments().getBoolean("retry");
        if (retry) {
            mContentView.findViewById(R.id.message).setVisibility(View.VISIBLE);
        }
        // build the dialog
        builder.setView(mContentView)
                .setTitle(R.string.login)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity activity = (MainActivity) getActivity();
                        // set the credential values
                        activity.setUsername(((EditText) mContentView.findViewById(R.id.username)).getText().toString());
                        activity.setPassword(((EditText) mContentView.findViewById(R.id.password)).getText().toString());
                        // do the log in
                        login(activity.getUsername(), activity.getPassword());
                        // close the dialog
                        LoginDialog.this.getDialog().cancel();
                    }
                });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    /**
     * Log in with provided username and password
     * @param username
     * @param password
     */
    private void login(final String username, final String password) {
        final MainActivity activity = (MainActivity) getActivity();
        // get the Jira REST API lib singleton
        JiraRestClient jiraRestClient = JiraRestClient.getInstance(activity);
        // set the Jira website's base URL
        jiraRestClient.setBaseURL(MainActivity.BASE_URL);
        // Execute the request to connect the user and retrieve its info
        jiraRestClient.connectUser(username, password, new JiraListener<User>() {
            @Override
            public void onResponse(User user) {
                activity.setUsername(username);
                activity.setPassword(password);
                activity.showAvatar(user);
                activity.initSpinners();
            }

            @Override
            public void onErrorResponse(Exception e) {
                // open the dialog to retry to log in
                activity.openLoginDialog(true);
            }
        });
    }
}
