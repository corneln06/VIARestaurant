package org.store.viarestaurant.viewModel;

import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.model.entities.MenuItems;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.components.MenuItemComponent;
import org.store.viarestaurant.viewModel.components.ReservationComponent;
import org.store.viarestaurant.viewModel.components.TableComponent;

import java.io.IOException;

public class ManagerController
{
  private final ReservationComponent reservationComponent =
      new ReservationComponent();
  private final TableComponent tableComponent = new TableComponent();
  private final MenuItemComponent menuItemComponent = new MenuItemComponent();


    ////// MAIN
  public void init(
      GridPane scheduleGrid,
      Pane scheduleOverlayPane,
      GridPane tableGrid)
  {
    reservationComponent.initView(
        scheduleGrid,
        scheduleOverlayPane
    );

    tableComponent.initGrid(tableGrid);
  }

  public void initModal(
      StackPane overlay,
      TextField guestName,
      DatePicker datePicker,
      TextField timeField,
      TextField partySize,
      ComboBox<String> tableCombo,
      Label errorLabel,
      StackPane overlayNewDish,
      TextField nameField,
      ComboBox<String> typeCombo,
      TextField priceField,
      CheckBox vegetarianCheckBox,
      ListView<String> allergiesList,
      Label errorLabelNewDish,
      TableView<MenuItems> table,
      Button submit,
      Button delete,
      Label title)
  {
    reservationComponent.initModal(
        overlay, guestName, datePicker, timeField,
        partySize, tableCombo, errorLabel, submit, delete, title
    );

    menuItemComponent.initModal(
        overlayNewDish, nameField, typeCombo, priceField,
        vegetarianCheckBox, allergiesList, errorLabelNewDish, table
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
    tableComponent.initClient(client);
  }

  public void refreshSchedule()
  {
    reservationComponent.refreshSchedule();
  }

  public void loadTables()
  {
    tableComponent.loadTables();
  }

  public void openNewReservationModal()
  {
    reservationComponent.openReservationModal();
  }

  public void closeNewReservationModal()
  {
    reservationComponent.closeReservationModal();
  }
  public void deleteReservation() { reservationComponent.deleteReservation();}

  public void createReservation()
  {
    reservationComponent.createReservation();
  }

  public void refreshMenuTable() {
    menuItemComponent.refreshMenuTable();
  }

  public void openNewDishModal() {
    menuItemComponent.openNewDishModal();
  }

  public void closeNewDishModal() {
    menuItemComponent.closeNewDishModal();
  }

  public void closeTableModal(){
      tableComponent.closeTableModal();
  }

  public void refreshTableGrid(){
      tableComponent.refreshTableGrid();
  }
  
  public void createDish() { menuItemComponent.createDish();}
}
