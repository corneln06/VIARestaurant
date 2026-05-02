package org.store.viarestaurant;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController
{
  private final SessionManager sessionManager = SessionManager.getInstance();

  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;
  @FXML private Label errorLabel;

  @FXML
  private void handleLogin()
  {
    Workers worker = sessionManager.authenticate(
        usernameField.getText(),
        passwordField.getText()
    );

    if (worker == null)
    {
      showError("Invalid credentials. Try host / waiter / manager.");
      return;
    }

    try
    {
      Parent root = loadDashboard(worker);
      Stage stage = (Stage) usernameField.getScene().getWindow();
      stage.getScene().setRoot(root);
    }
    catch (IOException exception)
    {
      showError("Unable to open dashboard.");
    }
  }

  private Parent loadDashboard(Workers worker) throws IOException
  {
    return switch (worker.getRole())
    {
      case Host -> {
        var loader = new FXMLLoader(HelloApplication.class.getResource("HostDashboard.fxml"));
        Parent root = loader.load();
        loader.<HostController>getController().initData(worker);
        yield root;
      }
      case Waiter -> {
        var loader = new FXMLLoader(HelloApplication.class.getResource("WaiterDashboard.fxml"));
        Parent root = loader.load();
        loader.<WaiterController>getController().initData(worker);
        yield root;
      }
      case Manager -> {
        var loader = new FXMLLoader(HelloApplication.class.getResource("ManagerDashboard.fxml"));
        Parent root = loader.load();
        loader.<ManagerController>getController().initData(worker);
        yield root;
      }
    };
  }

  private void showError(String message)
  {
    errorLabel.setText(message);
    errorLabel.setVisible(true);
    errorLabel.setManaged(true);
    passwordField.clear();
  }
}
