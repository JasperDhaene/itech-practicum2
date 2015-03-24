package be.thalarion.eventman;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import be.thalarion.eventman.R;
import be.thalarion.eventman.models.Person;
import fr.tkeunebr.gravatar.Gravatar;

public class ShowPersonActivity extends ActionBarActivity {

    public TextView name;
    public TextView email;
    public TextView birthdate;
    public ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_person);

        Bundle data = getIntent().getExtras();
        Person person = (Person) data.getParcelable("person");

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
        //this.avatar.setImageDrawable();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
