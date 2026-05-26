package org.store.viarestaurant.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorkersTest {

    private Workers worker;

    @BeforeEach
    public void setUp() {
        worker = new Host(1, "Jane", "Doe", "  Jane.Doe@Email.COM  ", "secret123");
    }

    @Test // I
    public void getEmail_afterConstruction_emailIsTrimmedAndLowerCased() {
        assertEquals("jane.doe@email.com", worker.getEmail());
    }

    @Test // I
    public void getEmail_alreadyLowerCase_noChange() {
        Workers w = new Host(2, "A", "B", "a@b.com", "pw");
        assertEquals("a@b.com", w.getEmail());
    }

    @Test // Z
    public void getEmail_nullEmail_returnsNull() {
        Workers w = new Host(3, "X", "Y", null, "pw");
        assertNull(w.getEmail());
    }

    @Test // Z
    public void getEmail_emptyEmail_returnsEmptyString() {
        Workers w = new Host(4, "X", "Y", "", "pw");
        assertEquals("", w.getEmail());
    }

    @Test // B
    public void getEmail_onlyWhitespace_trimsToEmptyString() {
        Workers w = new Host(5, "X", "Y", "   ", "pw");
        assertEquals("", w.getEmail());
    }

    @Test // S
    public void setEmail_mixedCaseWithSpaces_emailNormalized() {
        worker.setEmail("  NEW@EMAIL.ORG  ");
        assertEquals("new@email.org", worker.getEmail());
    }

    @Test // Z
    public void setEmail_null_emailBecomesNull() {
        worker.setEmail(null);
        assertNull(worker.getEmail());
    }

    @Test // I
    public void verifyPassword_correctPassword_returnsTrue() {
        assertTrue(worker.verifyPassword("secret123"));
    }

    @Test // I
    public void verifyPassword_wrongPassword_returnsFalse() {
        assertFalse(worker.verifyPassword("wrongpassword"));
    }

    @Test // Z
    public void verifyPassword_emptyString_returnsFalse() {
        assertFalse(worker.verifyPassword(""));
    }

    @Test // B
    public void verifyPassword_correctPasswordWrongCase_returnsFalse() {
        assertFalse(worker.verifyPassword("Secret123"));
    }

    @Test // B
    public void verifyPassword_passwordOffByOneChar_returnsFalse() {
        assertFalse(worker.verifyPassword("secret12"));
    }

    @Test // I
    public void getFirstName_afterConstruction_returnsFirstName() {
        assertEquals("Jane", worker.getFirstName());
    }

    @Test // S
    public void setFirstName_newName_firstNameUpdated() {
        worker.setFirstName("Emily");
        assertEquals("Emily", worker.getFirstName());
    }

    @Test // I
    public void getLastName_afterConstruction_returnsLastName() {
        assertEquals("Doe", worker.getLastName());
    }

    @Test // S
    public void setLastName_newName_lastNameUpdated() {
        worker.setLastName("Smith");
        assertEquals("Smith", worker.getLastName());
    }

    @Test // I
    public void getId_afterConstruction_returnsCorrectId() {
        assertEquals(1, worker.getId());
    }

    @Test // S
    public void setId_newId_idUpdated() {
        worker.setId(99);
        assertEquals(99, worker.getId());
    }

    @Test // Z
    public void setId_zero_idIsZero() {
        worker.setId(0);
        assertEquals(0, worker.getId());
    }

    @Test // I
    public void constructorNoPassword_emailNormalized() {
        Workers w = new Host(10, "Bob", "Builder", "  BOB@WORK.COM  ");
        assertEquals("bob@work.com", w.getEmail());
    }
}
