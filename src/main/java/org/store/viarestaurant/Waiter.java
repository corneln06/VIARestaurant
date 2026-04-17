package org.store.viarestaurant;

public class Waiter extends Workers
{
//    private int id;
//    private String firstName;
//    private String lastName;

    public Waiter(int id, String fn, String ln)
    {
        super(id,fn,ln);
    }
    @Override
    public String getFirstName() {
        return super.getFirstName();
    }

    @Override
    public String getLastName() {
        return super.getLastName();
    }

    @Override
    public int getId() {
        return super.getId();
    }
}
