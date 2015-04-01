package be.thalarion.eventman;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class DateDialogFragment extends android.support.v4.app.DialogFragment
                                                    implements DatePickerDialog.OnDateSetListener {


    private DatePickerDialog.OnDateSetListener datePickerListener;
    /*public interface DatePickerFragmentListener {
        public void onDateSet(Date date);
    }*/


    public DateDialogFragment() {
        // Required empty public constructor
    }

    public static DateDialogFragment newInstance(DatePickerDialog.OnDateSetListener listener) {

        DateDialogFragment f = new DateDialogFragment();

        f.setDatePickerListener(listener);


        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);



        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        notifyDatePickerListener(view,year,monthOfYear,dayOfMonth);
    }

    public DatePickerDialog.OnDateSetListener getDatePickerListener() {
        return this.datePickerListener;
    }

    public void setDatePickerListener(DatePickerDialog.OnDateSetListener listener) {
        this.datePickerListener = listener;
    }

    protected void notifyDatePickerListener(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(this.datePickerListener != null) {
            this.datePickerListener.onDateSet(view,year,monthOfYear,dayOfMonth);
        }
    }
}