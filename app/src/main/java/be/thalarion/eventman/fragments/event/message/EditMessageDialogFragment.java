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

import be.thalarion.eventman.R;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
import be.thalarion.eventman.fragments.EditDialogFragment;
import be.thalarion.eventman.models.Message;
import be.thalarion.eventman.models.Model;


public class EditMessageDialogFragment extends EditDialogFragment {

    private Message message;
    private EditText text;
    private TextView save;

    public EditMessageDialogFragment() {
        // Required empty public constructor
    }
    //TODO: first
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
        final View rootView = inflater.inflate(R.layout.fragment_edit_message_dialog, container, false);
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
        this.save = (TextView) rootView.findViewById(R.id.save);

        Bundle data = null;
        data = this.getArguments();

        if (data.getSerializable("action") == Model.ACTION.EDIT) {
            this.text.setText(message.getText());
        } else if (data.getSerializable("action") == Model.ACTION.NEW) {
            message = new Message();
        }
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
