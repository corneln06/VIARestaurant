package org.store.viarestaurant.server.dto.WorkersDTO;

import org.store.viarestaurant.model.enums.WorkerRole;

import java.io.Serializable;

public class CreateWorkerRequest implements Serializable
{
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String password;
  private final WorkerRole role;

  public CreateWorkerRequest(String firstName, String lastName, String email, String password, WorkerRole role)
  {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  public String getFirstName()  { return firstName; }
  public String getLastName()   { return lastName; }
  public String getEmail()      { return email; }
  public String getPassword()   { return password; }
  public WorkerRole getRole()   { return role; }
}
