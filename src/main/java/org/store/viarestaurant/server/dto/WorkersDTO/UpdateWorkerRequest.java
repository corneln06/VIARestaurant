package org.store.viarestaurant.server.dto.WorkersDTO;

import org.store.viarestaurant.model.enums.WorkerRole;

import java.io.Serializable;

public class UpdateWorkerRequest implements Serializable
{
  private final int id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final WorkerRole role;

  public UpdateWorkerRequest(int id, String firstName, String lastName, String email, WorkerRole role)
  {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.role = role;
  }

  public int getId()            { return id; }
  public String getFirstName()  { return firstName; }
  public String getLastName()   { return lastName; }
  public String getEmail()      { return email; }
  public WorkerRole getRole()   { return role; }
}