package be.thalarion.eventman;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import java.io.IOException;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;


public class ShowEventActivity extends ActionBarActivity {

    private TextView title, description, startDate, endDate;
    private ImageView banner; //TODO: vul de banner in. Geen idee hoe dit gedaan wordt momenteel.
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Bundle data = getIntent().getExtras();
        this.event = Parcels.unwrap(data.getParcelable("event"));

        this.title = ((TextView) this.findViewById(R.id.event_title));
        this.description = ((TextView) this.findViewById(R.id.event_description));
        this.startDate = ((TextView) this.findViewById(R.id.event_startdate));
        this.endDate = ((TextView) this.findViewById(R.id.event_enddate));
        //TODO: banner invullen

        // TODO: null-catching
        this.title.setText(event.getTitle());
        this.description.setText(event.getDescription());
        this.startDate.setText(event.getStartDate().toString());
        this.endDate.setText(event.getEndDate().toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_edit_event:

                intent = new Intent(this, EditEventActivity.class);
                intent.putExtra("event", Parcels.wrap(this.event));
                intent.putExtra("action", Model.ACTION.EDIT);
                startActivity(intent);

                break;
            case R.id.action_discard_event:

                // TODO: DestroyModelTask
                new AsyncTask<Void, Void, Exception>() {
                    @Override
                    protected Exception doInBackground(Void... params) {
                        try {
                            event.destroy();
                            // Allow garbage collection
                            event = null;
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

                //TODO: load the eventFragment here
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                break;
            default:
                return false;
        }
        return true;
    }
}
