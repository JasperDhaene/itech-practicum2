package be.thalarion.eventman;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.parceler.Parcels;

import java.io.IOException;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.models.Model;
import be.thalarion.eventman.models.Person;


public class ShowPersonActivity extends ActionBarActivity {
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_person);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ShowPersonFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Bundle data = getIntent().getExtras();
        person = Parcels.unwrap(data.getParcelable("person"));

        Intent intent;
        switch(item.getItemId()){
            case R.id.action_edit_person:
                intent = new Intent(this,EditPersonActivity.class);
                intent.putExtra("person", Parcels.wrap(person));
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
                            Toast.makeText(getApplicationContext(), getResources().getText(R.string.info_text_destroy), Toast.LENGTH_LONG).show();
                        } else ErrorHandler.announce(getApplicationContext(), e);
                    }
                }.execute();
//TODO: load the peopleFragment here
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                return false;
        }
        return true;
    }
}
