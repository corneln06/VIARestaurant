package org.store.viarestaurant.viewModel;

import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.components.MenuItemComponent;
import org.store.viarestaurant.viewModel.components.ReservationComponent;
import org.store.viarestaurant.viewModel.components.TableComponent;

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
          TableView<Object> table,
          Button submit,
          Button delete,
          Label title,
          Button toggleButton,
          Button primaryButton,
          StackPane allergyOverlay,
          TextField allergyNameField,
          Label allergyErrorLabel, Button createAllergyButton, Button deleteAllergyButton,
          Label allergyModalTitle)
  {
    reservationComponent.initModal(
        overlay, guestName, datePicker, timeField,
        partySize, tableCombo, errorLabel, submit, delete, title
    );

    menuItemComponent.initModal(
        overlayNewDish, nameField, typeCombo, priceField,
        vegetarianCheckBox, allergiesList, errorLabelNewDish, table,
            toggleButton, primaryButton
    );
    menuItemComponent.initAllergyModal( allergyOverlay,
            allergyNameField,
            allergyErrorLabel,
            createAllergyButton,
            deleteAllergyButton,
            allergyModalTitle);
  }

  public void initTableModal(
            StackPane overlay,
            Label title,
            Label badge,
            Label info,
            StackPane newTableOverlay , TextField  tableMaxSittingField, Label newTableErrorLabel,
            ComboBox<String> waiterComboBox,
            Button primaryButton,
            Button deleteTable,
            Button submitTable
  )
    {
        tableComponent.initModal(overlay, title, badge, info, newTableOverlay, tableMaxSittingField, newTableErrorLabel, waiterComboBox, primaryButton, deleteTable,submitTable);
    }

  public void initClient(Client client)
  {
    reservationComponent.initClient(client);
    tableComponent.initClient(client);
    menuItemComponent.initClient(client);
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
    menuItemComponent.openPrimaryModal();
  }

  public void closeNewDishModal() {
    menuItemComponent.closeNewDishModal();
  }
  public void closeNewAllergyModal() {
    menuItemComponent.closeNewAllergyModal();
  }

  public void closeTableModal(){
      tableComponent.closeTableModal();
  }

  public void refreshTableGrid(){
      tableComponent.refreshTableGrid();
  }
  
  public void createDish() { menuItemComponent.createDish();}

  public void openNewTableModal() { tableComponent.openNewTableModal();}

  public void closeNewTableModal() { tableComponent.closeNewTableModal(); }

  public void createTable() { tableComponent.createTable();}

  public void toggleMenuView()
  {
    menuItemComponent.toggleMenuView();
  }

  public void createAllergy() {
    menuItemComponent.createAllergy();
  }

  public void deleteAllergy() {
    menuItemComponent.deleteAllergy();
  }

  public void deleteTable() { tableComponent.deleteTable(); }
}
