package be.thalarion.eventman;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Person;


public class EditEventActivity extends ActionBarActivity {

    private Calendar calendar;
    private TextView startDateView,endDateView;
    private int year, month, day;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        startDateView = (TextView) findViewById(R.id.fld_startdate);
        endDateView = (TextView) findViewById(R.id.fld_enddate);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        Bundle data = getIntent().getExtras();
        if (data.getString("action").equals("edit")) {
            Event event = (Event) data.getParcelable("event");


            ((EditText) findViewById(R.id.fld_title)).setText(event.getTitle());
            ((EditText) findViewById(R.id.fld_description)).setText(event.getDescription());
            ((EditText) findViewById(R.id.fld_startdate)).setText(event.getStartDate().toString());
            ((EditText) findViewById(R.id.fld_enddate)).setText(event.getEndDate().toString());
        }

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: gemakkelijk dan een AsyncTask, right?
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            String title = ((EditText) findViewById(R.id.fld_title)).getText().toString();
                            String description = ((EditText) findViewById(R.id.fld_description)).getText().toString();
                            String dateString = ((EditText) findViewById(R.id.fld_startdate)).getText().toString();
                            Date startdate = null;
                            Date enddate = null;
                            try {
                                startdate = format.parse(dateString);
                                dateString = ((EditText) findViewById(R.id.fld_enddate)).getText().toString();
                                enddate = format.parse(dateString);
                            } catch (ParseException e) {
                                e.printStackTrace();//TODO: give more meaningful errors. With Toast preferably. I <3 Toast!
                            }

                            Bundle data = getIntent().getExtras();
                            if (data != null) {
                                if (data.getString("action").equals("edit")) {
                                    Event event = (Event) data.getParcelable("event");
                                    event.setTitle(title);
                                    event.setDescription(description);
                                    event.setStartDate(startdate);
                                    event.setEndDate(enddate);
                                } else {
                                    if (title != "") {
                                        new Event(title, description, startdate, enddate);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (APIException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

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
