package be.thalarion.eventman.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import be.thalarion.eventman.MessageFragment;
import be.thalarion.eventman.ShowEventDetailFragment;
import be.thalarion.eventman.models.Event;

/**
 * Created by jasper on 04/04/15.
 */
public class EventMessagePagerAdapter extends FragmentPagerAdapter {

    private Event event;

    public EventMessagePagerAdapter(FragmentManager fm,Event event) {
        super(fm);
        this.event = event;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ShowEventDetailFragment frag = ShowEventDetailFragment.newInstance(this.event);
                return frag;
                //return new MessageFragment();
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
