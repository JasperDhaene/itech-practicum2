package be.thalarion.eventman.cache;

import java.util.Date;

public class Cacheable<T> {

    // Maximum cache age in seconds
    private static final int MAX_AGE = 20;

    private T cache;
    private long time;

    public Cacheable(T cache) {
        this.cache = cache;
        this.time = new Date().getTime();
    }

    public T getCache() {
        return this.cache;
    }

    public boolean isValid() {
        return ((new Date().getTime() - this.time) < MAX_AGE);
    }

}
