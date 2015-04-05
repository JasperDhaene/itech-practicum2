package be.thalarion.eventman.models;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.thalarion.eventman.R;
import be.thalarion.eventman.api.APIException;

public class Event extends Model {

    private String title, description;
    private Date startDate, endDate;

    private Map<URL, Confirmation> confirmations;
    private List<Message> messages;
    public URL confirmationResource, messageResource;

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

    public Event() {
        this.confirmations = new HashMap<>();
        this.messages = new ArrayList<>();
    }


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
        this.confirmations = new HashMap<>();
        this.messages = new ArrayList<>();
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

            if(!json.isNull("start")) this.startDate = this.format.parse(json.getString("start"));
            else this.startDate = null;

            if(!json.isNull("end")) this.endDate = this.format.parse(json.getString("end"));
            else this.endDate = null;

            this.confirmations.clear();
            if(!json.isNull("confirmations")) {
                JSONObject jsonConfirmations = json.getJSONObject("confirmations");
                if(!jsonConfirmations.isNull("url")) this.confirmationResource = new URL(jsonConfirmations.getString("url"));
                if(!jsonConfirmations.isNull("list")) {
                    JSONArray list = jsonConfirmations.getJSONArray("list");
                    for(int i = 0; i < list.length(); i++) {
                        Confirmation c = new Confirmation(this);
                        c.fromJSON(list.getJSONObject(i));
                        this.confirmations.put(c.getPerson().getResource(), c);
                    }
                }
            }

            if(!json.isNull("messages")) {
                JSONObject jsonMessages = json.getJSONObject("messages");
                if(!jsonMessages.isNull("url")) this.messageResource = new URL(jsonMessages.getString("url"));
                if(!jsonMessages.isNull("list")) {
                    JSONArray list = jsonMessages.getJSONArray("list");
                    for(int i = 0; i < list.length(); i++) {
                        Message m = new Message(this);
                        m.fromJSON(list.getJSONObject(i));
                        this.messages.add(m);
                    }
                }
            }
        } catch (MalformedURLException | JSONException | ParseException e) {
            throw new APIException(e);
        }
    }

    @Override
    protected JSONObject toJSON() throws APIException {
        JSONObject event = new JSONObject();
        try {
            event.put("title", this.title);
            event.put("description", this.description);
            event.put("start", format.format(this.startDate));
            event.put("end", format.format(this.endDate));

            // Serialization of confirmations is handled in Confirmation.syncModelToNetwork()
        } catch (JSONException e) {
            throw new APIException(e);
        }
        return event;
    }

    @Override
    public void syncModelToNetwork() throws IOException, APIException {
        super.syncModelToNetwork();

        // Sync confirmations
        for(Confirmation c: this.confirmations.values()) {
            c.syncModelToNetwork();
        }
    }

    public String getTitle() {
        return this.title;
    }
    public String getFormattedTitle(Context c) {
        if(this.title == null)
            return c.getString(R.string.error_text_notitle);
        return this.title;
    }
    public String getDescription() {
        return this.description;
    }
    public String getFormattedDescription(Context c) {
        if(this.description == null)
            return c.getString(R.string.error_text_nodescription);
        return this.description;
    }
    public Date getStartDate() {
        return this.startDate;
    }
    public String getFormattedStartDate(Context c, SimpleDateFormat format) {
        if(this.startDate == null)
            return c.getString(R.string.error_text_nostartdate);
        return format.format(this.startDate);
    }
    public Date getEndDate() {
        return this.endDate;
    }
    public String getFormattedEndDate(Context c, SimpleDateFormat format) {
        if(this.endDate == null)
            return c.getString(R.string.error_text_noenddate);
        return format.format(this.endDate);
    }
    public List<Person> getConfirmations() {
        List<Person> people = new ArrayList<>();
        for(Confirmation c: this.confirmations.values()) {
            people.add(c.getPerson());
        }
        return people;
    }
    public List<Message> getMessages() { return this.messages; }
    public boolean hasConfirmed(Person p) {
        return this.confirmations.containsKey(p.getResource());
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
    public void confirm(Person p, boolean going) throws IOException, APIException {
        if(going) {
            Log.e("eventman", "Adding confirmation for " + p.getName() + " in list " + this.confirmations.size());
        } else Log.e("eventman", "Removing confirmation for " + p.getName() + " in list " + this.confirmations.size());
        if(going && !this.confirmations.containsKey(p.getResource())) {
            // Person has confirmed
            Confirmation c = new Confirmation(p, this);
            /**
             * This will call Event.syncModelFromNetwork() which will
             * refresh the event's confirmation list to obtain a proper
             * resource URL. Therefore adding this confirmation to the
             * list is no long necessary
             */
            c.syncModelToNetwork();
        } else if(!going && this.confirmations.containsKey(p.getResource())) {
            // Person has denied
            this.confirmations.get(p.getResource()).destroy();
            this.confirmations.remove(p.getResource());
        }
        Log.e("eventman", "New list size: " + this.confirmations.size());
    }
    public void createMessage(Person p, String text) throws IOException, APIException {
        Message m = new Message(this);
        m.setPerson(p);
        m.setText(text);
        this.messages.add(m);
        m.syncModelToNetwork(); // this will call Event.syncModelFromNetwork()
    }
    public void destroyMessage(Message m) throws IOException, APIException {
        this.messages.remove(m);
        m.destroy();
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
