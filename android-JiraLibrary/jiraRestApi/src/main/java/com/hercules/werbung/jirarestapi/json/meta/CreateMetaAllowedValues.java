package com.hercules.werbung.jirarestapi.json.meta;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class to access the JSON values of metadata of allowed values
 */
public class CreateMetaAllowedValues extends Meta {
    public CreateMetaAllowedValues(JSONObject o) {
        super(o);
        mJSONObject = o;
    }

    public int getId() {
        return mJSONObject.optInt("id");
    }

    public String getValue() {
        return mJSONObject.optString("value");
    }

    public ArrayList<Children> getChildrens() {
        ArrayList<Children> res = new ArrayList<>();
        if (mJSONObject != null) {
            JSONArray values = mJSONObject.optJSONArray("children");
            if (values != null) {
                for (int i = 0; i < values.length(); i++) {
                    res.add(new Children(values.optJSONObject(i)));
                }
            }
        }
        return res;
    }

    public class Children extends Meta {
        public Children(JSONObject o) {
            super(o);
            mJSONObject = o;
        }

        public int getId() {
            return mJSONObject.optInt("id");
        }

        public String getValue() {
            return mJSONObject.optString("value");
        }
    }
}
