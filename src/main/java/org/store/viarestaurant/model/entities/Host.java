package org.store.viarestaurant.model.entities;

import org.store.viarestaurant.model.enums.WorkerRole;

public class Host extends Workers
{
    //    private int id;
    //    private String firstName;
    //    private String lastName;
    private WorkerRole role;

    public Host(int id, String fn, String ln)
    {
        super(id,fn,ln);
        this.role = WorkerRole.Host;
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

    public void setRole(WorkerRole role) {
        this.role = role;
    }
}
