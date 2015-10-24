package com.hercules.werbung.jirarestapi.dom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to get the JSON representation of a Jira issue
 */
public class Issue {
    private JSONObject mJsonObject;

    private Issue() {
        mJsonObject=new JSONObject();
        try {
            mJsonObject.put("fields", new JSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Issue(JSONObject o) {
        mJsonObject = o;
    }

    /**
     * Get the Json representation of the issue
     *
     * @return Json object
     */
    public JSONObject getJsonObject() {
        return mJsonObject;
    }

    /**
     * get the Json representation of the issue fields
     *
     * @return
     */
    public JSONObject getJsonObjectFields() {
        return mJsonObject.optJSONObject("fields");
    }

    /**
     * get the key of the issue
     *
     * @return
     */
    public String getKey() {
        return mJsonObject.optString("key");
    }

    /**
     * get the summary of the issue
     *
     * @return
     */
    public String getSummary() {
        return getJsonObjectFields().optString("summary");
    }

    /**
     * Class to construct an issue
     */
    public static class Builder {
        private Issue mIssue;

        public Builder() {
            mIssue = new Issue();
        }

        /**
         * set the project key
         *
         * @param key
         * @return
         */
        public Builder setProject(String key) {
            try {
                JSONObject project = new JSONObject();
                project.put("key", key);
                mIssue.getJsonObjectFields().put("project", project);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        /**
         * set the issue type
         *
         * @param name
         * @return
         */
        public Builder setIssueType(String name) {
            try {
                JSONObject issueType = new JSONObject();
                issueType.put("name", name);
                mIssue.getJsonObjectFields().put("issuetype", issueType);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        /**
         * set a field represented by a string value
         *
         * @param key   Json key
         * @param value string value
         * @return
         */
        public Builder setField(String key, String value) {
            try {
                mIssue.getJsonObjectFields().put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        /**
         * set a field represented by a Json array
         *
         * @param key       Json key
         * @param jsonArray Json array
         * @return
         */
        public Builder setField(String key, JSONArray jsonArray) {
            try {
                mIssue.getJsonObjectFields().put(key, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return this;
        }

        /**
         * set a field represented by a Json object
         *
         * @param key        Json key
         * @param jsonObject Json object
         * @return
         */
        public Builder setField(String key, JSONObject jsonObject) {
            try {
                mIssue.getJsonObjectFields().put(key, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return this;
        }

        /**
         * build the issue
         *
         * @return the issue
         */
        public Issue build() {
            return mIssue;
        }

    }
}
