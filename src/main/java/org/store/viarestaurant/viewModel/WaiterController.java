package org.store.viarestaurant.viewModel;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.components.OrdersComponent;
import org.store.viarestaurant.viewModel.components.TableComponent;

public class WaiterController {

    private final TableComponent tableComponent = new TableComponent();
    private OrdersComponent ordersComponent;

    public void init(GridPane tableGrid) {
        tableComponent.initGrid(tableGrid);
    }

    public void initOrdersComponent(
            FlowPane ordersTableFlow,
            TilePane menuItemsPane,
            Label selectedOrderTableLabel,
            VBox orderSummaryLinesBox,
            Label orderSubtotalLabel,
            Label roundLabel,
            Button sendToKitchenButton,
            ToggleButton filterAll,
            ToggleButton filterStarters,
            ToggleButton filterMains,
            ToggleButton filterDesserts,
            ToggleButton filterDrinks,
            Runnable navigateToDetail,
            Runnable navigateToList) {
        ordersComponent = new OrdersComponent();
        ordersComponent.init(ordersTableFlow, menuItemsPane, selectedOrderTableLabel,
                orderSummaryLinesBox, orderSubtotalLabel, roundLabel, sendToKitchenButton,
                filterAll, filterStarters, filterMains, filterDesserts, filterDrinks,
                navigateToDetail, navigateToList);
    }

    public void initClient(Client client, int waiterId) {
        tableComponent.initClient(client);
        if (ordersComponent != null) ordersComponent.initClient(client, waiterId);
    }

    public void refreshTableGrid() {
        tableComponent.refreshTableGrid();
    }

    public void loadTables() {
        tableComponent.loadTables();
    }

    public void loadOrders() {
        if (ordersComponent != null) ordersComponent.loadData();
    }

    public void applyOrderFilter(String filter) {
        if (ordersComponent != null) ordersComponent.applyFilter(filter);
    }

    public void sendToKitchen() {
        if (ordersComponent != null) ordersComponent.sendToKitchen();
    }

    public void payOrder() {
        if (ordersComponent != null) ordersComponent.payOrder();
    }
}
