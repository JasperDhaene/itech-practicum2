package be.thalarion.eventman.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import be.thalarion.eventman.api.API;
import be.thalarion.eventman.api.APIException;

public abstract class Model {

    public static enum ACTION {
        EDIT, NEW
    }

    protected URI resource;

    public URI getResource() {
        return resource;
    }

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
                this.resource = new URI(response.getString("url"));
            } catch (URISyntaxException | JSONException e) {
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
            throw new APIException("Model must have a resource URI");

        fromJSON(API.getInstance().fetch(this.resource));
    }

    /**
     * destroy - Delete model instance
     * @throws IOException
     * @throws APIException
     */
    public void destroy() throws IOException, APIException {
        if(this.resource != null)
            API.getInstance().delete(this.resource);
    }

    /**
     * findAll - Retrieve and serialize list of all models from network
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
                        new URI(
                                jsonArray.getJSONObject(i).getString("url")
                        ));
                m = model.newInstance();
                m.resource = new URI(jsonObject.getString("url"));
                m.fromJSON(jsonObject);
                models.add(m);
            }
        } catch (URISyntaxException | JSONException | IllegalAccessException | InstantiationException e) {
            throw new APIException(e);
        }

        return models;
    }

    /**
     * find - Retrieve and serialize a model from network
     * @param model Model class object
     * @return model
     * @throws IOException
     * @throws APIException
     */
    public static <T extends Model> T find(Class<T> model, URI resource) throws IOException, APIException {
        T m;
        try {
            // GET /{model}/{id}
            JSONObject jsonObject = API.getInstance().fetch(resource);
            m = model.newInstance();
            m.resource = resource;
            m.fromJSON(jsonObject);

        } catch (IllegalAccessException | InstantiationException e) {
            throw new APIException(e);
        }

        return m;
    }

    /**
     * same - Compare a resource URI against a model
     * @param resource
     * @return
     */
    public boolean same(URI resource) {
        if(resource.toString().equals(this.resource.toString()) )
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

    @Override
    public boolean equals(Object o) {
        Model m = (Model) o;
        if(m.resource == null || this.resource == null)
            return false;

        return this.resource.equals(m.resource);
    }
}
