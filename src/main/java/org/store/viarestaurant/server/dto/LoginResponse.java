package org.store.viarestaurant.server.dto;

import org.store.viarestaurant.model.entities.Workers;

import java.io.Serializable;

public class LoginResponse implements Serializable
{
  private boolean success;
  private Workers role;

  public LoginResponse(boolean success, Workers role){
    this.success = success;
    this.role = role;
  }

  public Workers getRole()
  {
    return role;
  }

  public boolean isSuccess()
  {
    return success;
  }
}
