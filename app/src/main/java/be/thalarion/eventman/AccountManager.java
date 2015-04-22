package be.thalarion.eventman;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import be.thalarion.eventman.events.BusEvent;
import be.thalarion.eventman.events.PersonBusEvent;
import be.thalarion.eventman.models.Person;
import de.greenrobot.event.EventBus;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;

public class AccountManager {

    public static int defaultPhoto = R.drawable.ic_action_social_people;
    public static int defaultPerson = R.string.name_admin;
    public static int defaultEmail = R.string.email_admin;

    private MaterialNavigationDrawer activity;
    private Person person;

    public AccountManager(MaterialNavigationDrawer activity) {
        this.activity = activity;
        activity.setDrawerHeaderImage(R.drawable.material);
    }

    public void setAccount(final Person p) {
        this.person = p;
        ImageLoader il = ImageLoader.getInstance();
        il.loadImage(p.getAvatar(Person.AVATAR.THUMB), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                activity.getCurrentAccount().setTitle(p.getFormattedName(activity));
                activity.getCurrentAccount().setSubTitle(p.getFormattedEmail(activity));
                activity.getCurrentAccount().setPhoto(loadedImage);
                activity.notifyAccountDataChanged();
                activity.openDrawer();
            }
        });
    }

    public Person getPerson() {
        return this.person;
    }

    public boolean isNull() {
        return (this.person == null);
    }

    public void onStart() {
        EventBus.getDefault().register(this);
    }

    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(final PersonBusEvent event) {
        if (this.person == null) return;

        switch (event.action) {
            case DELETE:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        person = null;
                        Toast.makeText(activity, activity.getString(R.string.info_text_sign_out), Toast.LENGTH_SHORT).show();
                        activity.getCurrentAccount().setTitle(activity.getString(defaultPerson));
                        activity.getCurrentAccount().setSubTitle(activity.getString(defaultEmail));
                        activity.getCurrentAccount().setPhoto(defaultPhoto);
                        activity.notifyAccountDataChanged();
                        activity.openDrawer();
                    }
                });
                break;
            case UPDATE:
                if (this.person.equals(event.model)) {
                    this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setAccount((Person) event.model);
                        }
                    });
                }
                break;
            default:
                break;
        }
    }
}
