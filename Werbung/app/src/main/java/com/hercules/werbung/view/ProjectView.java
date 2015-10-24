package com.hercules.werbung.view;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hercules.werbung.R;
import com.hercules.werbung.jirarestapi.dom.Issue;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaField;
import com.hercules.werbung.jirarestapi.json.meta.Meta;

/**
 * This class describes the representation of a project input.
 */
public class ProjectView extends NewIssueFieldView {
    public ProjectView(Context context, CreateMetaField field, int viewId) {
        super(context, field, viewId);
    }

    @Override
    protected View setView(Context context, CreateMetaField field, int viewId) {
        View view = View.inflate(context, viewId, null);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        // If field is required, add * next to field name
        if (field.getRequired()) {
            textView.setText(field.getName() + " *");
        }
        else {
            textView.setText(field.getName());
        }
        spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
                Meta.getValues(field.getAllowedValues(), "key")));
        return view;
    }

    @Override
    public void addJsonField(Issue.Builder builder) {
        Spinner spinner = ((Spinner) getView().findViewById(R.id.spinner));
        builder.setProject(spinner.getSelectedItem().toString());
    }
}
