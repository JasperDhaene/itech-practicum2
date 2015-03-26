package be.thalarion.eventman;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import be.thalarion.eventman.models.Event;


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
        getMenuInflater().inflate(R.menu.menu_show_event, menu);
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
