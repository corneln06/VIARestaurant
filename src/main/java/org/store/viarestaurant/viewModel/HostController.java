package org.store.viarestaurant.viewModel;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.components.ReservationComponent;

public class HostController
{
  private final ReservationComponent reservationComponent =
      new ReservationComponent();

  public void init(
      GridPane scheduleGrid,
      Pane scheduleOverlayPane)
  {
    reservationComponent.initView(
        scheduleGrid,
        scheduleOverlayPane
    );
  }

  public void initModal(
      StackPane overlay,
      TextField guestName,
      DatePicker datePicker,
      TextField timeField,
      TextField partySize,
      ComboBox<String> tableCombo,
      Label errorLabel)
  {
    reservationComponent.initModal(
        overlay,
        guestName,
        datePicker,
        timeField,
        partySize,
        tableCombo,
        errorLabel
    );
  }

  public void initClient(Client client)
  {
    reservationComponent.initClient(client);
  }

  public void refreshSchedule()
  {
    reservationComponent.refreshSchedule();
  }

  public void openNewReservationModal()
  {
    reservationComponent.openReservationModal();
  }

  public void closeNewReservationModal()
  {
    reservationComponent.closeReservationModal();
  }

  public void createReservation()
  {
    reservationComponent.createReservation();
  }
}