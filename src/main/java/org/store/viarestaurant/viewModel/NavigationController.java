package org.store.viarestaurant.viewModel;


import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.store.viarestaurant.dao.WorkersDAOImpl;
import org.store.viarestaurant.model.entities.Host;
import org.store.viarestaurant.model.entities.Manager;
import org.store.viarestaurant.model.entities.Waiter;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.WorkerRole;
import org.store.viarestaurant.view.HelloApplication;

public class NavigationController
{
  

  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;
  @FXML private Label errorLabel;

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

  private final Workers testWorker =
    new Manager(1, "Adam", "Adam", "manager", "1234");

  private final Workers testWorker1 =
      new Waiter(2, "Adam", "Adam", "waiter", "1234");

  private final Workers testWorker2 =
      new Host(3, "Adam", "Adam", "host", "1234");

  @FXML
  private void handleLogin() {

    String username = usernameField.getText();
    String password = passwordField.getText();

    Workers loggedInWorker;

    // Test manager
    if (testWorker.getEmail().equals(username)
        && testWorker.verifyPassword(password)) {
      loggedInWorker = testWorker;
    }
    // Test waiter
    else if (testWorker1.getEmail().equals(username)
        && testWorker1.verifyPassword(password)) {
      loggedInWorker = testWorker1;
      System.out.println(loggedInWorker);
    }
    // Test host
    else if (testWorker2.getEmail().equals(username)
        && testWorker2.verifyPassword(password)) {
      loggedInWorker = testWorker2;
      System.out.println(loggedInWorker);
    }
    else {
      showError("Invalid credentials.");
      return;
    }
    try {
      Parent root = loadDashboard(loggedInWorker);

      Stage stage =
          (Stage) usernameField.getScene().getWindow();

      stage.getScene().setRoot(root);

    } catch (IOException exception) {
      showError("Unable to open dashboard.");
    }
  }

  private Parent loadDashboard(Workers worker) throws IOException
  {
    String fxml = switch (worker.getRole())
    {
      case Host -> "/org/store/viarestaurant/HostDashboard.fxml";
      case Waiter -> "/org/store/viarestaurant/WaiterDashboard.fxml";
      case Manager -> "/org/store/viarestaurant/ManagerDashboard.fxml";
    };

    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml));
    Parent root = loader.load();

    NavigationController controller = loader.getController();
    controller.initData(worker);

    return root;
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

  private void showError(String message)
  {
    errorLabel.setText(message);
    errorLabel.setVisible(true);
    errorLabel.setManaged(true);

    if (passwordField != null)
    {
      passwordField.clear();
    }
  }
}