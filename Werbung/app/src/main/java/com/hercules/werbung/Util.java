package com.hercules.werbung;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.hercules.werbung.jirarestapi.json.meta.CreateMeta;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaField;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaIssueType;
import com.hercules.werbung.view.CascadingSelectView;
import com.hercules.werbung.view.FreeTextView;
import com.hercules.werbung.view.IssueTypeView;
import com.hercules.werbung.view.NewIssueForm;
import com.hercules.werbung.view.NewIssueFieldView;
import com.hercules.werbung.view.MultiCheckBoxView;
import com.hercules.werbung.view.ProjectView;
import com.hercules.werbung.view.RadioButtonView;
import com.hercules.werbung.view.SingleSelectView;
import com.hercules.werbung.view.UserView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to create the fields and form to fill a new issue
 */
public final class Util {
    private Util() {
    }

    /**
     * Create a form to fill a new issue
     *
     * @param context
     * @param parentViewGroup the parent view group to create the form into
     * @param jiraObject      the jiraObject that describes the form fields
     * @return the form
     */
    public static NewIssueForm createNewIssueForm(Context context, ViewGroup parentViewGroup, CreateMeta jiraObject, Set<String> filter) {
        ArrayList<CreateMetaField> metaFields = jiraObject.getProjects().get(0).getIssueTypes().get(0).getFields();
        NewIssueForm newIssueForm = new NewIssueForm(parentViewGroup);

        if (metaFields.size() > 0) {
            for (Iterator<CreateMetaField> it = metaFields.iterator(); it.hasNext(); ) {
                CreateMetaField createMetaField = it.next();
                for (Iterator<String> fieldIterator = filter.iterator(); fieldIterator.hasNext(); ) {
                    if (createMetaField.getKey().equals(fieldIterator.next()) && !createMetaField.getKey().equals("issuetype")) {
                        // add the field views
                        NewIssueFieldView view = createJiraView(context, createMetaField);
                        Log.d("Util", "View:" + createMetaField.getName());
                        if (view != null) {
                            newIssueForm.addView(view);
                            Log.d("Util", "Add view:" + createMetaField.getName());
                        }

                        break;
                    }
                }
            }
        }

        newIssueForm.fillViewGroup();

        return newIssueForm;
    }

    /**
     * Create a field view based on the json representation of a field
     *
     * @param context
     * @param field   the json representation of a field
     * @return the field view
     */
    public static NewIssueFieldView createJiraView(Context context, CreateMetaField field) {
        String stdFieldType = field.getSchema().getType();
        String customFieldType = field.getSchema().getCustom();
        if ( customFieldType != null && customFieldType != "") {
            switch (customFieldType) {
                case "com.atlassian.jira.plugin.system.customfieldtypes:textfield":
                    return new FreeTextView(context, field, R.layout.free_text_view);
                case "com.atlassian.jira.plugin.system.customfieldtypes:multicheckboxes":
                    return new MultiCheckBoxView(context, field, R.layout.vertical_field_layout);
                case "com.atlassian.jira.plugin.system.customfieldtypes:cascadingselect":
                    return new CascadingSelectView(context, field, R.layout.vertical_field_layout);
                case "com.atlassian.jira.plugin.system.customfieldtypes:select":
                    return new SingleSelectView(context, field, R.layout.select_view);
                case "com.atlassian.jira.plugin.system.customfieldtypes:radiobuttons":
                    return new RadioButtonView(context, field, R.layout.radiogroup);
                case "com.atlassian.jira.plugin.system.customfieldtypes:textarea":
                    return new FreeTextView(context, field, R.layout.free_text_view);
                default:
                    Log.e("Util", "Custom type:" + customFieldType+" is unknown");
            }
        }else if (stdFieldType != null && stdFieldType != "") {
            switch (stdFieldType) {
                case "string":
                    return new FreeTextView(context, field, R.layout.free_text_view);
                case "issuetype":
                    return new IssueTypeView(context, field, R.layout.select_view);
                case "project":
                    return new ProjectView(context, field, R.layout.select_view);
                case "user":
                    return new UserView(context, field, R.layout.free_text_view);
                default:
                    Log.e("Util", "Standard type:" + stdFieldType+" is unknown");
            }
        }
        return null;
    }

    /**
     * Create issue type to screen map object from JSON data
     * @param jsonObject json object contains configuration data
     * @return issue type to screen map object
     */
    public static Map<String, String> getScreenMap(JSONObject jsonObject) {
        Map<String, String> result = new HashMap<>();
        try {
            if (jsonObject.has("issues")) {
                JSONArray issuesList = jsonObject.getJSONArray("issues");
                for(int i = 0; i < issuesList.length(); i++){
                    String description = issuesList.getJSONObject(i).getJSONObject("fields").getString("description");
                    JSONObject descriptionObject = new JSONObject(description);
                    if (descriptionObject.has("configuration")) {
                        JSONObject configurationObject = descriptionObject.getJSONObject("configuration");
                        String screenId = configurationObject.getString("screenId");
                        String issueType = configurationObject.getString("issueType");
                        result.put(issueType, screenId);
                    }
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Create map object from JSON data contains tab list or field list
     * @param jsonArray JSON array contains tab list or field list
     * @return map object
     */
    public static Map<String, String> getTabOrFieldMap(JSONArray jsonArray) {
        Map<String, String> result = new HashMap<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                String tabId = jsonArray.getJSONObject(i).getString("id");
                String tabName = jsonArray.getJSONObject(i).getString("name");
                result.put(tabId, tabName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Exclude Configuration type from issue type list
     * @param typeList the issue type list
     */
    public static void excludeConfigurationType(ArrayList<CreateMetaIssueType> typeList) {
        for (CreateMetaIssueType type:typeList) {
            if (type.getIssueTypeName().equals("Configuration")) typeList.remove(type);
        }
    }
}
