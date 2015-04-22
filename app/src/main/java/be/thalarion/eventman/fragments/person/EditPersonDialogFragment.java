package be.thalarion.eventman.fragments.person;

import android.content.Context;
import android.content.res.Resources;
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
import be.thalarion.eventman.models.Model;
import be.thalarion.eventman.models.Person;

public class EditPersonDialogFragment extends EditDialogFragment
        implements View.OnClickListener {

    private Person person;
    private EditText name, email;
    private TextView birthDate;

    public EditPersonDialogFragment() {
        // Required empty public constructor
    }

    public static EditPersonDialogFragment newInstance(URI personUri, Model.ACTION action) {
        EditPersonDialogFragment fragment = new EditPersonDialogFragment();

        Bundle bundle = new Bundle();
        if(personUri != null)
            bundle.putSerializable("personUri", personUri);
        bundle.putSerializable("action", action);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_person_dialog, container, false);

        // ActionBar
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

        this.name = ((EditText) rootView.findViewById(R.id.field_name));
        this.email = ((EditText) rootView.findViewById(R.id.field_email));
        this.birthDate = ((TextView) rootView.findViewById(R.id.field_birth_date));

        final Bundle data = getArguments();
        final Context context = getActivity();
        new AsyncTask<Void, Exception, Person>() {
            @Override
            protected Person doInBackground(Void... params) {
                try {
                    if (data.getSerializable("personUri") != null)
                        return Cache.find(Person.class, (URI) data.getSerializable("personUri"));
                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Person pers) {
                person = pers;
                if (data.getSerializable("action") == Model.ACTION.EDIT) {
                    name.setText(person.getName());
                    email.setText(person.getEmail());

                    birthDate.setText(person.getFormattedBirthDate(context));

                } else if (data.getSerializable("action") == Model.ACTION.NEW) {
                    person = new Person();
                }
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }
        }.execute();

        this.birthDate.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Resources resources = this.getActivity().getResources();
        if (v.getContentDescription().toString().equals(resources.getString(R.string.content_description_date_start))) {
            DialogFragment f = DateDialogFragment.newInstance(this, v);

            f.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }

    private void done() {
        final Context context = getActivity();
        new AsyncTask<Void, Exception, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                String name = EditPersonDialogFragment.this.name.getText().toString();
                String email = EditPersonDialogFragment.this.email.getText().toString();
                Date birthDate;
                try {
                    birthDate = Person.format.parse(EditPersonDialogFragment.this.birthDate.getText().toString());
                } catch (ParseException e) {
                    publishProgress(e);
                    birthDate = null;
                }

                // TODO: replace this by a refresh method (on swipe?)
                person.setName(name);
                person.setEmail(email);
                person.setBirthDate(birthDate);

                try {
                    person.syncModelToNetwork();
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
