package be.thalarion.eventman;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class AccountManager {

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
}
