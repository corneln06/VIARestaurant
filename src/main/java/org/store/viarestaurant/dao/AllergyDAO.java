package org.store.viarestaurant.dao;

import org.store.viarestaurant.model.entities.Allergy;

import java.sql.SQLException;

public interface AllergyDAO {

    Allergy createAllergy(String name) throws SQLException;
    Allergy getAllergyById(Integer id) throws SQLException;
    Allergy getAllergyByName(String name) throws SQLException;
    void delete(Integer id) throws SQLException;
    Allergy updateAllergy(Allergy allergy) throws SQLException;
}
