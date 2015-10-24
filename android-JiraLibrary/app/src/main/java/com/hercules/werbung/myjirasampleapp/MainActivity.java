package com.hercules.werbung.myjirasampleapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.hercules.werbung.jirarestapi.JiraRestClient;
import com.hercules.werbung.jirarestapi.com.JiraListener;
import com.hercules.werbung.jirarestapi.dom.Issue;
import com.hercules.werbung.jirarestapi.json.User;
import com.hercules.werbung.jirarestapi.json.meta.CreateMeta;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaField;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaIssueType;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaProject;
import com.hercules.werbung.jirarestapi.json.meta.Meta;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This the main class of the sample app to demonstrate:
 * - Authenticating a user and displaying its avatar
 * - Retrieving the lists of available projects and issue types that can be created
 * - Creating a new issue based on metadata retrieved in the step above
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Base URL of the Jira website
     */
    public static final String BASE_URL = "https://adqa-dev.atlassian.net";
    /**
     * Avatar image size
     */
    public static final String AVATAR_SIZE = "48x48";

    // Singleton of the Jira REST API lib
    private JiraRestClient mJiraRestClient;
    // Credentials
    private String mUsername="";
    private String mPassword="";

    // The activity's views
    private View mContentView=null;
    private Spinner mProjectSpinner;
    private Spinner mIssueTypeSpinner;
    private Button mSelectIssueTypeBtn;
    private Button mCreateIssueBtn;
    private CheckBox mRequiredCheckBox;

    // HashMap containing the retrieved projects and issue types
    private HashMap<CreateMetaProject, String[]> mProjectIssueTypes;
    // the selected project
    private CreateMetaProject mCurrentProject;
    // the select issue type name
    private String mCurrentIssueTypeName;
    // the dynamic issue type fields
    private ArrayList<UIfield> mUIfields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure that preferences contains values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);

        // Set the content view
        mContentView=getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(mContentView);

        // Init the Jira REST API singleton
        mJiraRestClient=JiraRestClient.getInstance(this);

        // get the views
        mProjectSpinner = (Spinner)findViewById(R.id.spinnerProject);
        mIssueTypeSpinner = (Spinner)findViewById(R.id.spinnerIssueType);
        mSelectIssueTypeBtn = (Button)findViewById(R.id.selectIssueTypeBtn);
        mCreateIssueBtn = (Button)findViewById(R.id.createIssueBtn);
        mRequiredCheckBox = (CheckBox)findViewById(R.id.requiredCheckBox);

        // set the project spinner's listener
        mProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentProject = (CreateMetaProject)mProjectIssueTypes.keySet().toArray()[position];
                if (mUsername != null && mPassword != null) {
                    // Execute the request to retrieve the Meta data for issue creation
                    mJiraRestClient.getCreateMeta(mUsername, mPassword, new JiraListener<CreateMeta>() {
                        @Override
                        public void onResponse(CreateMeta jiraObject) {
                            // Set the data received in the class members
                            Map<String, String[]> projectIssueTypes = new HashMap<String, String[]>();
                            ArrayList<CreateMetaProject> projects = jiraObject.getProjects();
                            ArrayList<CreateMetaIssueType> issueTypes = projects.get(0).getIssueTypes();
                            int len=issueTypes.size();

                            // init the Issue Type Spinner
                            String[] issueTypeNames= new String[len];
                            for (int j=0; j < len; j++) {
                                issueTypeNames[j]=issueTypes.get(j).getIssueTypeName();
                            }
                            mIssueTypeSpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, issueTypeNames));
                        }

                        @Override
                        public void onErrorResponse(Exception e) {
                            e.printStackTrace();
                        }
                    },new String[]{""+mCurrentProject.getProjectId()}, null, null, null);
            }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // set the issue type spinner's listener
        mIssueTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Set the data received in the relevant class members
                mCurrentIssueTypeName=((ArrayAdapter<String>)mIssueTypeSpinner.getAdapter()).getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // set the fields generation button's click listener
        mSelectIssueTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute the request to retrieve the Meta data for issue creation using the select project and issue type as parameters
                mJiraRestClient.getCreateMeta(mUsername, mPassword, new JiraListener<CreateMeta>() {
                    @Override
                    public void onResponse(CreateMeta jiraObject) {
                        // Construct the view with the issue fields
                        buildCreateIssueFields(jiraObject);
                    }

                    @Override
                    public void onErrorResponse(Exception e) {
                        e.printStackTrace();
                    }
                }, new String[]{"" + mCurrentProject.getProjectId()}, null, null, new String[]{mCurrentIssueTypeName});
            }
        });

        // set the submit issue button's click listener
        mCreateIssueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // build the issue to submit
                Issue.Builder builder  = new Issue.Builder();
                builder.setProject(mCurrentProject.getProjectKey());
                builder.setIssueType(mCurrentIssueTypeName);

                Issue issue = builder.build();
                setFields(issue);

                // Execute the request to post the new issue
                mJiraRestClient.postIssue(mUsername, mPassword, issue, new JiraListener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jiraObject) {
                        // display a success toast
                        Toast.makeText(MainActivity.this, "New issue with key:"+jiraObject.optString("key")+" created!",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorResponse(Exception e) {
                        e.printStackTrace();
                        // display an error toast
                        Toast.makeText(MainActivity.this, "Error: Issue not created!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // open the initial login dialog
        openLoginDialog(false);
    }

    /**
     * Set the UI fields values in an issue object
     * @param issue the issue to set
     */
    private void setFields(Issue issue) {
        for (int i=0; i<mUIfields.size(); i++) {
            UIfield field = mUIfields.get(i);

            try {
                if (field.type == Type.STRING) {
                    issue.getJsonObjectFields().put(field.key, ((EditText) field.view).getText().toString());
                } else if (field.type == Type.SELECT) {
                    issue.getJsonObjectFields().put(field.key, ((Spinner) field.view).getSelectedItem().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Build and display the UI fields to set an issue
     * @param jiraObject the metadata object to create an issue
     */
    private void buildCreateIssueFields(CreateMeta jiraObject) {
        ArrayList<CreateMetaField> metaFields = jiraObject.getProjects().get(0).getIssueTypes().get(0).getFields();
        mUIfields = new ArrayList<UIfield>();
        if (metaFields.size() > 0) {
            // get and clean the layout
            LinearLayout fieldLayout = (LinearLayout)mContentView.findViewById(R.id.fieldLayout);
            fieldLayout.removeAllViews();
            for (int i = 0; i < metaFields.size(); i++) {
                // add the field views
                addFieldView(fieldLayout, metaFields.get(i));
            }
            // set the create issue button visible
            mContentView.findViewById(R.id.createIssueBtn).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Add a field view
     * @param parentLayout the layout to add the view
     * @param createMetaField the metadata object describing the field
     */
    private void addFieldView(LinearLayout parentLayout, CreateMetaField createMetaField) {
        View fieldView = createFieldView(createMetaField, mRequiredCheckBox.isChecked());

        if (fieldView != null) {
            fieldView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            parentLayout.addView(fieldView);
        }
    }

    /**
     * create a field view
     * @param createMetaField the metadata object describing the field
     * @param onlyRequired true to only create the required fields
     * @return the field view
     */
    private View createFieldView(CreateMetaField createMetaField, boolean onlyRequired) {
        if (!onlyRequired || createMetaField.getRequired()) {
            String type = createMetaField.getSchema().getCustom();
            if (type != null && type != "") {
                switch (type) {
                    case "com.atlassian.jira.plugin.system.customfieldtypes:textfield":
                        return createEditText(this, createMetaField);
                    case "com.atlassian.jira.plugin.system.customfieldtypes:select":
                        return createSpinner(this, createMetaField);
                    default:
                        Log.d(this.getClass().getName(), "custom:" + type + " is unknown");
                        return null;
                }
            } else {
                type = createMetaField.getSchema().getType();
                if (type != null && type != "") {
                    switch (type) {
                        case "string":
                            return createEditText(this, createMetaField);
                        default:
                            Log.d(this.getClass().getName(), "type:" + type + " is unknown");
                            return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Create a field view for value selection
     * @param context
     * @param createMetaField the metadata object describing the field
     * @return a spinner view
     */
    private View createSpinner(Context context, CreateMetaField createMetaField) {
        Spinner spinner = new Spinner(context, Spinner.MODE_DROPDOWN);
        spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item,
                Meta.getValues(createMetaField.getAllowedValues(), "value")));
        mUIfields.add(new UIfield(createMetaField.getKey(), spinner, Type.SELECT));
        return spinner;
    }

    /**
     * Create a field view for text input
     * @param context
     * @param createMetaField he metadata object describing the field
     * @return an EditText view
     */
    private View createEditText(Context context, CreateMetaField createMetaField) {
        EditText editText = new EditText(context);
        editText.setHint(createMetaField.getName());
        mUIfields.add(new UIfield(createMetaField.getKey(), editText, Type.STRING));
        return editText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_issue) {
            startActivity(new Intent(this,GetIssueActivity.class));
            return true;
        }

        if (id == R.id.action_search) {
            startActivity(new Intent(this,SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getUsername() {
        return mUsername;
    };

    public String getPassword() {
        return mPassword;
    };

    public void setUsername(String username) {
        mUsername=username;
    };

    public void setPassword(String password) {
        mPassword=password;
    };

    /**
     * Display the name and avatar of the user
     * @param user
     */
    public void showAvatar(final User user) {
        if (mContentView!=null && user!=null) {
            final ImageView avatar = (ImageView)mContentView.findViewById(R.id.avatar);
            final TextView welcome = (TextView)mContentView.findViewById(R.id.welcome);
            Uri avatarUri = user.getAvatarURI(AVATAR_SIZE);
            if (avatarUri != null) {
                // get the avatar image
                ImageRequest request = new ImageRequest(avatarUri.toString(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                // display user name and avatar
                                welcome.setText(welcome.getText() + " " + user.getDisplayName());
                                avatar.setImageBitmap(bitmap);
                            }
                        }, 0, 0, null,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                avatar.setImageResource(R.drawable.no_image_available);
                            }
                        });
                mJiraRestClient.addToRequestQueue(request);
                avatar.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Open the Login dialog
     * @param retry true if opened on error, else false
     */
    public void openLoginDialog(boolean retry) {
        LoginDialog loginDialog = LoginDialog.newInstance(retry);
        loginDialog.show(getFragmentManager(), loginDialog.getClass().getName());
    }

    /**
     * Init the spinners for project and issue type with the metadata retrieved
     */
    public void initSpinners() {
        if (mUsername != null && mPassword != null) {
            // Execute the request to retrieve the list of projects and issue types.
            mJiraRestClient.getCreateMeta(mUsername, mPassword, new JiraListener<CreateMeta>() {
                @Override
                public void onResponse(CreateMeta jiraObject) {
                    // init the relevant class members with the metadata received
                    initMapProjectIssueTypes(jiraObject);
                    Object[] projects = mProjectIssueTypes.keySet().toArray();
                    String[] projectNames = new String[projects.length];
                    for (int i=0; i<projects.length; i++) {
                        projectNames[i]=((CreateMetaProject)projects[i]).getProjectName();
                    }
                    // set the spinner values
                    mProjectSpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, projectNames));
                    mIssueTypeSpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, mProjectIssueTypes.get((CreateMetaProject)projects[0])));
                }

                @Override
                public void onErrorResponse(Exception e) {
                    e.printStackTrace();
                }
            }, null, null, null, null);
        }
    }

    /**
     *
     * @param jiraObject
     */
    private void initMapProjectIssueTypes(CreateMeta jiraObject) {
        mProjectIssueTypes = new HashMap<CreateMetaProject, String[]>();
        ArrayList<CreateMetaProject> projects = jiraObject.getProjects();
        for (int i=0; i < projects.size(); i++) {
            ArrayList<CreateMetaIssueType> issueTypes = projects.get(i).getIssueTypes();
            int len=issueTypes.size();
            String[] issueTypeNames= new String[len];
            for (int j=0; j < len; j++) {
                issueTypeNames[j]=issueTypes.get(j).getIssueTypeName();
            }
            mProjectIssueTypes.put(projects.get(i), issueTypeNames);
        }
        mCurrentProject = projects.get(0);
        mCurrentIssueTypeName = mProjectIssueTypes.get(projects.get(0))[0];
    }

    /**
     * Class to bind a field view with Jira key and type
     */
    private class UIfield {
        public String key;
        public View view;
        public Type type;

        public UIfield (String key, View view, Type type) {
            this.key=key;
            this.view=view;
            this.type=type;
        }
    }

    /**
     * Enum for Jira field types
     */
    public enum Type {
        STRING,
        SELECT
    }



}
