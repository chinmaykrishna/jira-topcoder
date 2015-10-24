package com.hercules.werbung;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Class containing the drawer implementation
 */
public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String LOG_ISSUE_FRAGMENT = "LogIssueFragment";
    private static final String HISTORY_FRAGMENT = "HistoryFragment";
    private static final String MY_PROFILE_FRAGMENT = "MyProfileFragment";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == 0) {
            Fragment fragment = fragmentManager.findFragmentByTag(LOG_ISSUE_FRAGMENT);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (fragment == null) {
                fragment = LogIssueFragment.newInstance();
            }
            ft.replace(R.id.container, fragment, LOG_ISSUE_FRAGMENT);
            ft.commit();
        } else if (position == 1) {
            Fragment fragment = fragmentManager.findFragmentByTag(HISTORY_FRAGMENT);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (fragment == null) {
                fragment = HistoryFragment.newInstance();
            }
            ft.replace(R.id.container, fragment, HISTORY_FRAGMENT);
            ft.commit();
        } else if (position == 2) {
            Fragment fragment = fragmentManager.findFragmentByTag(MY_PROFILE_FRAGMENT);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (fragment == null) {
                fragment = MyProfileFragment.newInstance();
            }
            ft.replace(R.id.container, fragment, MY_PROFILE_FRAGMENT);
            ft.commit();
        }
    }

    //Get the title of current fragment
    public void onSectionAttached(int number) {
        System.out.println(mTitle);
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    //Change the title of page to current fragments title
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }


}
