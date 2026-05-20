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
  // List page — table picker
  private FlowPane ordersTableFlow;

  // Detail page — menu grid + order summary
  private TilePane menuItemsPane;
  private Label    selectedOrderTableLabel;
  private VBox     orderSummaryLinesBox;
  private Label    orderSubtotalLabel;
  private Label    roundLabel;
  private Button   sendToKitchenButton;

  private Runnable navigateToDetail;
  private Runnable navigateToList;

  private OrdersService ordersService;
  private int currentWaiterId;

  private List<MenuItems>  allMenuItems = new ArrayList<>();
  private List<MenuItems>  stagedItems  = new ArrayList<>();
  private List<TableOrder> openOrders   = new ArrayList<>();
  private TableOrder       selectedOrder;
  private String           activeFilter = "All";

  public void init(
      FlowPane ordersTableFlow,
      TilePane menuItemsPane,
      Label selectedOrderTableLabel,
      VBox orderSummaryLinesBox,
      Label orderSubtotalLabel,
      Label roundLabel,
      Button sendToKitchenButton,
      ToggleButton filterAllBtn,
      ToggleButton filterStartersBtn,
      ToggleButton filterMainsBtn,
      ToggleButton filterDessertsBtn,
      ToggleButton filterDrinksBtn,
      Runnable navigateToDetail,
      Runnable navigateToList)
  {
    this.ordersTableFlow          = ordersTableFlow;
    this.menuItemsPane            = menuItemsPane;
    this.selectedOrderTableLabel  = selectedOrderTableLabel;
    this.orderSummaryLinesBox     = orderSummaryLinesBox;
    this.orderSubtotalLabel       = orderSubtotalLabel;
    this.roundLabel               = roundLabel;
    this.sendToKitchenButton      = sendToKitchenButton;
    this.navigateToDetail         = navigateToDetail;
    this.navigateToList           = navigateToList;

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
      // Grid is not built until the waiter selects a table.
    });

    ordersService.onOrdersByWaiterLoaded(response -> {
      openOrders = new ArrayList<>(response.getOrders());
      buildTableList();
    });

    ordersService.onItemsAdded(response -> {
      if (response.isSuccess())
      {
        stagedItems.clear();
        // Re-fetch so the summary reflects the updated bill.
        ordersService.loadOrdersByWaiter(currentWaiterId);
        if (selectedOrder != null) buildOrderSummary();
      }
    });

    ordersService.onOrderPaid(response -> {
      if (response.isSuccess())
      {
        selectedOrder = null;
        stagedItems.clear();
        ordersService.loadOrdersByWaiter(currentWaiterId);
        navigateToList.run();
      }
    });
  }

  // Called by WaiterController when the Orders tab is shown.
  public void loadData()
  {
    selectedOrder = null;
    stagedItems.clear();
    ordersService.loadMenuItems();
    ordersService.loadOrdersByWaiter(currentWaiterId);
  }

  // Called from NavigationController's filter chip handlers.
  public void applyFilter(String filter)
  {
    this.activeFilter = filter;
    buildDetailMenuGrid(filter);
  }

  // ── List page ─────────────────────────────────────────────────────────────

  private void buildTableList()
  {
    if (ordersTableFlow == null) return;
    ordersTableFlow.getChildren().clear();
    for (TableOrder order : openOrders)
      ordersTableFlow.getChildren().add(buildTableCard(order));
  }

  private VBox buildTableCard(TableOrder order)
  {
    VBox card = new VBox(8);
    card.getStyleClass().add("order-table-card");

    Label tableLabel = new Label("Table " + order.getTable().getId());
    tableLabel.getStyleClass().add("card-title");

    Label orderLabel = new Label("Order #" + order.getId());
    orderLabel.getStyleClass().add("card-subtitle");

    Label billLabel = new Label(String.format("DKK %.2f", order.getBill()));
    billLabel.getStyleClass().add("muted-label");

    card.getChildren().addAll(tableLabel, orderLabel, billLabel);
    card.setOnMouseClicked(e -> selectOrder(order));
    return card;
  }

  private void selectOrder(TableOrder order)
  {
    this.selectedOrder = order;
    this.activeFilter  = "All";
    stagedItems.clear();

    selectedOrderTableLabel.setText("Table " + order.getTable().getId());
    roundLabel.setText("Round " + (order.getMenuItems().size() + 1));

    buildDetailMenuGrid("All");
    buildOrderSummary();
    navigateToDetail.run();
  }

  // ── Detail page ───────────────────────────────────────────────────────────

  private void buildDetailMenuGrid(String filter)
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
    card.setPrefWidth(200);

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

  private void toggleStaged(MenuItems item)
  {
    boolean alreadyIn = stagedItems.stream().anyMatch(s -> s.getId() == item.getId());
    if (alreadyIn) stagedItems.removeIf(s -> s.getId() == item.getId());
    else           stagedItems.add(item);

    buildDetailMenuGrid(activeFilter);
    buildOrderSummary();
  }

  private void buildOrderSummary()
  {
    if (selectedOrder == null || orderSummaryLinesBox == null) return;
    orderSummaryLinesBox.getChildren().clear();

    // Already-ordered items from the TableOrder
    for (String name : selectedOrder.getMenuItems())
    {
      Label row = new Label(name);
      row.getStyleClass().add("summary-line");
      orderSummaryLinesBox.getChildren().add(row);
    }

    // Staged (to be sent) items — with a separator and remove button
    if (!stagedItems.isEmpty())
    {
      orderSummaryLinesBox.getChildren().add(new Separator());
      for (MenuItems item : stagedItems)
      {
        HBox row = new HBox(8);
        Label nameLbl = new Label(item.getName());
        nameLbl.getStyleClass().add("summary-line");

        Label removeBtn = new Label("×");
        removeBtn.getStyleClass().add("staged-chip-remove");
        removeBtn.setOnMouseClicked(e -> { e.consume(); toggleStaged(item); });

        row.getChildren().addAll(nameLbl, removeBtn);
        orderSummaryLinesBox.getChildren().add(row);
      }
    }

    double stagedTotal = stagedItems.stream().mapToDouble(MenuItems::getPrice).sum();
    orderSubtotalLabel.setText(String.format("DKK %.2f", selectedOrder.getBill() + stagedTotal));
    sendToKitchenButton.setDisable(stagedItems.isEmpty());
  }

  public void payOrder()
  {
    if (selectedOrder == null) return;
    ordersService.payOrder(selectedOrder.getId());
  }

  // Called from NavigationController → WaiterController when "Send to Kitchen" is clicked.
  public void sendToKitchen()
  {
    if (selectedOrder == null || stagedItems.isEmpty()) return;
    ArrayList<String> names = new ArrayList<>();
    for (MenuItems item : stagedItems) names.add(item.getName());
    ordersService.addItemsToOrder(selectedOrder.getId(), names);
    // Success path handled in the onItemsAdded callback.
  }
}
