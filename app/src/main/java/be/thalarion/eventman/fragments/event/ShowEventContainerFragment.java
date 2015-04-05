package be.thalarion.eventman.fragments.event;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;

import be.thalarion.eventman.R;
import be.thalarion.eventman.adapters.EventMessagePagerAdapter;

public class ShowEventContainerFragment extends android.support.v4.app.Fragment {

    private EventMessagePagerAdapter adapter;
    private ViewPager viewPager;

    public ShowEventContainerFragment() {
        // Required empty public constructor
    }

    public static ShowEventContainerFragment newInstance(URL url) {
        ShowEventContainerFragment fragment = new ShowEventContainerFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("url", url);

        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.viewpager_event, container, false);
        setHasOptionsMenu(true);

        this.adapter = new EventMessagePagerAdapter(
                this.getActivity().getSupportFragmentManager(),
                (URL) getArguments().getSerializable("url"));
        this.viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_container);
        this.viewPager.setAdapter(adapter);

        return rootView;
    }

/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event, menu);
    }
    */
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.action_edit_event:

                EditEventDialogFragment editEventFrag = EditEventDialogFragment.newInstance(this.event, Model.ACTION.EDIT);

                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(editEventFrag,this.getActivity().getResources().getString(R.string.title_edit_event));

                break;
            case R.id.action_discard_event:
                new AsyncTask<Void, Void, Exception>() {
                    private Context context;

                    @Override
                    protected void onPreExecute() {
                        this.context = getActivity();
                    }

                    @Override
                    protected Exception doInBackground(Void... params) {
                        try {
                            event.destroy();
                            // Allow garbage collection
                            event = null;
                        } catch (APIException | IOException e) {
                            return e;
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Exception e) {
                        if(e == null) {
                            Toast.makeText(this.context, this.context.getResources().getText(R.string.info_text_destroy), Toast.LENGTH_LONG).show();
                        } else ErrorHandler.announce(this.context, e);
                    }
                }.execute();

                EventsFragment eventsFrag = new EventsFragment();

                ((MaterialNavigationDrawer) this.getActivity()).setFragmentChild(eventsFrag,this.getActivity().getResources().getString(R.string.title_people));

                break;
            default:
                return false;
        }
        return true;
    }*/

}
