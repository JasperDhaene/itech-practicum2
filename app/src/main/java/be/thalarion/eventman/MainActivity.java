package be.thalarion.eventman;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialAccountListener;


public class MainActivity extends MaterialNavigationDrawer implements MaterialAccountListener {

    /**
     * neokree MaterialNavigationDrawer
     * 1. you have an init method
     * 2. you must not override onCreate method
     * 3. you must not call setContentView method, because the library have it's own layout
     * 4. you must not override onBackPressed method, because the library implement it on its own
     */

    @Override
    public void init(Bundle bundle) {
        addSection(newSection(getResources().getString(R.string.title_overview), R.drawable.ic_action_action_schedule, new OverviewFragment()));
        addSection(newSection(getResources().getString(R.string.title_events), R.drawable.ic_action_image_nature_people, new EventsFragment()));
        addSection(newSection(getResources().getString(R.string.title_people), R.drawable.ic_action_social_people, new PeopleFragment()));

        setDefaultSectionLoaded(0);

        setAccountListener(this);

        MaterialAccount account = new MaterialAccount(this.getResources(),
                "Florian Dejonckheere",
                "florian@floriandejonckheere.be",
                R.drawable.gravatar,
                R.drawable.material);
        this.addAccount(account);

        allowArrowAnimation();
    }

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

    }

    @Override
    public void onChangeAccount(MaterialAccount materialAccount) {

    }
}
