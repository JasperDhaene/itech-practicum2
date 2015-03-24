package be.thalarion.eventman.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.eventman.R;
import be.thalarion.eventman.models.Event;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<Event> dataSet;
    private Context context;

    public EventsAdapter(Context context) {
        this.context = context;
        this.dataSet = new ArrayList<>();
    }

    public void setDataSet(List<Event> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView description;
        public ImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = ((TextView) itemView.findViewById(R.id.event_list_view_title));
            this.description = ((TextView) itemView.findViewById(R.id.event_list_view_description));
            this.avatar = ((ImageView) itemView.findViewById(R.id.event_list_view_avatar));
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(dataSet.get(position).getTitle());
        holder.description.setText(dataSet.get(position).getDescription());
        String color = stringHash(dataSet.get(position).getTitle());
        TextDrawable drawable = TextDrawable.builder().buildRect(color, context.getResources().getColor(colorHash(color)));
        holder.avatar.setImageDrawable(drawable);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(dataSet == null) return 0;
        return dataSet.size();
    }

    private String stringHash(String key) {
        String[] split = key.split(" ");
        if(split.length >= 2) {
            return split[0].substring(0, 1).toUpperCase() + split[1].substring(0, 1).toLowerCase();
        } else return split[0].substring(0, 1).toUpperCase();
    }

    private static int[] colors = {
        R.color.md_red,
        R.color.md_pink,
        R.color.md_purple,
        R.color.md_indigo,
        R.color.md_blue,
        R.color.md_green,
        R.color.md_lime,
        R.color.md_yellow,
        R.color.md_amber,
        R.color.md_deep_orange
    };

    private int colorHash(String key) {
        int code = key.charAt(0);
        if(key.length() >= 2)
            code += key.charAt(1);

        code = code % colors.length;
        return colors[code];
    }

}
