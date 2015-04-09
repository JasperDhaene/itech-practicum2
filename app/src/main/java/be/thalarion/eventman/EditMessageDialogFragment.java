package be.thalarion.eventman;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;
import java.net.URL;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.cache.Cache;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Message;
import be.thalarion.eventman.models.Model;
import be.thalarion.eventman.models.Person;


public class EditMessageDialogFragment extends EditDialogFragment {

    private Message message;
    private EditText text;

    public EditMessageDialogFragment() {
        // Required empty public constructor
    }

    public EditMessageDialogFragment(Message message){
        this.message = message;
        Bundle bundle = new Bundle();


        //always edit for now.
        bundle.putSerializable("action", Model.ACTION.EDIT);
        this.setArguments(bundle);
    }

    public static EditMessageDialogFragment newInstance(String url, Model.ACTION action) {

        EditMessageDialogFragment f = new EditMessageDialogFragment();
        Bundle bundle = new Bundle();
        /*if (!url.equals("")) {
            bundle.putString("url",url);
        }*/


        bundle.putSerializable("action", action);

        f.setArguments(bundle);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_message_dialog, container, false);
        /*
        final Context context = getActivity();
        new AsyncTask<Bundle, Exception, Message>() {
            private Bundle data = null;

            @Override
            protected Message doInBackground(Bundle... params) {
                Message mess = null;
                this.data = params[0];
                try {
                    // TODO: retrieve message via url
                    String s = this.data.getString("url");
                    mess = Cache.find(Message.class, new URL(s));

                } catch (IOException | APIException e) {
                    publishProgress(e);
                }
                return mess;
            }

            @Override
            protected void onPostExecute(Message mess) {
                message = mess;
                if (this.data.getSerializable("action") == Model.ACTION.EDIT) {

                } else if (this.data.getSerializable("action") == Model.ACTION.NEW) {
                    message = new Message();
                }
            }

            @Override
            protected void onProgressUpdate(Exception... values) {
                ErrorHandler.announce(context, values[0]);
            }
        }.execute(getArguments());*/

        this.text = (EditText) rootView.findViewById(R.id.field_message);

        Bundle data = null;
        data = this.getArguments();

        if (data.getSerializable("action") == Model.ACTION.EDIT) {
            this.text.setText(message.getText());
        } else if (data.getSerializable("action") == Model.ACTION.NEW) {
            message = new Message();
        }


        return rootView;
    }


}
