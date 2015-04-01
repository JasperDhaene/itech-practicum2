package be.thalarion.eventman.models;

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

    // I'd use a Set here, but for some ironic reason you cannot retrieve items from it
    private Map<Confirmation, Confirmation> confirmations;
    private List<Message> messages;
    public URL confirmationResource, messageResource;

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

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

            if(!json.isNull("start_date")) this.startDate = this.format.parse(json.getString("start_date"));
            else this.startDate = null;

            if(!json.isNull("end_date")) this.endDate = this.format.parse(json.getString("end_date"));
            else this.endDate = null;

            if(!json.isNull("confirmations")) {
                JSONObject jsonConfirmations = json.getJSONObject("confirmations");
                if(!jsonConfirmations.isNull("url")) this.confirmationResource = new URL(jsonConfirmations.getString("url"));
                if(!jsonConfirmations.isNull("list")) {
                    JSONArray list = jsonConfirmations.getJSONArray("list");
                    for(int i = 0; i < list.length(); i++) {
                        Confirmation c = new Confirmation(this);
                        c.fromJSON(list.getJSONObject(i));
                        this.confirmations.put(c, c);
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
            event.put("start_date", this.format.format(this.startDate));
            event.put("end_date", this.format.format(this.endDate));

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
        for(Confirmation c: this.confirmations.keySet()) {
            c.syncModelToNetwork();
        }
    }

    public String getTitle() {
        return this.title;
    }
    public String getDescription() {
        return this.description;
    }
    public Date getStartDate() {
        return this.startDate;
    }
    public Date getEndDate() {
        return this.endDate;
    }
    public List<Person> getConfirmations() {
        List<Person> people = new ArrayList<>();
        for(Confirmation c: this.confirmations.keySet()) {
            people.add(c.getPerson());
        }
        return people;
    }
    public List<Message> getMessages() { return this.messages; }

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
        if(going && !this.confirmations.containsKey(p)) {
            // Person has confirmed
            Confirmation c = new Confirmation(p, this);
            this.confirmations.put(c, null);
            c.syncModelToNetwork(); // this will call Event.syncModelFromNetwork()
        } else if(!going && this.confirmations.containsKey(p)) {
            // Person has denied
            this.confirmations.get(new Confirmation(p, null)).destroy();
            this.confirmations.remove(p);
        }
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
