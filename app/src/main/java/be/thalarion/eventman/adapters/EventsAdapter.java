package be.thalarion.eventman.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import be.thalarion.eventman.MainActivity;
import be.thalarion.eventman.R;
import be.thalarion.eventman.ShowEventFragment;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView description;
        public ImageView avatar;
        public Event event;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = ((TextView) itemView.findViewById(R.id.event_list_view_title));
            this.description = ((TextView) itemView.findViewById(R.id.event_list_view_description));
            this.avatar = ((ImageView) itemView.findViewById(R.id.event_list_view_avatar));
            this.checkBox = ((CheckBox) itemView.findViewById(R.id.list_item_checkbox));

            ((LinearLayout) itemView.findViewById(R.id.list_item_container)).setOnClickListener(this);

            this.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((CheckBox) v).isChecked()){
                        //TODO find out why app crashes at syncModelToNetwork
                        //variables are for debug purposes.
                        MainActivity a  = ((MainActivity)v.getContext());
                        Person p = ((MainActivity)v.getContext()).getAccountManager().getPerson();
                        event.confirm(p,true);
                        try {
                            event.syncModelToNetwork();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (APIException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }

        @Override
        public void onClick(View v) {

            ((MaterialNavigationDrawer) v.getContext()).setFragmentChild(
                    ShowEventFragment.newInstance(this.event),
                    v.getResources().getString(R.string.title_show_event)
            );
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_event, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String color;
        if(dataSet.get(position).getTitle() != null) {
            //holder.title.setTextAppearance(context, R.style.Title);
            holder.title.setText(dataSet.get(position).getTitle());

            color = Event.hash(dataSet.get(position).getTitle());
        } else {
            //holder.title.setTextAppearance(context, R.style.TitleMissing);
            holder.title.setText(R.string.error_text_notitle);

            color = Event.hash("Ev");
        }
        if(dataSet.get(position).getDescription() != null) {
            //holder.description.setTextAppearance(context, R.style.SubTitle);
            holder.description.setText(dataSet.get(position).getDescription());
        } else {
           // holder.description.setTextAppearance(context, R.style.SubTitleMissing);
            holder.description.setText(R.string.error_text_nodescription);
        }

        TextDrawable drawable = TextDrawable.builder().buildRect(
                color,
                context.getResources().getColor(Event.colorFromString(color))
        );
        holder.avatar.setImageDrawable(drawable);

        holder.event = dataSet.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(dataSet == null) return 0;
        return dataSet.size();
    }

}
