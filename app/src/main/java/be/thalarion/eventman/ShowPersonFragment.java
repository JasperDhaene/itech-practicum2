package be.thalarion.eventman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

        ImageLoader.getInstance().loadImage(this.person.getAvatar(Person.AVATAR.LARGE), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                avatar.setImageBitmap(loadedImage);
            }
        });

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

                EditPersonDialogFragment editPersonFrag = EditPersonDialogFragment.newInstance(this.person,Model.ACTION.EDIT);

                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(editPersonFrag,this.getActivity().getResources().getString(R.string.title_edit_person));

                break;
            case R.id.action_discard_person:
                new AsyncTask<Void, Void, Exception>() {
                    private Context context;

                    @Override
                    protected void onPreExecute() {
                        this.context = getActivity();
                    }

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
                            Toast.makeText(this.context, this.context.getResources().getText(R.string.info_text_destroy), Toast.LENGTH_LONG).show();
                        } else ErrorHandler.announce(this.context, e);
                    }
                }.execute();

                PeopleFragment peopleFrag = new PeopleFragment();

                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(peopleFrag,this.getActivity().getResources().getString(R.string.title_people));

                break;
            /*case R.id.action_login:
                ((MainActivity)getActivity()).getAccountManager().setAccount(this.person);
                Toast.makeText(getActivity(), String.format(getString(R.string.info_text_login), person.getFormattedName(getActivity())), Toast.LENGTH_SHORT).show();
                break;*/
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

}
