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
import java.net.URL;

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

    public static EventPagerFragment newInstance(URL url) {
        EventPagerFragment fragment = new EventPagerFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("url", url);

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
                (URL) getArguments().getSerializable("url"));
        this.viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_container);
        this.viewPager.setAdapter(pagerAdapter);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_edit_event:
                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(
                        EditEventDialogFragment.newInstance(this.event.getResource(), Model.ACTION.EDIT),
                        this.getActivity().getString(R.string.title_edit_event));
                return true;
            case R.id.action_discard_event:
                final Context context = getActivity();
                new AsyncTask<Void, Void, Exception>() {
                    @Override
                    protected Exception doInBackground(Void... params) {
                        try {
                            event.destroy();
                            event = null;
                        } catch (APIException | IOException e) {
                            return e;
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Exception e) {
                        if(e == null) {
                            Toast.makeText(context, context.getResources().getText(R.string.info_text_destroy), Toast.LENGTH_SHORT).show();
                        } else ErrorHandler.announce(context, e);
                    }
                }.execute();

                getActivity().onBackPressed();
                return true;
            default:
                return false;
        }
    }

}
