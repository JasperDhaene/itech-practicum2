package be.thalarion.eventman;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;

import be.thalarion.eventman.api.PeopleFetchTask;


/**
 * People layout contains a list of people
 */
public class PeopleFragment extends android.support.v4.app.Fragment
        implements android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_people, container, false);
        setHasOptionsMenu(true);

        // Swipe to refresh
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_people_swipe_container);
        swipeLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        swipeLayout.setOnRefreshListener(this);

        // Simply calling setRefreshing does not work properly
        swipeLayout.post(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(true);
            }
        });

        // People list
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.activity_people_list_view);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        PeopleAdapter adapter = new PeopleAdapter();
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onRefresh() {
        try {
            URI uri = new URI("http://events.restdesc.org/people");
            new PeopleFetchTask(getActivity()).execute(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
