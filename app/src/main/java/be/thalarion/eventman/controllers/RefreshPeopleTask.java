package be.thalarion.eventman.controllers;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import be.thalarion.eventman.R;
import be.thalarion.eventman.models.APIException;
import be.thalarion.eventman.models.Person;

public class RefreshPeopleTask extends AsyncTask<URI, Exception, List<Person>> {

    private Activity activity;

    public RefreshPeopleTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected List<Person> doInBackground(URI... params) {
        try {
            return Person.findAll();
        } catch (IOException | APIException e) {
            e.printStackTrace();
            publishProgress(e);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Exception... values) {
        // Use progress updates to report errors instead
        Toast.makeText(activity, R.string.error_fetch + ": " + values[0].getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(List<Person> people) {
        ((PeopleAdapter) ((RecyclerView) activity.findViewById(R.id.activity_people_list_view)).getAdapter()).setDataSet(people);
        ((SwipeRefreshLayout) activity.findViewById(R.id.activity_people_swipe_container)).setRefreshing(false);
    }
}
