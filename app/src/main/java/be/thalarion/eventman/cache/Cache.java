package be.thalarion.eventman.cache;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.thalarion.eventman.api.APIException;
import be.thalarion.eventman.models.Model;

public class Cache {

    private static Map<URL, Cacheable<? extends Model>> objectCache = new HashMap();
    private static Map<Class<? extends Model>, Cacheable<List<URL>>> listCache = new HashMap();

    /**
     * findAll - Find all model resources in cache
     * @param model
     * @param <T>
     * @return
     * @throws IOException
     * @throws APIException
     */
    public static <T extends Model> List<T> findAll(Class<T> model) throws IOException, APIException {
        if(!listCache.containsKey(model)) {
            List<T> models = Model.findAll(model);
            List<URL> urls = new ArrayList<>();
            for(T t: models) {
                objectCache.put(t.getResource(), new Cacheable<T>(t));
                urls.add(t.getResource());
            }
            listCache.put(model, new Cacheable(urls));
        }

        if(!listCache.get(model).isValid()) {
            invalidate(model);
            return findAll(model);
        }

        List<T> models = new ArrayList<>();
        for(URL url: listCache.get(model).getCache()) {
            models.add((T) find(model, url));
        }
        return models;
    }

    /**
     * find - Find a single resource in cache
     * @param model
     * @param resource
     * @param <T>
     * @return
     * @throws IOException
     * @throws APIException
     */
    public static <T extends Model> T find(Class<? extends Model> model, URL resource) throws IOException, APIException {
        if(!objectCache.containsKey(resource)) {
            objectCache.put(resource, new Cacheable(Model.find(model, resource)));
        }

        Cacheable<? extends Model> c = objectCache.get(resource);

        if(!c.isValid()) {
            invalidate(resource);
            return find(model, resource);
        }

        return (T) c.getCache();
    }

    /**
     * invalidate - Remove all model resources from cache (cascaded)
     * @param model
     */
    public static void invalidate(Class<? extends Model> model) {
        if(listCache.containsKey(model)) {
            for (URL url : listCache.get(model).getCache()) {
                invalidate(url);
            }
            listCache.remove(model);
        }
    }

    /**
     * invalidate - Remove a resource from cache
     * @param resource
     */
    public static void invalidate(URL resource) {
        objectCache.remove(resource);
    }

}
