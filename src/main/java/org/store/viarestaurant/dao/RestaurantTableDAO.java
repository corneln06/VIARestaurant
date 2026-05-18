package org.store.viarestaurant.dao;

import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.state.TableState;

import java.sql.SQLException;
import java.util.ArrayList;

public interface RestaurantTableDAO {
    RestaurantTable createRestaurantTable(int maxSitting)throws SQLException;
    ArrayList<RestaurantTable> getAllRestaurantTables() throws SQLException;
    RestaurantTable getRestaurantTableByID(Integer id) throws SQLException;
    void deleteRestaurantTableByID(Integer id) throws SQLException;
    RestaurantTable updateRestaurantTable(RestaurantTable restaurantTable) throws SQLException;

}
