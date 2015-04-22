package be.thalarion.eventman.events;

import be.thalarion.eventman.models.Model;

public abstract class BusEvent {

    public enum ACTION {
        CREATE, UPDATE, DELETE;
    }

    public final Model model;
    public final ACTION action;

    public BusEvent(Model model, ACTION action) {
        this.model = model;
        this.action = action;
    }

}
