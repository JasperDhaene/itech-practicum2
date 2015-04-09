package be.thalarion.eventman.fragments;

import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public abstract class EditDialogFragment extends DialogFragment {

    public void onDateSet(DatePicker view, View target, int year, int monthOfYear, int dayOfMonth) {
        ((TextView) target).setText(
                new StringBuilder().append(year).append("-")
                        .append(monthOfYear).append("-").append(dayOfMonth));
    }

    public void onTimeSet(TimePicker view, View target, int hourOfDay, int minute) {
        ((TextView) target).setText(new StringBuilder().append(hourOfDay).append(":").append(minute));

    }

}
