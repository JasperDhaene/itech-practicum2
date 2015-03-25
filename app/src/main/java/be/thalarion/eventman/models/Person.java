package be.thalarion.eventman.models;

import android.os.Parcel;
import android.os.Parcelable;

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

import be.thalarion.eventman.api.API;
import be.thalarion.eventman.api.APIException;
import fr.tkeunebr.gravatar.Gravatar;

/**
 * WARNING: this class contains a lot of synchronous networked methods.
 * Updating the model should run in a separate thread.
 */
public class Person extends Model implements Parcelable {

    private String name, email;
    private Date birthDate;

    public int AVATAR_SIZE_LARGE = 256;
    public int AVATAR_SIZE_SMALL = 56;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Person - create a new person
     * @param name
     * @param email
     * @param birthDate
     * @throws IOException
     * @throws be.thalarion.eventman.api.APIException
     */
    public Person(String name, String email, Date birthDate) throws IOException, APIException {
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;


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
            System.out.println("TODO");
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    private Person(JSONObject json) throws APIException {
        try {
            if(!json.isNull("name")) this.name = json.getString("name");
            if(!json.isNull("email")) this.email = json.getString("email");

            this.resource = new URL(json.getString("url"));

            try {
                this.birthDate = format.parse(json.getString("birth_date"));
            } catch (ParseException e) {
                // Thrown if birth_date is null
            }
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
    public String getAvatar(int size) {
        return Gravatar.init()
                .with(email)
                .size(size)
                .build();
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
     * Parcelling part
     */
    public Person(Parcel in){
        String[] data = new String[2];
        Date birthdate;

        in.readStringArray(data);
        birthdate = new Date(in.readLong());
        this.name = data[0];
        this.email= data[1];

        this.birthDate = birthdate;
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.name,
                this.email});
        dest.writeLong(this.birthDate.getTime());
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };




}
