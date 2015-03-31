package be.thalarion.eventman;

import android.util.Log;

import be.thalarion.eventman.models.Person;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;

public class AccountManager {

    private MaterialNavigationDrawer activity;
    private Person person;

    public AccountManager(MaterialNavigationDrawer activity) {
        this.activity = activity;
        activity.setDrawerHeaderImage(R.drawable.material);
    }

    public void setAccount(final Person p) {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(activity.getCurrentAccount() == null) {
                    MaterialAccount account = new MaterialAccount(activity.getResources(),
                            p.getFormattedName(activity),
                            p.getFormattedEmail(activity),
                            R.drawable.gravatar,
                            R.drawable.material
                    );
                    account.setPhoto(R.drawable.gravatar);
                    activity.addAccount(account);
                } else {
                    activity.getCurrentAccount().setTitle(p.getFormattedName(activity));
                    activity.getCurrentAccount().setSubTitle(p.getFormattedEmail(activity));
                    activity.notifyAccountDataChanged();
                }
                activity.openDrawer();
            }
        });
    }
}
