package org.store.viarestaurant.server.dto.RestaurantTableDTO;

import org.store.viarestaurant.model.state.AvailableState;
import org.store.viarestaurant.model.state.TableState;

import java.io.Serializable;

public class TableCreateRequest implements Serializable {
  private final Integer maxSitting;
  private final TableState state;
  //private final InternalSchedule internalSchedule;

  public TableCreateRequest(Integer maxSitting) { //, InternalSchedule internalSchedule
    this.maxSitting = maxSitting;
    this.state = new AvailableState();
    // this.internalSchedule = internalSchedule;
  }

  public Integer getMaxSitting() {
    return maxSitting;
  }

  public TableState getState() {
    return state;
  }

  //public InternalSchedule getInternalSchedule(){return internalSchedule;}
}
