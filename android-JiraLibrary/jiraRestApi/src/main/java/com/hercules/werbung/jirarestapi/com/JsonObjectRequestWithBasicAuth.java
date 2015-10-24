package com.hercules.werbung.jirarestapi.com;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to execute JSON request with basic authentication
 */
public class JsonObjectRequestWithBasicAuth extends JsonObjectRequest {
    // Request headers
    private Map<String, String> headers = new HashMap<String, String>();

    public JsonObjectRequestWithBasicAuth(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String username, String password) {
        super(method, url, jsonRequest, listener, errorListener);

        if (username != null && password != null) {
            String loginEncoded = new String(Base64.encode((username + ":" + password).getBytes(), Base64.NO_WRAP));
            setHeader("Authorization", "Basic " + loginEncoded);
        }
    }

    public JsonObjectRequestWithBasicAuth(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String username, String password) {
        super(url, jsonRequest, listener, errorListener);

        if (username != null && password != null) {
            String loginEncoded = new String(Base64.encode((username + ":" + password).getBytes(), Base64.NO_WRAP));
            setHeader("Authorization", "Basic " + loginEncoded);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    public void setHeader(String title, String content) {
        headers.put(title, content);
    }

}
