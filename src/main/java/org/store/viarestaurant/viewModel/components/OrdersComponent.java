package org.store.viarestaurant.viewModel.components;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.entities.TableOrder;
import org.store.viarestaurant.model.enums.MenuTypes;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.services.OrdersService;

import java.util.*;

public class OrdersComponent
{
  private TilePane menuItemsPane;
  private FlowPane stagedItemsFlow;
  private Button addToOrderButton;
  private StackPane orderPickerOverlay;
  private ComboBox<String> orderComboBox;
  private Label orderPickerErrorLabel;

  private OrdersService ordersService;
  private int currentWaiterId;

  private List<MenuItems> allMenuItems = new ArrayList<>();
  private List<MenuItems> stagedItems  = new ArrayList<>();
  private List<TableOrder> openOrders  = new ArrayList<>();
  private final Map<String, TableOrder> orderDisplayMap = new LinkedHashMap<>();
  private String activeFilter = "All";

  public void init(
      TilePane menuItemsPane,
      FlowPane stagedItemsFlow,
      Button addToOrderButton,
      StackPane orderPickerOverlay,
      ComboBox<String> orderComboBox,
      Label orderPickerErrorLabel,
      ToggleButton filterAllBtn,
      ToggleButton filterStartersBtn,
      ToggleButton filterMainsBtn,
      ToggleButton filterDessertsBtn,
      ToggleButton filterDrinksBtn)
  {
    this.menuItemsPane        = menuItemsPane;
    this.stagedItemsFlow      = stagedItemsFlow;
    this.addToOrderButton     = addToOrderButton;
    this.orderPickerOverlay   = orderPickerOverlay;
    this.orderComboBox        = orderComboBox;
    this.orderPickerErrorLabel = orderPickerErrorLabel;

    // Only one filter chip active at a time — ToggleGroup handles mutual exclusion.
    ToggleGroup filterGroup = new ToggleGroup();
    filterAllBtn.setToggleGroup(filterGroup);
    filterStartersBtn.setToggleGroup(filterGroup);
    filterMainsBtn.setToggleGroup(filterGroup);
    filterDessertsBtn.setToggleGroup(filterGroup);
    filterDrinksBtn.setToggleGroup(filterGroup);
    filterAllBtn.setSelected(true);
  }

  public void initClient(Client client, int waiterId)
  {
    this.currentWaiterId = waiterId;
    this.ordersService   = new OrdersService(client);

    ordersService.onMenuItemsLoaded(response -> {
      allMenuItems = new ArrayList<>(response.getMenuItems());
      buildMenuGrid(activeFilter);
    });

    ordersService.onOrdersByWaiterLoaded(response -> {
      openOrders = new ArrayList<>(response.getOrders());
      populateOrderComboBox();
    });

    // On success: clear the cart and close the picker.
    // On failure: show the error message inside the picker modal.
    ordersService.onItemsAdded(response -> {
      if (response.isSuccess())
      {
        stagedItems.clear();
        buildStagedPanel();
        closeOrderPicker();
      }
      else
      {
        showPickerError(response.getMessage());
      }
    });
  }

  // Called by WaiterController when the Orders tab is shown.
  public void loadData()
  {
    ordersService.loadMenuItems();
    ordersService.loadOrdersByWaiter(currentWaiterId);
  }

  // Called from NavigationController's filter chip handlers.
  public void applyFilter(String filter)
  {
    this.activeFilter = filter;
    buildMenuGrid(filter);
  }

  private void buildMenuGrid(String filter)
  {
    if (menuItemsPane == null) return;
    menuItemsPane.getChildren().clear();

    for (MenuItems item : allMenuItems)
    {
      if (matchesFilter(item, filter))
        menuItemsPane.getChildren().add(buildMenuCard(item));
    }
  }

  private boolean matchesFilter(MenuItems item, String filter)
  {
    return switch (filter) {
      case "Starters" -> item.getType() == MenuTypes.Starter;
      case "Mains"    -> item.getType() == MenuTypes.Main;
      case "Desserts" -> item.getType() == MenuTypes.Dessert;
      case "Drinks"   -> item.getType() == MenuTypes.Beverage
                      || item.getType() == MenuTypes.AlcoholicBeverage;
      default         -> true;
    };
  }

  private VBox buildMenuCard(MenuItems item)
  {
    VBox card = new VBox(6);
    card.getStyleClass().add("menu-item-tile");
    card.setPrefWidth(180);

    // Highlight the card if this item is already in the staged list.
    boolean staged = stagedItems.stream().anyMatch(s -> s.getId() == item.getId());
    if (staged) card.getStyleClass().add("menu-item-tile-staged");

    Label name = new Label(item.getName());
    name.getStyleClass().add("tile-title");
    name.setWrapText(true);

    Label price = new Label(String.format("DKK %.2f", item.getPrice()));
    price.getStyleClass().add("tile-price");

    Label type = new Label(item.getType().toString());
    type.getStyleClass().add("tile-meta");

    card.getChildren().addAll(name, price, type);
    card.setOnMouseClicked(e -> toggleStaged(item));
    return card;
  }

  // Clicking a card toggles the item in/out of the staged list.
  // Rebuilding the grid refreshes the highlight on all cards.
  private void toggleStaged(MenuItems item)
  {
    boolean alreadyIn = stagedItems.stream().anyMatch(s -> s.getId() == item.getId());
    if (alreadyIn) stagedItems.removeIf(s -> s.getId() == item.getId());
    else           stagedItems.add(item);

    buildStagedPanel();
    buildMenuGrid(activeFilter);
  }

  private void buildStagedPanel()
  {
    stagedItemsFlow.getChildren().clear();

    for (MenuItems item : stagedItems)
    {
      HBox chip = new HBox(8);
      chip.getStyleClass().add("staged-chip");

      Label nameLabel = new Label(item.getName());
      nameLabel.getStyleClass().add("staged-chip-label");

      Label removeBtn = new Label("×");
      removeBtn.getStyleClass().add("staged-chip-remove");
      removeBtn.setOnMouseClicked(e -> {
        e.consume(); // prevent the click from bubbling to the card
        toggleStaged(item);
      });

      chip.getChildren().addAll(nameLabel, removeBtn);
      stagedItemsFlow.getChildren().add(chip);
    }

    // Disable the button when there's nothing staged.
    addToOrderButton.setDisable(stagedItems.isEmpty());
  }

  // Opens the order picker modal. Refreshes the order list first so it
  // reflects any tables that were seated after the Orders tab was opened.
  public void openOrderPicker()
  {
    if (stagedItems.isEmpty()) return;
    hidePickerError();
    orderComboBox.getSelectionModel().clearSelection();
    ordersService.loadOrdersByWaiter(currentWaiterId);
    orderPickerOverlay.setVisible(true);
    orderPickerOverlay.setManaged(true);
  }

  public void closeOrderPicker()
  {
    if (orderPickerOverlay == null) return;
    orderPickerOverlay.setVisible(false);
    orderPickerOverlay.setManaged(false);
  }

  private void populateOrderComboBox()
  {
    orderDisplayMap.clear();
    orderComboBox.getItems().clear();

    for (TableOrder order : openOrders)
    {
      String label = "Table " + order.getTable().getId()
          + "  (Order #" + order.getId() + ")";
      orderDisplayMap.put(label, order);
      orderComboBox.getItems().add(label);
    }
  }

  public void confirmAddToOrder()
  {
    hidePickerError();
    String selected = orderComboBox.getValue();
    if (selected == null)
    {
      showPickerError("Please select an order.");
      return;
    }
    TableOrder order = orderDisplayMap.get(selected);
    if (order == null)
    {
      showPickerError("Invalid order selection.");
      return;
    }

    ArrayList<String> names = new ArrayList<>();
    for (MenuItems item : stagedItems) names.add(item.getName());

    ordersService.addItemsToOrder(order.getId(), names);
    // Success path is handled in the onItemsAdded callback.
  }

  private void showPickerError(String message)
  {
    orderPickerErrorLabel.setText(message);
    orderPickerErrorLabel.setVisible(true);
    orderPickerErrorLabel.setManaged(true);
  }

  private void hidePickerError()
  {
    orderPickerErrorLabel.setVisible(false);
    orderPickerErrorLabel.setManaged(false);
  }
}
