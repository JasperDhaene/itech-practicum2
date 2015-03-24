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
public class Person extends Model {

    private String name, email, avatar;
    private Date birthDate;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Person - create a new person
     * @param name
     * @param email
     * @param birthDate
     * @throws IOException
     * @throws APIException
     */
    public Person(String name, String email, Date birthDate) throws IOException, APIException {
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.avatar = getGravatar(this.email);

        JSONObject p = new JSONObject();
        try {
            p.put("name", this.name);
            p.put("email", this.email);
            p.put("birth_date", format.format(this.birthDate));

            JSONObject response = API.getInstance().create(
                    API.getInstance().resolve("people"),
                    p.toString()
            );
            this.resource = new URL(response.getString("url"));
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    private Person(JSONObject json) throws APIException {
        try {
            this.name = json.getString("name");
            this.email = json.getString("email");
            this.avatar = getGravatar(this.email);
            this.resource = new URL(json.getString("url"));

            try {
                this.birthDate = format.parse(json.getString("birth_date"));
            } catch (ParseException e) { }
        } catch (JSONException | MalformedURLException e) {
            throw new APIException(e);
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
            throw new APIException(e);
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
        return this.avatar;
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
        updateField("birth_date", format.format(this.birthDate));
    }

    /**
     * update - Sync model from network to memory
     * @throws IOException
     * @throws APIException
     */
    public void update() throws IOException, APIException {
        this.name = fetchField("name");
        this.email = fetchField("email");

        try {
            this.birthDate = format.parse(fetchField("birth_date"));
        } catch (ParseException e) { }
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
