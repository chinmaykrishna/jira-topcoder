package com.hercules.werbung.view;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hercules.werbung.R;
import com.hercules.werbung.jirarestapi.dom.Issue;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaAllowedValues;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaField;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class describes the representation of a radio button field.
 */
public class RadioButtonView extends NewIssueFieldView {
    public RadioButtonView(Context context, CreateMetaField field, int viewId) {
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
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        ArrayList<CreateMetaAllowedValues> values = field.getAllowedValues();
        for (Iterator<CreateMetaAllowedValues> it = values.iterator(); it.hasNext(); ) {
            CreateMetaAllowedValues value = it.next();
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(value.getValue() + ":");
            radioButton.setTag(value.getId());
            radioGroup.addView(radioButton);
        }
        return view;
    }

    @Override
    public void addJsonField(Issue.Builder builder) {
        int checkedRadioButtonId = ((RadioGroup) getView().findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        String value = ((RadioButton) getView().findViewById(checkedRadioButtonId)).getTag().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setField(getCreateMetaField().getKey(), jsonObject);
    }
}
