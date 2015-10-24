package com.hercules.werbung.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.hercules.werbung.jirarestapi.dom.Issue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tab manager class manages tab for particular issue type
 * @author TCSASSEMBLER
 * @version 1.0
 *
 */
public class TabManager {
    private ViewFlipper viewFlipper;
    private Context mContext;
    private Map<String, ViewGroup> mTabViewMap;
    private Map<String, NewIssueForm> mTabFormMap;
    private List<String> mTabIdList;
    private int currentTab = 0;
    private int requestedTab = 0;
    private String mProjectName;
    private String mIssueType;

    /**
     * Constructor for TabManager
     * @param context the context
     * @param viewFlipper the viewflipper that holds all tabs
     * @param projectName the project name
     * @param issueType the issue type name
     */
    public TabManager(Context context, ViewFlipper viewFlipper, String projectName, String issueType) {
        this.mContext = context;
        this.viewFlipper = viewFlipper;
        mProjectName = projectName;
        mIssueType = issueType;
        mTabViewMap = new HashMap<>();
        mTabFormMap = new HashMap<>();
        mTabIdList = new ArrayList<>();
    }

    /**
     * Initialize tabs list
     * @param tabSet set of tab ID
     */
    public void initTabs(Set<String> tabSet) {
        for (String TabId: tabSet) {
            LinearLayout layout = new LinearLayout(mContext);
            layout.setOrientation(LinearLayout.VERTICAL);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            viewFlipper.addView(layout, layoutParams);
            mTabViewMap.put(TabId, layout);
            mTabIdList.add(TabId);
        }
    }

    /**
     * check if current tab has any empty required fields
     * @return true if there is any empty required fields
     */
    public boolean isCurrentTabEmpty() {
        return mTabFormMap.get(mTabIdList.get(currentTab)).checkEmptyRequiredView();
    }

    /**
     * Get current tab ID
     * @return current tab ID
     */
    public String getCurrentTabId() {
        return mTabIdList.get(currentTab);
    }

    /**
     * Get tab by its id
     * @param id the tab ID
     * @return ViewGroup that holds the tab
     */
    public ViewGroup getTabById(String id) {
        return mTabViewMap.get(id);
    }

    /**
     * Get ID of next tab
     * @return tab ID if there is next tab, otherwise null
     */
    public String getNextTabId() {
        if (currentTab >= mTabIdList.size() - 1) return null;
        else {
            return mTabIdList.get(currentTab +1);
        }
    }

    /**
     * Flip to previous tab
     */
    public void prevTab() {
        if (currentTab > 0) {
            viewFlipper.showPrevious();
            currentTab--;
        }
    }

    /**
     * Flip to next tab
     */
    public void nextTab() {
        if (currentTab < mTabIdList.size() - 1) {
            viewFlipper.showNext();
            currentTab++;
        }
    }

    /**
     * Check if there is next tab
     * @return true if there is next tab, false if not
     */
    public boolean hasNext() {
        return currentTab + 1 < mTabIdList.size();
    }

    /**
     * Check if there is previous tab
     * @return true if there is previous tab, false if not
     */
    public boolean hasPrev() {
        return currentTab > 0;
    }

    /**
     * Check if next tab is rendered or not
     * @return true if next tab is rendered, false if not
     */
    public boolean isNextTabRequested() {
        return (currentTab + 1) < requestedTab;
    }

    /**
     * Get the number of requested/rendered tabs
     * @return the number of requested/rendered tabs
     */
    public int getRequestedTab() {
        return requestedTab;
    }

    /**
     * Increase the number of requested/rendered tabs by 1
     */
    public void increaseRequestedTab() {
        requestedTab++;
    }

    /**
     * Add new issue form
     * @param form new issue form
     */
    public void addFormTab(NewIssueForm form) {
        mTabFormMap.put(mTabIdList.get(requestedTab), form);
    }

    /**
     * Create the issue to post based on the form fields values
     *
     * @return the issue
     */
    public Issue createIssue() {
        Issue.Builder builder = new Issue.Builder();
        builder.setProject(mProjectName);
        builder.setIssueType(mIssueType);
        if (mTabIdList != null) {
            for (Iterator<String> it = mTabIdList.iterator(); it.hasNext(); ) {
                mTabFormMap.get(it.next()).createIssue(builder);
            }
        }
        return builder.build();
    }
}
