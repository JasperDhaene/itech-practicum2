package be.thalarion.eventman.models;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.IOException;
import java.net.URL;

import be.thalarion.eventman.api.API;
import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.cache.Cache;

@Parcel
public class Confirmation extends Model {

    public Person person;
    public Event event;

    public Confirmation() { }

    public Confirmation(Person person, Event event) {
        this.person = person;
        this.event = event;
    }

    public Confirmation(Event event) { this.event = event; }

    /**
     * API Methods
     */
    @Override
    protected String getCanonicalName() { return ""; }

    @Override
    protected void fromJSON(JSONObject json) throws APIException {
        try {
            if (json.getBoolean("going")) {
                JSONObject p = json.getJSONObject("person");
                this.person = Cache.find(Person.class, new URL(p.getString("url")));
                this.resource = new URL(json.getString("url"));
            }
        } catch (IOException | JSONException e) {
            /**
             * Thrown on one of the following errors
             * - going, person, url is not available
             * - url cannot be parsed
             */
            throw new APIException(e);
        }
    }

    @Override
    protected JSONObject toJSON() throws APIException {
        JSONObject json = new JSONObject();
        try {
            json.put("going", true);
            JSONObject person = new JSONObject();
            person.put("name", this.person.getName());
            person.put("url", this.person.resource);
        } catch (JSONException e) {
            throw new APIException(e);
        }
        return json;
    }

    /**
     * syncModelToNetwork - Ensure confirmation
     * Confirmation handling works a bit differently. A new confirmation does not have a resource, and
     * upon POSTing does not receive one either. As a consequence the containing model has to be refreshed.
     * @throws IOException
     * @throws APIException
     */
    @Override
    public void syncModelToNetwork() throws IOException, APIException {
        JSONObject json = toJSON();

        if (this.resource == null) {
            // Create new resource
            API.getInstance().create(
                    this.event.confirmationResource,
                    json.toString()
            );
            this.event.syncModelToMemory();
        } else {
            // Update existing resource
            API.getInstance().update(this.resource, json.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        return ((Confirmation) o).person.equals(this.person);
    }
}
