package be.thalarion.eventman;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DateDialogFragment extends android.support.v4.app.DialogFragment
                                                    implements DatePickerDialog.OnDateSetListener {


    private EditDialogFragment datePickerListener;
    private View target;


    public DateDialogFragment() {
        // Required empty public constructor
    }

    public static DateDialogFragment newInstance(EditDialogFragment listener,View v) {

        DateDialogFragment f = new DateDialogFragment();

        f.setDatePickerListener(listener);
        f.setTarget(v);

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

    public EditDialogFragment getDatePickerListener() {
        return this.datePickerListener;
    }

    public void setDatePickerListener(EditDialogFragment listener) {
        this.datePickerListener = listener;
    }

    protected void notifyDatePickerListener(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(this.datePickerListener != null) {
            this.datePickerListener.onDateSet(view,this.target,year,monthOfYear,dayOfMonth);
        }
    }

    public View getTarget() {
        return target;
    }

    public void setTarget(View target) {
        this.target = target;
    }
}