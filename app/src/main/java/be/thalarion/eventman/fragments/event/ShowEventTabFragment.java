package be.thalarion.eventman.fragments.event;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import be.thalarion.eventman.R;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class ShowEventTabFragment extends android.support.v4.app.Fragment {

    private TextView title, description, startDate, endDate;
    private ImageView banner; //TODO: vul de banner in. Geen idee hoe dit gedaan wordt momenteel.
    private Event event;

    public ShowEventTabFragment() {
        // Required empty public constructor
    }

    public static ShowEventTabFragment newInstance(URL url) {
        ShowEventTabFragment fragment = new ShowEventTabFragment();

        Bundle bundle = new Bundle();

        bundle.putSerializable("url", url);
        fragment.setArguments(bundle);

        return fragment;
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

        final Context context = this.getActivity();

        new AsyncTask<Bundle, Exception, Event>() {
            @Override
            protected Event doInBackground(Bundle... params) {
                Event event = null;
                Bundle data = params[0];
                try {
                    event = Cache.find(Event.class, (URL) data.getSerializable("url"));
                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return event;
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
                startDate.setText(event.getFormattedStartDate(context, Event.format));
                endDate.setText(event.getFormattedEndDate(context, Event.format));
            }
        }.execute(getArguments());

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_edit_event:

                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(
                        EditEventDialogFragment.newInstance(this.event.getResource(), Model.ACTION.EDIT),
                        this.getActivity().getString(R.string.title_edit_event));
                break;
            case R.id.action_discard_event:
                final Context context = getActivity();

                new AsyncTask<Void, Void, Exception>() {
                    @Override
                    protected Exception doInBackground(Void... params) {
                        try {
                            event.destroy();
                            event = null;
                        } catch (APIException | IOException e) {
                            return e;
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Exception e) {
                        if(e == null) {
                            Toast.makeText(context, context.getResources().getText(R.string.info_text_destroy), Toast.LENGTH_SHORT).show();
                        } else ErrorHandler.announce(context, e);
                    }
                }.execute();

                // TODO: shouldn't this return to EventsFragment?
                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(
                        new EventsFragment(),
                        this.getActivity().getString(R.string.title_people));
                break;
            default:
                return false;
        }
        return true;
    }


}
