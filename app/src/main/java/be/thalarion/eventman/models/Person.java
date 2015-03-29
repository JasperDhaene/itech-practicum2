package be.thalarion.eventman.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Transient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.thalarion.eventman.api.API;
import be.thalarion.eventman.api.APIException;
import fr.tkeunebr.gravatar.Gravatar;

@Parcel
public class Person extends Model {

    // Keep public modifier for parcelling library
    public String name, email;
    public Date birthDate;

    @Transient
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public Person() { }

    /**
     * Person - create a new person
     * @param name
     * @param email
     * @param birthDate
     */
    public Person(String name, String email, Date birthDate) {
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
    }

    /**
     * API Methods
     *
     */
    @Override
    protected String getCanonicalName() { return "people"; }

    @Override
    protected void fromJSON(JSONObject json) throws APIException {
        try {
            if(!json.isNull("name")) this.name = json.getString("name");
            else this.name = null;

            if(!json.isNull("email")) this.email = json.getString("email");
            else this.email = null;

            if(!json.isNull("birth_date")) this.birthDate = this.format.parse(json.getString("birth_date"));
            else this.birthDate = null;
        } catch (JSONException | ParseException e) {
            throw new APIException(e);
        }
    }

    @Override
    protected JSONObject toJSON() throws APIException {
        JSONObject person = new JSONObject();
        try {
            person.put("name", this.name);
            person.put("email", this.email);
            person.put("birth_date", this.format.format(this.birthDate));
        } catch (JSONException e) {
            throw new APIException(e);
        }
        return person;
    }

    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
    public Date getBirthDate() {
        return this.birthDate;
    }
    public String getAvatar(int size) {
        String email = "dummy@email.be";
        if(this.email != null) email = this.email;
        return Gravatar.init()
                .with(email)
                .size(size)
                .build();
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

}
