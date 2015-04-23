package be.thalarion.eventman.fragments.event.message;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.thalarion.eventman.R;
import be.thalarion.eventman.models.Message;


public class MessageDialogFragment extends DialogFragment {

    public MessageDialogFragment() {
        // Required empty public constructor
    }

    public static MessageDialogFragment newInstance(Message message) {

        MessageDialogFragment f = new MessageDialogFragment();
        Bundle bundle = new Bundle();

        bundle.putString("text", message.getText());
        bundle.putString("date", Message.formatReadable.format(message.getDate()));

        f.setArguments(bundle);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_message_dialog, container, false);

        Bundle data = getArguments();
        ((TextView) rootView.findViewById(R.id.text)).setText(data.getString("text"));
        ((TextView) rootView.findViewById(R.id.date)).setText(data.getString("date"));

        return rootView;
    }

}
