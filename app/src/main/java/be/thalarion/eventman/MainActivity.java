package be.thalarion.eventman;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialAccountListener;


public class MainActivity extends MaterialNavigationDrawer implements DrawerLayout.DrawerListener,
                                                                      MaterialAccountListener {

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

        // ImageLoader configuration
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.denyCacheImageMultipleSizesInMemory();
        config.memoryCache(new LruMemoryCache(2 * 1024 * 1024)); // 2 MiB memory cache
        ImageLoader.getInstance().init(config.build());

        allowArrowAnimation();
        setDefaultSectionLoaded(0);

        MaterialAccount account = new MaterialAccount(getResources(),
                getString(R.string.name_admin),
                getString(R.string.email_admin),
                R.drawable.ic_action_social_people,
                R.drawable.material
        );
        addAccount(account);

        setAccountListener(this);
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

    @Override
    public void onAccountOpening(MaterialAccount materialAccount) {
        if(accountManager.getPerson() != null)
            setFragmentChild(ShowPersonFragment.newInstance(accountManager.getPerson().getResource().toString()),
                    getResources().getString(R.string.title_show_person)
            );
    }

    @Override
    public void onChangeAccount(MaterialAccount materialAccount) { }
}
