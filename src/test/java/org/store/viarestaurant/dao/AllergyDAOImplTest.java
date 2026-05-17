package org.store.viarestaurant.dao;

import org.junit.jupiter.api.*;
import org.store.viarestaurant.model.entities.Allergy;
import org.store.viarestaurant.model.entities.Reservation;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AllergyDAOImplTest {
    private AllergyDAOImpl dao;
    private int createdId;

    @BeforeAll
    void setup() throws SQLException {
        dao = AllergyDAOImpl.getInstance();
    }

    @Test
    @Order(1)
    void testCreateAllergy() throws SQLException
    {
        Allergy allergy = dao.createAllergy("Work");

        assertNotNull(allergy);
        assertTrue(allergy.getId() > 0);
        assertEquals("Work", allergy.getName());

        createdId = allergy.getId();
    }

    @Test
    @Order(2)
    void testGetAllergyByID() throws SQLException {
        Allergy allergy = dao.getAllergyById(createdId);

        assertNotNull(allergy);
        assertEquals("Peanuts", allergy.getName());
    }

    @Test
    @Order(3)
    void testGetAllergyByName() throws SQLException {
        Allergy allergy = dao.getAllergyByName("Peanuts");

        assertNotNull(allergy);
        assertEquals("Peanuts", allergy.getName());
    }

  @Test
  @Order(4)
  void testGetAllAllergies() throws SQLException {
    ArrayList<Allergy> allergies = dao.getAllAllergies();

    assertNotNull(allergies);
    assertFalse(allergies.isEmpty());

    boolean found = allergies.stream()
        .anyMatch(a -> a.getName().equals("Peanuts"));

    assertTrue(found, "Peanuts allergy should be in the list");

    allergies.forEach(a -> {
      assertTrue(a.getId() > 0);
      assertNotNull(a.getName());
    });
  }
    @Test
    @Order(5)
    void testUpdateAllergy() throws SQLException {
        Allergy allergy = dao.getAllergyById(createdId);
        allergy.setName("Nuts");
        dao.updateAllergy(allergy);

        Allergy allergy1 = dao.getAllergyById(createdId);

        assertNotNull(allergy);
        assertEquals("Nuts", allergy1.getName());
    }



    @Test
    @Order(6)
    void testDeleteById() throws SQLException
    {
        dao.delete(createdId);
        assertNull(dao.getAllergyById(createdId));
    }
}
