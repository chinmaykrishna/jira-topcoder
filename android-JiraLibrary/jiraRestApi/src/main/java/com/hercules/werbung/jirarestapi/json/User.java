package com.hercules.werbung.jirarestapi.json;

import android.net.Uri;

import org.json.JSONObject;

/**
 * Class to access values of the JSON representation of Jira user
 */
public class User{
    private JSONObject mJSONObject;

    public User(JSONObject o) {
        mJSONObject=o;
    }

    public JSONObject getJSONObject () {
        return mJSONObject;
    }

    public Uri getAvatarURI(String size) {
        Uri res=null;
        if (mJSONObject != null) {
            JSONObject avatarsJSON = mJSONObject.optJSONObject("avatarUrls");
            if (avatarsJSON != null) {
                res = Uri.parse(avatarsJSON.optString(size,""));
            }
        }
        return res;
    }

    public String getDisplayName() {
        return mJSONObject.optString("displayName","");
    }
}

