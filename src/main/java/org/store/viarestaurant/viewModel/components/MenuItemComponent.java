package org.store.viarestaurant.viewModel.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.dao.AllergyDAOImpl;
import org.store.viarestaurant.dao.MenuItemDAOImpl;
import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.enums.MenuTypes;

import java.sql.SQLException;
import java.util.ArrayList;

public class MenuItemComponent
{
  private StackPane newDishOverlay;
  private TextField dishNameField;
  private ComboBox<String> dishTypeComboBox;
  private TextField dishPriceField;
  private CheckBox isVegetarianCheckBox;
  private ListView<String> allergiesListView;
  private Label newDishErrorLabel;
  private TableView<MenuItems> menuTable;
  private boolean isEditMode = false;
  private MenuItems editingMenuItem;
  private Button submitDishButton;
  private Button deleteDishButton;
  private Label dishModalTitle;

  public void initModal(
      StackPane overlay,
      TextField nameField,
      ComboBox<String> typeCombo,
      TextField priceField,
      CheckBox vegetarianCheckBox,
      ListView<String> allergiesList,
      Label errorLabel,
      TableView<MenuItems> table,
      Button submitButton,
      Button deleteButton,
      Label modalTitle)
  {
    this.newDishOverlay = overlay;
    this.dishNameField = nameField;
    this.dishTypeComboBox = typeCombo;
    this.dishPriceField = priceField;
    this.isVegetarianCheckBox = vegetarianCheckBox;
    this.allergiesListView = allergiesList;
    this.newDishErrorLabel = errorLabel;
    this.menuTable = table;
    this.submitDishButton = submitButton;
    this.deleteDishButton = deleteButton;
    this.dishModalTitle = modalTitle;

    TableColumn<MenuItems, String> nameCol =
        (TableColumn<MenuItems, String>) table.getColumns().get(0);
    TableColumn<MenuItems, String> typeCol =
        (TableColumn<MenuItems, String>) table.getColumns().get(1);
    TableColumn<MenuItems, String> priceCol =
        (TableColumn<MenuItems, String>) table.getColumns().get(2);

    nameCol.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getName()));
    typeCol.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getType().toString()));
    priceCol.setCellValueFactory(data ->
        new SimpleStringProperty(String.format("%.2f", data.getValue().getPrice())));

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

    table.setRowFactory(tv -> {
      TableRow<MenuItems> row = new TableRow<>();
      row.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2 && !row.isEmpty()){
          openMenuAsForm(row.getItem());
        }
      });
      return row;
    });
  }

  private void openMenuAsForm(MenuItems item)
  {
    setEditMode(true);
    editingMenuItem = item;
    dishNameField.setText(item.getName());
    dishTypeComboBox.setValue(item.getType().toString());
    dishPriceField.setText(String.valueOf(item.getPrice()));
    isVegetarianCheckBox.setSelected(item.isVegetarian());

    populateAllergies();
    hideError();
    showOverlay(newDishOverlay);
  }

  public void refreshMenuTable()
  {
    try {
      menuTable.getItems().setAll(MenuItemDAOImpl.getInstance().getAllMenuItems());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void openNewDishModal()
  {
    setEditMode(false);
    dishNameField.clear();
    dishPriceField.clear();
    dishTypeComboBox.getSelectionModel().clearSelection();
    isVegetarianCheckBox.setSelected(false);
    allergiesListView.getSelectionModel().clearSelection();
    populateAllergies();
    hideError();
    showOverlay(newDishOverlay);
  }

  public void closeNewDishModal()
  {
    hideOverlay(newDishOverlay);
  }

  public void createDish()
  {
    hideError();

    String name = dishNameField.getText() == null ? "" : dishNameField.getText().trim();
    if (name.isBlank()) { showError("Dish name is required."); return; }

    String typeStr = dishTypeComboBox.getValue();
    if (typeStr == null) { showError("Select a menu type."); return; }

    String priceText = dishPriceField.getText() == null ? "" : dishPriceField.getText().trim();
    double price;
    try {
      price = Double.parseDouble(priceText);
    } catch (NumberFormatException e) {
      showError("Price must be a number."); return;
    }

    boolean isVegetarian = isVegetarianCheckBox.isSelected();
    ArrayList<String> allergies =
        new ArrayList<>(allergiesListView.getSelectionModel().getSelectedItems());

    if (isEditMode){
      try {
        MenuItems updated = new MenuItems(
            editingMenuItem.getId(), name, MenuTypes.valueOf(typeStr), price, isVegetarian, allergies
        );
        MenuItemDAOImpl.getInstance().updateMenuItem(updated);
        closeNewDishModal();
        refreshMenuTable();
      }
      catch (SQLException e){
        showError("Database Error: " + e.getMessage());
      }
      return;
    }
    try {
      MenuItemDAOImpl.getInstance().createMenuItem(
          name, MenuTypes.valueOf(typeStr), price, isVegetarian, allergies
      );
      closeNewDishModal();
      refreshMenuTable();
    } catch (SQLException e) {
      showError("Database error: " + e.getMessage());
    }
  }

  private void populateAllergies()
  {
    try {
      allergiesListView.getItems().clear();
      AllergyDAOImpl.getInstance().getAllAllergies()
          .forEach(a -> allergiesListView.getItems().add(a.getName()));
    } catch (SQLException e) {
      showError("Could not load allergies.");
    }
  }

  private void showOverlay(StackPane overlay)
  {
    overlay.setVisible(true);
    overlay.setManaged(true);
  }

  private void hideOverlay(StackPane overlay)
  {
    overlay.setVisible(false);
    overlay.setManaged(false);
  }

  private void showError(String message)
  {
    newDishErrorLabel.setText(message);
    newDishErrorLabel.setVisible(true);
    newDishErrorLabel.setManaged(true);
  }

  private void hideError()
  {
    newDishErrorLabel.setVisible(false);
    newDishErrorLabel.setManaged(false);
  }
  private void setEditMode(boolean edit) {
    isEditMode = edit;
    submitDishButton.setText(edit ? "Apply" : "Create");
    deleteDishButton.setVisible(edit);
    deleteDishButton.setManaged(edit);
    dishModalTitle.setText(edit ? "Edit Dish" : "New Dish");
  }

  public void deleteDish(){
    if (editingMenuItem == null){
      showError("No dish selected");
      return;
    }
    try {
      MenuItemDAOImpl.getInstance().delete(editingMenuItem.getId());
      closeNewDishModal();
      refreshMenuTable();
    }
    catch (SQLException e){
      showError("Database error: " + e.getMessage());
    }

  }
}