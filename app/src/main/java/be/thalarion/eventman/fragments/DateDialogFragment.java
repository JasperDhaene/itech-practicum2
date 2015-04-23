package be.thalarion.eventman.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

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
    public @NonNull Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        String date = ((TextView) target).getText().toString();

        // Date has a guaranteed yyyy-MM-dd format
        c.set(Integer.parseInt(date.substring(0,4)),
                Integer.parseInt(date.substring(5,7)),
                Integer.parseInt(date.substring(8,10))
        );

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

    public void setDatePickerListener(EditDialogFragment listener) {
        this.datePickerListener = listener;
    }

    protected void notifyDatePickerListener(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (this.datePickerListener != null)
            this.datePickerListener.onDateSet(view, this.target, year, monthOfYear + 1, dayOfMonth);
    }

    public void setTarget(View target) {
        this.target = target;
    }

}