package com.hercules.werbung.jirarestapi;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hercules.werbung.jirarestapi.com.JiraListener;
import com.hercules.werbung.jirarestapi.com.JsonArrayRequestWithBasicAuth;
import com.hercules.werbung.jirarestapi.com.JsonObjectRequestWithBasicAuth;
import com.hercules.werbung.jirarestapi.dom.Issue;
import com.hercules.werbung.jirarestapi.json.User;
import com.hercules.werbung.jirarestapi.json.meta.CreateMeta;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Library class for Jira REST API
 */
public class JiraRestClient {
    // Base URL of Jira REST API
    private String mBaseURL;
    // Path to the Jira API method
    private final String MYSELF = "/rest/api/2/myself";
    private final String CREATE_META = "/rest/api/2/issue/createmeta?expand=projects.issuetypes.fields";
    private final String ISSUE = "/rest/api/2/issue";
    private final String SEARCH = "/rest/api/2/search";
    private final String TABS = "/rest/api/2/screens/%s/tabs";
    private final String FIELDS = "/rest/api/2/screens/%s/tabs/%s/fields";

    // Singleton of the library
    private static JiraRestClient mInstance;
    // Queue for the requests
    private RequestQueue mRequestQueue;
    // Application context
    private static Context mCtx;

    /**
     * Constructor
     * @param context
     */
    private JiraRestClient(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * Get the singleton instance of the library
     * @param context
     * @return the singleton instance of the library
     */
    public static synchronized JiraRestClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new JiraRestClient(context);
        }
        return mInstance;
    }

    /**
     * Get the request queue
     * @return the request queue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Add to the request queue
     * @param req a request
     * @param <T> type of request
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Get the base URL of Jira REST API
     * @return the base URL of Jira REST API
     */
    public String getBaseURL () {
        return mBaseURL;
    }

    /**
     * Set the base URL of Jira REST API
     * @param baseURL the base URL of Jira REST API
     */
    public void setBaseURL (String baseURL) {
        mBaseURL=baseURL;
    }

    /**
     * Verify if the base URL of Jira REST API is set
     * @return true if set, else false.
     */
    public boolean isSetBaseURL() {
        return (mBaseURL != null);
    }

    /**
     * Connect the user to the Jira REST API
     * @param username
     * @param password
     * @param listener listener invoked on connect success or error
     */
    public void connectUser(String username, String password, final JiraListener<User> listener) {
        // check if base URL is set
        if (!isSetBaseURL()) {
            Log.e(this.getClass().getName(), "Base URL is not set");
            return;
        }
        // Execute a request to user info using the provided credentials
        JsonObjectRequestWithBasicAuth jsObjRequest = new JsonObjectRequestWithBasicAuth (Request.Method.GET, getBaseURL()+MYSELF, null,

            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    listener.onResponse(new User(response));
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onErrorResponse(error);
                }
            }, username, password
        );

        addToRequestQueue(jsObjRequest);
    };

    /**
     * Get the metadata for new issue creation
     * @param username
     * @param password
     * @param listener listener invoked on request success or error
     * @param projectIds optional, to filter on some project ids
     * @param projectKeys optional, to filter on some project keys
     * @param issueTypeIds optional, to filter on some issue type ids
     * @param issueTypeNames optional, to filter on some issue type names
     */
    public void getCreateMeta(String username, String password, final JiraListener<CreateMeta> listener, String[] projectIds, String[] projectKeys, String[] issueTypeIds, String[] issueTypeNames) {
        if (!isSetBaseURL()) {
            Log.e(this.getClass().getName(), "Base URL is not set");
            return;
        }
        // build the GET request with parameters
        String url = getBaseURL()+CREATE_META;
        if ( projectIds != null && projectIds.length > 0) {
            url += "&projectIds=" + projectIds[0];
            for (int i=1; i<projectIds.length ; i++) {
                url += "," + projectIds[i];
            }
        }
        if ( projectKeys != null && projectKeys.length > 0) {
            url += "&projectKeys=" + projectKeys[0];
            for (int i=1; i<projectKeys.length ; i++) {
                url += "," + projectKeys[i].replaceAll(" ", "%20");
            }
        }
        if ( issueTypeIds != null && issueTypeIds.length > 0) {
            url += "&issuetypeIds=" + issueTypeIds[0];
            for (int i=1; i<issueTypeIds.length ; i++) {
                url += "," + issueTypeIds[i];
            }
        }
        if ( issueTypeNames != null && issueTypeNames.length > 0) {
            for (int i=0; i<issueTypeNames.length ; i++) {
                url += "&issuetypeNames=" + issueTypeNames[i].replaceAll(" ", "%20");
            }
        }

        // Execute the request
        JsonObjectRequestWithBasicAuth jsObjRequest = new JsonObjectRequestWithBasicAuth (Request.Method.GET, url, null,

            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(this.getClass().getName(), "CreateMeta onResponse");
                    listener.onResponse(new CreateMeta(response));
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(this.getClass().getName(), "CreateMeta onErrorResponse");
                    listener.onErrorResponse(error);
                }
            }, username, password
        );

        addToRequestQueue(jsObjRequest);
    }

    /**
     * Post a new issue
     * @param username
     * @param password
     * @param issue issue to post
     * @param listener listener invoked on request success or error
     */
    public void postIssue(String username, String password, Issue issue, final JiraListener<JSONObject> listener) {
        if (!isSetBaseURL()) {
            Log.e(this.getClass().getName(), "Base URL is not set");
            return;
        }
        // Construct the URL
        String url = getBaseURL() + ISSUE;
        JSONObject jsonRequest = issue.getJsonObject();
        // Execute the request passing a JSON object describing the issue.
        JsonObjectRequestWithBasicAuth jsObjRequest = new JsonObjectRequestWithBasicAuth(Request.Method.POST, url, jsonRequest,
        new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }, username, password);

        addToRequestQueue(jsObjRequest);
    }

    /**
     * Retrieve an issue
     *
     * @param username
     * @param password
     * @param issueIdOrKey issue to retrieve
     * @param fields       the list of fields to return for the issue
     * @param expand       the key to expand in the json
     * @param listener     listener invoked on request success or error
     */
    public void getIssue(String username,
                         String password,
                         String issueIdOrKey,
                         String[] fields,
                         String[] expand,
                         final JiraListener<JSONObject> listener) {
        if (!isSetBaseURL()) {
            Log.e(this.getClass().getName(), "Base URL is not set");
            return;
        }

        // Construct the URL
        Uri.Builder builder = Uri.parse(getBaseURL() + ISSUE + "/" + issueIdOrKey).buildUpon();

        if (fields != null && fields.length > 0) {
            builder.appendQueryParameter("fields", createCommaSeparatedString(fields));
        }

        if (expand != null && expand.length > 0) {
            builder.appendQueryParameter("expand", createCommaSeparatedString(expand));
        }

        // Execute the request passing a JSON object describing the issue.
        JsonObjectRequestWithBasicAuth jsObjRequest = new JsonObjectRequestWithBasicAuth(Request.Method.GET, builder.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }, username, password);

        addToRequestQueue(jsObjRequest);
    }

    /**
     * Search for Issue
     *
     * @param username
     * @param password
     * @param jql           a JQL query string
     * @param startAt       the index of the first issue to return (0-based)
     * @param maxResults    the maximum number of issues to return (defaults to 50).
     * @param validateQuery whether to validate the JQL query
     * @param fields        the list of fields to return for the issue
     * @param expand        A comma-separated list of the parameters to expand.
     * @param listener      listener invoked on request success or error
     */
    public void search(String username,
                       String password,
                       String jql,
                       Integer startAt,
                       Integer maxResults,
                       Boolean validateQuery,
                       String[] fields,
                       String[] expand,
                       final JiraListener<JSONObject> listener) {
        if (!isSetBaseURL()) {
            Log.e(this.getClass().getName(), "Base URL is not set");
            return;
        }
        // Construct the URL
        Uri.Builder builder = Uri.parse(getBaseURL() + SEARCH).buildUpon();

        if (jql != null) {
            builder.appendQueryParameter("jql", jql);
        }

        if (startAt != null) {
            builder.appendQueryParameter("startAt", startAt.toString());
        }

        if (maxResults != null) {
            builder.appendQueryParameter("maxResults", maxResults.toString());
        }

        if (validateQuery != null) {
            builder.appendQueryParameter("validateQuery", validateQuery.toString());
        }

        if (fields != null && fields.length > 0) {
            builder.appendQueryParameter("fields", createCommaSeparatedString(fields));
        }

        if (expand != null && expand.length > 0) {
            builder.appendQueryParameter("expand", createCommaSeparatedString(expand));
        }

        // Execute the request passing a JSON object describing the issue.
        JsonObjectRequestWithBasicAuth jsObjRequest = new JsonObjectRequestWithBasicAuth(Request.Method.GET, builder.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }, username, password);

        addToRequestQueue(jsObjRequest);
    }

    /**
     * Returns a list of all tabs for the given screen
     *
     * @param username
     * @param password
     * @param screenId      id of screen
     * @param projectKey    the key of the project; this parameter is optional
     * @param listener      listener invoked on request success or error
     */
    public void getTabsByScreenId(String username,
                         String password,
                         String screenId,
                         String projectKey,
                         final JiraListener<JSONArray> listener) {

        // check if base URL is properly set
        if (!isSetBaseURL()) {
            Log.e(this.getClass().getName(), "Base URL is not set");
            return;
        }

        // check if screen ID is missing or empty
        if (screenId == null || screenId.trim().equals("")) {
            Log.e(this.getClass().getName(), "Screen ID is not set");
            listener.onErrorResponse(new Exception("Screen ID is not set"));
            return;
        }

        // Construct the URL
        Uri.Builder builder = Uri.parse(getBaseURL() + String.format(TABS, screenId)).buildUpon();

        // Check optional project key
        if (projectKey != null && !projectKey.trim().equals("")) {
            builder.appendQueryParameter("projectKey", projectKey);
        }

        // Execute the request passing a JSON object describing the issue.
        JsonArrayRequestWithBasicAuth jsObjRequest = new JsonArrayRequestWithBasicAuth(Request.Method.GET, builder.toString(), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }, username, password);

        addToRequestQueue(jsObjRequest);
    }

    /**
     * Gets all fields for a given tab
     *
     * @param username
     * @param password
     * @param screenId      id of screen
     * @param tabId      id of screen
     * @param projectKey    the key of the project; this parameter is optional
     * @param listener      listener invoked on request success or error
     */
    public void getFieldsByTabId(String username,
                                 String password,
                                 String screenId,
                                 String tabId,
                                 String projectKey,
                                 final JiraListener<JSONArray> listener) {
        // check base URL
        if (!isSetBaseURL()) {
            Log.e(this.getClass().getName(), "Base URL is not set");
            return;
        }

        // check if screen ID is missing or empty
        if (screenId == null || screenId.trim().equals("")) {
            Log.e(this.getClass().getName(), "Screen ID is not set");
            listener.onErrorResponse(new Exception("Screen ID is not set"));
            return;
        }

        // check if tab ID is missing or empty
        if (tabId == null || tabId.trim().equals("")) {
            Log.e(this.getClass().getName(), "Tab ID is not set");
            listener.onErrorResponse(new Exception("Tab ID is not set"));
            return;
        }

        // Construct the URL
        Uri.Builder builder = Uri.parse(getBaseURL() + String.format(FIELDS, screenId, tabId)).buildUpon();

        if (projectKey != null && !projectKey.trim().equals("")) {
            builder.appendQueryParameter("projectKey", projectKey);
        }

        // Execute the request passing a JSON object describing the issue.
        JsonArrayRequestWithBasicAuth jsObjRequest = new JsonArrayRequestWithBasicAuth(Request.Method.GET, builder.toString(), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }, username, password);

        addToRequestQueue(jsObjRequest);
    }

    public static String createCommaSeparatedString(String[] list) {
        if (list == null || list.length == 0) return null;
        String ret = list[0];
        for (int i = 1; i < list.length; i++) {
            ret += "," + list[i];
        }
        return ret;
    }

}
