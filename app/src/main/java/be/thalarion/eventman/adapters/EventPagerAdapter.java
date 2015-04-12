package be.thalarion.eventman.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.net.URL;

import be.thalarion.eventman.fragments.event.message.MessagesTab;
import be.thalarion.eventman.fragments.event.EventTab;

public class EventPagerAdapter extends FragmentPagerAdapter {

    private URL url;

    public EventPagerAdapter(FragmentManager fm, URL url) {
        super(fm);
        this.url = url;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return EventTab.newInstance(this.url);
            case 1:
                return MessagesTab.newInstance(this.url);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Event";
            case 1:
                return "Messages";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
