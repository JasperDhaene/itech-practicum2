package be.thalarion.eventman.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimeDialogFragment extends android.support.v4.app.DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private EditDialogFragment timePickerListener;
    private View target;

    public TimeDialogFragment() {
        // Required empty public constructor
    }

    public static TimeDialogFragment newInstance(EditDialogFragment listener, View v) {

        TimeDialogFragment fragment = new TimeDialogFragment();

        fragment.setTimePickerListener(listener);
        fragment.setTarget(v);


        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        String date = ((TextView) target).getText().toString();
        c.set(Calendar.HOUR,Integer.parseInt(date.substring(0,2)));
        c.set(Calendar.MINUTE,Integer.parseInt(date.substring(3,5)));

        TimePickerDialog timePicker = new TimePickerDialog(
                getActivity(),
                this,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getActivity()));

        return timePicker;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        notifyTimePickerListener(view, hourOfDay, minute);
    }

    public EditDialogFragment getTimePickerListener() {
        return this.timePickerListener;
    }

    public void setTimePickerListener(EditDialogFragment listener) {
        this.timePickerListener = listener;
    }

    protected void notifyTimePickerListener(TimePicker view, int hourOfDay, int minute) {
        if(this.timePickerListener != null)
            this.timePickerListener.onTimeSet(view, this.target, hourOfDay, minute);
    }

    public void setTarget(View target) {
        this.target = target;
    }

    public View getTarget() {
        return target;
    }
}
