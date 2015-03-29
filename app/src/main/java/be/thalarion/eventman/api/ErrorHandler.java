package be.thalarion.eventman.api;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import be.thalarion.eventman.BuildConfig;
import be.thalarion.eventman.R;

public class ErrorHandler {

    /**
     * announce - Handle exceptions gracefully
     * @param context
     * @param e The exception to handle
     */
    public static void announce(Context context, Exception e) {
        if(BuildConfig.DEBUG) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
            e.printStackTrace();
        } else {
            try {
                throw e;
            } catch (APIException err) {
                Toast.makeText(context, context.getResources().getString(R.string.error_api), Toast.LENGTH_LONG);
            } catch (IOException err) {
                Toast.makeText(context, context.getResources().getString(R.string.error_io), Toast.LENGTH_LONG);
            } catch (Exception err) {
                Toast.makeText(context, context.getResources().getString(R.string.error_unknown), Toast.LENGTH_LONG);
            }
        }
    }
}
