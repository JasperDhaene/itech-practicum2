package be.thalarion.eventman;


import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeDialogFragment extends android.support.v4.app.DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private EditDialogFragment timePickerListener;
    private View target;

    public TimeDialogFragment() {
        // Required empty public constructor
    }

    public static TimeDialogFragment newInstance(EditDialogFragment listener, View v) {

        TimeDialogFragment f = new TimeDialogFragment();

        f.setTimePickerListener(listener);
        f.setTarget(v);


        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(getActivity(), this, hour, minute,
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
        if(this.timePickerListener != null) {
            this.timePickerListener.onTimeSet(view,this.target, hourOfDay, minute);
        }
    }

    public void setTarget(View target) {
        this.target = target;
    }

    public View getTarget() {
        return target;
    }
}
