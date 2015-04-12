package be.thalarion.eventman.fragments.event;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;

import be.thalarion.eventman.R;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.fragments.DateDialogFragment;
import be.thalarion.eventman.fragments.EditDialogFragment;
import be.thalarion.eventman.fragments.TimeDialogFragment;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;

public class EditEventDialogFragment extends EditDialogFragment
        implements View.OnClickListener {

    private EditText title, description;
    private TextView startDate, startTime, endDate, endTime;

    private Event event;

    public static EditEventDialogFragment newInstance(URI uri, Model.ACTION action) {
        EditEventDialogFragment fragment = new EditEventDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("action", action);
        bundle.putSerializable("uri", uri);

        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_event_dialog, container, false);

        setHasOptionsMenu(false);
        final View doneBar = inflater.inflate(R.layout.actionbar_done_cancel, null);
        doneBar.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { done(); } });
        doneBar.findViewById(R.id.actionbar_cancel).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { cancel(); } });
        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(doneBar,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        actionBar.setDisplayHomeAsUpEnabled(false);

        this.title = ((EditText) rootView.findViewById(R.id.field_title));
        this.description = ((EditText) rootView.findViewById(R.id.field_description));
        this.startDate = ((TextView) rootView.findViewById(R.id.field_start_date));
        this.endDate = ((TextView) rootView.findViewById(R.id.field_end_date));
        this.startTime = ((TextView) rootView.findViewById(R.id.field_start_time));
        this.endTime = ((TextView) rootView.findViewById(R.id.field_end_time));

        final Bundle data = getArguments();
        final Context context = getActivity();
        new AsyncTask<Void, Exception, Event>(){
            @Override
            protected Event doInBackground(Void... params) {
                try {
                    return Cache.find(Event.class, (URI) data.getSerializable("uri"));
                } catch (IOException | APIException e) {
                    publishProgress(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Event ev) {
                event = ev;
                if (data.getSerializable("action") == Model.ACTION.EDIT) {
                    title.setText(event.getFormattedTitle(context));
                    description.setText(event.getFormattedDescription(context));
                    startDate.setText(event.getFormattedStartDate(context, Event.formatDate));
                    startTime.setText(event.getFormattedStartDate(context, Event.formatTime));
                    endDate.setText(event.getFormattedEndDate(context, Event.formatDate));
                    endTime.setText(event.getFormattedEndDate(context, Event.formatTime));
                } else if (data.getSerializable("action") == Model.ACTION.NEW) {
                    event = new Event();
                }
            }

            @Override
            protected void onProgressUpdate(Exception... e) {
                ErrorHandler.announce(context, e[0]);
            }
        }.execute();

        this.startDate.setOnClickListener(this);
        this.endDate.setOnClickListener(this);
        this.startTime.setOnClickListener(this);
        this.endTime.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        // TODO: da fuq ies dies
        String c = v.getContentDescription().toString();
        if(c.equals("Date_Start") || c.equals("Date_End")){
            DialogFragment f = DateDialogFragment.newInstance(this, v);

            f.show(getActivity().getSupportFragmentManager(), "datePicker");
        }else if(c.equals("Time_Start") || c.equals("Time_End")){
            DialogFragment f = TimeDialogFragment.newInstance(this, v);

            f.show(getActivity().getSupportFragmentManager(), "timePicker");
        }
    }

    private void done() {
        final Context context = getActivity();
        new AsyncTask<Void, Exception, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Date startDateText = null, endDateText = null;
                //TODO: shit's fucked up cause two digits numbers ain't enforced
                StringBuilder builderStart = new StringBuilder().append(startDate.getText().toString())
                        .append("T")
                        .append(startTime.getText().toString())
                        .append(".000Z");

                StringBuilder builderEnd = new StringBuilder().append(endDate.getText().toString())
                        .append("T")
                        .append(endTime.getText().toString())
                        .append(".000Z");

                // Parse dates and publish an exception on invalid dates
                try {
                    startDateText = Event.format.parse(builderStart.toString());
                } catch (ParseException e) {
                    startDateText = null;
                    publishProgress(e);
                }
                try {
                    endDateText = Event.format.parse(builderEnd.toString());
                } catch (ParseException e) {
                    endDateText = null;
                    publishProgress(e);
                }

                // TODO: replace this by a refresh method (on swipe?)
                event.setTitle(title.getText().toString());
                event.setDescription(description.getText().toString());
                event.setStartDate(startDateText);
                event.setEndDate(endDateText);

                try {
                    event.syncModelToNetwork();
                } catch (IOException | APIException e) {
                    publishProgress(e);
                    return false;
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success)
                    Toast.makeText(context, context.getResources().getText(R.string.info_text_edit), Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }
        }.execute();
        getActivity().onBackPressed();
    }

    private void cancel() {
        getActivity().onBackPressed();
    }

}
