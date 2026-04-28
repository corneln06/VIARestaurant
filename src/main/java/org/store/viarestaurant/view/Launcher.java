package org.store.viarestaurant.view;

import javafx.application.Application;
import org.store.viarestaurant.config.DatabaseInitializer;

public class Launcher
{
  public static void main(String[] args)
  {
      DatabaseInitializer.initialize();
      Application.launch(HelloApplication.class, args);
  }
}
