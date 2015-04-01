package be.thalarion.eventman;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import org.parceler.Parcels;

import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditEventDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, View.OnClickListener {


    public EditEventDialogFragment() {
        // Required empty public constructor
    }

    public static EditEventDialogFragment newInstance(Event event, Model.ACTION action) {

        EditEventDialogFragment f = new EditEventDialogFragment();
        Bundle bundle = new Bundle();
        if (event != null) {
            bundle.putParcelable("event", Parcels.wrap(event));
        }

        bundle.putSerializable("action", action);

        f.setArguments(bundle);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_event_dialog, container, false);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //TODO from which textView did this come?
        ((TextView) this.getView().findViewById(R.id.field_start_date)).setText(
                new StringBuilder().append(year).append("-")
                        .append(monthOfYear).append("-").append(dayOfMonth));
    }

    @Override
    public void onClick(View v) { // Parameter v stands for the view that was clicked.
        DialogFragment f = DateDialogFragment.newInstance(this);

        f.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}
