package be.thalarion.eventman.fragments.event.message;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import be.thalarion.eventman.R;
import be.thalarion.eventman.adapters.MessagesAdapter;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Message;


public class MessagesTab extends android.support.v4.app.Fragment
        implements android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {

    private Event event;
    private View rootView;

    public MessagesTab() {
        // Required empty public constructor
    }

    public static MessagesTab newInstance(URI eventUri){
        MessagesTab fragment = new MessagesTab();

        Bundle bundle = new Bundle();
        bundle.putSerializable("eventUri", eventUri);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.swipe_list, container, false);

        final Context context = this.getActivity().getApplicationContext();
        new AsyncTask<Bundle, Exception, Event>() {
            @Override
            protected Event doInBackground(Bundle... params) {
                Event event = null;
                Bundle data = params[0];
                try {
                    event = Cache.find(Event.class, (URI) data.getSerializable("eventUri"));
                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return event;
            }

            @Override
            protected void onPostExecute(Event ev) {
                event = ev;
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }
        }.execute(getArguments());

        // Swipe to refresh
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_list_container);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.material_pink_500), getResources().getColor(R.color.material_indigo_500));
        swipeLayout.setOnRefreshListener(this);

        // Message list
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.swipe_list_view);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        MessagesAdapter adapter = new MessagesAdapter();
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
    public void onRefresh() {
        // onRefresh is called only when the user is explicitly swiping
        Cache.invalidate(Event.class);
        refresh();
    }

    /**
     * refresh - Refresh models from cache
     */
    public void refresh() {
        new AsyncTask<Bundle, Exception, List<Message>>() {
            @Override
            protected List<Message> doInBackground(Bundle... params) {
                try {
                    // Throws an APIException if the resource isn't available anymore
                    event = Cache.find(Event.class, (URI) params[0].getSerializable("eventUri"));
                    return event.getMessages();
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
            protected void onPostExecute(List<Message> messages) {
                ((MessagesAdapter) ((RecyclerView) rootView.findViewById(R.id.swipe_list_view)).getAdapter()).setDataSet(messages);
                ((SwipeRefreshLayout) rootView.findViewById(R.id.swipe_list_container)).setRefreshing(false);
            }
        }.execute(getArguments());
    }
}
