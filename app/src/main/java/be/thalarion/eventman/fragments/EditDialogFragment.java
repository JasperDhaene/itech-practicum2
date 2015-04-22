package be.thalarion.eventman.fragments;

import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public abstract class EditDialogFragment extends DialogFragment {

    public void onDateSet(DatePicker view, View target, int year, int monthOfYear, int dayOfMonth) {
        StringBuilder builder = new StringBuilder().append(year)
                .append("-");
        if(monthOfYear<10){
            builder.append(0)
                    .append(monthOfYear);
        }else{
            builder.append(monthOfYear);
        }
        builder.append("-");
        if(dayOfMonth<10){
            builder.append(0)
                    .append(dayOfMonth);
        }else{
            builder.append(dayOfMonth);
        }
        ((TextView) target).setText(builder);
    }

    public void onTimeSet(TimePicker view, View target, int hourOfDay, int minute) {
        ((TextView) target).setText(new StringBuilder().append(hourOfDay).append(":").append(minute));

    }

}
