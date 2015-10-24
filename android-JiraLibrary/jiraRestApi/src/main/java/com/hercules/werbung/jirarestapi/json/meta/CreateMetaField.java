package com.hercules.werbung.jirarestapi.json.meta;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class to access the JSON values of metadata of fields
 */
public class CreateMetaField extends Meta{
    private String mKey;

    public CreateMetaField(String key, JSONObject o) {
        super(o);
        mKey=key;
    }

    public ArrayList<CreateMetaAllowedValues> getAllowedValues() {
        ArrayList<CreateMetaAllowedValues> res = new ArrayList<CreateMetaAllowedValues>();
        if (mJSONObject != null) {
            JSONArray values = mJSONObject.optJSONArray("allowedValues");
            if (values != null) {
                for (int i = 0; i < values.length(); i++) {
                    res.add(new CreateMetaAllowedValues(values.optJSONObject(i)));
                }
            }
        }
        return res;
    }

    public String getKey() {
        return mKey;
    }

    public boolean getRequired() {
        return mJSONObject.optBoolean("required",false);
    }

    public CreateMetaSchema getSchema() {
        return new CreateMetaSchema(mJSONObject.optJSONObject("schema"));
    }

    public String getName() {
        return mJSONObject.optString("name");
    }
}
