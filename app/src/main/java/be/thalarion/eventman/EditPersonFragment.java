package be.thalarion.eventman;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import be.thalarion.eventman.models.Person;

public class EditPersonFragment extends android.support.v4.app.Fragment {

    private TextView name, email, birthDate;
    private ImageView avatar;
    private Person person;


    public EditPersonFragment() {
        // Required empty public constructor
    }

    public EditPersonFragment(Person person) {
        this.person = person;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_person, container, false);
        setHasOptionsMenu(true);

        Activity activity = this.getActivity();



        ((EditText) rootView.findViewById(R.id.field_name)).setHint("Jasper");

        /*this.name = (TextView) rootView.findViewById(R.id.person_name);
        this.email = (TextView) rootView.findViewById(R.id.person_email);
        this.birthDate = (TextView) rootView.findViewById(R.id.person_birthdate);
        this.avatar = (ImageView) rootView.findViewById(R.id.person_avatar);

        if(this.person.getName() != null)
            this.name.setText(person.getName());
        else
            this.name.setText(R.string.error_text_noname);

        if(this.person.getEmail() != null)
            this.email.setText(person.getEmail());
        else
            this.email.setText(R.string.error_text_noemail);

        if(this.person.getBirthDate() != null)
            this.birthDate.setText(Person.format.format(person.getBirthDate()));
        else
            this.birthDate.setText(R.string.error_text_nobirthdate);
            */



        return rootView;
    }



}
