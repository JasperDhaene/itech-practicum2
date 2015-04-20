package be.thalarion.eventman.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;

public class DateDialogFragment extends android.support.v4.app.DialogFragment
                                                    implements DatePickerDialog.OnDateSetListener {


    private EditDialogFragment datePickerListener;
    private View target;
    private Event event;


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
        final Calendar c = Calendar.getInstance();

        String date = ((TextView) target).getText().toString();

        //date has a guaranteed yyyy-MM-dd format
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