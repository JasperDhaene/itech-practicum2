package be.thalarion.eventman.events;

import be.thalarion.eventman.models.Person;
import be.thalarion.eventman.models.Model;

public class PersonBusEvent extends BusEvent {

    public PersonBusEvent(Model model, ACTION action) {
        super(model, action);
    }

    public PersonBusEvent(Person person, ACTION action) {
        super(person, action);
    }
}
