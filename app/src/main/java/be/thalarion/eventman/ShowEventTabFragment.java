package be.thalarion.eventman;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowEventTabFragment extends android.support.v4.app.Fragment {

    private TextView title, description, startDate, endDate;
    private ImageView banner; //TODO: vul de banner in. Geen idee hoe dit gedaan wordt momenteel.
    private Event event;


    public ShowEventTabFragment() {
        // Required empty public constructor
    }

    public static ShowEventTabFragment newInstance(String event_url) {
        ShowEventTabFragment f = new ShowEventTabFragment();

        Bundle bundle = new Bundle();

        bundle.putString("event_url",event_url);
        f.setArguments(bundle);

        return f;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_event, container, false);
        setHasOptionsMenu(true);

        this.title = ((TextView) rootView.findViewById(R.id.event_title));
        this.description = ((TextView) rootView.findViewById(R.id.event_description));
        this.startDate = ((TextView) rootView.findViewById(R.id.event_startdate));
        this.endDate = ((TextView) rootView.findViewById(R.id.event_enddate));
        //TODO: banner invullen

        final Context context = this.getActivity().getApplicationContext();
        new AsyncTask<Bundle, Exception, Event>() {
            private Bundle data = null;
            @Override
            protected Event doInBackground(Bundle... params) {
                Event event = null;
                Bundle data = params[0];
                try {
                    event = Cache.find(Event.class, new URL(data.getString("event_url")));
                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return event;
            }

            @Override
            protected void onPostExecute(Event ev) {
                event = ev;
                if(event.getTitle() != null)
                    title.setText(event.getTitle());
                else
                    title.setText(R.string.error_text_notitle);

                if(event.getDescription() != null)
                    description.setText(event.getDescription());
                else
                    title.setText(R.string.error_text_nodescription);

                if(event.getStartDate() != null)
                    startDate.setText(Event.format.format(event.getStartDate()));
                else
                    startDate.setText(R.string.error_text_nostartdate);

                if(event.getEndDate() != null)
                    endDate.setText(Event.format.format(event.getEndDate()));
                else
                    endDate.setText(R.string.error_text_noenddate);
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }
        }.execute(getArguments());

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_edit_event:

                EditEventDialogFragment editEventFrag = EditEventDialogFragment.newInstance(this.event, Model.ACTION.EDIT);

                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(editEventFrag,this.getActivity().getResources().getString(R.string.title_edit_event));

                break;
            case R.id.action_discard_event:
                new AsyncTask<Void, Void, Exception>() {
                    private Context context;

                    @Override
                    protected void onPreExecute() {
                        this.context = getActivity();
                    }

                    @Override
                    protected Exception doInBackground(Void... params) {
                        try {
                            event.destroy();
                            // Allow garbage collection
                            event = null;
                        } catch (APIException | IOException e) {
                            return e;
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Exception e) {
                        if(e == null) {
                            Toast.makeText(this.context, this.context.getResources().getText(R.string.info_text_destroy), Toast.LENGTH_LONG).show();
                        } else ErrorHandler.announce(this.context, e);
                    }
                }.execute();

                EventsFragment eventsFrag = new EventsFragment();

                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(eventsFrag,this.getActivity().getResources().getString(R.string.title_people));

                break;
            default:
                return false;
        }
        return true;
    }


}
