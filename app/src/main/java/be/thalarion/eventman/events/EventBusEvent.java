package be.thalarion.eventman.events;

import be.thalarion.eventman.models.Event;
import be.thalarion.eventman.models.Model;

public class EventBusEvent extends BusEvent {

    public EventBusEvent(Model model, ACTION action) {
        super(model, action);
    }

    public EventBusEvent(Event event, ACTION action) {
        super(event, action);
    }

}
