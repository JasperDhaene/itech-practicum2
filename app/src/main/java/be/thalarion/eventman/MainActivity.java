package be.thalarion.eventman;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialAccountListener;


public class MainActivity extends MaterialNavigationDrawer implements DrawerLayout.DrawerListener {

    /**
     * neokree MaterialNavigationDrawer
     * 1. you have an init method
     * 2. you must not override onCreate method
     * 3. you must not call setContentView method, because the library have it's own layout
     * 4. you must not override onBackPressed method, because the library implement it on its own
     */

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private boolean mUserLearnedDrawer;

    private AccountManager accountManager;

    public AccountManager getAccountManager() {
        return accountManager;
    }

    @Override
    public void init(Bundle bundle) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if(mUserLearnedDrawer)
            this.disableLearningPattern();

        addSection(newSection(getResources().getString(R.string.title_overview), R.drawable.ic_action_action_schedule, new OverviewFragment()));
        addSection(newSection(getResources().getString(R.string.title_events), R.drawable.ic_action_image_nature_people, new EventsFragment()));
        addSection(newSection(getResources().getString(R.string.title_people), R.drawable.ic_action_social_people, new PeopleFragment()));

        allowArrowAnimation();
        setDefaultSectionLoaded(0);

        setDrawerListener(this);

        this.accountManager = new AccountManager(this);
    }

    @Override
    public void onDrawerOpened(View drawerView) { }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) { }

    @Override
    public void onDrawerClosed(View drawerView) {
        if(!mUserLearnedDrawer) {
            // The user manually opened the drawer; store this flag to prevent auto-showing
            // the navigation drawer automatically in the future.
            mUserLearnedDrawer = true;
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(this);
            sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) { }

    @Override
    protected void onStart() {
        super.onStart();
        // set the indicator for child fragments
        // N.B. call this method AFTER the init() to leave the time to instantiate the ActionBarDrawerToggle
        this.setHomeAsUpIndicator(R.drawable.ic_action_navigation_arrow_back);
    }

    @Override
    public void onHomeAsUpSelected() {
        // when the back arrow is selected this method is called
    }
}
