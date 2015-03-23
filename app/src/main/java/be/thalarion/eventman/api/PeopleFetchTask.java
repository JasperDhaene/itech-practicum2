package be.thalarion.eventman.api;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.thalarion.eventman.PeopleAdapter;
import be.thalarion.eventman.R;
import fr.tkeunebr.gravatar.Gravatar;

public class PeopleFetchTask extends AsyncTask<URI, Void, List<Person>> {

    private Activity activity;

    public PeopleFetchTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected List<Person> doInBackground(URI... params) {
        return Person.findAll();
    }

    @Override
    protected void onPostExecute(List<Person> people) {
        if(people == null)
            Toast.makeText(activity, R.string.error_fetch, Toast.LENGTH_SHORT);

        ((PeopleAdapter) ((RecyclerView) activity.findViewById(R.id.activity_people_list_view)).getAdapter()).setDataSet(people);
        ((SwipeRefreshLayout) activity.findViewById(R.id.activity_people_swipe_container)).setRefreshing(false);
    }
}
