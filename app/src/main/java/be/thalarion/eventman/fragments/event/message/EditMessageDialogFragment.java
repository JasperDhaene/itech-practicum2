package be.thalarion.eventman.fragments.event.message;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;

import be.thalarion.eventman.MainActivity;
import be.thalarion.eventman.R;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.fragments.EditDialogFragment;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Message;
import be.thalarion.eventman.models.Model;


public class EditMessageDialogFragment extends EditDialogFragment {

    private Message message;
    private Event event;
    private EditText text;
    private TextView save;

    public EditMessageDialogFragment() {
        // Required empty public constructor
    }

    //TODO: delete in favour of finding messages in Cache
    public EditMessageDialogFragment(Message message) {
        this.message = message;
    }

    public static EditMessageDialogFragment newInstance(URI eventUri,Message message, Model.ACTION action) {

        EditMessageDialogFragment f = new EditMessageDialogFragment(message);
        Bundle bundle = new Bundle();

        bundle.putSerializable("eventURI", eventUri);
        //bundle.putSerializable("messageURI", messageUri);
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
        new AsyncTask<Bundle, Exception, Void>() {
            private Bundle data = null;

            @Override
            protected Void doInBackground(Bundle... params) {
                this.data = params[0];
                try {//TODO: werkt nog niet via URI
                    /*URI uri = (URI) this.data.getSerializable("messageURI");
                    message = Cache.find(Message.class, uri);*/
                    if(this.data.getSerializable("eventURI")!=null) {
                        event = Cache.find(Event.class, (URI) this.data.getSerializable("eventURI"));
                    }
                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void param) {
                if (this.data.getSerializable("action") == Model.ACTION.EDIT) {
                    text.setText(message.getText());
                } else if (this.data.getSerializable("action") == Model.ACTION.NEW) {
                    message = new Message(null,null,event);
                }
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }
        }.execute(getArguments());

        this.text = (EditText) rootView.findViewById(R.id.field_message);
        this.save = (TextView) rootView.findViewById(R.id.save);

        this.save.setOnClickListener(new OnClickListener(){
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
                            if(message.getPerson()==null){//new Message
                                message.setPerson(((MainActivity) getActivity()).getAccountManager().getPerson());
                            }

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
