package be.thalarion.eventman;


import android.content.Intent;
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

import java.io.IOException;
import java.util.List;

import be.thalarion.eventman.adapters.PeopleAdapter;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.models.Model;
import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


/**
 * People layout contains a list of people
 */
public class PeopleFragment extends android.support.v4.app.Fragment
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

        // People list
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.swipe_list_view);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        PeopleAdapter adapter = new PeopleAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        swipeLayout.post(new Runnable(){
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
                // The refresh listener does not get called for some obscure reason
                refresh();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.people, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_add_person:

                /*Intent intent = new Intent(this.getActivity(), EditPersonActivity.class);
                intent.putExtra("action", Model.ACTION.NEW);
                startActivity(intent);*/

                EditPersonDialogFragment f = EditPersonDialogFragment.newInstance(null,Model.ACTION.NEW);
                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(f, this.getActivity().getResources().getString(R.string.title_edit_person));
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onRefresh() {
        // onRefresh is called only when the user is explicitly swiping
        Cache.invalidate(Person.class);
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
