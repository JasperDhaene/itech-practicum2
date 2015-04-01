package be.thalarion.eventman;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
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

        this.title = ((TextView) this.findViewById(R.id.event_title));
        this.description = ((TextView) this.findViewById(R.id.event_description));
        this.startDate = ((TextView) this.findViewById(R.id.event_startdate));
        this.endDate = ((TextView) this.findViewById(R.id.event_enddate));
        //TODO: banner invullen

        final Context context = getApplicationContext();
        new AsyncTask<Bundle, Exception, Event>() {
            @Override
            protected Event doInBackground(Bundle... params) {
                Event event = null;
                try {
                    event = Cache.find(Event.class, new URL(getIntent().getStringExtra("event")));
                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return event;
            }

            @Override
            protected void onPostExecute(Event ev) {
                event = ev;
                if(event.getTitle() != null)
                    title.setText(event.getTitle());
                else
                    title.setText(R.string.error_text_notitle);

                if(event.getDescription() != null)
                    description.setText(event.getDescription());
                else
                    title.setText(R.string.error_text_nodescription);

                if(event.getStartDate() != null)
                    startDate.setText(Event.format.format(event.getStartDate()));
                else
                    startDate.setText(R.string.error_text_nostartdate);

                if(event.getEndDate() != null)
                    endDate.setText(Event.format.format(event.getEndDate()));
                else
                    endDate.setText(R.string.error_text_noenddate);
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }
        }.execute(getIntent().getExtras());
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
                intent.putExtra("event", this.event.getResource().toString());
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
