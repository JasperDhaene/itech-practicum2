package be.thalarion.eventman.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.thalarion.eventman.api.API;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.cache.Cache;

public class Message extends Model {

    private Person person;
    private String text;
    private Date date;
    private Event event;

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public Message() { }

    public Message(Person person, String text, Event event) {
        this.person = person;
        this.text = text;
        this.date = new Date();
    }

    public Message(Event event) { this.event = event; }

    /**
     * API Methods
     */
    @Override
    protected String getCanonicalName() { return ""; }

    @Override
    protected void fromJSON(JSONObject json) throws APIException {
        try {
            if(!json.isNull("text")) this.text = json.getString("text");
            if(!json.isNull("created_at")) this.date = this.format.parse(json.getString("created_at"));
            JSONObject p = json.getJSONObject("person");
            this.person = Cache.find(Person.class, new URL(p.getString("url")));
            this.resource = new URL(json.getString("url"));
        } catch (ParseException | IOException | JSONException e) {
            throw new APIException(e);
        }
    }

    @Override
    protected JSONObject toJSON() throws APIException {
        JSONObject json = new JSONObject();
        try {
            json.put("text", this.text);
            json.put("created_at", this.format.format(this.date));
            JSONObject person = new JSONObject();
            person.put("name", this.person.getName());
            person.put("url", this.person.resource);
        } catch (JSONException e) {
            throw new APIException(e);
        }
        return json;
    }

    /**
     * syncModelToNetwork - Ensure message
     * Message handling works a bit differently. A new message does not have a resource, and
     * upon POSTing does not receive one either. As a consequence the containing model has to be refreshed.
     * This method calls Event.syncModelToMemory()
     * @throws IOException
     * @throws APIException
     */
    @Override
    public void syncModelToNetwork() throws IOException, APIException {
        JSONObject json = toJSON();

        if (this.resource == null) {
            // Create new resource
            API.getInstance().create(
                    this.event.messageResource,
                    json.toString()
            );
            this.event.syncModelToMemory();
        } else {
            // Update existing resource
            API.getInstance().update(this.resource, json.toString());
        }
    }

    public Person getPerson() {
        return person;
    }
    public String getText() {
        return text;
    }
    public Date getDate() {
        return date;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
    public void setText(String text) {
        this.text = text;
    }

}
