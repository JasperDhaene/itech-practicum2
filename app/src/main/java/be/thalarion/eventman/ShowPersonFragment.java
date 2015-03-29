package be.thalarion.eventman;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import be.thalarion.eventman.models.Person;


public class ShowPersonFragment extends android.support.v4.app.Fragment {

    private TextView name, email, birthDate;
    private ImageView avatar;
    private Person person;

    public ShowPersonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bundle data = getIntent().getExtras();
        //this.person = Parcels.unwrap(data.getParcelable("person"));

        Activity activity = this.getActivity();

        /*this.name = ((TextView) activity.findViewById(R.id.person_name));
        this.email = ((TextView) activity.findViewById(R.id.person_email));
        this.birthDate = ((TextView) activity.findViewById(R.id.person_birthdate));
        this.avatar = ((ImageView) activity.findViewById(R.id.person_avatar));*/

        // TODO: null-catching
       /* this.name.setText(person.getName());
        this.email.setText(person.getEmail());
        this.birthDate.setText(person.getBirthDate().toString());
        */

       // this.name.setText("Jasper");

       /* Picasso.with(activity)
                .load(person.getAvatar(getResources().getDimensionPixelSize(R.dimen.avatar_large)))
                .into(avatar);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_person, container, false);
        setHasOptionsMenu(true);

        Bundle data = getActivity().getIntent().getExtras();
        this.person = Parcels.unwrap(data.getParcelable("person"));

        this.name = ((TextView) rootView.findViewById(R.id.person_name));
        this.email = ((TextView) rootView.findViewById(R.id.person_email));
        this.birthDate = ((TextView) rootView.findViewById(R.id.person_birthdate));
        this.avatar = ((ImageView) rootView.findViewById(R.id.person_avatar));

        // TODO: null-catching
        this.name.setText(person.getName());
        this.email.setText(person.getEmail());
        this.birthDate.setText(person.getBirthDate().toString());


        Picasso.with(getActivity())
                .load(person.getAvatar(getResources().getDimensionPixelSize(R.dimen.avatar_large)))
                .into(avatar);

        return rootView;
    }

}
