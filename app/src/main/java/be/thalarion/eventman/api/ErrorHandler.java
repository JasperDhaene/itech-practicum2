package be.thalarion.eventman.api;

import android.content.Context;
import android.widget.Toast;

public class ErrorHandler {

    /**
     * announce - Handle exceptions gracefully
     * @param context
     * @param e The exception to handle
     * @return boolean Whether or not the exception is fatal (operation-local)
     */
    public static boolean announce(Context context, Exception e) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
        e.printStackTrace();
        return true;
    }

}
