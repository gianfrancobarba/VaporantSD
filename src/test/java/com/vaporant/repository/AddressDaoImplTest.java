package com.vaporant.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vaporant.model.AddressBean;

@ExtendWith(MockitoExtension.class)
class AddressDaoImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private AddressDaoImpl addressDao;

    @Test
    @DisplayName("saveAddress - Dati validi - Inserimento con successo")
    void testSaveAddressSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        AddressBean address = new AddressBean();
        address.setVia("Via Roma");
        address.setNumCivico("10");
        address.setCitta("Milano");
        address.setCap("20100");
        address.setProvincia("MI");

        int result = addressDao.saveAddress(address);

        assertEquals(1, result, "saveAddress dovrebbe ritornare 1 per inserimento riuscito");
        // ✅ Verify TUTTI i 5 setters (kill VoidMethodCallMutator)
        verify(preparedStatement).setString(1, "Via Roma"); // Via
        verify(preparedStatement).setString(2, "10"); // numCivico
        verify(preparedStatement).setString(3, "Milano"); // citta
        verify(preparedStatement).setString(4, "20100"); // CAP
        verify(preparedStatement).setString(5, "MI"); // provincia
    }

    @Test
    @DisplayName("saveAddress - SQLException dal DataSource - Propaga eccezione")
    void testSaveAddressException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        AddressBean address = new AddressBean();
        assertThrows(SQLException.class, () -> addressDao.saveAddress(address));
    }

    @Test
    @DisplayName("deleteAddress - Indirizzo esistente - Cancellazione con successo")
    void testDeleteAddressSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        AddressBean address = new AddressBean();
        address.setId(1);

        int result = addressDao.deleteAddress(address);

        assertEquals(1, result, "deleteAddress dovrebbe ritornare 1 per cancellazione riuscita");
        verify(preparedStatement).setInt(1, 1);
    }

    @Test
    @DisplayName("deleteAddress - SQLException dal DataSource - Propaga eccezione")
    void testDeleteAddressException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        AddressBean address = new AddressBean();
        assertThrows(SQLException.class, () -> addressDao.deleteAddress(address));
    }

    @Test
    @DisplayName("findByCred - Credenziali esistenti - Restituisce AddressBean con TUTTI 6 campi")
    void testFindByCredSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("Via")).thenReturn("Via Roma");
        when(resultSet.getString("numCivico")).thenReturn("10");
        when(resultSet.getString("citta")).thenReturn("Milano");
        when(resultSet.getString("CAP")).thenReturn("20100");
        when(resultSet.getString("provincia")).thenReturn("MI");

        AddressBean result = addressDao.findByCred("20100", "Via Roma", "10");

        // ✅ whiTee: assert TUTTI i 6 campi (prima era solo 2)
        assertNotNull(result, "findByCred dovrebbe ritornare AddressBean per credenziali esistenti");
        assertEquals(1, result.getId(), "ID indirizzo dovrebbe essere 1");
        assertEquals("Via Roma", result.getVia(), "Via dovrebbe essere 'Via Roma'");
        assertEquals("10", result.getNumCivico(), "Numero civico dovrebbe essere '10'");
        assertEquals("Milano", result.getCitta(), "Città dovrebbe essere 'Milano'");
        assertEquals("20100", result.getCap(), "CAP dovrebbe essere '20100'");
        assertEquals("MI", result.getProvincia(), "Provincia dovrebbe essere 'MI'");
    }

    @Test
    @DisplayName("findByCred - Credenziali inesistenti - Restituisce null")
    void testFindByCredNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false);

        AddressBean result = addressDao.findByCred("20100", "Via Roma", "10");

        assertNull(result, "findByCred dovrebbe ritornare null per credenziali inesistenti");
    }

    @Test
    @DisplayName("findByCred - SQLException dal DataSource - Propaga eccezione")
    void testFindByCredException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> addressDao.findByCred("20100", "Via Roma", "10"));
    }

    @Test
    @DisplayName("findByID - ID utente con indirizzi - Restituisce ArrayList con elementi mappati")
    void testFindByIDSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // ✅ whiTee: Simula 2 indirizzi
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("ID")).thenReturn(1, 2);
        when(resultSet.getString("Via")).thenReturn("Via Roma", "Via Milano");
        when(resultSet.getString("numCivico")).thenReturn("10", "20");
        when(resultSet.getString("citta")).thenReturn("Milano", "Milano");
        when(resultSet.getString("CAP")).thenReturn("20100", "20121");
        when(resultSet.getString("provincia")).thenReturn("MI", "MI");

        ArrayList<AddressBean> results = addressDao.findByID(100);

        assertNotNull(results, "findByID dovrebbe ritornare ArrayList per utente con indirizzi");
        assertEquals(2, results.size(), "ArrayList dovrebbe contenere 2 indirizzi");

        // ✅ whiTee: verify almeno primi 2 elementi mappati correttamente
        assertEquals(1, results.get(0).getId());
        assertEquals("Via Roma", results.get(0).getVia());
        assertEquals("10", results.get(0).getNumCivico());
        assertEquals("20100", results.get(0).getCap());

        assertEquals(2, results.get(1).getId());
        assertEquals("Via Milano", results.get(1).getVia());
        assertEquals("20", results.get(1).getNumCivico());
        assertEquals("20121", results.get(1).getCap());
    }

    @Test
    @DisplayName("findByID - User senza indirizzi - Restituisce ArrayList vuota")
    void testFindByID_EmptyList() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        ArrayList<AddressBean> results = addressDao.findByID(999);

        // ✅ whiTee pattern: empty collection SEMPRE testato
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("findByID - SQLException dal DataSource - Propaga eccezione")
    void testFindByIDException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> addressDao.findByID(1));
    }

    @Test
    @DisplayName("findAddressByID - ID esistente - Restituisce AddressBean popolato")
    void testFindAddressByIDSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID")).thenReturn(1);

        AddressBean result = addressDao.findAddressByID(1);

        assertNotNull(result, "findAddressByID dovrebbe ritornare AddressBean per ID esistente");
        assertEquals(1, result.getId(), "ID indirizzo dovrebbe essere 1");
    }

    @Test
    @DisplayName("findAddressByID - ID esistente - TUTTI 6 campi mappati correttamente")
    void testFindAddressByID_AllFieldsMapped() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID")).thenReturn(5);
        when(resultSet.getString("Via")).thenReturn("Via Test");
        when(resultSet.getString("numCivico")).thenReturn("100");
        when(resultSet.getString("citta")).thenReturn("Roma");
        when(resultSet.getString("CAP")).thenReturn("00100");
        when(resultSet.getString("provincia")).thenReturn("RM");

        AddressBean result = addressDao.findAddressByID(5);

        // ✅ whiTee: assert TUTTI i 6 campi
        assertNotNull(result, "findAddressByID dovrebbe ritornare AddressBean per ID esistente");
        assertEquals(5, result.getId());
        assertEquals("Via Test", result.getVia());
        assertEquals("100", result.getNumCivico());
        assertEquals("Roma", result.getCitta());
        assertEquals("00100", result.getCap());
        assertEquals("RM", result.getProvincia());

        verify(preparedStatement).setInt(1, 5);
        verify(preparedStatement).executeQuery();
    }

    @Test
    @DisplayName("findAddressByID - ID inesistente - Restituisce null")
    void testFindAddressByIDNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false);

        AddressBean result = addressDao.findAddressByID(1);

        assertNull(result, "findAddressByID dovrebbe ritornare null per ID inesistente");
    }

    @Test
    @DisplayName("findAddressByID - SQLException dal DataSource - Propaga eccezione")
    void testFindAddressByIDException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> addressDao.findAddressByID(1));
    }

    @Test
    @DisplayName("Resource Management - SQLException chiude correttamente Connection e PreparedStatement")
    void findAddressByID_sqlException_closesResources() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Test database error"));

        // Act & Assert
        assertThrows(SQLException.class, () -> addressDao.findAddressByID(1),
                "SQLException dovrebbe essere propagata");

        // Verify resource cleanup (try-with-resources)
        verify(preparedStatement).close();
        verify(connection).close();
    }

    // ============================================================
    // PHASE 2: Resource Cleanup Verification Tests
    // ============================================================

    @Test
    @DisplayName("saveAddress - Verifica chiusura PreparedStatement e Connection")
    void testSaveAddress_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        AddressBean address = new AddressBean();
        address.setVia("Via Roma");
        address.setNumCivico("10");
        address.setCitta("Milano");
        address.setCap("20100");
        address.setProvincia("MI");

        addressDao.saveAddress(address);

        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("deleteAddress - Verifica chiusura PreparedStatement e Connection")
    void testDeleteAddress_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        AddressBean address = new AddressBean();
        address.setId(1);

        addressDao.deleteAddress(address);

        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("findByCred - Verifica chiusura PreparedStatement e Connection")
    void testFindByCred_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("Via")).thenReturn("Via Roma");
        when(resultSet.getString("numCivico")).thenReturn("10");
        when(resultSet.getString("citta")).thenReturn("Milano");
        when(resultSet.getString("CAP")).thenReturn("20100");
        when(resultSet.getString("provincia")).thenReturn("MI");

        addressDao.findByCred("20100", "Via Roma", "10");

        // ResultSet created inline, not explicitly closed
        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("findByID - Verifica chiusura PreparedStatement e Connection")
    void testFindByID_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("Via")).thenReturn("Via Roma");
        when(resultSet.getString("numCivico")).thenReturn("10");
        when(resultSet.getString("citta")).thenReturn("Milano");
        when(resultSet.getString("CAP")).thenReturn("20100");
        when(resultSet.getString("provincia")).thenReturn("MI");

        addressDao.findByID(1);

        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("findAddressByID - Verifica chiusura PreparedStatement e Connection")
    void testFindAddressByID_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("ID")).thenReturn(1);

        addressDao.findAddressByID(1);

        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("findByID - SQLException chiude correttamente risorse")
    void testFindByID_SQLException_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB error"));

        assertThrows(SQLException.class, () -> addressDao.findByID(1));

        verify(preparedStatement).close();
        verify(connection).close();
    }
}
