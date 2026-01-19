package com.vaporant.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        assertEquals(1, result);
        verify(preparedStatement).setString(1, "Via Roma");
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

        assertEquals(1, result);
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
    @DisplayName("findByCred - Credenziali esistenti - Restituisce AddressBean popolato")
    void testFindByCredSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("Via")).thenReturn("Via Roma");

        AddressBean result = addressDao.findByCred("20100", "Via Roma", "10");

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Via Roma", result.getVia());
    }

    @Test
    @DisplayName("findByCred - Credenziali inesistenti - Restituisce null")
    void testFindByCredNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false);

        AddressBean result = addressDao.findByCred("20100", "Via Roma", "10");

        assertNull(result);
    }

    @Test
    @DisplayName("findByCred - SQLException dal DataSource - Propaga eccezione")
    void testFindByCredException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> addressDao.findByCred("20100", "Via Roma", "10"));
    }

    @Test
    @DisplayName("findByID - ID utente con indirizzi - Restituisce ArrayList di AddressBean")
    void testFindByIDSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID")).thenReturn(1);

        ArrayList<AddressBean> results = addressDao.findByID(1);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getId());
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

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    @DisplayName("findAddressByID - ID inesistente - Restituisce null")
    void testFindAddressByIDNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false);

        AddressBean result = addressDao.findAddressByID(1);

        assertNull(result);
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
}
