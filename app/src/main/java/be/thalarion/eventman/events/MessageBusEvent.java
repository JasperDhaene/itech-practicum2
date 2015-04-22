package be.thalarion.eventman.events;

import be.thalarion.eventman.models.Message;
import be.thalarion.eventman.models.Model;

public class MessageBusEvent extends BusEvent {

    public MessageBusEvent(Model model, ACTION action) {
        super(model, action);
    }

    public MessageBusEvent(Message message, ACTION action) {
        super(message, action);
    }
}
