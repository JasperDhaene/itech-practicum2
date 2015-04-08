package be.thalarion.eventman.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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

import java.util.ArrayList;
import java.util.List;

import be.thalarion.eventman.MainActivity;
import be.thalarion.eventman.R;
import be.thalarion.eventman.ShowPersonFragment;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Message;
import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by jasper on 08/04/15.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<Message> dataSet;
    private Context context;

    public MessagesAdapter(Context context) {
        this.context = context;
        this.dataSet = new ArrayList<>();
    }

    public void setDataSet(List<Message> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text,date;

        public ImageView avatar;
        public LinearLayout container;
        public Message message;

        public ViewHolder(View itemView) {
            super(itemView);
            this.text = ((TextView) itemView.findViewById(R.id.message_list_view_text));
            this.avatar = ((ImageView) itemView.findViewById(R.id.message_list_view_avatar));
            this.date = ((TextView) itemView.findViewById(R.id.message_list_view_date));

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
