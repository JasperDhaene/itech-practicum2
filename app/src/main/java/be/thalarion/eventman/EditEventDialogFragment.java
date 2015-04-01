package be.thalarion.eventman;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;
import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditEventDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private Event event;
    private EditText field_title,field_description;
    private TextView field_startdate,field_enddate;

    public EditEventDialogFragment() {
        // Required empty public constructor
    }

    public static EditEventDialogFragment newInstance(Event event, Model.ACTION action) {

        EditEventDialogFragment f = new EditEventDialogFragment();
        Bundle bundle = new Bundle();
        if (event != null) {
            bundle.putParcelable("event", Parcels.wrap(event));
        }

        bundle.putSerializable("action", action);

        f.setArguments(bundle);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((ActionBarActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cancel);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_event_dialog, container, false);

        this.field_title = ((EditText) rootView.findViewById(R.id.field_title));
        this.field_description = ((EditText) rootView.findViewById(R.id.field_description));
        this.field_startdate = ((TextView) rootView.findViewById(R.id.field_start_date));
        this.field_enddate = ((TextView) rootView.findViewById(R.id.field_end_date));

        Bundle data = getArguments();
        if (data.getSerializable("action") == Model.ACTION.EDIT) {
            this.event = Parcels.unwrap(data.getParcelable("event"));


            this.field_title.setText(event.getTitle());
            this.field_description.setText(event.getDescription());
            //TODO: implement null handling
            //TODO: look how the dateformat affects everything
            this.field_startdate.setText(Event.format.format(event.getStartDate()));
            //TODO: implement null handling
            this.field_enddate.setText(Event.format.format(event.getEndDate()));
        } else if (data.getSerializable("action") == Model.ACTION.NEW) {
            this.event = new Event();
        }

        this.field_startdate.setOnClickListener(this);
        this.field_enddate.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_save:
                new AsyncTask<Void, Void, Exception>() {
                    @Override
                    protected Exception doInBackground(Void... params) {
                        String title = field_title.getText().toString();
                        String description = field_description.getText().toString();
                        Date startdate,enddate;
                        try {
                            startdate = Event.format.parse(field_startdate.getText().toString());
                            enddate = Event.format.parse(field_enddate.getText().toString());
                        } catch (ParseException e) {
                            return e; //TODO: what dafuq dit mag hier niet returnn wi. Zet dan een default waarde als datum en print af dat er een fucking error is. Lul.
                        }
                        //new Person has been created if action==ACTION.NEW
                        event.setTitle(title);
                        event.setDescription(description);
                        event.setStartDate(startdate);
                        event.setEndDate(enddate);

                        try {
                            event.syncModelToNetwork();
                        } catch (IOException | APIException e) {
                            return e;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Exception e) {
                        if (e == null) {
                            //TODO first: crasht hier voor no apparent reason
                            // Toast.makeText(getActivity(), getResources().getText(R.string.info_text_edit), Toast.LENGTH_LONG).show();
                        } else {//ErrorHandler.announce(getActivity(), e);
                        } //TODO: crash
                    }
                }.execute();

                EventsFragment f = new EventsFragment();
                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(f, this.getResources().getString(R.string.title_events));

                break;

            default:
                return false;
        }
        return true;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //TODO from which textView did this come?
        ((TextView) this.getView().findViewById(R.id.field_start_date)).setText(
                new StringBuilder().append(year).append("-")
                        .append(monthOfYear).append("-").append(dayOfMonth));
    }

    @Override
    public void onClick(View v) { // Parameter v stands for the view that was clicked.
        DialogFragment f = DateDialogFragment.newInstance(this);

        f.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}
