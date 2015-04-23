package be.thalarion.eventman.fragments.event;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.io.IOException;
import java.net.URI;

import be.thalarion.eventman.R;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.models.Event;

public class EventTab extends android.support.v4.app.Fragment {

    private TextView title, description, startDate, endDate;
    private ImageView banner;
    private Event event;


    public EventTab() {
        // Required empty constructor
    }

    public static EventTab newInstance(URI eventUri) {

        EventTab fragment = new EventTab();

        Bundle bundle = new Bundle();
        bundle.putSerializable("eventUri", eventUri);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_event, container, false);

        this.title = ((TextView) rootView.findViewById(R.id.event_title));
        this.description = ((TextView) rootView.findViewById(R.id.event_description));
        this.startDate = ((TextView) rootView.findViewById(R.id.event_startdate));
        this.endDate = ((TextView) rootView.findViewById(R.id.event_enddate));
        this.banner = ((ImageView) rootView.findViewById(R.id.event_banner));

        final Context context = this.getActivity();
        final Bundle data = getArguments();
        new AsyncTask<Void, Exception, Event>() {
            @Override
            protected Event doInBackground(Void... params) {
                try {
                    return Cache.find(Event.class, (URI) data.getSerializable("eventUri"));
                } catch (IOException | APIException e) {
                    publishProgress(e);
                    return null;
                }
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }

            @Override
            protected void onPostExecute(Event ev) {
                event = ev;

                title.setText(event.getFormattedTitle(context));
                description.setText(event.getFormattedDescription(context));

                startDate.setText(event.getFormattedStartDate(context, Event.formatDate) + " " +
                                    event.getFormattedStartDate(context, Event.formatTime));

                endDate.setText(event.getFormattedEndDate(context, Event.formatDate) + " " +
                                    event.getFormattedEndDate(context, Event.formatTime));

                String color = Event.hash(event.getFormattedTitle(context));
                TextDrawable drawable = TextDrawable.builder().buildRect(
                        color,
                        context.getResources().getColor(Event.colorFromString(color))
                );
                banner.setImageDrawable(drawable);
            }
        }.execute();

        return rootView;
    }

}
