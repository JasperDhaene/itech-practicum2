package be.thalarion.eventman.api;

import java.util.Date;

public class Event {

    public String title;
    public String description;
    public Date startDate;
    public Date endDate;

    public Event(String title, String description, Date startDate, Date endDate) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
