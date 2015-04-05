package be.thalarion.eventman.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import be.thalarion.eventman.MessageFragment;
import be.thalarion.eventman.ShowEventTabFragment;

/**
 * Created by jasper on 04/04/15.
 */
public class EventMessagePagerAdapter extends FragmentPagerAdapter {

    private String event_url;

    public EventMessagePagerAdapter(FragmentManager fm,String event_url) {
        super(fm);
        this.event_url = event_url;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ShowEventTabFragment.newInstance(this.event_url);
            case 1:
                return new MessageFragment();

        }
        return null; //unreachable
    }

    @Override
    public int getCount() {
        return 2;
    }
}
