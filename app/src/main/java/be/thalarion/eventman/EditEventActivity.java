package be.thalarion.eventman;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;
import be.thalarion.eventman.models.Person;


public class EditEventActivity extends ActionBarActivity {

    private Calendar calendar;
    private TextView startDateView,endDateView;
    private int year, month, day;
    private Button save;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        this.startDateView = (TextView) findViewById(R.id.field_start_date);
        this.endDateView = (TextView) findViewById(R.id.field_end_date);
        this.calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, (month + 1), day);

        Bundle data = getIntent().getExtras();
        if (data.get("action") == Model.ACTION.EDIT) {
            this.event = Parcels.unwrap(data.getParcelable("event"));

            // TODO: null-catching
            ((EditText) findViewById(R.id.field_title)).setText(event.getTitle());
            ((EditText) findViewById(R.id.field_description)).setText(event.getDescription());
            ((EditText) findViewById(R.id.field_start_date)).setText(Event.format.format(event.getStartDate()));
            ((EditText) findViewById(R.id.field_end_date)).setText(Event.format.format(event.getEndDate()));
        }

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Exception>() {

                    @Override
                    protected Exception doInBackground(Void... params) {
                        String title = ((EditText) findViewById(R.id.field_title)).getText().toString();
                        String description = ((EditText) findViewById(R.id.field_description)).getText().toString();
                        Date startDate, endDate;
                        try {
                            startDate = Event.format.parse(((EditText) findViewById(R.id.field_start_date)).getText().toString());
                            endDate = Event.format.parse(((EditText) findViewById(R.id.field_end_date)).getText().toString());
                        } catch (ParseException e) {
                            return e;
                        }

                        if (event != null) {
                            // Update existing event
                            event.setTitle(title);
                            event.setDescription(description);
                            event.setStartDate(startDate);
                            event.setEndDate(endDate);
                        } else {
                            // Create new event
                            event = new Event(title, description, startDate, endDate);
                        }

                        try {
                            event.syncModelToNetwork();
                        } catch (IOException | APIException e) {
                            return e;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Exception e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), getResources().getText(R.string.info_text_edit), Toast.LENGTH_LONG).show();
                        } else
                            ErrorHandler.announce(getApplicationContext(), e);
                    }
                }.execute();

                //TODO: load the eventFragment here
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //TODO: No options in menubar for now. Make this a dummy method?

        return super.onOptionsItemSelected(item);
    }

    public void setDate(View view) {
        showDialog(666); // because the beast!
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 666) {// as seen on http://www.tutorialspoint.com/android/android_datepicker_control.htm
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        startDateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
        endDateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
        //TODO first: this will will changes both dates in the view, but the data only records the actual change. Search for a better way to show Dates when not busy thinking about cats.
    }
}
