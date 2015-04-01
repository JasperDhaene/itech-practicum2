package be.thalarion.eventman.cache;

import java.util.Date;

public class Cacheable<T> {

    // Maximum cache age in seconds
    private static final int MAX_AGE = 20;

    private T cache;
    public Date date;

    public Cacheable(T cache) {
        this.cache = cache;
        this.date = new Date();
    }

    public T getCache() {
        return this.cache;
    }

    public boolean isValid() {
        return ((new Date().getTime() - this.date.getTime()) < MAX_AGE);
    }

}
