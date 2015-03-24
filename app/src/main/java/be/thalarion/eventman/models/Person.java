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

import fr.tkeunebr.gravatar.Gravatar;

/**
 * WARNING: this class contains a lot of synchronous networked methods.
 * Updating the model should run in a separate thread.
 */
public class Person {

    /**
     * Data fields
     *
     */
    private String name, email, avatar;
    private Date birthDate;
    private URL resource;

    private Person(String name, String email, Date birthDate) {
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.avatar = getGravatar(this.email);
        // sync to db and fill in this.resource
    }

    private Person(JSONObject json){
        try {
            this.name = json.getString("name");
            this.email = json.getString("email");
            this.avatar = getGravatar(this.email);
            try {
                this.resource = new URL(json.getString("url"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            try {
                this.birthDate = format.parse(json.getString("birth_date"));
            } catch (ParseException err){
                err.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * API Methods
     *
     */

    public static List<Person> findAll() throws IOException, APIException {
        List<Person> people = new ArrayList<>();

        URL resourceRoot = null;
        try {
            resourceRoot = API.getInstance().resolve("people");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fetch /people
        JSONObject json = API.getInstance().fetch(resourceRoot);
        try {
            JSONArray jsonArray = json.getJSONArray("people");
            for(int i = 0; i < jsonArray.length(); i++) {
                // Fetch /people/{id}
                JSONObject jsonPerson = API.getInstance().fetch(
                        new URL(
                                jsonArray.getJSONObject(i).getString("url")
                        ));
                people.add(new Person(jsonPerson));
            }
        } catch (JSONException e) {
            throw new APIException(e);
        }

        return people;
    }

    public void destroy() {

    }

    // Getters
    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
    public Date getBirthDate() {
        return this.birthDate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void updateFields() throws IOException, APIException {
        this.name = fetchField("name");
        this.email = fetchField("email");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.birthDate = format.parse(fetchField("birth_date"));
        } catch (ParseException e) {
            throw new APIException(e);
        }
    }

    // Setters
    public void setName(String name) throws IOException, APIException {
        this.name = name;
        updateField("name", this.name);
    }

    public void setEmail(String email) throws IOException, APIException {
        this.email = email;
        updateField("email", this.email);
    }

    public void setBirthDate(Date birthDate) throws IOException, APIException {
        this.birthDate = birthDate;
        JSONObject data = new JSONObject();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        updateField("birth_date", format.format(this.birthDate));
    }

    /**
     * Internal methods
     */
    private String getGravatar(String email) {
        return Gravatar.init()
                .with(email)
                .size(256)
                .build();
    }

    private String fetchField(String field) throws IOException, APIException {
        try {
            return API.getInstance().fetch(resource).getString(field);
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    private void updateField(String field, String value) throws IOException, APIException {
        JSONObject data = new JSONObject();
        try {
            data.put(field, value);
            // Exception is thrown when the key is null, but constants are never null.
        } catch (JSONException e) { }
        API.getInstance().update(resource, data.toString());
    }
}
