package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.InternalSchedule;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.entities.TableOrder;
import org.store.viarestaurant.model.state.TableState;

import java.io.Serializable;

public class TableCreateRequest implements Serializable
{
  private final Integer maxSitting;
  private final TableState state;
  private final InternalSchedule internalSchedule;

  public TableCreateRequest(Integer maxSitting, TableState state, InternalSchedule internalSchedule){
    this.maxSitting = maxSitting;
    this.state = state;
    this.internalSchedule = internalSchedule;
  }

  public Integer getMaxSitting()
  {
    return maxSitting;
  }

  public TableState getState()
  {
    return state;
  }

  public InternalSchedule getInternalSchedule()
  {
    return internalSchedule;
  }
}
