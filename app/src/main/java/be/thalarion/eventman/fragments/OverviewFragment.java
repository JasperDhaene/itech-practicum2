package be.thalarion.eventman.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.thalarion.eventman.R;


/**
 * Overview layout contains ongoing and future events
 */
public class OverviewFragment extends android.support.v4.app.Fragment
                            implements android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);
        setHasOptionsMenu(true);

        SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_overview_swipe_container);
        swipeLayout.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onRefresh() {
        new FetchTask().execute();
    }

    private class FetchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            ((SwipeRefreshLayout) getActivity().findViewById(R.id.activity_overview_swipe_container)).setRefreshing(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}
