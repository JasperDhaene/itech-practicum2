package be.thalarion.eventman.fragments.person;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
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
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class EditPersonDialogFragment extends EditDialogFragment
        implements View.OnClickListener {

    private Person person;
    private EditText name, email;
    private TextView birthDate;

    public EditPersonDialogFragment() {
        // Required empty public constructor
    }

    public static EditPersonDialogFragment newInstance(URL url, Model.ACTION action) {
        EditPersonDialogFragment fragment = new EditPersonDialogFragment();

        Bundle bundle = new Bundle();
        if(url != null)
            bundle.putSerializable("url", url);
        bundle.putSerializable("action", action);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cancel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_person_dialog, container, false);

        this.name = ((EditText) rootView.findViewById(R.id.field_name));
        this.email = ((EditText) rootView.findViewById(R.id.field_email));
        this.birthDate = ((TextView) rootView.findViewById(R.id.field_birth_date));

        final Bundle data = getArguments();
        final Context context = getActivity();
        new AsyncTask<Void, Exception, Person>() {
            @Override
            protected Person doInBackground(Void... params) {
                try {
                    return Cache.find(Person.class, (URL) data.getSerializable("url"));
                } catch (IOException | APIException e) {
                    publishProgress(e);
                    return null;
                }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
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

                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(new PeopleFragment(), this.getResources().getString(R.string.title_people));

                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO: da fuq ies dies
        if (v.getContentDescription().toString().equals("Date_Start")) {
            DialogFragment f = DateDialogFragment.newInstance(this, v);

            f.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }
}
