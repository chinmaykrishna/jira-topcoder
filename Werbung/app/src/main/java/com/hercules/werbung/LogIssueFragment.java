package com.hercules.werbung;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.hercules.werbung.jirarestapi.JiraRestClient;
import com.hercules.werbung.jirarestapi.com.JiraListener;
import com.hercules.werbung.jirarestapi.dom.Issue;
import com.hercules.werbung.jirarestapi.json.meta.CreateMeta;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaIssueType;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaProject;
import com.hercules.werbung.view.NewIssueForm;
import com.hercules.werbung.view.TabManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the activity class to create an new issue.
 * - it retrieves the lists of available projects and issue types that can be created
 * - it allows to create a new issue based on metadata retrieved in the step above
 */
public class LogIssueFragment extends Fragment {
    /**
     * Base URL of the Jira website
     */
    public static final String BASE_URL = "https://adqa-dev.atlassian.net";
    public static final String USERNAME = "topcoder";
    public static final String PASSWORD = "pass1234";

    // JQL query to get all issues have Configuration type
    public static final String JQL_CONFIGURATION = "Type = Configuration";

    // Singleton of the Jira REST API lib
    private JiraRestClient mJiraRestClient;

    // The activity's views
    private View mRootView = null;
    private Spinner mProjectSpinner;
    private Spinner mIssueTypeSpinner;
    private Button mCreateIssueBtn;
    private Button mPrevBtn;
    private Button mNextBtn;
    // the view flipper to handle screens
    private ViewFlipper mViewFlipper;

    // HashMap containing the retrieved projects and issue types
    private HashMap<CreateMetaProject, String[]> mProjectIssueTypes;

    // HashMap containing the retrieved projects and issue types
    private Map<String, String> mTypeScreenIdMap;

    // HashMap containing the screen ID and tab ID
    private Map<String, String> mTabMap;

    // HashMap containing the tab ID and field ID
    private Map<String, String> mTabFieldMap;

    // the selected project
    private CreateMetaProject mCurrentProject;
    // the create meta
    private CreateMeta mCreateMeta;
    // the selected project id
    private int mCurrentProjectId;

    // flag to check if the spinner is initializing
    private boolean isFirst = true;

    // List of issue type for selected project
    private ArrayList<CreateMetaIssueType> mCurrentIssueTypeList = new ArrayList<>();

    // ID of selected issue type
    private CreateMetaIssueType mCurrentIssueType;
    // the form to create a new issue
    private NewIssueForm mFieldForm;

    // Tab manager object
    private TabManager mTabManager;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static LogIssueFragment newInstance() {
        LogIssueFragment fragment = new LogIssueFragment();
        return fragment;
    }

    public LogIssueFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get the content view
        mRootView = inflater.inflate(R.layout.fragment_log_issue, container, false);
        
        // Init the Jira REST API singleton
        mJiraRestClient = JiraRestClient.getInstance(getActivity());
        mJiraRestClient.setBaseURL(BASE_URL);

        // get the views
        mProjectSpinner = (Spinner) mRootView.findViewById(R.id.spinnerProject);
        mIssueTypeSpinner = (Spinner) mRootView.findViewById(R.id.spinnerIssueType);
        mCreateIssueBtn = (Button) mRootView.findViewById(R.id.createIssueBtn);
        mPrevBtn = (Button) mRootView.findViewById(R.id.prevScreenBtn);
        mNextBtn = (Button) mRootView.findViewById(R.id.nextScreenBtn);
        mViewFlipper = (ViewFlipper) mRootView.findViewById(R.id.screenViewFlipper);

        mPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTabManager.isCurrentTabEmpty()) {
                    Toast.makeText(getActivity(), "Please fill all required fields before go to prev tab", Toast.LENGTH_LONG).show();
                    return;
                }
                mTabManager.prevTab();

                processBtnState();
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTabManager.isCurrentTabEmpty()) {
                    Toast.makeText(getActivity(), "Please fill all required fields before go to next tab", Toast.LENGTH_LONG).show();
                    return;
                }
                mNextBtn.setEnabled(false);
                // Check if next tab is request or not
                if (mTabManager.isNextTabRequested()) {
                    // if requested, move to next tab
                    mTabManager.nextTab();
                    processBtnState();
                } else {
                    // else, request field for next tab
                    mRootView.findViewById(R.id.meta_frame).setVisibility(View.VISIBLE);
                    mViewFlipper.setVisibility(View.INVISIBLE);
                    requestFieldForTab(mTypeScreenIdMap.get(mCurrentIssueType.getIssueTypeId()), mTabManager.getNextTabId());
                }
            }
        });

        // set the project spinner's listener
        mProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentProject = (CreateMetaProject) mProjectIssueTypes.keySet().toArray()[position];
                mCurrentProjectId = mCurrentProject.getProjectId();
                mRootView.findViewById(R.id.meta_frame).setVisibility(View.VISIBLE);
                mViewFlipper.setVisibility(View.INVISIBLE);
                if (USERNAME != null && PASSWORD != null) {
                    // Execute the request to retrieve the Meta data for issue creation
                    mJiraRestClient.getCreateMeta(USERNAME, PASSWORD, new JiraListener<CreateMeta>() {
                        @Override
                        public void onResponse(CreateMeta jiraObject) {
                            isFirst = false;
                            mCreateMeta = jiraObject;

                            ArrayList<CreateMetaProject> projects = jiraObject.getProjects();
                            mCurrentIssueTypeList = projects.get(0).getIssueTypes();

                            // Exclude Configuration type from issue type list
                            Util.excludeConfigurationType(mCurrentIssueTypeList);
                            int len = mCurrentIssueTypeList.size();

                            // init the Issue Type Spinner
                            String[] issueTypeNames = new String[len];
                            for (int j = 0; j < len; j++) {
                                issueTypeNames[j] = mCurrentIssueTypeList.get(j).getIssueTypeName();
                            }
                            mIssueTypeSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_spinner_dropdown_item, issueTypeNames));
                        }

                        @Override
                        public void onErrorResponse(Exception e) {
                            e.printStackTrace();
                        }
                    }, new String[]{"" + mCurrentProjectId}, null, null, null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // set the issue type spinner's listener
        mIssueTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst) return;
                mNextBtn.setEnabled(false);
                mPrevBtn.setEnabled(false);
                mCreateIssueBtn.setEnabled(false);
                mViewFlipper.requestFocus();
                mViewFlipper.removeAllViews();

                mCurrentIssueType = mCurrentIssueTypeList.get(position);

                // get tab list for this issue type
                requestTabForScreen(mTypeScreenIdMap.get(mCurrentIssueType.getIssueTypeId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // set the submit issue button's click listener
        mCreateIssueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTabManager.isCurrentTabEmpty()) {
                    Toast.makeText(getActivity(), "Please fill all required fields before create", Toast.LENGTH_LONG).show();
                    return;
                }
                // create a new issue
                Issue issue = mTabManager.createIssue();
                try {
                    Log.d(this.getClass().getName(), "Issue to post:" + issue.getJsonObject().toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Execute the request to post the new issue
                mJiraRestClient.postIssue(USERNAME, PASSWORD, issue, new JiraListener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jiraObject) {
                        // display a success toast
                        Toast.makeText(getActivity(), "New issue with key:" + jiraObject.optString("key") + " created!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorResponse(Exception e) {
                        e.printStackTrace();
                        // display an error toast
                        Toast.makeText(getActivity(), "Error: Issue not created!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // get screen list
        requestScreenList();

        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NavigationDrawerActivity) activity).onSectionAttached(1);
    }


    /**
     * Request screen for issue type
     */
    private void requestScreenList() {

        mJiraRestClient.search(USERNAME, PASSWORD, JQL_CONFIGURATION, null, null, null, new String[]{"description", "project"}, null, new JiraListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jiraObject) {
                mTypeScreenIdMap = Util.getScreenMap(jiraObject);

                // init the Project and IssueType spinners
                initSpinners();
            }

            @Override
            public void onErrorResponse(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Request tab list for screen
     * @param screenId
     */
    private void requestTabForScreen(final String screenId) {
        mJiraRestClient.getTabsByScreenId(USERNAME, PASSWORD, screenId, mCurrentProject.getProjectName(), new JiraListener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jiraObject) {
                // Save to tab field map
                mTabMap = Util.getTabOrFieldMap(jiraObject);

                // Create new tab manager object
                mTabManager = new TabManager(getActivity(), mViewFlipper, mCurrentProject.getProjectKey(), mCurrentIssueType.getIssueTypeName());
                mTabManager.initTabs(mTabMap.keySet());

                // Request fields for first tab
                requestFieldForTab(screenId, mTabManager.getCurrentTabId());
            }

            @Override
            public void onErrorResponse(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Request fields list by screen ID and tab Id
     * @param screenId
     * @param tabId
     */
    private void requestFieldForTab(String screenId, final String tabId) {

        mJiraRestClient.getFieldsByTabId(USERNAME, PASSWORD, screenId, tabId, mCurrentProject.getProjectName(), new JiraListener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jiraArray) {
                // Save to tab field map
                mTabFieldMap = Util.getTabOrFieldMap(jiraArray);

                // Construct the view with the issue fields
                buildNewIssueForm(mTabManager.getTabById(tabId));

                // Mark this tab as requested, so we won't have to request again when using prev, next button
                mTabManager.increaseRequestedTab();

                // If it isn't first request, then flip to next page
                if (mTabManager.getRequestedTab() != 1) {
                    mTabManager.nextTab();
                }

                // Check and process Prev, Next, Create button state
                processBtnState();

                // Hide the progress bar
                mRootView.findViewById(R.id.meta_frame).setVisibility(View.INVISIBLE);

                // Show the tab
                mViewFlipper.setVisibility(View.VISIBLE);
            }

            @Override
            public void onErrorResponse(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Build and display the UI fields for a tab
     */
    private void buildNewIssueForm(final ViewGroup viewGroup) {
        // clean the layout
        viewGroup.removeAllViews();
        // Create the form with the fields
        mFieldForm = Util.createNewIssueForm(getActivity(), viewGroup, mCreateMeta, mTabFieldMap.keySet());

        // Add form to TabManager
        mTabManager.addFormTab(mFieldForm);

        // set visible the create issue button
        mRootView.findViewById(R.id.createIssueBtn).setVisibility(View.VISIBLE);
    }

    /**
     * Init the spinners for project and issue type with the metadata retrieved
     */
    public void initSpinners() {
        if (USERNAME != null && PASSWORD != null) {
            // Execute the request to retrieve the list of projects and issue types.
            mJiraRestClient.getCreateMeta(USERNAME, PASSWORD, new JiraListener<CreateMeta>() {
                @Override
                public void onResponse(CreateMeta jiraObject) {
                    // init the relevant class members with the metadata received
                    initMapProjectIssueTypes(jiraObject);
                    Object[] projects = mProjectIssueTypes.keySet().toArray();
                    String[] projectNames = new String[projects.length];
                    for (int i = 0; i < projects.length; i++) {
                        projectNames[i] = ((CreateMetaProject) projects[i]).getProjectName();
                    }
                    // set the spinner values
                    mProjectSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item, projectNames));
                    mIssueTypeSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item, mProjectIssueTypes.get(projects[0])));
                }

                @Override
                public void onErrorResponse(Exception e) {
                    e.printStackTrace();
                }
            }, null, null, null, null);
        }
    }

    /**
     * Init the map that contains combination of project and issue types
     *
     * @param jiraObject CreateMetaData representation to extract projects & issue types
     */
    private void initMapProjectIssueTypes(CreateMeta jiraObject) {
        mProjectIssueTypes = new HashMap<>();
        ArrayList<CreateMetaProject> projects = jiraObject.getProjects();
        for (int i = 0; i < projects.size(); i++) {
            ArrayList<CreateMetaIssueType> issueTypes = projects.get(i).getIssueTypes();
            int len = issueTypes.size();
            String[] issueTypeNames = new String[len];
            for (int j = 0; j < len; j++) {
                issueTypeNames[j] = issueTypes.get(j).getIssueTypeName();
            }
            mProjectIssueTypes.put(projects.get(i), issueTypeNames);
        }
        // default project
        mCurrentProject = projects.get(0);
        mCurrentIssueTypeList = projects.get(0).getIssueTypes();

        // exclude Configuration type
        Util.excludeConfigurationType(mCurrentIssueTypeList);
        // default issue type
        mCurrentIssueType = mCurrentIssueTypeList.get(0);
    }

    /**
     * Check and process Create, Prev, Next buttons
     */
    private void processBtnState() {
        if (mTabManager.hasNext()) {
            mNextBtn.setEnabled(true);
            mCreateIssueBtn.setEnabled(false);
        }
        else {
            mNextBtn.setEnabled(false);
            // Create button is only accessible in final tab
            mCreateIssueBtn.setEnabled(true);
        }
        if (mTabManager.hasPrev()) mPrevBtn.setEnabled(true);
        else mPrevBtn.setEnabled(false);
    }

}
