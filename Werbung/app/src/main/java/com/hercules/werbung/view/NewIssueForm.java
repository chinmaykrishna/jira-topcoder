package com.hercules.werbung.view;

import android.view.ViewGroup;

import com.hercules.werbung.jirarestapi.dom.Issue;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class represents a new issue form
 */
public class NewIssueForm {
    // class members
    private ArrayList<NewIssueFieldView> mViewList;
    private ViewGroup mViewGroup;

    /**
     * Constructor
     *
     * @param viewGroup to build the form
     */
    public NewIssueForm(ViewGroup viewGroup) {
        mViewList = new ArrayList<>();
        mViewGroup = viewGroup;
    }

    /**
     * Add a field view to the form
     *
     * @param view to ass to the form
     */
    public void addView(NewIssueFieldView view) {
        mViewList.add(view);
    }

    /**
     * Inflate the wiews in the view group
     *
     * @return the view group
     */
    public ViewGroup fillViewGroup() {
        if (mViewList != null) {
            for (Iterator<NewIssueFieldView> it = mViewList.iterator(); it.hasNext(); ) {
                mViewGroup.addView(it.next().getView());
            }
        }
        return mViewGroup;
    }

    /**
     * Create the issue to post based on the form fields values
     *
     * @return the issue
     */
    public Issue createIssue() {
        Issue.Builder builder = new Issue.Builder();
        if (mViewList != null) {
            for (Iterator<NewIssueFieldView> it = mViewList.iterator(); it.hasNext(); ) {
                it.next().addJsonField(builder);
            }
        }
        return builder.build();
    }

    /**
     * Check if there is any empty required field in this tab
     * @return the empty status of this tab
     */
    public boolean checkEmptyRequiredView() {
        boolean result = false;
        if (mViewList != null) {
            for (Iterator<NewIssueFieldView> it = mViewList.iterator(); it.hasNext(); ) {
                NewIssueFieldView nextView = it.next();
                if (nextView.isRequired() && nextView.isEmpty()) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Add fields to issue builder
     */
    public void createIssue(Issue.Builder builder) {
        if (mViewList != null) {
            for (Iterator<NewIssueFieldView> it = mViewList.iterator(); it.hasNext(); ) {
                it.next().addJsonField(builder);
            }
        }
    }
}
