package be.thalarion.eventman.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.net.URI;

import be.thalarion.eventman.fragments.event.EventTab;
import be.thalarion.eventman.fragments.event.message.MessagesTab;

public class EventPagerAdapter extends FragmentPagerAdapter {

    private URI uri;

    public EventPagerAdapter(FragmentManager fm, URI uri) {
        super(fm);
        this.uri = uri;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return EventTab.newInstance(this.uri);
            case 1:
                return MessagesTab.newInstance(this.uri);
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
