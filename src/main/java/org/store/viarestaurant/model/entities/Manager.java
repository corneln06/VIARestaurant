package org.store.viarestaurant.model.entities;

import org.store.viarestaurant.model.enums.WorkerRole;

public class Manager extends Workers
{

    //    private int id;
    //    private String firstName;
    //    private String lastName;
    private WorkerRole role;

    public Manager(String firstName, String lastName, String email, String rawPassword)
    {
        super(firstName,lastName,email,rawPassword);
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
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    public Integer getId() {
        return super.getId();
    }


    public WorkerRole getRole() {
        return role;
    }

  @Override public void setRole(WorkerRole role)
  {
    this.role = role;
  }
}
