package org.store.viarestaurant.view;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application
{
  @Override public void start(Stage stage) throws IOException
  {
    FXMLLoader fxmlLoader = new FXMLLoader(
        HelloApplication.class.getResource("/org/store/viarestaurant/LoginView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
    scene.getStylesheets().add(
      getClass().getResource("Stylesheet.css").toExternalForm()
    );
    stage.setTitle("ViaRestaurant");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }
}