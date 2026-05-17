package org.store.viarestaurant.viewModel;

import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.dao.AllergyDAOImpl;
import org.store.viarestaurant.dao.MenuItemDAOImpl;
import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.enums.MenuTypes;
import javafx.beans.property.SimpleStringProperty;

import java.sql.SQLException;
import java.util.ArrayList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.components.ReservationComponent;
import org.store.viarestaurant.viewModel.components.TableComponent;

public class ManagerController
{
  private final ReservationComponent reservationComponent =
      new ReservationComponent();
  private final TableComponent tableComponent = new TableComponent();

  private StackPane newDishOverlay;
  private TextField dishNameField;
  private ComboBox<String> dishTypeComboBox;
  private TextField dishPriceField;
  private CheckBox isVegetarianCheckBox;
  private ListView<String> allergiesListView;
  private Label newDishErrorLabel;
  private TableView<MenuItems> menuTable;

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
        overlay,
        guestName,
        datePicker,
        timeField,
        partySize,
        tableCombo,
        errorLabel,
            submit,
            delete,
            title
    );

    this.newDishOverlay = overlayNewDish;
    this.dishNameField = nameField;
    this.dishTypeComboBox = typeCombo;
    this.dishPriceField = priceField;
    this.isVegetarianCheckBox = vegetarianCheckBox;
    this.allergiesListView = allergiesList;
    this.newDishErrorLabel = errorLabelNewDish;
    this.menuTable = table;

    TableColumn<MenuItems, String> nameCol = (TableColumn<MenuItems, String>) table.getColumns().get(0);
    TableColumn<MenuItems, String> typeCol = (TableColumn<MenuItems, String>) table.getColumns().get(1);
    TableColumn<MenuItems, String> priceCol = (TableColumn<MenuItems, String>) table.getColumns().get(2);

    nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
    typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType().toString()));
    priceCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getPrice())));

    dishTypeComboBox.getItems().setAll(
        "Starter", "Main", "Dessert", "Beverage", "AlcoholicBeverage"
    );

    dishTypeComboBox.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText("Menu type");
          setStyle("-fx-text-fill: #9ea8c0;");
        } else {
          setText(item);
          setStyle("-fx-text-fill: #1d2440;");
        }
      }
    });
    allergiesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

  public void refreshMenuTable() {
    try {
      menuTable.getItems().setAll(MenuItemDAOImpl.getInstance().getAllMenuItems());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void openNewDishModal() {
    dishNameField.clear();
    dishPriceField.clear();
    dishTypeComboBox.getSelectionModel().clearSelection();
    isVegetarianCheckBox.setSelected(false);
    allergiesListView.getSelectionModel().clearSelection();
    populateAllergies();
    hideDishError();
    showOverlay(newDishOverlay);
  }

  public void closeNewDishModal() {
    hideOverlay(newDishOverlay);
  }

  public void createDish() {
    hideDishError();

    String name = dishNameField.getText() == null ? "" : dishNameField.getText().trim();
    if (name.isBlank()) { showDishError("Dish name is required."); return; }

    String typeStr = dishTypeComboBox.getValue();
    if (typeStr == null) { showDishError("Select a menu type."); return; }

    String priceText = dishPriceField.getText() == null ? "" : dishPriceField.getText().trim();
    double price;
    try {
      price = Double.parseDouble(priceText);
    } catch (NumberFormatException e) {
      showDishError("Price must be a number."); return;
    }

    boolean isVegetarian = isVegetarianCheckBox.isSelected();
    ArrayList<String> allergies = new ArrayList<>(allergiesListView.getSelectionModel().getSelectedItems());

    try {
      MenuItemDAOImpl.getInstance().createMenuItem(
          name, MenuTypes.valueOf(typeStr), price, isVegetarian, allergies
      );
      closeNewDishModal();
      refreshMenuTable();
    } catch (SQLException e) {
      showDishError("Database error: " + e.getMessage());
    }
  }

  private void populateAllergies() {
    try {
      allergiesListView.getItems().clear();
      AllergyDAOImpl.getInstance().getAllAllergies()
          .forEach(a -> allergiesListView.getItems().add(a.getName()));
    } catch (SQLException e) {
      showDishError("Could not load allergies.");
    }
  }

  private void showOverlay(StackPane overlay) {
    overlay.setVisible(true);
    overlay.setManaged(true);
  }

  private void hideOverlay(StackPane overlay) {
    overlay.setVisible(false);
    overlay.setManaged(false);
  }

  private void showDishError(String message) {
    newDishErrorLabel.setText(message);
    newDishErrorLabel.setVisible(true);
    newDishErrorLabel.setManaged(true);
  }

  private void hideDishError() {
    newDishErrorLabel.setVisible(false);
    newDishErrorLabel.setManaged(false);
  }

  public void closeTableModal(){
      tableComponent.closeTableModal();
  }

  public void refreshTableGrid(){
      tableComponent.refreshTableGrid();
  }
}
