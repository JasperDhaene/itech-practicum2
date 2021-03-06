package be.thalarion.eventman.adapters;

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
import be.thalarion.eventman.fragments.person.ShowPersonFragment;
import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private List<Person> dataSet;

    public PeopleAdapter() {
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
        public LinearLayout container;
        public Person person;
        public ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            this.name = ((TextView) itemView.findViewById(R.id.person_list_view_name));
            this.email = ((TextView) itemView.findViewById(R.id.person_list_view_email));
            this.avatar = ((ImageView) itemView.findViewById(R.id.person_list_view_avatar));
            this.container = ((LinearLayout) itemView.findViewById(R.id.list_item_container));
            this.icon = ((ImageView) itemView.findViewById(R.id.list_item_icon));

            this.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) v.getContext()).getAccountManager().setAccount(person);
                    Toast.makeText(v.getContext(), String.format(v.getContext().getString(R.string.info_text_sign_in), person.getFormattedName(v.getContext())), Toast.LENGTH_SHORT).show();
                }
            });
            this.container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ((MaterialNavigationDrawer) v.getContext()).setFragmentChild(
                    ShowPersonFragment.newInstance(this.person.getResource()),
                    v.getResources().getString(R.string.title_show_person)
                    );
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_person, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (dataSet.get(position).getName() != null)
            holder.name.setText(dataSet.get(position).getName());
        else
            holder.name.setText(R.string.error_text_noname);

        if (dataSet.get(position).getEmail() != null)
            holder.email.setText(dataSet.get(position).getEmail());
        else
            holder.email.setText(R.string.error_text_noemail);

        ImageLoader.getInstance().loadImage(dataSet.get(position).getAvatar(Person.AVATAR.THUMB), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.avatar.setImageBitmap(loadedImage);
            }
        });

        holder.person = dataSet.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (dataSet == null) return 0;
        return dataSet.size();
    }

}
