package be.thalarion.eventman;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import org.parceler.Parcels;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.models.Model;
import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class EditPersonDialogFragment extends EditDialogFragment
        implements  View.OnClickListener {

    private Person person;
    private EditText field_name,field_email;
    private TextView field_birthdate;

    public EditPersonDialogFragment() {
        // Required empty public constructor
    }

    //TODO: als Parcels geen vertraging oplevert, verwijder deze constructor aangezien het geen good practice is
    public EditPersonDialogFragment(Person person) {
        this.person = person;
    }

    public static EditPersonDialogFragment newInstance(Person person, Model.ACTION action) {

        EditPersonDialogFragment f = new EditPersonDialogFragment();
        Bundle bundle = new Bundle();
        if (person != null) {
            bundle.putParcelable("person", Parcels.wrap(person));
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

        Bundle data = getArguments();
        if (data.getSerializable("action") == Model.ACTION.EDIT) {
            this.person = Parcels.unwrap(data.getParcelable("person"));

            this.field_name.setText(person.getName());
            this.field_email.setText(person.getEmail());
            //TODO: implement null handling
            this.field_birthdate.setText(Person.format.format(person.getBirthDate()));
        } else if (data.getSerializable("action") == Model.ACTION.NEW) {
            this.person = new Person();
        }

        this.field_birthdate.setOnClickListener(this);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_save:
                new AsyncTask<Void, Void, Exception>() {
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
                            //TODO first: crasht hier voor no apparent reason
                            // Toast.makeText(getActivity(), getResources().getText(R.string.info_text_edit), Toast.LENGTH_LONG).show();
                        } else ErrorHandler.announce(getActivity(), e);
                    }
                }.execute();

                PeopleFragment f = new PeopleFragment();
                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(f, this.getResources().getString(R.string.title_people));

                break;

            default:
                return false;
        }
        return true;
    }

    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_edit_person_dialog, null));


        return builder.create();


    }*/




    @Override
    public void onClick(View v) { // Parameter v stands for the view that was clicked.
        if(v.getContentDescription().toString().equals("Date_Start")){
            DialogFragment f = DateDialogFragment.newInstance(this,v);

            f.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }
}
