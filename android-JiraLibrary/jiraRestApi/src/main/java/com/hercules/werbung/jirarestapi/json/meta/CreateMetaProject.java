package com.hercules.werbung.jirarestapi.json.meta;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class to access the JSON values of metadata of projects
 */
public class CreateMetaProject extends Meta{
    public CreateMetaProject(JSONObject o) {
        super(o);
        mJSONObject=o;
    }

    public ArrayList<CreateMetaIssueType> getIssueTypes() {
        ArrayList<CreateMetaIssueType> res = new ArrayList<CreateMetaIssueType>();
        if (mJSONObject != null) {
            JSONArray issueTypes = mJSONObject.optJSONArray("issuetypes");
            for (int i=0; i<issueTypes.length(); i++) {
                res.add(new CreateMetaIssueType(issueTypes.optJSONObject(i)));
            }
        }
        return res;
    }

    public String getProjectName() {
        return mJSONObject.optString("name");
    }

    public int getProjectId() {
        return mJSONObject.optInt("id");
    }

    public String getProjectKey() {
        return mJSONObject.optString("key");
    }
}
