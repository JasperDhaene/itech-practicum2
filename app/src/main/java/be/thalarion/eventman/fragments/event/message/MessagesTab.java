package be.thalarion.eventman.fragments.event.message;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

    public static MessagesTab newInstance(URI uri){
        MessagesTab fragment = new MessagesTab();

        Bundle bundle = new Bundle();
        bundle.putSerializable("uri", uri);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.swipe_list, container, false);

        // ActionBar
        setHasOptionsMenu(true);

        final Context context = this.getActivity().getApplicationContext();
        new AsyncTask<Bundle, Exception, Event>() {
            @Override
            protected Event doInBackground(Bundle... params) {
                Event event = null;
                Bundle data = params[0];
                try {
                    event = Cache.find(Event.class, (URI) data.getSerializable("uri"));
                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return event;
            }

            @Override
            protected void onPostExecute(Event ev) {
                event = ev;
                event.getMessages();
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }
        }.execute(getArguments());

        // Swipe to refresh
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_list_container);
        swipeLayout.setColorSchemeColors(Color.argb(255, 233, 30, 99),Color.argb(255,63,81,181) );
        swipeLayout.setOnRefreshListener(this);

        // Message list
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.swipe_list_view);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        MessagesAdapter adapter = new MessagesAdapter(getActivity());
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
        inflater.inflate(R.menu.events, menu); //TODO: verander nog é
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
        new AsyncTask<Void, Exception, List<Message>>() {
            @Override
            protected List<Message> doInBackground(Void... params) {
                try {
                    List<Event> eventList = Cache.findAll(Event.class);
                    Event refreshedEvent = null;
                    for(Event ev:eventList){
                        // Search for the refreshed event
                        if(ev.equals(event))
                            refreshedEvent=ev;
                    }

                    if(refreshedEvent!=null){
                        event = refreshedEvent;
                    } else {
                        //TODO: find meaningful exception or Toast that says the event from which \
                        // you ask messages has been deleted since the last refresh
                    }

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
        }.execute();
    }
}
