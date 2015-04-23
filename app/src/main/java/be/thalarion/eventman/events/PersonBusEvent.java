package be.thalarion.eventman.events;

import be.thalarion.eventman.models.Person;

public class PersonBusEvent extends BusEvent {

    public PersonBusEvent(Person person, ACTION action) {
        super(person, action);
    }

}
