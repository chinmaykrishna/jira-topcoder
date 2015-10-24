package com.hercules.werbung.view;

import android.content.Context;
import android.view.View;

import com.hercules.werbung.jirarestapi.dom.Issue;
import com.hercules.werbung.jirarestapi.json.meta.CreateMetaField;

/**
 * This class describes the abstract representation of a field input.
 */
public abstract class NewIssueFieldView {
    // class members
    private CreateMetaField mField;
    private View mView = null;
    private int mViewId;

    /**
     * Constructor
     *
     * @param context current context
     * @param field   the Json representation of a field
     * @param viewId  the id of the layout to construct the field view
     */
    public NewIssueFieldView(Context context, CreateMetaField field, int viewId) {
        mField = field;
        mViewId = viewId;
        mView = setView(context, field, viewId);
    }

    /**
     * Get the Json representation of the field
     *
     * @return the Json representation of the field
     */
    public CreateMetaField getCreateMetaField() {
        return mField;
    }

    /**
     * Get the Json representation of the field
     *
     * @return the Json representation of the field
     */
    public View getView() {
        return mView;
    }

    /**
     * Get the id of the layout used to construct the field view
     *
     * @return the id of the layout used to construct the field view
     */
    public int getViewId() {
        return mViewId;
    }

    /**
     * Method to override to construct the field view
     *
     * @param context the current context
     * @param field   the Json representation of a field
     * @param viewId  the id of the layout to construct the field view
     * @return the view
     */
    protected abstract View setView(Context context, CreateMetaField field, int viewId);

    /**
     * Method to override to construct the issue Json object
     *
     * @param builder an issue builder
     */
    public abstract void addJsonField(Issue.Builder builder);

    /**
     * Method to check if input is blank
     *
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * Method to check if input is required
     *
     */
    public boolean isRequired() {
        return mField.getRequired();
    }
}
