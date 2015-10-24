package com.hercules.werbung.view;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.hercules.werbung.R;
import com.hercules.werbung.jirarestapi.dom.Issue;
import com.hercules.werbung.jirarestapi.json.meta.CreateMeta;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaAllowedValues;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaField;
import com.hercules.werbung.jirarestapi.json.meta.Meta;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class describes the representation of a cascading select input.
 */
public class CascadingSelectView extends NewIssueFieldView {
    public CascadingSelectView(Context context, CreateMetaField field, int viewId) {
        super(context, field, viewId);
    }

    @Override
    protected View setView(final Context context, CreateMetaField field, int viewId) {
        LinearLayout layout = (LinearLayout) View.inflate(context, viewId, null);
        TextView textView = (TextView) layout.findViewById(R.id.textView);
        // If field is required, add * beside field name
        if (field.getRequired()) {
            textView.setText(field.getName() + " *");
        }
        else {
            textView.setText(field.getName());
        }

        Spinner parentSpinner = new Spinner(context);
        parentSpinner.setTag("parentSpinner");
        final Spinner childSpinner = new Spinner(context);
        childSpinner.setTag("childSpinner");

        ArrayList<String> parentValues = new ArrayList<>();
        final ArrayList<String[]> childrenValues = new ArrayList<>();

        ArrayList<CreateMetaAllowedValues> allowedValues = field.getAllowedValues();
        for (Iterator<CreateMetaAllowedValues> it = allowedValues.iterator(); it.hasNext(); ) {
            CreateMetaAllowedValues value = it.next();
            parentValues.add(value.getValue());
            childrenValues.add(CreateMeta.getValues(value.getChildrens(), "value"));
        }

        String[] parentSpinnerValues = new String[parentValues.size()];
        parentSpinnerValues = parentValues.toArray(parentSpinnerValues);
        parentSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
                parentSpinnerValues));
        parentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] childSpinnerValues = childrenValues.get(position);
                childSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
                        childSpinnerValues));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        layout.addView(parentSpinner);
        layout.addView(childSpinner);

        return layout;
    }

    @Override
    public void addJsonField(Issue.Builder builder) {
        JSONObject cascadeSelectJson = new JSONObject();
        try {
            // Child field
            JSONObject cascadeSelectChildJson = new JSONObject();
            cascadeSelectChildJson.put("value", ((Spinner) getView().findViewWithTag("childSpinner")).getSelectedItem());
            // Parent field
            cascadeSelectJson.put("value", ((Spinner) getView().findViewWithTag("parentSpinner")).getSelectedItem());
            cascadeSelectJson.put("child", cascadeSelectChildJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setField(getCreateMetaField().getKey(), cascadeSelectJson);
    }
}
