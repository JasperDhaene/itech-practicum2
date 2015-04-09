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
    private EditText field_name, field_email;
    private TextView field_birthdate;

    public EditPersonDialogFragment() {
        // Required empty public constructor
    }

    //TODO: als Parcels geen vertraging oplevert, verwijder deze constructor aangezien het geen good practice is
    public EditPersonDialogFragment(Person person) {
        this.person = person;
    }

    public static EditPersonDialogFragment newInstance(String person_url, Model.ACTION action) {

        EditPersonDialogFragment f = new EditPersonDialogFragment();
        Bundle bundle = new Bundle();
        if (!person_url.equals("")) {
            bundle.putString("person_url", person_url);
        }


        bundle.putSerializable("action", action);

        f.setArguments(bundle);

        return f;
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_person_dialog, container, false);

        this.field_name = ((EditText) rootView.findViewById(R.id.field_name));
        this.field_email = ((EditText) rootView.findViewById(R.id.field_email));
        this.field_birthdate = ((TextView) rootView.findViewById(R.id.field_birth_date));

        final Context context = getActivity();
        new AsyncTask<Bundle, Exception, Person>() {
            private Bundle data = null;

            @Override
            protected Person doInBackground(Bundle... params) {
                Person pers = null;
                this.data = params[0];
                try {
                    String s = this.data.getString("person_url");
                    pers = Cache.find(Person.class, new URL(s));
                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return pers;
            }

            @Override
            protected void onPostExecute(Person pers) {
                person = pers;
                if (this.data.getSerializable("action") == Model.ACTION.EDIT) {
                    field_name.setText(person.getName());
                    field_email.setText(person.getEmail());

                    if (person.getBirthDate() != null) {
                        field_birthdate.setText(Person.format.format(person.getBirthDate()));
                    } else {
                        field_birthdate.setText(Model.DEFAULT_DATE);
                    }
                } else if (this.data.getSerializable("action") == Model.ACTION.NEW) {
                    person = new Person();
                }
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }
        }.execute(getArguments());


        this.field_birthdate.setOnClickListener(this);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                new AsyncTask<Void, Void, Exception>() {
                    private Context context;

                    @Override
                    protected void onPreExecute() {
                        this.context = getActivity();
                    }

                    @Override
                    protected Exception doInBackground(Void... params) {
                        String name = field_name.getText().toString();
                        String email = field_email.getText().toString();
                        Date birthDate;
                        try {
                            birthDate = Person.format.parse(field_birthdate.getText().toString());
                        } catch (ParseException e) {
                            return e; //TODO: what dafuq dit mag hier niet returnn wi. Zet dan een default waarde als datum en print af dat er een fucking error is. Lul.
                        }
                        //new Person has been created if action==ACTION.NEW
                        person.setName(name);
                        person.setEmail(email);
                        person.setBirthDate(birthDate);

                        try {
                            person.syncModelToNetwork();
                        } catch (IOException | APIException e) {
                            return e;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Exception e) {
                        if (e == null) {
                            Toast.makeText(this.context, this.context.getResources().getText(R.string.info_text_edit), Toast.LENGTH_LONG).show();
                        } else ErrorHandler.announce(this.context, e);
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
    public void onClick(View v) { // Parameter v stands for the view that was clicked.
        if (v.getContentDescription().toString().equals("Date_Start")) {
            DialogFragment f = DateDialogFragment.newInstance(this, v);

            f.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }
}
