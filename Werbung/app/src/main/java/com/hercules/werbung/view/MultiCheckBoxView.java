package com.hercules.werbung.view;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hercules.werbung.R;
import com.hercules.werbung.jirarestapi.dom.Issue;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaAllowedValues;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaField;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class describes the representation of a multi check box input.
 */
public class MultiCheckBoxView extends NewIssueFieldView {
    public MultiCheckBoxView(Context context, CreateMetaField field, int viewId) {
        super(context, field, viewId);
    }

    @Override
    protected View setView(Context context, CreateMetaField field, int viewId) {
        LinearLayout layout = (LinearLayout) View.inflate(context, viewId, null);
        TextView textView = (TextView) layout.findViewById(R.id.textView);
        // If field is required, add * next to field name
        if (field.getRequired()) {
            textView.setText(field.getName() + " *");
        }
        else {
            textView.setText(field.getName());
        }
        ArrayList<CreateMetaAllowedValues> values = field.getAllowedValues();
        for (Iterator<CreateMetaAllowedValues> it = values.iterator(); it.hasNext(); ) {
            CreateMetaAllowedValues value = it.next();
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(value.getValue());
            layout.addView(checkBox);
        }
        return layout;
    }

    @Override
    public void addJsonField(Issue.Builder builder) {
        //ArrayList<String> values = new ArrayList<>();
        LinearLayout layout = (LinearLayout) getView();
        JSONArray checkBox = new JSONArray();

        try {
            for (int i = 0; i < layout.getChildCount(); i++) {
                View view = layout.getChildAt(i);
                if (view instanceof CheckBox) {
                    if (((CheckBox) view).isChecked()) {
                        JSONObject checkBoxValue = new JSONObject();
                        checkBoxValue.put("value", ((CheckBox) view).getText().toString());
                        checkBox.put(checkBoxValue);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setField(getCreateMetaField().getKey(), checkBox);
    }
}
