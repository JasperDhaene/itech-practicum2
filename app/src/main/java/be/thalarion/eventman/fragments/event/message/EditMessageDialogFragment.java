package be.thalarion.eventman.fragments.event.message;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;

import be.thalarion.eventman.MainActivity;
import be.thalarion.eventman.R;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.events.BusEvent;
import be.thalarion.eventman.events.MessageBusEvent;
import be.thalarion.eventman.fragments.EditDialogFragment;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Message;
import be.thalarion.eventman.models.Model;
import de.greenrobot.event.EventBus;


public class EditMessageDialogFragment extends EditDialogFragment {

    private Message message;
    private Event event;
    private EditText text;

    public EditMessageDialogFragment() {
        // Required empty public constructor
    }

    public static EditMessageDialogFragment newInstance(URI messageUri, URI eventUri, Model.ACTION action) {

        EditMessageDialogFragment f = new EditMessageDialogFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable("messageUri", messageUri);
        bundle.putSerializable("eventUri", eventUri);
        bundle.putSerializable("action", action);

        f.setArguments(bundle);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_edit_message_dialog, container, false);

        final Context context = getActivity();
        final Bundle data = getArguments();
        new AsyncTask<Void, Exception, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    event = Cache.find(Event.class, (URI) data.getSerializable("eventUri"));
                    if (data.getSerializable("messageUri") != null)
                        for (Message m: event.getMessages()) {
                            if (m.equals((URI) data.getSerializable("messageUri"))) {
                                Log.e("eventman", "Message found: " + m.getText());
                                message = m;
                                break;
                            }
                        }
                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void param) {
                if (data.getSerializable("action") == Model.ACTION.EDIT) {
                    text.setText(message.getText());
                } else if (data.getSerializable("action") == Model.ACTION.NEW) {
                    message = new Message(null, null, event);
                }
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }
        }.execute();

        this.text = (EditText) rootView.findViewById(R.id.field_message);

        rootView.findViewById(R.id.save).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AsyncTask<Void, Void, Exception>() {
                    private Context context;

                    @Override
                    protected void onPreExecute() {
                        this.context = v.getContext();
                    }

                    @Override
                    protected Exception doInBackground(Void... params) {
                        try {
                            // New Message
                            if (message.getPerson() == null) {
                                message.setPerson(((MainActivity) getActivity()).getAccountManager().getPerson());
                                EventBus.getDefault().post(new MessageBusEvent(message, BusEvent.ACTION.CREATE));
                            } else EventBus.getDefault().post(new MessageBusEvent(message, BusEvent.ACTION.UPDATE));

                            message.setText(text.getText().toString());
                            message.syncModelToNetwork();
                        } catch (APIException | IOException e) {
                            return e;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Exception e) {
                        if (e == null) {
                            Toast.makeText(this.context, this.context.getResources().getText(R.string.info_text_edit), Toast.LENGTH_LONG).show();
                            dismiss();
                        } else ErrorHandler.announce(this.context, e);
                    }
                }.execute();
            }
        });

        return rootView;
    }

}
