package be.thalarion.eventman.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import be.thalarion.eventman.api.API;
import be.thalarion.eventman.api.APIException;

public abstract class Model {

    protected URL resource;

    public abstract void update() throws IOException, APIException;

    public void destroy() throws IOException, APIException {
        API.getInstance().delete(this.resource);
    }

    protected String fetchField(String field) throws IOException, APIException {
        try {
            JSONObject json = API.getInstance().fetch(resource);
            if(json.isNull(field)) {
                return null;
            } else return json.getString(field);
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    //TODO: dit zal een request doen per veld dat aangepast word. Eerst dacht ik dat update() hiervoor was, maar deze wordt nooit gebruikt.
    // Misschien nog veranderen zodat je een persoon in 1 keer kunt updaten

    protected void updateField(String field, String value) throws IOException, APIException {
        JSONObject data = new JSONObject();
        try {
            data.put(field, value);
            // Exception is thrown when the key is null, but constants are never null.
        } catch (JSONException e) { }
        API.getInstance().update(resource, data.toString());
    }

}
