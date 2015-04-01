package be.thalarion.eventman.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.eventman.EditPersonDialogFragment;
import be.thalarion.eventman.R;
import be.thalarion.eventman.ShowPersonFragment;
import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private List<Person> dataSet;
    private Context context;

    public PeopleAdapter(Context context) {
        this.context = context;
        this.dataSet = new ArrayList<>();
    }

    public void setDataSet(List<Person> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView email;
        public ImageView avatar;
        public CardView card;
        public Person person;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.name = ((TextView) itemView.findViewById(R.id.person_list_view_name));
            this.email = ((TextView) itemView.findViewById(R.id.person_list_view_email));
            this.avatar = ((ImageView) itemView.findViewById(R.id.person_list_view_avatar));
            this.card = ((CardView) itemView.findViewById(R.id.card));

            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //TODO: replace by newInstance
            ((MaterialNavigationDrawer) v.getContext()).setFragmentChild(
                    new ShowPersonFragment(this.person),
                    v.getResources().getString(R.string.title_show_person)
                    );
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_person, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(dataSet.get(position).getName() != null) {
            holder.name.setTextAppearance(context, R.style.Title);
            holder.name.setText(dataSet.get(position).getName());
        } else {
            holder.name.setTextAppearance(context, R.style.TitleMissing);
            holder.name.setText(R.string.error_text_noname);
        }
        if(dataSet.get(position).getEmail() != null) {
            holder.email.setTextAppearance(context, R.style.SubTitle);
            holder.email.setText(dataSet.get(position).getEmail());
        } else {
            holder.email.setTextAppearance(context, R.style.SubTitleMissing);
            holder.email.setText(R.string.error_text_noemail);
        }

        // TODO: find out if/how Picasso handles memory management on a large number of files
        Picasso.with(this.context)
                .load(dataSet.get(position)
                .getAvatar(context.getResources().getDimensionPixelSize(R.dimen.avatar_small)))
                .into(holder.avatar);

        holder.person = dataSet.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(dataSet == null) return 0;
        return dataSet.size();
    }

}
