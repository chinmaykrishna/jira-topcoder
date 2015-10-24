package com.hercules.werbung.view;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hercules.werbung.R;
import com.hercules.werbung.jirarestapi.dom.Issue;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaField;

import java.util.zip.Inflater;

/**
 * This class describes the representation of a free text input.
 */
public class FreeTextView extends NewIssueFieldView {
    private boolean isRequired = false;
    public FreeTextView(Context context, CreateMetaField field, int viewId) {
        super(context, field, viewId);
    }

    @Override
    protected View setView(Context context, CreateMetaField field, int viewId) {
        View view = View.inflate(context, viewId, null);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        // If field is required, add * beside field name
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
        String value = ((EditText) getView().findViewById(R.id.editText)).getText().toString();
        builder.setField(getCreateMetaField().getKey(), value);
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
