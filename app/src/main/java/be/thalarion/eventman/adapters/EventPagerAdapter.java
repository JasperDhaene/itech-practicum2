package be.thalarion.eventman.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.net.URI;

import be.thalarion.eventman.R;
import be.thalarion.eventman.fragments.event.EventTab;
import be.thalarion.eventman.fragments.event.message.MessagesTab;

public class EventPagerAdapter extends FragmentPagerAdapter {

    private URI eventUri;
    private Context context;

    public EventPagerAdapter(FragmentManager fm, URI eventUri, Context context) {
        super(fm);
        this.eventUri = eventUri;
        this.context = context;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return EventTab.newInstance(this.eventUri);
            case 1:
                return MessagesTab.newInstance(this.eventUri);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.title_event);
            case 1:
                return context.getString(R.string.title_messages);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
