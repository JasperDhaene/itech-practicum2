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
import java.util.List;

/**
 * WARNING: this class contains a lot of synchronous networked methods.
 * Updating the model should run in a separate thread.
 */
public class Event extends Model {

    private String title, description;
    private Date startDate, endDate;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    //                                                      2014-08-04 T 18:00:00.000 Z

    /**
     * Event - create a new event
     * @param title
     * @param description
     * @param startDate
     * @param endDate
     * @throws IOException
     * @throws APIException
     */
    public Event(String title, String description, Date startDate, Date endDate) throws IOException, APIException{
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;

        JSONObject event = new JSONObject();
        try {
            event.put("title", this.title);
            event.put("description", this.description);
            event.put("start", format.format(this.startDate));
            event.put("end", format.format(this.endDate));

            JSONObject response = API.getInstance().create(
                    API.getInstance().resolve("events"),
                    event.toString()
            );
            this.resource = new URL(response.getString("url"));
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    private Event(JSONObject json) throws APIException {
        try {
            this.title = json.getString("title");
            this.description = json.getString("description");
            this.resource = new URL(json.getString("url"));

            try {
                this.startDate = format.parse(json.getString("start"));
                this.endDate = format.parse(json.getString("end"));
            } catch (ParseException e) { }
        } catch (JSONException | MalformedURLException e) {
            throw new APIException(e);
        }
    }


    /**
     * API Methods
     *
     */
    public static List<Event> findAll() throws IOException, APIException {
        List<Event> events = new ArrayList<>();

        URL resourceRoot = null;
        try {
            resourceRoot = API.getInstance().resolve("events");
        } catch (Exception e) {
            throw new APIException(e);
        }

        // Fetch /events
        JSONObject json = API.getInstance().fetch(resourceRoot);
        try {
            JSONArray jsonArray = json.getJSONArray("events");
            for(int i = 0; i < jsonArray.length(); i++) {
                // Fetch /events/{id}
                JSONObject jsonEvent = API.getInstance().fetch(
                        new URL(
                                jsonArray.getJSONObject(i).getString("url")
                        ));
                events.add(new Event(jsonEvent));
            }
        } catch (JSONException e) {
            throw new APIException(e);
        }

        return events;
    }

    // Getters
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

    // Setters
    public void setTitle(String title) throws IOException, APIException {
        this.title = title;
        updateField("title", this.title);
    }

    public void setDescription(String description) throws IOException, APIException {
        this.description = description;
        updateField("description", this.description);
    }

    public void setStartDate(Date startDate) throws IOException, APIException {
        this.startDate = startDate;
        updateField("start", format.format(this.startDate));
    }

    public void setEndDate(Date endDate) throws IOException, APIException {
        this.startDate = endDate;
        updateField("end", format.format(this.endDate));
    }

    /**
     * update - Sync model from network to memory
     * @throws IOException
     * @throws APIException
     */
    public void update() throws IOException, APIException {
        this.title = fetchField("title");
        this.description = fetchField("description");

        try {
            this.startDate = format.parse(fetchField("start"));
            this.startDate = format.parse(fetchField("end"));
        } catch (ParseException e) { }
    }
}
