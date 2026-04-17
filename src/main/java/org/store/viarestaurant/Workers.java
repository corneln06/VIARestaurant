package org.store.viarestaurant;

public class Workers {
    private final int id;
    private String firstName;
    private String lastName;

    public Workers(int id,String fn, String ln)
    {
        this.id = id;
        this.firstName = fn;
        this.lastName = ln;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

