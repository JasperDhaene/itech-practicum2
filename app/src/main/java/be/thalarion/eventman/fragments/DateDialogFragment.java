package be.thalarion.eventman.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

public class DateDialogFragment extends android.support.v4.app.DialogFragment
                                                    implements DatePickerDialog.OnDateSetListener {


    private EditDialogFragment datePickerListener;
    private View target;


    public DateDialogFragment() {
        // Required empty public constructor
    }

    public static DateDialogFragment newInstance(EditDialogFragment listener, View v) {
        DateDialogFragment fragment = new DateDialogFragment();

        fragment.setDatePickerListener(listener);
        fragment.setTarget(v);

        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO: fill in existing date instead of current date
        final Calendar c = Calendar.getInstance();

        return new DatePickerDialog(
                getActivity(),
                this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        notifyDatePickerListener(view, year, monthOfYear, dayOfMonth);
    }

    public EditDialogFragment getDatePickerListener() {
        return this.datePickerListener;
    }

    public void setDatePickerListener(EditDialogFragment listener) {
        this.datePickerListener = listener;
    }

    protected void notifyDatePickerListener(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(this.datePickerListener != null)
            this.datePickerListener.onDateSet(view, this.target, year, monthOfYear + 1, dayOfMonth);
    }

    public void setTarget(View target) {
        this.target = target;
    }

    public View getTarget() {
        return target;
    }

}