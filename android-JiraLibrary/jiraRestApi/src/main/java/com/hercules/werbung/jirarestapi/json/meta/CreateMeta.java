package com.hercules.werbung.jirarestapi.json.meta;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Abstract class of metadata
 */
public class CreateMeta extends Meta{
    public CreateMeta(JSONObject o) {
        super(o);
    }

    public ArrayList<CreateMetaProject> getProjects() {
        ArrayList<CreateMetaProject> res = new ArrayList<CreateMetaProject>();
        if (mJSONObject != null) {
            JSONArray projects = mJSONObject.optJSONArray("projects");
            for (int i=0; i<projects.length(); i++) {
                res.add(new CreateMetaProject(projects.optJSONObject(i)));
            }
        }
        return res;
    }
}
