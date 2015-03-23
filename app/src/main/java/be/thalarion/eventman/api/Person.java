package be.thalarion.eventman.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.tkeunebr.gravatar.Gravatar;

/**
 * WARNING: this class contains ONLY synchronous methods. Interactions with this class
 * should be contained within a separate thread!
 */
public class Person {

    /**
     * Data fields
     *
     */
    private String name, email, avatar, resource;
    private Date birthDate;

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
            this.resource = json.getString("url");

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

    public static List<Person> findAll() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return people;
    }

    public void destroy() {

    }

    // Getters
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public Date getBirthDate() {
        return birthDate;
    }

    public String getAvatar() {
        return avatar;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
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
}
