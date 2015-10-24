package com.hercules.werbung.jirarestapi.json.meta;

import org.json.JSONObject;

/**
 * Class to access the JSON values of metadata of field schemas
 */
public class CreateMetaSchema extends Meta{
    public CreateMetaSchema(JSONObject o) {
        super(o);
        mJSONObject = o;
    }

    public String getCustom() {
        return mJSONObject.optString("custom");
    }

    public String getType() {
        return mJSONObject.optString("type");
    }
}
