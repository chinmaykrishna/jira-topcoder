package com.hercules.werbung.jirarestapi.json.meta;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Guillaume on 12/08/2015.
 */
public abstract class Meta {
    protected JSONObject mJSONObject;

    public Meta(JSONObject o) {
        mJSONObject = o;
    }

    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    public static String[] getValues(ArrayList<? extends Meta> metaArray, String key) {
        String[] res = new String[metaArray.size()];
        int i= 0;
        for (Iterator<? extends Meta> it = metaArray.iterator(); it.hasNext() && i<res.length ; i++) {
            Meta m = it.next();
            res[i]=m.getJSONObject().optString(key);
        }
        return res;
    }
}
