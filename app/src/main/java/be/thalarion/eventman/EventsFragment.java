package be.thalarion.eventman;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import be.thalarion.eventman.adapters.EventsAdapter;
import be.thalarion.eventman.models.APIException;
import be.thalarion.eventman.models.Event;

/**
 * Events layout contains a list of all events
 */
public class EventsFragment extends android.support.v4.app.Fragment
        implements android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.swipe_list, container, false);
        setHasOptionsMenu(true);

        // Swipe to refresh
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_list_container);
        swipeLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        swipeLayout.setOnRefreshListener(this);

        // Event list
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.swipe_list_view);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        EventsAdapter adapter = new EventsAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_add_event:
                Toast.makeText(getActivity(), "Add event", Toast.LENGTH_SHORT).show();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onRefresh() {
        new AsyncTask<Void, Exception, List<Event>>() {
            @Override
            protected List<Event> doInBackground(Void... params) {
                try {
                    return Event.findAll();
                } catch (IOException | APIException e) {
                    e.printStackTrace();
                    publishProgress(e);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                // Use progress updates to report errors instead
                // On production, replace message by 'R.string.error_fetch'
                Toast.makeText(getActivity(), values[0].getMessage(), Toast.LENGTH_LONG).show();
            }
            @Override
            protected void onPostExecute(List<Event> events) {
                ((EventsAdapter) ((RecyclerView) getActivity().findViewById(R.id.swipe_list_view)).getAdapter()).setDataSet(events);
                ((SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_list_container)).setRefreshing(false);
            }
        }.execute();
    }
}