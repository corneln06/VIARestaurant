package org.store.viarestaurant.viewModel;

import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.components.ReservationComponent;
import org.store.viarestaurant.viewModel.components.TableComponent;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class HostController
{
  private final TableComponent tableComponent = new TableComponent();



  private final ReservationComponent reservationComponent =
      new ReservationComponent();

  public void init(
      GridPane scheduleGrid,
      Pane scheduleOverlayPane, GridPane tableGrid)
  {

    tableComponent.initGrid(tableGrid);

    reservationComponent.initView(
        scheduleGrid,
        scheduleOverlayPane
    );
  }

  public void initReservationModal(
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

  public void initTableModal(
      StackPane overlay,
      Label title,
      Label badge,
      Label info)
  {
    tableComponent.initModal(overlay, title, badge, info);
  }

  public void initClient(Client client)
  {
    reservationComponent.initClient(client);
  }

  //InitClient for Tables missing TODO

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

  //table component functionns

  public void refreshTableGrid(){
    tableComponent.refreshTableGrid();
  }

  public void closeTableModal(){
    tableComponent.closeTableModal();
  }



 

}