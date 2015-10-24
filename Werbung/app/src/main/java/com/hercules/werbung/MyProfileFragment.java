package com.hercules.werbung;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Fragment that displays the user profile
 */
public class MyProfileFragment extends Fragment {
    private String mUsername;
    private String mUserEmail;
    private Bitmap mAvatar;

    private View mDrawerHeader;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static MyProfileFragment newInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        return fragment;
    }

    public MyProfileFragment() {
    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p/>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the user data from the preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUsername = sp.getString(LoginActivity.PREF_USER_NAME, "");
        mUserEmail = sp.getString(LoginActivity.PREF_EMAIL, "");

        String encoded_image = sp.getString(LoginActivity.PREF_ENCODED_AVATAR, "");
        if (!encoded_image.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(encoded_image, Base64.DEFAULT);
            mAvatar = BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        setHasOptionsMenu(true);
    }

    /**
     * Method called to create the fragment view
     *
     * @param inflater           to inflate the fragment view
     * @param container          the fragment view container
     * @param savedInstanceState
     * @return the fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_my_profile, container,
                false);

        // Display and fill the header with the user data if the user has an avatar; else hide the header
        mDrawerHeader = layout.findViewById(R.id.nav_header);
        if (mAvatar == null) {
            mDrawerHeader.setVisibility(View.GONE);
        } else {
            mDrawerHeader.setVisibility(View.VISIBLE);
            ((ImageView) mDrawerHeader.findViewById(R.id.avatar)).setImageBitmap(mAvatar);
            ((TextView) mDrawerHeader.findViewById(R.id.username)).setText(mUsername);
            ((TextView) mDrawerHeader.findViewById(R.id.email)).setText(mUserEmail);
        }

        return layout;
    }

    /**
     * method called when fragment is attached to the activity
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NavigationDrawerActivity) activity).onSectionAttached(3);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
     * {@link Activity#onCreateOptionsMenu(Menu) Activity.onCreateOptionsMenu}
     * for more information.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_profile, menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}