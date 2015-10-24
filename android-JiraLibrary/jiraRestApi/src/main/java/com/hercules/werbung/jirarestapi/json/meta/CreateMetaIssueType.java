package com.hercules.werbung.jirarestapi.json.meta;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class to access the JSON values of metadata of issue types
 */
public class CreateMetaIssueType extends Meta{
    public CreateMetaIssueType(JSONObject o) {
        super(o);
    }

    public JSONObject getJSONObject () {
        return mJSONObject;
    }

    public ArrayList<CreateMetaField> getFields() {
        ArrayList<CreateMetaField> res = new ArrayList<CreateMetaField>();
        if (mJSONObject != null) {
            JSONObject fields = mJSONObject.optJSONObject("fields");
            if (fields != null) {
                for ( Iterator<String> it = fields.keys(); it.hasNext(); ) {
                    String key=it.next();
                    res.add(new CreateMetaField(key, fields.optJSONObject(key)));
                }
            }
        }
        return res;
    }

    public String getIssueTypeId()  {
        return mJSONObject.optString("id");
    }

    public String getIssueTypeName() {
        return mJSONObject.optString("name");
    }
}
