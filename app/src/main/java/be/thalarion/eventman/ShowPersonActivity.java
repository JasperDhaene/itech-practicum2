package be.thalarion.eventman;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.models.Model;
import be.thalarion.eventman.models.Person;

public class ShowPersonActivity extends ActionBarActivity {

    public TextView name;
    public TextView email;
    public TextView birthdate;
    public ImageView avatar;
    public Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_person);

        Bundle data = getIntent().getExtras();
        this.person = (Person) data.getParcelable("person");

        this.name = ((TextView) this.findViewById(R.id.person_name));
        this.email = ((TextView) this.findViewById(R.id.person_email));
        this.birthdate = ((TextView) this.findViewById(R.id.person_birthdate));
        this.avatar = ((ImageView) this.findViewById(R.id.person_avatar));

        this.name.setText(person.getName());
        this.email.setText(person.getEmail());
        this.birthdate.setText(person.getBirthDate().toString());


        Picasso.with(this)
                .load(person.getAvatar(666)) //Gravatar.MAX_IMAGE_SIZE_PIXEL
                .into(avatar);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.action_edit_person:
                //TODO:change this so that it goes to 'edit' instead of 'new' person
                intent = new Intent(this,EditPersonActivity.class);
                startActivity(intent);

                break;
            case R.id.action_discard_person:

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            System.out.println("trala");
                            ((Model) person).destroy();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (APIException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                //TODO first: load the peopleFragment here
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);




                break;
            default:
                return false;
        }
        return true;
    }
}
