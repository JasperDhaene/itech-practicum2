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
import be.thalarion.eventman.models.Person;


public class EditPersonActivity extends ActionBarActivity {

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);
        dateView = (TextView) findViewById(R.id.fld_birthdate);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //TODO: gemakkelijk dan een AsyncTask, right?
            new Thread(new Runnable() {
                        public void run() {
                try {
                    String name = ((EditText) findViewById(R.id.fld_name)).getText().toString();
                    String email = ((EditText) findViewById(R.id.fld_email)).getText().toString();
                    String birthdate = ((EditText) findViewById(R.id.fld_birthdate)).getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
                    Date date;
                    try {
                        date = format.parse(birthdate);
                    } catch (ParseException e) {
                        date = new Date();
                    }

                    if(name!=null) {
                        Person person = new Person(name, email, date);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (APIException e) {
                    e.printStackTrace();
                }
                }
            }).start();

                //TODO: load the peopleFragment here
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_person, menu);
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

    public void setDate(View view){
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
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
}
