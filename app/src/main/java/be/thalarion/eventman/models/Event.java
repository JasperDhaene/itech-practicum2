package be.thalarion.eventman.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Transient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.thalarion.eventman.R;
import be.thalarion.eventman.api.APIException;

@Parcel
public class Event extends Model {

    // Keep public modifier for parcelling library
    public String title, description;
    public Date startDate, endDate;

    @Transient
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public Event() { }

    /**
     * Event - create a new event
     * @param title
     * @param description
     * @param startDate
     * @param endDate
     */
    public Event(String title, String description, Date startDate, Date endDate) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * API Methods
     *
     */
    @Override
    protected String getCanonicalName() { return "events"; }

    @Override
    protected void fromJSON(JSONObject json) throws APIException {
        try {
            if(!json.isNull("title")) this.title= json.getString("title");
            else this.title = null;

            if(!json.isNull("description")) this.description = json.getString("description");
            else this.description = null;

            if(!json.isNull("start_date")) this.startDate = this.format.parse(json.getString("start_date"));
            else this.startDate = null;

            if(!json.isNull("end_date")) this.endDate = this.format.parse(json.getString("end_date"));
            else this.endDate = null;
        } catch (JSONException | ParseException e) {
            throw new APIException(e);
        }
    }

    @Override
    protected JSONObject toJSON() throws APIException {
        JSONObject event = new JSONObject();
        try {
            event.put("title", this.title);
            event.put("description", this.description);
            event.put("start_date", this.format.format(this.startDate));
            event.put("end_date", this.format.format(this.endDate));
        } catch (JSONException e) {
            throw new APIException(e);
        }
        return event;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public Date getStartDate() {
        return startDate;
    }
    public Date getEndDate() {
        return endDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * hash - Get icon-presentable hashcode (one or two characters)
     * @param key
     * @return
     */
    public static String hash(String key) {
        String[] split = key.split(" ");
        if(split.length >= 2) {
            return split[0].substring(0, 1).toUpperCase() + split[1].substring(0, 1).toLowerCase();
        } else return split[0].substring(0, 1).toUpperCase();
    }

    private static int[] colors = {
            R.color.md_red,
            R.color.md_pink,
            R.color.md_purple,
            R.color.md_indigo,
            R.color.md_blue,
            R.color.md_green,
            R.color.md_lime,
            R.color.md_yellow,
            R.color.md_amber,
            R.color.md_deep_orange
    };

    /**
     * colorFromString - Synthesize a color from a string
     * @param key
     * @return Color resource ID
     */
    public static int colorFromString(String key) {
        int code = key.charAt(0);
        if(key.length() >= 2)
            code += key.charAt(1);

        code = code % colors.length;
        return colors[code];
    }

}
