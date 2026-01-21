package com.vaporant.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vaporant.model.OrderBean;

@ExtendWith(MockitoExtension.class)
class OrderDaoImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private OrderDaoImpl orderDao;

    @Test
    @DisplayName("saveOrder - Dati validi - Inserimento con successo")
    void testSaveOrderSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        OrderBean order = new OrderBean();
        order.setId_utente(1);
        order.setId_indirizzo(1);
        order.setPrezzoTot(100.0);
        order.setDataAcquisto(LocalDate.now());
        order.setMetodoPagamento("Carta");

        int result = orderDao.saveOrder(order);

        assertEquals(1, result, "saveOrder dovrebbe ritornare 1 per inserimento riuscito");
        // Verify all setters (kill VoidMethodCallMutator)
        verify(preparedStatement).setInt(1, 1); // ID_Utente
        verify(preparedStatement).setInt(2, 1); // ID_Indirizzo
        verify(preparedStatement).setDouble(3, 100.0); // prezzoTot
        verify(preparedStatement).setString(4, order.getDataAcquisto().toString()); // dataAcquisto
        verify(preparedStatement).setString(5, "Carta"); // metodoPagamento
    }

    @Test
    @DisplayName("saveOrder - SQLException dal DataSource - Propaga eccezione")
    void testSaveOrderException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        OrderBean order = new OrderBean();
        order.setDataAcquisto(LocalDate.now());
        assertThrows(SQLException.class, () -> orderDao.saveOrder(order));
    }

    @Test
    @DisplayName("deleteOrder - Ordine esistente - Cancellazione con successo")
    void testDeleteOrderSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        OrderBean order = new OrderBean();
        order.setId_ordine(1);

        int result = orderDao.deleteOrder(order);

        assertEquals(1, result, "deleteOrder dovrebbe ritornare 1 per cancellazione riuscita");
        verify(preparedStatement).setInt(1, 1);
    }

    @Test
    @DisplayName("deleteOrder - SQLException dal DataSource - Propaga eccezione")
    void testDeleteOrderException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        OrderBean order = new OrderBean();
        assertThrows(SQLException.class, () -> orderDao.deleteOrder(order));
    }

    @Test
    @DisplayName("findByKey - ID esistente - Restituisce OrderBean popolato")
    void testFindByKeySuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID_Ordine")).thenReturn(1);
        when(resultSet.getDate("dataAcquisto")).thenReturn(Date.valueOf(LocalDate.now()));

        OrderBean result = orderDao.findByKey(1);

        assertNotNull(result, "findByKey dovrebbe ritornare OrderBean per ID esistente");
        assertEquals(1, result.getId_ordine(), "ID ordine dovrebbe essere 1");

        // Verify ALL bean setters (kill VoidMethodCall)
        assertNotNull(result.getDataAcquisto(), "DataAcquisto should be set");
    }

    @Test
    @DisplayName("findByKey - ID inesistente - Restituisce null")
    void testFindByKeyNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false);

        OrderBean result = orderDao.findByKey(1);

        assertNull(result, "findByKey dovrebbe ritornare null per ID inesistente");
    }

    @Test
    @DisplayName("findByKey - SQLException dal DataSource - Propaga eccezione")
    void testFindByKeyException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> orderDao.findByKey(1));
    }

    @Test
    @DisplayName("getIdfromDB - Database con ordini - Restituisce max ID")
    void testGetIdfromDBSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("max_id")).thenReturn(10);

        int id = orderDao.getIdfromDB();

        assertEquals(10, id, "getIdfromDB dovrebbe ritornare max ID 10");
    }

    @Test
    @DisplayName("getIdfromDB - Database vuoto - Restituisce -1")
    void testGetIdfromDBEmpty() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(false);

        int id = orderDao.getIdfromDB();

        assertEquals(-1, id, "getIdfromDB dovrebbe ritornare -1 per database vuoto");
    }

    @Test
    @DisplayName("getIdfromDB - SQLException dal DataSource - Propaga eccezione")
    void testGetIdfromDBException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> orderDao.getIdfromDB());
    }

    @Test
    @DisplayName("findByIdUtente - Utente con ordini - Restituisce ArrayList di OrderBean")
    void testFindByIdUtenteSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID_Ordine")).thenReturn(1);
        when(resultSet.getDate("dataAcquisto")).thenReturn(Date.valueOf(LocalDate.now()));

        ArrayList<OrderBean> results = orderDao.findByIdUtente(1);

        assertNotNull(results, "findByIdUtente dovrebbe ritornare ArrayList per utente con ordini");
        assertEquals(1, results.size(), "ArrayList dovrebbe contenere 1 ordine");
    }

    @Test
    @DisplayName("findByIdUtente - Utente senza ordini - Restituisce null")
    void testFindByIdUtenteNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false);

        ArrayList<OrderBean> results = orderDao.findByIdUtente(1);

        assertNull(results, "findByIdUtente dovrebbe ritornare null per utente senza ordini");
    }

    @Test
    @DisplayName("findByIdUtente - SQLException dal DataSource - Propaga eccezione")
    void testFindByIdUtenteException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> orderDao.findByIdUtente(1));
    }

    @Test
    @DisplayName("Resource Management - SQLException chiude correttamente Connection e PreparedStatement")
    void findByKey_sqlException_closesResources() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Test database error"));

        // Act & Assert
        assertThrows(SQLException.class, () -> orderDao.findByKey(1),
                "SQLException dovrebbe essere propagata");

        // Verify resource cleanup (try-with-resources)
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @Test
    @DisplayName("findByKey - Verifica setInt parametro ID")
    void testFindByKeyAllParametersSet() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false); // Not found

        // Act
        OrderBean result = orderDao.findByKey(999);

        // Assert
        assertNull(result, "findByKey dovrebbe ritornare null se non trovato");
        // Verify setter parameter (kill VoidMethodCallMutator)
        verify(preparedStatement).setInt(1, 999);
    }

    @Test
    @DisplayName("findByIdUtente - Verifica setInt parametro ID utente")
    void testFindByIdUtenteParameterSet() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false); // No orders

        // Act
        ArrayList<OrderBean> result = orderDao.findByIdUtente(456);

        // Assert
        assertNull(result, "findByIdUtente dovrebbe ritornare null se non trovato");
        // Verify setter parameter
        verify(preparedStatement).setInt(1, 456);
    }

    @Test
    @DisplayName("findByIdUtente - Multiple orders - Verifica loop while rs.next()")
    void testFindByIdUtenteMultipleOrders() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(true); // Has results

        // Simulate 3 orders: next() returns true 3 times, then false
        when(resultSet.next()).thenReturn(true, true, true, false);
        when(resultSet.getInt("ID_Ordine")).thenReturn(1, 2, 3);
        when(resultSet.getInt("ID_Utente")).thenReturn(10);
        when(resultSet.getInt("ID_Indirizzo")).thenReturn(5);
        when(resultSet.getDouble("prezzoTot")).thenReturn(100.0, 200.0, 300.0);
        when(resultSet.getDate("dataAcquisto")).thenReturn(java.sql.Date.valueOf(LocalDate.now()));
        when(resultSet.getString("metodoPagamento")).thenReturn("PayPal", "Carta", "Contrassegno");

        // Act
        ArrayList<OrderBean> result = orderDao.findByIdUtente(10);

        // Assert
        assertNotNull(result, "findByIdUtente dovrebbe ritornare ArrayList");
        assertEquals(3, result.size(), "Dovrebbe ritornare 3 ordini");

        // Verify loop iterations (kill NegateConditionalsMutator on while)
        verify(resultSet, times(4)).next(); // 3 true + 1 false
    }

    @Test
    @DisplayName("findByIdUtente - Multiple orders - Verify 2+ ordini mappati con TUTTI 6 campi")
    void testFindByIdUtente_MultipleOrders_AllFieldsMapped() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("ID_Ordine")).thenReturn(1, 2);
        when(resultSet.getInt("ID_Utente")).thenReturn(100, 100);
        when(resultSet.getInt("ID_Indirizzo")).thenReturn(5, 6);
        when(resultSet.getDouble("prezzoTot")).thenReturn(50.0, 75.5);
        when(resultSet.getDate("dataAcquisto")).thenReturn(
                java.sql.Date.valueOf("2024-01-15"),
                java.sql.Date.valueOf("2024-01-20"));
        when(resultSet.getString("metodoPagamento")).thenReturn("Carta", "PayPal");

        // Act
        ArrayList<OrderBean> results = orderDao.findByIdUtente(100);

        // Assert collection size
        assertNotNull(results, "findByIdUtente dovrebbe ritornare ArrayList");
        assertEquals(2, results.size(), "Dovrebbe ritornare 2 ordini");

        OrderBean first = results.get(0);
        assertEquals(1, first.getId_ordine(), "ID ordine 1 dovrebbe essere 1");
        assertEquals(100, first.getId_utente(), "ID utente dovrebbe essere 100");
        assertEquals(5, first.getId_indirizzo(), "ID indirizzo dovrebbe essere 5");
        assertEquals(50.0, first.getPrezzoTot(), 0.0001, "Prezzo totale dovrebbe essere 50.0");
        assertEquals(LocalDate.parse("2024-01-15"), first.getDataAcquisto(),
                "Data acquisto dovrebbe essere 2024-01-15");
        assertEquals("Carta", first.getMetodoPagamento(), "Metodo pagamento dovrebbe essere Carta");

        OrderBean second = results.get(1);
        assertEquals(2, second.getId_ordine(), "ID ordine 2 dovrebbe essere 2");
        assertEquals(100, second.getId_utente(), "ID utente dovrebbe essere 100");
        assertEquals(75.5, second.getPrezzoTot(), 0.0001, "Prezzo totale dovrebbe essere 75.5");

        verify(preparedStatement).setInt(1, 100);
        verify(preparedStatement).executeQuery();
    }

    // ============================================================
    // Resource Cleanup Verification Tests
    // ============================================================

    @Test
    @DisplayName("saveOrder - Verifica chiusura PreparedStatement e Connection")
    void testSaveOrder_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        OrderBean order = new OrderBean();
        order.setId_utente(1);
        order.setId_indirizzo(1);
        order.setPrezzoTot(100.0);
        order.setDataAcquisto(LocalDate.now());
        order.setMetodoPagamento("Carta");

        orderDao.saveOrder(order);

        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("deleteOrder - Verifica chiusura PreparedStatement e Connection")
    void testDeleteOrder_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        OrderBean order = new OrderBean();
        order.setId_ordine(1);

        orderDao.deleteOrder(order);

        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("findByIdUtente - Verifica chiusura PreparedStatement e Connection")
    void testFindByIdUtente_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("ID_Ordine")).thenReturn(1);
        when(resultSet.getDate("dataAcquisto")).thenReturn(Date.valueOf(LocalDate.now()));

        orderDao.findByIdUtente(1);

        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("getIdfromDB - Verifica chiusura Statement e Connection")
    void testGetIdfromDB_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("max_id")).thenReturn(10);

        orderDao.getIdfromDB();

        // NOTE: getIdfromDB uses Statement, not PreparedStatement
        verify(statement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("getIdfromDB - SQLException chiude correttamente risorse")
    void testGetIdfromDB_SQLException_ClosesResources() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(SQLException.class, () -> orderDao.getIdfromDB());

        verify(statement).close();
        verify(connection).close();
    }
}
