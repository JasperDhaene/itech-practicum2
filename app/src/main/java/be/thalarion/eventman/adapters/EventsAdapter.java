package be.thalarion.eventman.adapters;

import android.content.Context;
import android.os.AsyncTask;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import be.thalarion.eventman.AccountManager;
import be.thalarion.eventman.MainActivity;
import be.thalarion.eventman.R;
import be.thalarion.eventman.ShowEventContainerFragment;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.api.ErrorHandler;
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
                public void onClick(final View v) {
                    final AccountManager am  = ((MainActivity)v.getContext()).getAccountManager();
                    if(am.isNull()) {
                        ((CheckBox) v).setChecked(false);
                        Toast.makeText(v.getContext(), v.getResources().getString(R.string.error_not_signed_in), Toast.LENGTH_LONG).show();
                    } else {
                        if (((CheckBox) v).isChecked()) {
                            new AsyncTask<Event, Void, Exception>() {
                                @Override
                                protected Exception doInBackground(Event... params) {
                                    try {
                                        event.confirm(am.getPerson(), true);
                                    } catch (APIException | IOException e) {
                                        return e;
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Exception e) {
                                    if (e == null) {
                                        Toast.makeText(v.getContext(), v.getResources().getText(R.string.info_confirmation_accept), Toast.LENGTH_LONG).show();
                                    } else ErrorHandler.announce(v.getContext(), e);
                                }
                            }.execute(event);
                        } else {
                            new AsyncTask<Event, Void, Exception>() {
                                @Override
                                protected Exception doInBackground(Event... params) {
                                    try {
                                        event.confirm(am.getPerson(), false);
                                    } catch (APIException | IOException e) {
                                        return e;
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Exception e) {
                                    if (e == null) {
                                        Toast.makeText(v.getContext(), v.getResources().getText(R.string.info_confirmation_decline), Toast.LENGTH_LONG).show();
                                    } else ErrorHandler.announce(v.getContext(), e);
                                }
                            }.execute(event);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            ((MaterialNavigationDrawer) v.getContext()).setFragmentChild(
                    ShowEventContainerFragment.newInstance(this.event.getResource().toString()), //TODO: should i switch to URL approach?
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
        if (dataSet.get(position).getTitle() != null) {
            //holder.title.setTextAppearance(context, R.style.Title);
            holder.title.setText(dataSet.get(position).getTitle());

            color = Event.hash(dataSet.get(position).getTitle());
        } else {
            //holder.title.setTextAppearance(context, R.style.TitleMissing);
            holder.title.setText(R.string.error_text_notitle);

            color = Event.hash("Ev");
        }
        if (dataSet.get(position).getDescription() != null) {
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

        Person p = ((MainActivity) context).getAccountManager().getPerson();

        if(p != null)
            holder.checkBox.setChecked(dataSet.get(position).hasConfirmed(p));

        holder.event = dataSet.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (dataSet == null) return 0;
        return dataSet.size();
    }

}
