package be.thalarion.eventman.api;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;

import be.thalarion.eventman.BuildConfig;
import be.thalarion.eventman.R;

public class ErrorHandler {

    /**
     * announce - Handle exceptions gracefully
     * @param context
     * @param e The exception to handle
     */
    public static void announce(Context context, Exception e) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } else try {
            throw e;
        } catch (APIException err) {
            Toast.makeText(context, String.format(
                    context.getString(R.string.error_text),
                    context.getString(R.string.error_type_api)), Toast.LENGTH_LONG).show();
        } catch (IOException err) {
            Toast.makeText(context, String.format(
                    context.getString(R.string.error_text),
                    context.getString(R.string.error_type_io)), Toast.LENGTH_LONG).show();
        } catch (ParseException err) {
            Toast.makeText(context, context.getString(R.string.error_text_date), Toast.LENGTH_LONG).show();
        } catch (Exception err) {
            Toast.makeText(context, String.format(
                    context.getString(R.string.error_text),
                    context.getString(R.string.error_type_unknown)), Toast.LENGTH_LONG).show();
        }
    }
}
