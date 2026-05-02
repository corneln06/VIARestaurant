package org.store.viarestaurant;

import java.util.List;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class WaiterController
{
  private final SessionManager sessionManager = SessionManager.getInstance();

  @FXML private Label sidebarName;
  @FXML private Label sidebarRole;
  @FXML private Button btnTables;
  @FXML private Button btnOrders;
  @FXML private Button btnBills;
  @FXML private AnchorPane tablesPage;
  @FXML private AnchorPane ordersListPage;
  @FXML private AnchorPane ordersDetailPage;
  @FXML private AnchorPane billsListPage;
  @FXML private AnchorPane billDetailPage;
  @FXML private FlowPane waiterTablesFlow;
  @FXML private FlowPane ordersListFlow;
  @FXML private FlowPane billsListFlow;
  @FXML private Label selectedOrderTableLabel;
  @FXML private ToggleButton filterAllButton;
  @FXML private ToggleButton filterStartersButton;
  @FXML private ToggleButton filterMainsButton;
  @FXML private ToggleButton filterSidesButton;
  @FXML private ToggleButton filterDrinksButton;
  @FXML private TilePane menuItemsFlow;
  @FXML private Label roundLabel;
  @FXML private VBox orderSummaryLinesBox;
  @FXML private Label orderSubtotalLabel;
  @FXML private Button sendToKitchenButton;
  @FXML private TableView<BillRow> billTable;
  @FXML private TableColumn<BillRow, String> itemColumn;
  @FXML private TableColumn<BillRow, String> qtyColumn;
  @FXML private TableColumn<BillRow, String> unitColumn;
  @FXML private TableColumn<BillRow, String> totalColumn;
  @FXML private Label selectedBillTableLabel;
  @FXML private Label billSubtotalLabel;
  @FXML private Label billVatLabel;
  @FXML private Label billTotalLabel;

  private ToggleGroup menuFilterGroup;
  private int currentOrderTableId = -1;
  private int currentBillTableId = -1;

  @FXML
  private void initialize()
  {
    configureMenuFilters();
    configureBillTable();
    showTablesPage();
  }

  public void initData(Workers worker)
  {
    sidebarName.setText(worker.getFirstName() + " " + worker.getLastName());
    sidebarRole.setText("Waiter");
    refreshAllViews();
  }

  @FXML
  private void showTablesPage()
  {
    showPage(tablesPage);
    setActiveNavButton(btnTables, btnOrders, btnBills);
    refreshLiveTables();
  }

  @FXML
  private void showOrdersPage()
  {
    showPage(ordersListPage);
    setActiveNavButton(btnOrders, btnTables, btnBills);
    refreshOrdersList();
  }

  @FXML
  private void showBillsPage()
  {
    showPage(billsListPage);
    setActiveNavButton(btnBills, btnTables, btnOrders);
    refreshBillsList();
  }

  @FXML
  private void backToOrdersList()
  {
    showOrdersPage();
  }

  @FXML
  private void backToBillsList()
  {
    showBillsPage();
  }

  @FXML
  private void sendToKitchen()
  {
    if (currentOrderTableId < 0)
    {
      return;
    }

    sessionManager.sendToKitchen(currentOrderTableId);
    refreshAllViews();
    refreshOrderSummary();
  }

  @FXML
  private void splitByGuest()
  {
    logBillAction("Split by guest");
  }

  @FXML
  private void splitEvenly()
  {
    logBillAction("Split evenly");
  }

  @FXML
  private void applyDiscount()
  {
    logBillAction("Apply discount");
  }

  @FXML
  private void addTip()
  {
    logBillAction("Add tip");
  }

  @FXML
  private void printAndCloseTable()
  {
    if (currentBillTableId < 0)
    {
      return;
    }

    sessionManager.closeTable(currentBillTableId);
    if (currentOrderTableId == currentBillTableId)
    {
      currentOrderTableId = -1;
    }
    currentBillTableId = -1;
    refreshAllViews();
    showBillsPage();
  }

  private void refreshAllViews()
  {
    refreshLiveTables();
    refreshOrdersList();
    refreshBillsList();

    if (currentOrderTableId > 0)
    {
      refreshMenuItems();
      refreshOrderSummary();
    }

    if (currentBillTableId > 0)
    {
      refreshBillDetail();
    }
  }

  private void refreshLiveTables()
  {
    waiterTablesFlow.getChildren().clear();

    for (Table table : sessionManager.getTables())
    {
      String badgeText = "Ready";
      Runnable action = null;

      switch (stateOf(table))
      {
        case RESERVED -> badgeText = "Reserved";
        case UNAVAILABLE -> {
          badgeText = sessionManager.getOrderLines(table.getId()).isEmpty()
              ? "Arrived"
              : "Open Order";
          action = () -> openOrderDetail(table.getId());
        }
        case AVAILABLE -> {}
      }

      waiterTablesFlow.getChildren().add(createTableCard(table, badgeText, action));
    }
  }

  private void refreshOrdersList()
  {
    ordersListFlow.getChildren().clear();

    for (Table table : sessionManager.getTables())
    {
      if (sessionManager.getOrderLines(table.getId()).isEmpty())
      {
        continue;
      }

      ordersListFlow.getChildren().add(
          createTableCard(table, "Open Order", () -> openOrderDetail(table.getId()))
      );
    }

    if (ordersListFlow.getChildren().isEmpty())
    {
      ordersListFlow.getChildren().add(createEmptyState("No active orders yet."));
    }
  }

  private void refreshBillsList()
  {
    billsListFlow.getChildren().clear();

    for (Integer tableId : sessionManager.getSentTables())
    {
      Table table = sessionManager.getTableById(tableId);
      if (table == null)
      {
        continue;
      }

      billsListFlow.getChildren().add(
          createTableCard(table, "PAY", () -> openBillDetail(tableId))
      );
    }

    if (billsListFlow.getChildren().isEmpty())
    {
      billsListFlow.getChildren().add(createEmptyState("No bills are ready for payment."));
    }
  }

  private void openOrderDetail(int tableId)
  {
    currentOrderTableId = tableId;
    showPage(ordersDetailPage);
    setActiveNavButton(btnOrders, btnTables, btnBills);
    refreshMenuItems();
    refreshOrderSummary();
  }

  private void openBillDetail(int tableId)
  {
    currentBillTableId = tableId;
    showPage(billDetailPage);
    setActiveNavButton(btnBills, btnTables, btnOrders);
    refreshBillDetail();
  }

  private void refreshMenuItems()
  {
    menuItemsFlow.getChildren().clear();

    for (MenuItems item : sessionManager.getMenu())
    {
      if (matchesSelectedFilter(item))
      {
        menuItemsFlow.getChildren().add(createMenuTile(item));
      }
    }
  }

  private void refreshOrderSummary()
  {
    Table table = sessionManager.getTableById(currentOrderTableId);
    if (table == null)
    {
      return;
    }

    List<SessionManager.OrderLine> lines = sessionManager.getOrderLines(currentOrderTableId);
    selectedOrderTableLabel.setText("Table " + table.getId());
    roundLabel.setText(sessionManager.getSentTables().contains(currentOrderTableId)
        ? "Round 2"
        : "Round 1");
    sendToKitchenButton.setText(sessionManager.getSentTables().contains(currentOrderTableId)
        ? "Send Another Round"
        : "Send to Kitchen");
    sendToKitchenButton.setDisable(lines.isEmpty());

    orderSummaryLinesBox.getChildren().clear();
    if (lines.isEmpty())
    {
      orderSummaryLinesBox.getChildren().add(createEmptyState("No items added yet."));
    }
    else
    {
      for (SessionManager.OrderLine line : lines)
      {
        HBox row = new HBox(10);
        row.getStyleClass().add("summary-row");

        Label lineLabel = new Label(
            line.qty() + " × " + line.item().getName() + "  " + formatCurrency(line.qty()
                * line.item().getPrice())
        );
        lineLabel.getStyleClass().add("summary-line");
        HBox.setHgrow(lineLabel, Priority.ALWAYS);

        Button removeButton = new Button("×");
        removeButton.getStyleClass().addAll("btn-base", "icon-btn");
        removeButton.setOnAction(event -> {
          sessionManager.removeOrderLine(currentOrderTableId, line.item());
          refreshAllViews();
          refreshOrderSummary();
        });

        row.getChildren().addAll(lineLabel, removeButton);
        orderSummaryLinesBox.getChildren().add(row);
      }
    }

    orderSubtotalLabel.setText(formatCurrency(sessionManager.calculateSubtotal(currentOrderTableId)));
  }

  private void refreshBillDetail()
  {
    Table table = sessionManager.getTableById(currentBillTableId);
    if (table == null)
    {
      return;
    }

    selectedBillTableLabel.setText("Table " + table.getId());
    List<BillRow> rows = sessionManager.getOrderLines(currentBillTableId)
        .stream()
        .map(line -> new BillRow(
            line.item().getName(),
            line.qty(),
            line.item().getPrice(),
            line.qty() * line.item().getPrice()
        ))
        .toList();
    billTable.setItems(FXCollections.observableArrayList(rows));

    double subtotal = sessionManager.calculateSubtotal(currentBillTableId);
    double vat = subtotal * 0.25;
    billSubtotalLabel.setText(formatCurrency(subtotal));
    billVatLabel.setText(formatCurrency(vat));
    billTotalLabel.setText(formatCurrency(subtotal + vat));
  }

  private VBox createTableCard(Table table, String badgeText, Runnable action)
  {
    VBox card = new VBox(8);
    card.getStyleClass().addAll("table-card", getStateStyleClass(table));
    card.setPrefWidth(185);

    Label tableName = new Label("Table " + table.getId());
    tableName.getStyleClass().add("card-title");

    Label seatsLabel = new Label(table.getMaxSitting() + " seats");
    seatsLabel.getStyleClass().add("card-subtitle");

    Label badge = new Label(badgeText);
    badge.getStyleClass().addAll("state-badge", getBadgeStyleClass(table));

    card.getChildren().addAll(tableName, seatsLabel, badge);

    if (action != null)
    {
      card.getStyleClass().add("interactive-card");
      card.setOnMouseClicked(event -> action.run());
    }

    return card;
  }

  private VBox createMenuTile(MenuItems item)
  {
    VBox tile = new VBox(10);
    tile.getStyleClass().add("menu-item-tile");
    tile.setPrefWidth(210);

    Label itemName = new Label(item.getName());
    itemName.getStyleClass().add("tile-title");

    Label itemType = new Label(getMenuTypeLabel(item.getType()));
    itemType.getStyleClass().add("tile-meta");

    HBox footer = new HBox(10);
    Label priceLabel = new Label(formatCurrency(item.getPrice()));
    priceLabel.getStyleClass().add("tile-price");
    HBox.setHgrow(priceLabel, Priority.ALWAYS);

    Button addButton = new Button("+");
    addButton.getStyleClass().addAll("btn-base", "add-item-btn");
    addButton.setOnAction(event -> {
      sessionManager.addOrderLine(currentOrderTableId, item);
      refreshAllViews();
      refreshOrderSummary();
    });

    footer.getChildren().addAll(priceLabel, addButton);
    tile.getChildren().addAll(itemName, itemType, footer);
    return tile;
  }

  private Label createEmptyState(String text)
  {
    Label emptyLabel = new Label(text);
    emptyLabel.getStyleClass().add("empty-state");
    return emptyLabel;
  }

  private void configureMenuFilters()
  {
    menuFilterGroup = new ToggleGroup();

    filterAllButton.setToggleGroup(menuFilterGroup);
    filterStartersButton.setToggleGroup(menuFilterGroup);
    filterMainsButton.setToggleGroup(menuFilterGroup);
    filterSidesButton.setToggleGroup(menuFilterGroup);
    filterDrinksButton.setToggleGroup(menuFilterGroup);

    filterAllButton.setUserData("ALL");
    filterStartersButton.setUserData("STARTERS");
    filterMainsButton.setUserData("MAINS");
    filterSidesButton.setUserData("DESSERTS");
    filterDrinksButton.setUserData("DRINKS");
    filterAllButton.setSelected(true);

    menuFilterGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null)
      {
        filterAllButton.setSelected(true);
        return;
      }
      refreshMenuItems();
    });
  }

  private void configureBillTable()
  {
    itemColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().item()));
    qtyColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
        String.valueOf(cell.getValue().qty())
    ));
    unitColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
        formatCurrency(cell.getValue().unit())
    ));
    totalColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
        formatCurrency(cell.getValue().total())
    ));
  }

  private boolean matchesSelectedFilter(MenuItems item)
  {
    String filter = String.valueOf(menuFilterGroup.getSelectedToggle().getUserData());
    return switch (filter)
    {
      case "STARTERS" -> item.getType() == MenuTypes.Starter;
      case "MAINS" -> item.getType() == MenuTypes.Main;
      case "DESSERTS" -> item.getType() == MenuTypes.Dessert;
      case "DRINKS" ->
          item.getType() == MenuTypes.Beverage || item.getType() == MenuTypes.AlcoholicBeverage;
      default -> true;
    };
  }

  private void showPage(AnchorPane page)
  {
    setManagedVisibility(tablesPage, page == tablesPage);
    setManagedVisibility(ordersListPage, page == ordersListPage);
    setManagedVisibility(ordersDetailPage, page == ordersDetailPage);
    setManagedVisibility(billsListPage, page == billsListPage);
    setManagedVisibility(billDetailPage, page == billDetailPage);
  }

  private void setManagedVisibility(AnchorPane page, boolean visible)
  {
    page.setVisible(visible);
    page.setManaged(visible);
  }

  private void setActiveNavButton(Button activeButton, Button... inactiveButtons)
  {
    if (!activeButton.getStyleClass().contains("nav-btn-active"))
    {
      activeButton.getStyleClass().add("nav-btn-active");
    }

    for (Button button : inactiveButtons)
    {
      button.getStyleClass().remove("nav-btn-active");
    }
  }

  private void logBillAction(String action)
  {
    System.out.println(action + " clicked for table " + currentBillTableId + ".");
  }

  private String getMenuTypeLabel(MenuTypes type)
  {
    return switch (type)
    {
      case Starter -> "Starter";
      case Main -> "Main";
      case Dessert -> "Dessert";
      case Beverage -> "Drinks";
      case AlcoholicBeverage -> "Bar";
    };
  }

  private String getStateStyleClass(Table table)
  {
    return switch (stateOf(table))
    {
      case AVAILABLE -> "table-available";
      case RESERVED -> "table-reserved";
      case UNAVAILABLE -> "table-unavailable";
    };
  }

  private String getBadgeStyleClass(Table table)
  {
    return switch (stateOf(table))
    {
      case AVAILABLE -> "badge-available";
      case RESERVED -> "badge-reserved";
      case UNAVAILABLE -> "badge-unavailable";
    };
  }

  private String formatCurrency(double amount)
  {
    return String.format("$%.2f", amount);
  }

  public record BillRow(String item, int qty, double unit, double total)
  {
  }

  private StateView stateOf(Table table)
  {
    if (table.getStatus() instanceof AvailableState) return StateView.AVAILABLE;
    if (table.getStatus() instanceof ReservedState) return StateView.RESERVED;
    return StateView.UNAVAILABLE;
  }

  private enum StateView
  {
    AVAILABLE, RESERVED, UNAVAILABLE
  }
}
