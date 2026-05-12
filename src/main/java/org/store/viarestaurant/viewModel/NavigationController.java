package org.store.viarestaurant.viewModel;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.view.HelloApplication;

public class NavigationController
{

  @FXML private Label sidebarName;
  @FXML private Label sidebarRole;

  @FXML private Button btnTables;
  @FXML private Button btnReservations;
  @FXML private Button btnOrders;
  @FXML private Button btnBills;
  @FXML private Button btnMenu;

  @FXML private Button logOutButton;
  @FXML private AnchorPane tablesPage;
  @FXML private AnchorPane reservationsPage;
  @FXML private AnchorPane ordersListPage;
  @FXML private AnchorPane ordersDetailPage;
  @FXML private AnchorPane billsListPage;
  @FXML private AnchorPane billDetailPage;
  @FXML private AnchorPane menuPage;

  public void initData(Workers worker)
  {
    if (sidebarName != null)
    {
      sidebarName.setText(worker.getFirstName() + " " + worker.getLastName());
    }

    if (sidebarRole != null)
    {
      sidebarRole.setText(worker.getRole().toString());
    }
    showDefaultPage(worker);
  }

  private void showDefaultPage(Workers worker)
  {
    switch (worker.getRole())
    {
      case Host -> showTablesPage();
      case Waiter -> showWaiterTablesPage();
      case Manager -> showMenuPage();
    }
  }

  @FXML
  private void showTablesPage()
  {
    showOnly(tablesPage);
    setActive(btnTables, btnReservations);
  }

  @FXML
  private void showReservationsPage()
  {
    showOnly(reservationsPage);
    setActive(btnReservations, btnTables);
  }

  @FXML
  private void showWaiterTablesPage()
  {
    showOnly(tablesPage);
    setActive(btnTables, btnOrders, btnBills);
  }

  @FXML
  private void showOrdersPage()
  {
    showOnly(ordersListPage);
    setActive(btnOrders, btnTables, btnBills);
  }

  @FXML
  private void showBillsPage()
  {
    showOnly(billsListPage);
    setActive(btnBills, btnTables, btnOrders);
  }

  @FXML
  private void showOrderDetailPage()
  {
    showOnly(ordersDetailPage);
    setActive(btnOrders, btnTables, btnBills);
  }

  @FXML
  private void showBillDetailPage()
  {
    showOnly(billDetailPage);
    setActive(btnBills, btnTables, btnOrders);
  }
  @FXML
  private void handleLogOut() throws IOException
  {
    FXMLLoader fxmlLoader = new FXMLLoader(
        HelloApplication.class.getResource("/org/store/viarestaurant/LoginView.fxml"));

    Scene scene = new Scene(fxmlLoader.load(), 1100, 700);

    scene.getStylesheets().add(
        getClass()
            .getResource("/org/store/viarestaurant/Stylesheet.css")
            .toExternalForm()
    );
    Stage stage = (Stage) logOutButton.getScene().getWindow();

    stage.setScene(scene);
    stage.show();

  }

  @FXML
  private void showMenuPage()
  {
    showOnly(menuPage);
    setActive(btnMenu);
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

  private void showOnly(AnchorPane selectedPage)
  {
    setVisible(tablesPage, selectedPage == tablesPage);
    setVisible(reservationsPage, selectedPage == reservationsPage);
    setVisible(ordersListPage, selectedPage == ordersListPage);
    setVisible(ordersDetailPage, selectedPage == ordersDetailPage);
    setVisible(billsListPage, selectedPage == billsListPage);
    setVisible(billDetailPage, selectedPage == billDetailPage);
    setVisible(menuPage, selectedPage == menuPage);
  }

  private void setVisible(AnchorPane page, boolean visible)
  {
    if (page == null)
    {
      return;
    }

    page.setVisible(visible);
    page.setManaged(visible);
  }

  private void setActive(Button activeButton, Button... inactiveButtons)
  {
    if (activeButton != null && !activeButton.getStyleClass().contains("nav-btn-active"))
    {
      activeButton.getStyleClass().add("nav-btn-active");
    }

    for (Button button : inactiveButtons)
    {
      if (button != null)
      {
        button.getStyleClass().remove("nav-btn-active");
      }
    }
  }

}