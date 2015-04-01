package be.thalarion.eventman;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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

    //todo: REMOVE
    public ShowPersonFragment(Person person) {
        this.person = person;
    }

    public static ShowPersonFragment newInstance(Person person) {
        ShowPersonFragment f = new ShowPersonFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("person",Parcels.wrap(person));

        f.setArguments(bundle);

        return f;
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
                .load(this.person.getAvatar(getResources().getDimensionPixelSize(R.dimen.avatar_large)))
                .into(this.avatar);

        //((MaterialNavigationDrawer) getActivity()).

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

                showDialog();

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

                PeopleFragment f = new PeopleFragment();

                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(f,this.getActivity().getResources().getString(R.string.title_people));

                break;
            case R.id.action_login:
                MaterialAccount account = ((MaterialNavigationDrawer) getActivity()).getCurrentAccount();
                // TODO: null-catching
                account.setTitle(this.person.getName());
                account.setSubTitle(this.person.getEmail());
                ((MaterialNavigationDrawer) getActivity()).notifyAccountDataChanged();
                break;
            default:
                return false;
        }
        return true;
    }

    private void showToast(String message) {

        Toast mToast = new Toast(this.getActivity());
        mToast.setText( message);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    private View positiveAction;

    void showDialog() {
        //DialogFragment newFragment = new EditPersonDialogFragment();
        //newFragment.show(getFragmentManager(), "dialog");

        /*getFragmentManager().beginTransaction()
                .add(R.id.container, new EditPersonDialogFragment())
                .addToBackStack(null).commit();*/

        EditPersonDialogFragment f = EditPersonDialogFragment.newInstance(this.person,Model.ACTION.EDIT);

        ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(f,this.getActivity().getResources().getString(R.string.title_edit_person));
/*
        MaterialDialog dialog = new MaterialDialog.Builder(this.getActivity())
                .title(R.string.title_activity_edit_person)
                .customView(R.layout.fragment_edit_person_dialog,false)
                .positiveText(R.string.save)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //showToast("Password: " + passwordInput.getText().toString());
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);


        //TODO: zorg dat 'save' enkel zichtbaar wordt nadat er iets ingetyped is.
        //alles wat niet ingetyped is moet blijven zoals het was. We doen niet mee aan null-submitting!

        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.show();
        positiveAction.setEnabled(false); // disabled by default*/
    }


}
