package com.hercules.werbung.view;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hercules.werbung.R;
import com.hercules.werbung.jirarestapi.dom.Issue;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaField;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class describes the representation of a user input.
 */
public class UserView extends NewIssueFieldView {
    public UserView(Context context, CreateMetaField field, int viewId) {
        super(context, field, viewId);
    }

    @Override
    protected View setView(Context context, CreateMetaField field, int viewId) {
        View view = View.inflate(context, viewId, null);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        // If field is required, add * next to field name
        if (field.getRequired()) {
            textView.setText(field.getName() + " *");
        }
        else {
            textView.setText(field.getName());
        }
        return view;
    }

    @Override
    public void addJsonField(Issue.Builder builder) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", ((EditText) getView().findViewById(R.id.editText)).getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setField(getCreateMetaField().getKey(), jsonObject);
    }

    /**
     * Check if field's value is empty or not
     * @return true if this is empty field
     */
    @Override
    public boolean isEmpty() {
        String value = ((EditText) getView().findViewById(R.id.editText)).getText().toString();
        return value.trim().equalsIgnoreCase("");
    }
}
