package be.thalarion.eventman.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.transfuse.annotations.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import be.thalarion.eventman.api.API;
import be.thalarion.eventman.api.APIException;

public abstract class Model {

    public static enum ACTION {
        EDIT, NEW
    }

    protected URL resource;

    /**
     * syncModelToNetwork - Sync in-memory model with API model
     * @throws IOException
     * @throws APIException
     */
    public void syncModelToNetwork() throws IOException, APIException {
        JSONObject json = toJSON();

        if (this.resource == null) {
            // Create new resource
            JSONObject response = API.getInstance().create(
                    API.getInstance().resolve(getCanonicalName()),
                    json.toString()
            );

            try {
                this.resource = new URL(response.getString("url"));
            } catch (JSONException e) {
                throw new APIException(e);
            }
        } else {
            // Update existing resource
            API.getInstance().update(this.resource, json.toString());
        }
    }

    /**
     * syncModelToMemory - Sync API model with in-memory model
     * @throws IOException
     * @throws APIException
     */
    public void syncModelToMemory() throws IOException, APIException {
        if(this.resource == null)
            throw new APIException("Model must have a resource URL");

        JSONObject json = API.getInstance().fetch(this.resource);

        fromJSON(API.getInstance().fetch(this.resource));
    }

    /**
     * destroy - Delete model instance
     * @throws IOException
     * @throws APIException
     */
    public void destroy() throws IOException, APIException {
        API.getInstance().delete(this.resource);
    }

    /**
     * findAll - Retrieve list of all models from network
     * @param model Model class object
     * @return List of models
     * @throws IOException
     * @throws APIException
     */
     public static <T extends Model> List<T> findAll(Class<T> model) throws IOException, APIException {
        List<T> models = new ArrayList<>();

        try {
            T m = model.newInstance();
            // GET /{model}
            JSONObject json = API.getInstance().fetch(
                    API.getInstance().resolve(m.getCanonicalName())
            );

            JSONArray jsonArray = json.getJSONArray(m.getCanonicalName());
            for(int i = 0; i < jsonArray.length(); i++) {
                // GET /{model}/{id}
                JSONObject jsonObject = API.getInstance().fetch(
                        new URL(
                                jsonArray.getJSONObject(i).getString("url")
                        ));
                m = model.newInstance();
                m.resource = new URL(jsonObject.getString("url"));
                m.fromJSON(jsonObject);
                models.add(m);
            }
        } catch (JSONException | IllegalAccessException | InstantiationException e) {
            throw new APIException(e);
        }

        return models;
    }

    /**
     * same - Compare a resource URL against a model
     * @param resource
     * @return
     */
    public boolean same(URL resource) {
        if(resource == this.resource)
            return true;

        return false;
    }

    /**
     * fromJSON - Deserialize a JSON object to a model
     * @param json
     * @throws APIException
     */
    protected abstract void fromJSON(JSONObject json) throws APIException;

    /**
     * toJSON - Serialize a model to JSON
     * @return
     * @throws APIException
     */
    protected abstract JSONObject toJSON() throws APIException;

    /**
     * getCanonicalName - Get canonical representation of model name
     * @return
     */
    protected abstract String getCanonicalName();

}
