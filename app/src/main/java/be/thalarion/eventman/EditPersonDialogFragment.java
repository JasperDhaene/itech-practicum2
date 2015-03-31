package be.thalarion.eventman;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.parceler.Parcels;

import be.thalarion.eventman.models.Person;


public class EditPersonDialogFragment extends android.support.v4.app.DialogFragment {

    private Person person;

    public EditPersonDialogFragment() {
        // Required empty public constructor
    }

    //TODO: als Parcels geen vertraging oplevert, verwijder deze constructor aangezien het geen good practice is
    public EditPersonDialogFragment(Person person){
        this.person = person;
    }

    public static EditPersonDialogFragment newInstance(Person person) {
        EditPersonDialogFragment f = new EditPersonDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("person",Parcels.wrap(person));

        f.setArguments(bundle);

        return f;
    }


        @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save_menu, menu);
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

        Bundle data = getArguments();
        this.person = Parcels.unwrap(data.getParcelable("person"));


        ((EditText) rootView.findViewById(R.id.field_name)).setText(person.getName());
        ((EditText) rootView.findViewById(R.id.field_email)).setText(person.getEmail());
        ((TextView) rootView.findViewById(R.id.field_birth_date)).setText(Person.format.format(person.getBirthDate()));

        return rootView;
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



}
