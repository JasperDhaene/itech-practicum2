package be.thalarion.eventman.api;

import java.util.Date;

public class Person {

    public String name;
    public String email;
    public Date birthDate;

    public Person(String name, String email, Date birthDate) {
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
    }
}
