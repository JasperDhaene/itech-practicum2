package be.thalarion.eventman;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import java.io.IOException;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowEventFragment extends android.support.v4.app.Fragment {

    private TextView title, description, startDate, endDate;
    private ImageView banner; //TODO: vul de banner in. Geen idee hoe dit gedaan wordt momenteel.
    private Event event;


    public ShowEventFragment() {
        // Required empty public constructor
    }

    public static ShowEventFragment newInstance(Event event) {
        ShowEventFragment f = new ShowEventFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("event", Parcels.wrap(event));

        f.setArguments(bundle);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_show_event, container, false);
        setHasOptionsMenu(true);

        Bundle data = getArguments();
        this.event = Parcels.unwrap(data.getParcelable("event"));

        this.title = ((TextView) rootView.findViewById(R.id.event_title));
        this.description = ((TextView) rootView.findViewById(R.id.event_description));
        this.startDate = ((TextView) rootView.findViewById(R.id.event_startdate));
        this.endDate = ((TextView) rootView.findViewById(R.id.event_enddate));
        //TODO: banner invullen

        if(this.event.getTitle() != null)
            this.title.setText(event.getTitle());
        else
            this.title.setText(R.string.error_text_notitle);

        if(this.event.getDescription() != null)
            this.description.setText(event.getDescription());
        else
            this.title.setText(R.string.error_text_nodescription);

        if(this.event.getStartDate() != null)
            this.startDate.setText(Event.format.format(event.getStartDate()));
        else
            this.startDate.setText(R.string.error_text_nostartdate);

        if(this.event.getEndDate() != null)
            this.endDate.setText(Event.format.format(event.getEndDate()));
        else
            this.endDate.setText(R.string.error_text_noenddate);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.action_edit_event:

                showDialog();

                break;
            case R.id.action_discard_event:
                new AsyncTask<Void, Void, Exception>() {
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
                            Toast.makeText(getActivity(), getResources().getText(R.string.info_text_destroy), Toast.LENGTH_LONG).show();
                        } else ErrorHandler.announce(getActivity(), e);
                    }
                }.execute();

                EventsFragment f = new EventsFragment();

                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(f,this.getActivity().getResources().getString(R.string.title_people));

                break;
            //TODO: maybe make this an 'attend' button for testing
            /*
            case R.id.action_login:
                MaterialAccount account = ((MaterialNavigationDrawer) getActivity()).getCurrentAccount();
                // TODO: null-catching
                account.setTitle(this.person.getName());
                account.setSubTitle(this.person.getEmail());
                ((MaterialNavigationDrawer) getActivity()).notifyAccountDataChanged();
                break;*/
            default:
                return false;
        }
        return true;
    }

    void showDialog() {

        EditEventDialogFragment f = EditEventDialogFragment.newInstance(this.event, Model.ACTION.EDIT);

        ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(f,this.getActivity().getResources().getString(R.string.title_edit_event));
    }


}
