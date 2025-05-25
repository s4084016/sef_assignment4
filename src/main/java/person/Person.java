package person;

import java.util.HashMap;
import java.util.Date;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private HashMap<Date, Integer> demeritRecords;
    private boolean isSuspended;

    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.demeritRecords = new HashMap<>();
        this.isSuspended = false;
    }

    public boolean addPerson() {

        return true;
    }

    public boolean updatePersonalDetails(String personID, String firstName, String lastName, String address, String birthdate) {

        return true;
    }

    public String addDemeritPoints(String date, int points) {

        return "Success";
    }
}
