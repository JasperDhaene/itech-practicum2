package be.thalarion.eventman;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.IOException;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.models.Model;
import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;


public class ShowPersonFragment extends android.support.v4.app.Fragment {

    private TextView name, email, birthDate;
    private ImageView avatar;
    private Person person;

    public ShowPersonFragment() {
        // Required empty public constructor
    }

    public ShowPersonFragment(Person person) {
        this.person = person;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_person, container, false);
        setHasOptionsMenu(true);

        this.name = (TextView) rootView.findViewById(R.id.person_name);
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

        // TODO: what happens to avatars if email is null?
        Picasso.with(rootView.getContext())
                .load(this.person.getAvatar(Person.AVATAR.LARGE))
                .into(this.avatar);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.person, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.action_edit_person:
                intent = new Intent(getActivity(), EditPersonActivity.class);
                intent.putExtra("person", Parcels.wrap(this.person));
                intent.putExtra("action", Model.ACTION.EDIT);
                startActivity(intent);
                break;
            case R.id.action_discard_person:
                new AsyncTask<Void, Void, Exception>() {
                    @Override
                    protected Exception doInBackground(Void... params) {
                        try {
                            person.destroy();
                            // Allow garbage collection
                            person = null;
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

                //TODO: load the peopleFragment here
                intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.action_login:
                ((MainActivity)getActivity()).getAccountManager().setAccount(this.person);
                Toast.makeText(getActivity(), String.format(getString(R.string.info_text_login), person.getFormattedName(getActivity())), Toast.LENGTH_SHORT).show();
                break;
            default:
                return false;
        }
        return true;
    }

}
