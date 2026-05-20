package org.store.viarestaurant.viewModel.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.dao.AllergyDAOImpl;
import org.store.viarestaurant.dao.MenuItemDAOImpl;
import org.store.viarestaurant.model.entities.Allergy;
import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.enums.MenuTypes;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.AllergyDTO.GetAllergiesRequest;
import org.store.viarestaurant.viewModel.services.AllergyService;

import java.io.IOException;
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
  private TableView<Object> menuTable;

  private Button toggleButton;
  private Button primaryButton;

  private StackPane newAllergyOverlay;
  private TextField allergyNameField;
  private Label newAllergyErrorLabel;

  private AllergyService allergyService;

  private Allergy selectedAllergy;
  private boolean editingAllergy = false;

  private Button createAllergyButton;
  private Button deleteAllergyButton;


  private boolean showingAllergies = false;

  ///////////////////////////////////
  ////////////// INITIALIZATIONS
  /// //////////////////////////
  public void initModal(
      StackPane overlay,
      TextField nameField,
      ComboBox<String> typeCombo,
      TextField priceField,
      CheckBox vegetarianCheckBox,
      ListView<String> allergiesList,
      Label errorLabel,
      TableView<Object> table,
      Button toggleB,
      Button primaryB
  )
  {
    this.newDishOverlay = overlay;
    this.dishNameField = nameField;
    this.dishTypeComboBox = typeCombo;
    this.dishPriceField = priceField;
    this.isVegetarianCheckBox = vegetarianCheckBox;
    this.allergiesListView = allergiesList;
    this.newDishErrorLabel = errorLabel;
    this.menuTable = table;
    this.toggleButton = toggleB;
    this.primaryButton = primaryB;

    configureMenuColumns();
    initAllergyRowDoubleClick();

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

  public void initAllergyModal(
          StackPane overlay,
          TextField nameField,
          Label errorLabel,
          Button createAllergyButton,
          Button deleteAllergyButton
  )
  {
    this.newAllergyOverlay = overlay;
    this.allergyNameField = nameField;
    this.newAllergyErrorLabel = errorLabel;
    this.createAllergyButton = createAllergyButton;
    this.deleteAllergyButton = deleteAllergyButton;
  }

  public void initClient(Client client)
  {
    this.allergyService = new AllergyService(client);

    this.allergyService.onAllergiesLoaded(response ->
    {
      menuTable.getItems().setAll(response.getAllergies());
    });

    this.allergyService.onAllergyCreated(response ->
    {
      try {
        loadAllergyTable();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    this.allergyService.onAllergyUpdated(response ->
    {
      try {
        loadAllergyTable();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    this.allergyService.onAllergyDeleted(response ->
    {
      try {
        loadAllergyTable();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }
  private void initAllergyRowDoubleClick()
  {
    menuTable.setRowFactory(tv -> {
      TableRow<Object> row = new TableRow<>();

      row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && !row.isEmpty() && showingAllergies)
        {
          Allergy allergy = (Allergy) row.getItem();
          openEditAllergyModal(allergy);
        }
      });

      return row;
    });
  }
/////////////////////////////////////
/// ////////// OPEN / CLOSE MODALS
/// //////////////////////////////////

  private void openNewDishModal()
  {
    dishNameField.clear();
    dishPriceField.clear();
    dishTypeComboBox.getSelectionModel().clearSelection();
    isVegetarianCheckBox.setSelected(false);
    allergiesListView.getSelectionModel().clearSelection();
    populateAllergies();
    hideError();
    showOverlay(newDishOverlay);
  }

    public void openPrimaryModal() {

    if (isShowingAllergies())
    {
      openNewAllergyModal();
    }
    else
    {
      openNewDishModal();
    }
    }

  private void openNewAllergyModal()
  {
    selectedAllergy = null;
    setEditingAllergy(false);

    allergyNameField.clear();

    createAllergyButton.setText("Create");
    createAllergyButton.setVisible(true);
    createAllergyButton.setManaged(true);

    deleteAllergyButton.setVisible(false);
    deleteAllergyButton.setManaged(false);

    hideAllergyError();
    showOverlay(newAllergyOverlay);
  }

  public void closeNewDishModal()
  {
    hideOverlay(newDishOverlay);
  }
  public void closeNewAllergyModal()
  {
    hideOverlay(newAllergyOverlay);
  }

  private void openEditAllergyModal(Allergy allergy)
  {
    this.selectedAllergy = allergy;
    setEditingAllergy(true);

    allergyNameField.setText(allergy.getName());


    createAllergyButton.setText("Apply");
    createAllergyButton.setVisible(true);
    createAllergyButton.setManaged(true);

    deleteAllergyButton.setVisible(true);
    deleteAllergyButton.setManaged(true);

    hideAllergyError();
    showOverlay(newAllergyOverlay);
  }

  //////////////////////////////////////////////
  /// //////////// CREATE ITEMS
  /// ////////////////////////////////////////

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

  public void createAllergy()
  {
    String name = allergyNameField.getText();

    if (name == null || name.isBlank())
    {
      showAllergyError("Name required");
      return;
    }

    try
    {
      if (editingAllergy)
      {
        allergyService.updateAllergy(selectedAllergy.getId(), name);
      }
      else
      {
        allergyService.createAllergy(name);
      }

      closeNewAllergyModal();
    }
    catch (IOException e)
    {
      showAllergyError("Network error: " + e.getMessage());
    }
  }

  /////////////////////////////////
  /// ////////// DELETE ITEMS
  /// ///////////////////////////////


  public void deleteAllergy()
  {
    if (selectedAllergy == null)
    {
      showAllergyError("No allergy selected");
      return;
    }

    try
    {
      allergyService.deleteAllergy(selectedAllergy.getId());
      closeNewAllergyModal();
    }
    catch (IOException e)
    {
      showAllergyError("Network error: " + e.getMessage());
    }
  }
  //////////////////////////////////////////////
  /// ///////// TABLE LOAD
  ///////////////////////////////////////

  public void refreshMenuTable()
  {

    try {
      configureMenuColumns();
      menuTable.getItems().setAll(MenuItemDAOImpl.getInstance().getAllMenuItems());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  private void loadAllergyTable() throws IOException
  {
    configureAllergyColumns();
    allergyService.loadAllergies();
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



  ////////////////////////////////////////////
  /// //////////// TABLE CONFIGURATIONS
  /// ////////////////////////////////////////

  @SuppressWarnings("unchecked")
private void configureMenuColumns()
{
  TableColumn<Object, String> nameCol =
          (TableColumn<Object, String>) menuTable.getColumns().get(0);

  TableColumn<Object, String> typeCol =
          (TableColumn<Object, String>) menuTable.getColumns().get(1);

  TableColumn<Object, String> priceCol =
          (TableColumn<Object, String>) menuTable.getColumns().get(2);

  nameCol.setText("Name");
  typeCol.setText("Type");
  priceCol.setText("Price");

  nameCol.setCellValueFactory(data ->
  {
    MenuItems item = (MenuItems) data.getValue();
    return new SimpleStringProperty(item.getName());
  });

  typeCol.setCellValueFactory(data ->
  {
    MenuItems item = (MenuItems) data.getValue();
    return new SimpleStringProperty(item.getType().toString());
  });

  priceCol.setCellValueFactory(data ->
  {
    MenuItems item = (MenuItems) data.getValue();
    return new SimpleStringProperty(
            String.format("%.2f", item.getPrice()));
  });
}

  @SuppressWarnings("unchecked")
  private void configureAllergyColumns()
  {
    TableColumn<Object, String> col1 =
            (TableColumn<Object, String>) menuTable.getColumns().get(0);

    TableColumn<Object, String> col2 =
            (TableColumn<Object, String>) menuTable.getColumns().get(1);
    TableColumn<Object, String> col3 =
            (TableColumn<Object, String>) menuTable.getColumns().get(2);


    col1.setText("ID");
    col2.setText("Name");
    col3.setText("-");


    col1.setCellValueFactory(data ->
    {
      Allergy allergy = (Allergy) data.getValue();
      return new SimpleStringProperty(
              String.valueOf(allergy.getId()));
    });

    col2.setCellValueFactory(data ->
    {
      Allergy allergy = (Allergy) data.getValue();
      return new SimpleStringProperty(allergy.getName());
    });
    col3.setCellValueFactory(data ->
            null);
  }

  ///////////////////////////
  /// ////// UI HELPERS
/////////////////////////////////

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


  public void toggleMenuView()
  {
    setShowingAllergies(!showingAllergies);

    try
    {
      if (showingAllergies)
      {
        loadAllergyTable();
        toggleButton.setText("Show Menu");
        primaryButton.setText("+ New Allergy");
      }
      else
      {
        refreshMenuTable();
        toggleButton.setText("Show Allergies");
        primaryButton.setText("+ New Dish");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }


  ///////////////////////////
  ////////////// ERRORS
  //////////////////////////

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

  private void showAllergyError(String msg)
  {
    newAllergyErrorLabel.setText(msg);
    newAllergyErrorLabel.setVisible(true);
    newAllergyErrorLabel.setManaged(true);
  }

  private void hideAllergyError()
  {
    newAllergyErrorLabel.setVisible(false);
    newAllergyErrorLabel.setManaged(false);
  }

  ///////////////////////////////////////
  ////////// BOOLEANS CHECKERS SETTERS
  //////////////////////////////////////

  public boolean isShowingAllergies() {
    return showingAllergies;
  }

  public void setShowingAllergies(boolean showingAllergies) {
    this.showingAllergies = showingAllergies;
  }

  public boolean isEditingAllergy() {
    return editingAllergy;
  }
  public void setEditingAllergy(boolean edit) { this.editingAllergy = edit; }


}