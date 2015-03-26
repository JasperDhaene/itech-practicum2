package be.thalarion.eventman;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.models.Model;
import be.thalarion.eventman.models.Person;


public class EditPersonActivity extends ActionBarActivity {

    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private Button save;

    private Person p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);

        this.dateView = (TextView) findViewById(R.id.field_birth_date);
        this.calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, (month + 1), day);

        Bundle data = getIntent().getExtras();
        if(data.getSerializable("action") == Model.ACTION.EDIT){
            this.p = Parcels.unwrap(data.getParcelable("person"));

            // TODO: null-catching
            ((EditText) findViewById(R.id.field_name)).setText(p.getName());
            ((EditText) findViewById(R.id.field_email)).setText(p.getEmail());
            ((EditText) findViewById(R.id.field_birth_date)).setText(Person.format.format(p.getBirthDate()));
        }

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Exception>() {

                    @Override
                    protected Exception doInBackground(Void... params) {
                        String name = ((EditText) findViewById(R.id.field_name)).getText().toString();
                        String email = ((EditText) findViewById(R.id.field_email)).getText().toString();
                        Date birthDate;
                        try {
                            birthDate = Person.format.parse(((EditText) findViewById(R.id.field_birth_date)).getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return e;
                        }

                        try {
                            if (p != null) {
                                // Update existing person
                                p.setName(name);
                                p.setEmail(email);
                                p.setBirthDate(birthDate);
                            } else {
                                // Create new person
                                p = new Person(name, email, birthDate);
                            }
                        } catch (IOException | APIException e) {
                            e.printStackTrace();
                            return e;
                        }
                            return null;
                    }

                    @Override
                    protected void onPostExecute(Exception e) {
                        if(e == null) {
                            Toast.makeText(getApplicationContext(), getResources().getText(R.string.info_text_edit), Toast.LENGTH_LONG).show();
                        } else Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }.execute();

                //TODO: load the peopleFragment here
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.person_edit, menu);
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
        // Kom, kom, we gaan hier met ISO8601 werken hé.
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
        dateView.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }
}
