package be.thalarion.eventman.fragments.event.message;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import be.thalarion.eventman.R;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.models.Message;
import be.thalarion.eventman.models.Model;


public class MessageDialogFragment extends DialogFragment {

    private Message message;
    private TextView text;


    public MessageDialogFragment() {
        // Required empty public constructor
    }

    public static MessageDialogFragment newInstance(String text,String date) {

        MessageDialogFragment f = new MessageDialogFragment();
        Bundle bundle = new Bundle();

        bundle.putString("text", text);
        bundle.putString("date", date);

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