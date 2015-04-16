package be.thalarion.eventman.fragments.event;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;

import be.thalarion.eventman.R;
import be.thalarion.eventman.adapters.EventPagerAdapter;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class EventPagerFragment extends android.support.v4.app.Fragment {

    private EventPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private Event event;

    public static EventPagerFragment newInstance(URI uri) {
        EventPagerFragment fragment = new EventPagerFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("uri", uri);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.viewpager_event, container, false);

        // ActionBar
        setHasOptionsMenu(false);
        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE,
                ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

        this.pagerAdapter = new EventPagerAdapter(
                this.getActivity().getSupportFragmentManager(),
                (URI) getArguments().getSerializable("uri"));
        this.viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_container);
        this.viewPager.setAdapter(pagerAdapter);

        return rootView;
    }

}
