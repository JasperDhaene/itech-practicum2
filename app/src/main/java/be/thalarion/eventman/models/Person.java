package be.thalarion.eventman.models;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Transient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.thalarion.eventman.R;
import be.thalarion.eventman.api.API;
import be.thalarion.eventman.api.APIException;
import fr.tkeunebr.gravatar.Gravatar;

@Parcel
public class Person extends Model {

    // Keep public modifier for parcelling library
    public String name, email, avatarSmall, avatarLarge;
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
    public String getFormattedName(Context c) {
        if(this.name == null)
            return c.getString(R.string.error_text_noname);
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
    public String getFormattedEmail(Context c) {
        if(this.email == null)
            return c.getString(R.string.error_text_noemail);
        return this.email;
    }
    public Date getBirthDate() {
        return this.birthDate;
    }
    public String getFormattedBirthDate(Context c) {
        if(this.birthDate == null)
            return c.getString(R.string.error_text_nobirthdate);
        return this.format.format(this.birthDate);
    }
    public String getAvatar(AVATAR size) {
        return this.avatarFromString(this.getName(), size);
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

    public enum AVATAR {
        THUMB, MEDIUM, LARGE;
    }
    /**
     * avatarFromString - Synthesize an avatar URL from a string
     * @param key
     * @param size
     * @return String
     */
    private static String avatarFromString(String key, AVATAR size) {
        int code = 0;
        for(int i = 0; i < key.length(); i++) {
            code += key.charAt(i);
            code %= 196;
        }

        String baseUrl = "http://api.randomuser.me/portraits/";

        switch(size) {
            case THUMB:
                baseUrl += "thumb/";
                break;
            case MEDIUM:
                baseUrl += "med/";
                break;
        }

        return baseUrl + (code > 99 ? "wo" : "") + "men/" + (code % 99) + ".jpg";
    }

}
