package be.thalarion.eventman.api;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.thalarion.eventman.PeopleAdapter;
import be.thalarion.eventman.R;

public class PeopleFetchTask extends AsyncTask<URI, Void, List<Person>> {

    private Activity activity;

    public PeopleFetchTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected List<Person> doInBackground(URI... params) {
        List<Person> list = new ArrayList<>();

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(params[0]);
        request.setHeader("Accept", "application/json");

        try {
            // Fetch list of people
            HttpResponse response = client.execute(request);
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));

            JSONArray people = (JSONArray) json.get("people");

            for(int i = 0; i < people.length(); i++){
                JSONObject person = people.getJSONObject(i);
                HttpGet requestPerson = new HttpGet(person.getString("url"));
                requestPerson.setHeader("Accept", "application/json");

                    // Fetch specific person
                    HttpResponse responsePerson = client.execute(requestPerson);
                    JSONObject jsonPerson = new JSONObject(EntityUtils.toString(responsePerson.getEntity()));
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        Person p = new Person(
                                jsonPerson.getString("name"),
                                jsonPerson.getString("email"),
                                format.parse(jsonPerson.getString("birth_date"))
                        );
                        list.add(p);
                    } catch (ParseException err){
                        err.printStackTrace();
                    }
            }

        } catch (Exception e) {
            Toast.makeText(activity, R.string.error_fetch_people, Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<Person> people) {
        ((PeopleAdapter) ((RecyclerView) activity.findViewById(R.id.activity_people_list_view)).getAdapter()).setDataSet(people);
        ((SwipeRefreshLayout) activity.findViewById(R.id.activity_people_swipe_container)).setRefreshing(false);
    }
}
