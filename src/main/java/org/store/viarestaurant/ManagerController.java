package org.store.viarestaurant;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

public class ManagerController
{
  private final SessionManager sessionManager = SessionManager.getInstance();

  @FXML private Label sidebarName;
  @FXML private Label sidebarRole;
  @FXML private TableView<MenuItems> menuTable;
  @FXML private TableColumn<MenuItems, String> nameColumn;
  @FXML private TableColumn<MenuItems, String> typeColumn;
  @FXML private TableColumn<MenuItems, String> priceColumn;
  @FXML private StackPane newDishOverlay;
  @FXML private TextField dishNameField;
  @FXML private TextField dishIngredientsField;
  @FXML private TextField dishPriceField;
  @FXML private ComboBox<MenuTypes> dishTypeComboBox;
  @FXML private Label newDishErrorLabel;

  @FXML
  private void initialize()
  {
    configureMenuTable();
    configureTypeComboBox();
    refreshMenuTable();
  }

  public void initData(Workers worker)
  {
    sidebarName.setText(worker.getFirstName() + " " + worker.getLastName());
    sidebarRole.setText("Manager");
    refreshMenuTable();
  }

  @FXML
  private void openNewDishModal()
  {
    dishNameField.clear();
    dishIngredientsField.clear();
    dishPriceField.clear();
    dishTypeComboBox.getSelectionModel().clearSelection();
    hideDishError();
    newDishOverlay.setVisible(true);
    newDishOverlay.setManaged(true);
  }

  @FXML
  private void createDish()
  {
    hideDishError();

    String name = dishNameField.getText() == null ? "" : dishNameField.getText().trim();

    if (name.isBlank())
    {
      showDishError("Dish name is required.");
      return;
    }

    double price;
    try
    {
      price = Double.parseDouble(dishPriceField.getText().trim());
    }
    catch (NumberFormatException exception)
    {
      showDishError("Price must be a valid number.");
      return;
    }

    MenuTypes type = dishTypeComboBox.getValue();
    if (type == null)
    {
      showDishError("Select a menu type.");
      return;
    }

    sessionManager.addMenuItem(name, price, type);
    refreshMenuTable();
    closeNewDishModalAction();
  }

  @FXML
  private void closeNewDishModal(MouseEvent event)
  {
    closeNewDishModalAction();
  }

  @FXML
  private void closeNewDishModalAction()
  {
    newDishOverlay.setVisible(false);
    newDishOverlay.setManaged(false);
  }

  @FXML
  private void consumeModalClick(MouseEvent event)
  {
    event.consume();
  }

  private void configureMenuTable()
  {
    nameColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
    typeColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
        formatType(cell.getValue().getType())
    ));
    priceColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
        String.format("$%.2f", cell.getValue().getPrice())
    ));
  }

  private void configureTypeComboBox()
  {
    dishTypeComboBox.setItems(FXCollections.observableArrayList(MenuTypes.values()));
    dishTypeComboBox.setConverter(new StringConverter<>()
    {
      @Override
      public String toString(MenuTypes type)
      {
        return type == null ? "" : formatType(type);
      }

      @Override
      public MenuTypes fromString(String string)
      {
        return null;
      }
    });
  }

  private void refreshMenuTable()
  {
    menuTable.setItems(FXCollections.observableArrayList(sessionManager.getMenu()));
  }

  private String formatType(MenuTypes type)
  {
    return switch (type)
    {
      case Starter -> "Starters";
      case Main -> "Mains";
      case Dessert -> "Dessert";
      case Beverage -> "Drinks";
      case AlcoholicBeverage -> "Bar";
    };
  }

  private void showDishError(String message)
  {
    newDishErrorLabel.setText(message);
    newDishErrorLabel.setVisible(true);
    newDishErrorLabel.setManaged(true);
  }

  private void hideDishError()
  {
    newDishErrorLabel.setVisible(false);
    newDishErrorLabel.setManaged(false);
  }
}
