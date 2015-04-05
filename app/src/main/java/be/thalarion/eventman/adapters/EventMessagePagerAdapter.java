package be.thalarion.eventman.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.net.URL;

import be.thalarion.eventman.fragments.event.MessageFragment;
import be.thalarion.eventman.fragments.event.ShowEventTabFragment;

public class EventMessagePagerAdapter extends FragmentPagerAdapter {

    private URL url;

    public EventMessagePagerAdapter(FragmentManager fm, URL url) {
        super(fm);
        this.url = url;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ShowEventTabFragment.newInstance(this.url);
            case 1:
                return new MessageFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
