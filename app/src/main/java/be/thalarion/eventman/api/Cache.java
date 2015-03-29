package be.thalarion.eventman.api;


import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.thalarion.eventman.models.Model;

public class Cache {

    private static Map<Class<? extends Model>, List<? extends Model>> cache = new HashMap<>();

    public static <T extends Model> List<T> findAll(Class<T> model) throws IOException, APIException {
        if(!cache.containsKey(model))
            cache.put(model, Model.findAll(model));

        // we know it's safe, but the compiler can't prove it
        return (List<T>) cache.get(model);
    }

    public static <T extends Model> T find(Class<T> model, URL resource) throws IOException, APIException {
        List<T> list = findAll(model);

        for(T t: list)
            if(t.same(resource))
                return t;

        return null;
    }

    public static <T extends Model> void invalidate(Class<T> model) {
        cache.remove(model);
    }

}
