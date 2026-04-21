package org.store.viarestaurant.model.entities;

import org.store.viarestaurant.model.enums.WorkerRole;

public class Manager extends Workers
{

    //    private int id;
    //    private String firstName;
    //    private String lastName;
    private final WorkerRole role;

    public Manager(int id, String fn, String ln)
    {
        super(id,fn,ln);
        this.role = WorkerRole.Manager;
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

    public WorkerRole getRole() {
        return role;
    }
}
