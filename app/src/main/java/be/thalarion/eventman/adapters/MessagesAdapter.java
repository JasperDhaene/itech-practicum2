package be.thalarion.eventman.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import be.thalarion.eventman.fragments.event.message.EditMessageDialogFragment;

import be.thalarion.eventman.R;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;

import be.thalarion.eventman.fragments.event.message.MessageDialogFragment;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Message;
import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>{

    private List<Message> dataSet;
    private final Context context;

    public MessagesAdapter(Context context) {
        this.context = context;
        this.dataSet = new ArrayList<>();
    }

    public void setDataSet(List<Message> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView text,date;

        public ImageView avatar,iconEdit,iconDiscard;
        public LinearLayout container;
        public Message message;


        public ViewHolder(View itemView) {
            super(itemView);
            this.text = ((TextView) itemView.findViewById(R.id.message_list_view_text));
            this.avatar = ((ImageView) itemView.findViewById(R.id.message_list_view_avatar));
            this.date = ((TextView) itemView.findViewById(R.id.message_list_view_date));
            this.iconDiscard = ((ImageView) itemView.findViewById(R.id.list_item_icon_discard));
            this.iconEdit = ((ImageView) itemView.findViewById(R.id.list_item_icon_edit));


            this.iconEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toon een edit dialoog

                    /*EditMessageDialogFragment editMessageFrag = EditMessageDialogFragment.newInstance(
                            message.getResource().toString(), Model.ACTION.EDIT);*/

                    EditMessageDialogFragment editMessageFrag = new EditMessageDialogFragment(message);


                    editMessageFrag.show(((MaterialNavigationDrawer) v.getContext()).getSupportFragmentManager(),"editMessage");
                }
            });

            this.iconDiscard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, Exception>() {
                        private Context context;

                        @Override
                        protected void onPreExecute() {
                            this.context = iconDiscard.getContext();;
                        }

                        @Override
                        protected Exception doInBackground(Void... params) {
                            try {
                                message.destroy();
                                // Allow garbage collection
                                message = null;
                            } catch (APIException | IOException e) {
                                return e;
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Exception e) {
                            if (e == null) {
                                Toast.makeText(this.context, this.context.getResources().getText(R.string.info_text_destroy), Toast.LENGTH_LONG).show();
                                //TODO: Doe een refresh van de messages ??

                            } else ErrorHandler.announce(this.context, e);
                        }
                    }.execute();

                }
            });

            ((LinearLayout) itemView.findViewById(R.id.list_item_container)).setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            MessageDialogFragment showMessageFrag =  MessageDialogFragment.newInstance(message.getText());

            showMessageFrag.show(((MaterialNavigationDrawer) v.getContext()).getSupportFragmentManager(),"showMessage");
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(dataSet.get(position).getText() != null) {

            holder.text.setText(dataSet.get(position).getText());
        } else {

            holder.text.setText(R.string.error_text_noname);
        }

        if(dataSet.get(position).getPerson() != null) {
            ImageLoader.getInstance().loadImage(dataSet.get(position).getPerson().getAvatar(Person.AVATAR.THUMB), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.avatar.setImageBitmap(loadedImage);
                }
            });
            //TODO:what if person is null?
        }

        if(dataSet.get(position).getDate() != null) {

            holder.date.setText(Event.format.format(dataSet.get(position).getDate()).toString());
        }

        holder.message = dataSet.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(dataSet == null) return 0;
        return dataSet.size();
    }


}
