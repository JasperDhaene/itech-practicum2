package be.thalarion.eventman.fragments.event;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

import be.thalarion.eventman.R;
import be.thalarion.eventman.adapters.PeopleAdapter;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.models.Person;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends android.support.v4.app.Fragment implements android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {

    //TODO: i stopped implementing this midway and had to merge api in this branch before i could procede.

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.people, menu); //TODO: verander nog é
    }


    @Override
    public void onRefresh() {
        // onRefresh is called only when the user is explicitly swiping
        //Cache.invalidate(Person.class); TODO: bekijk de message versie van de cache hiervoor
        refresh();
    }

    /**
     * refresh - Refresh models from cache
     */
    public void refresh() {
        new AsyncTask<Void, Exception, List<Person>>() {
            @Override
            protected List<Person> doInBackground(Void... params) {
                try {
                    return Cache.findAll(Person.class);
                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                // Use progress updates to report errors instead
                ErrorHandler.announce(getActivity(), values[0]);
            }
            @Override
            protected void onPostExecute(List<Person> people) {
                ((PeopleAdapter) ((RecyclerView) getActivity().findViewById(R.id.swipe_list_view)).getAdapter()).setDataSet(people);
                ((SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_list_container)).setRefreshing(false);
            }
        }.execute();
    }
}
