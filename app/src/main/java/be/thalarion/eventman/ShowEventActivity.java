package be.thalarion.eventman;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;


public class ShowEventActivity extends ActionBarActivity {

    public TextView title;
    public TextView description;
    public TextView startDate;
    public TextView endDate;
    public ImageView banner; //TODO: vul de banner in. Geen idee hoe dit gedaan wordt momenteel.
    public Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Bundle data = getIntent().getExtras();
        this.event = (Event) data.getParcelable("event");

        this.title = ((TextView) this.findViewById(R.id.event_title));
        this.description = ((TextView) this.findViewById(R.id.event_description));
        this.startDate = ((TextView) this.findViewById(R.id.event_startdate));
        this.endDate = ((TextView) this.findViewById(R.id.event_enddate));
        //TODO: banner invullen

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
                //TODO:change this so that it goes to 'edit' instead of 'new' person
                intent = new Intent(this, EditEventActivity.class);
                intent.putExtra("event", this.event);
                intent.putExtra("action", "edit");
                startActivity(intent);

                break;
            case R.id.action_discard_event:

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            ((Model) event).destroy();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (APIException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

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
