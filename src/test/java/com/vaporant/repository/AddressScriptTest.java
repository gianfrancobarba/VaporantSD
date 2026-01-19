package com.vaporant.repository;

import com.vaporant.model.AddressBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AddressScript - Wrapper/adapter for AddressBean
 * Pattern: Simple POJO testing with data transformation verification
 */
@DisplayName("AddressScript - Address Wrapper Tests")
class AddressScriptTest {

    private AddressBean testAddress;

    @BeforeEach
    void setUp() {
        // Create test AddressBean
        testAddress = new AddressBean();
        testAddress.setId(1);
        testAddress.setVia("Via Roma");
        testAddress.setNumCivico("42"); // Correct: setNumCivico
        testAddress.setCap("00100");
        testAddress.setCitta("Roma");
        testAddress.setProvincia("RM");
        testAddress.setId_utente(123); // Correct: setId_utente(int)
        testAddress.setStato("Italia");
    }

    // ========== CONSTRUCTOR TESTS ==========

    @Test
    @DisplayName("Constructor creates AddressScript from AddressBean successfully")
    void constructor_validAddressBean_createsAddressScript() {
        // Act
        AddressScript script = new AddressScript(testAddress);

        // Assert
        assertNotNull(script, "AddressScript should be created");
        assertEquals(1, script.getId(), "ID should match AddressBean ID");
        assertNotNull(script.getIndirizzo(), "Indirizzo should not be null");
    }

    @Test
    @DisplayName("Constructor copies ID from AddressBean")
    void constructor_copiesId() {
        // Arrange
        testAddress.setId(42);

        // Act
        AddressScript script = new AddressScript(testAddress);

        // Assert
        assertEquals(42, script.getId(), "ID should be copied from AddressBean");
    }

    @Test
    @DisplayName("Constructor calls toStringScript on AddressBean")
    void constructor_callsToStringScript() {
        // Act
        AddressScript script = new AddressScript(testAddress);

        // Assert
        assertNotNull(script.getIndirizzo(), "Indirizzo should be populated from toStringScript()");
        assertFalse(script.getIndirizzo().isEmpty(), "Indirizzo should not be empty");
    }

    // ========== GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("getId returns correct ID")
    void getId_returnsCorrectId() {
        // Arrange
        AddressScript script = new AddressScript(testAddress);

        // Act
        int result = script.getId();

        // Assert
        assertEquals(1, result, "getId dovrebbe ritornare l'ID impostato nel constructor");
    }

    @Test
    @DisplayName("setId updates ID successfully")
    void setId_updatesId() {
        // Arrange
        AddressScript script = new AddressScript(testAddress);

        // Act
        script.setId(99);

        // Assert
        assertEquals(99, script.getId(), "setId dovrebbe aggiornare l'ID");
    }

    @Test
    @DisplayName("getIndirizzo returns correct address string")
    void getIndirizzo_returnsCorrectString() {
        // Arrange
        AddressScript script = new AddressScript(testAddress);

        // Act
        String result = script.getIndirizzo();

        // Assert
        assertNotNull(result, "getIndirizzo should return a string");
        assertFalse(result.isEmpty(), "Address string should not be empty");
    }

    @Test
    @DisplayName("setIndirizzo updates address string successfully")
    void setIndirizzo_updatesString() {
        // Arrange
        AddressScript script = new AddressScript(testAddress);
        String newAddress = "Nuovo Indirizzo Test, 123";

        // Act
        script.setIndirizzo(newAddress);

        // Assert
        assertEquals(newAddress, script.getIndirizzo(), "setIndirizzo should update the address string");
    }

    // ========== toString TESTS ==========

    @Test
    @DisplayName("toString returns formatted string with ID and address")
    void toString_returnsFormattedString() {
        // Arrange
        AddressScript script = new AddressScript(testAddress);

        // Act
        String result = script.toString();

        // Assert
        assertNotNull(result, "toString should return a string");
        assertTrue(result.contains(String.valueOf(script.getId())),
                "toString should contain the ID");
        assertTrue(result.contains(script.getIndirizzo()),
                "toString should contain the indirizzo");
    }

    @Test
    @DisplayName("toString format matches expected pattern")
    void toString_matchesExpectedFormat() {
        // Arrange
        testAddress.setId(5);
        AddressScript script = new AddressScript(testAddress);
        script.setIndirizzo("Test Address");

        // Act
        String result = script.toString();

        // Assert
        assertEquals("5 Test Address", result, "toString should follow 'id indirizzo' format");
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @DisplayName("Constructor with AddressBean having ID 0")
    void constructor_addressBeanWithIdZero() {
        // Arrange
        testAddress.setId(0);

        // Act
        AddressScript script = new AddressScript(testAddress);

        // Assert
        assertEquals(0, script.getId(), "Should handle ID 0 correctly");
    }

    @Test
    @DisplayName("Constructor with AddressBean having negative ID")
    void constructor_addressBeanWithNegativeId() {
        // Arrange
        testAddress.setId(-1);

        // Act
        AddressScript script = new AddressScript(testAddress);

        // Assert
        assertEquals(-1, script.getId(), "Should handle negative ID correctly");
    }

    @Test
    @DisplayName("setIndirizzo with empty string")
    void setIndirizzo_emptyString() {
        // Arrange
        AddressScript script = new AddressScript(testAddress);

        // Act
        script.setIndirizzo("");

        // Assert
        assertEquals("", script.getIndirizzo(), "setIndirizzo dovrebbe accettare stringa vuota");
    }

    @Test
    @DisplayName("setIndirizzo with null value")
    void setIndirizzo_nullValue() {
        // Arrange
        AddressScript script = new AddressScript(testAddress);

        // Act
        script.setIndirizzo(null);

        // Assert
        assertNull(script.getIndirizzo(), "setIndirizzo dovrebbe accettare valore null");
    }

    @Test
    @DisplayName("Multiple AddressScript instances from same AddressBean are independent")
    void multipleInstances_areIndependent() {
        // Act
        AddressScript script1 = new AddressScript(testAddress);
        AddressScript script2 = new AddressScript(testAddress);

        // Modify script1
        script1.setId(100);
        script1.setIndirizzo("Modified Address");

        // Assert
        assertNotEquals(script1.getId(), script2.getId(),
                "Instances should be independent");
        assertNotEquals(script1.getIndirizzo(), script2.getIndirizzo(),
                "Instances should have independent indirizzo values");
    }
}
