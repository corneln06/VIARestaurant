package org.store.viarestaurant.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.store.viarestaurant.dao.WorkersDAOImpl;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.view.HelloApplication;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController extends NavigationController{

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            Workers attemptingLoginIn =
                    WorkersDAOImpl.getInstance().getWorkerByEmail(username);

            if (attemptingLoginIn == null ||
                    !attemptingLoginIn.getEmail().equals(username) ||
                    !attemptingLoginIn.verifyPassword(password)) {

                showError("Invalid credentials.");
                return;
            }

            Parent root = loadDashboard(attemptingLoginIn);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (SQLException exception) {
            exception.printStackTrace();
            showError("Database error. Please try again.");

        } catch (IOException exception) {
            exception.printStackTrace();
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
