package be.thalarion.eventman.models;

import org.json.JSONObject;

import java.io.IOException;

import be.thalarion.eventman.api.APIException;

/**
 * Two possible implementations:
 * 1. Simple implementation: parse only name and resource
 * 2. Cross-reference with Person
 *
 * Implementation 1 allows for the bare minimum
 * Implementation 2 allows for extra features like avatars, ...
 */

public class Confirmation {

    public Person person;

    public Confirmation(Event event, Person person) {
        this.person = person;
    }

    public Confirmation(JSONObject json) throws APIException {
//        try {
//            if (!json.getBoolean("going")) return;

//            this.resource = new URL(json.getString("url"));
//            JSONObject person = json.getJSONObject("person");
//            this.person = Person.find(new URL(person.getString("url")));

//        } catch (JSONException | MalformedURLException e) {
//            throw new APIException(e);
//        }
    }

//    @Override
    public void update() throws IOException, APIException { }

}
