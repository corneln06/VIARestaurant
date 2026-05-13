package org.store.viarestaurant.dao;

import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.WorkerRole;

import java.sql.SQLException;
import java.util.ArrayList;

public interface WorkersDAO
{
  Workers createWorkers( String firstName, String lastName, String email, String rawPassword, WorkerRole role) throws SQLException;
  ArrayList<Workers> getAllWorkers() throws SQLException;
  Workers getWorkerById(Integer id) throws SQLException;
  Workers getWorkerByEmail(String email) throws SQLException;
  void deleteWorkerById(Integer id) throws SQLException;
  Workers updateWorker(Workers worker) throws SQLException;
}
