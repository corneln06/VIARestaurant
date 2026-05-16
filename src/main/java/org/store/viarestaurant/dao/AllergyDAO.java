package org.store.viarestaurant.dao;

import org.store.viarestaurant.model.entities.Allergy;
import org.store.viarestaurant.model.entities.TableOrder;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AllergyDAO {

    Allergy createAllergy(String name) throws SQLException;
    ArrayList<Allergy> getAllAllergies() throws SQLException;
    Allergy getAllergyById(Integer id) throws SQLException;
    Allergy getAllergyByName(String name) throws SQLException;
    void delete(Integer id) throws SQLException;
    Allergy updateAllergy(Allergy allergy) throws SQLException;
}
