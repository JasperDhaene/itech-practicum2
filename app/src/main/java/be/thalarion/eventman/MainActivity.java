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


public class MainActivity extends MaterialNavigationDrawer {

    /**
     * neokree MaterialNavigationDrawer
     * 1. you have an init method
     * 2. you must not override onCreate method
     * 3. you must not call setContentView method, because the library have it's own layout
     * 4. you must not override onBackPressed method, because the library implement it on its own
     */

    @Override
    public void init(Bundle bundle) {
        MaterialSection secOverview = newSection(getResources().getString(R.string.title_overview), new OverviewFragment());
        MaterialSection secEvents = newSection(getResources().getString(R.string.title_events), new EventsFragment());
        MaterialSection secPeople = newSection(getResources().getString(R.string.title_people), new PeopleFragment());

        addSection(secOverview);
        addSection(secEvents);
        addSection(secPeople);

        MaterialAccount account = new MaterialAccount(this.getResources(),
                "Florian Dejonckheere",
                "florian@floriandejonckheere.be",
                R.drawable.gravatar,
                R.drawable.material);
        this.addAccount(account);
    }
}
