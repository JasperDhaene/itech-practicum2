package be.thalarion.eventman.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public abstract class Model {

    protected URL resource;

    public abstract void update() throws IOException, APIException;

    public void destroy() throws IOException, APIException {
        API.getInstance().delete(this.resource);
    }

    protected String fetchField(String field) throws IOException, APIException {
        try {
            return API.getInstance().fetch(resource).getString(field);
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    protected void updateField(String field, String value) throws IOException, APIException {
        JSONObject data = new JSONObject();
        try {
            data.put(field, value);
            // Exception is thrown when the key is null, but constants are never null.
        } catch (JSONException e) { }
        API.getInstance().update(resource, data.toString());
    }

}