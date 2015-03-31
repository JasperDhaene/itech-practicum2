package be.thalarion.eventman.api;

import android.util.Log;

import java.util.Date;
import java.util.List;

public class CacheObject<T extends List> {

    // Maximum cache age in seconds
    private static final int MAX_AGE = 60;

    private T t;
    private Date date;

    public CacheObject (T t) {
        setCache(t);
    }

    public void setCache(T t) {
        this.t = t;
        this.date = new Date();
    }

    public T getCache() {
        return this.t;
    }

    public boolean isValid() {
        return ((new Date().getTime() - this.date.getTime()) < MAX_AGE);
    }

}
