package com.vaporant.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
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

        assertEquals(1, result);
        verify(preparedStatement).setInt(1, 1);
    }

    @Test
    void testSaveOrderException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        OrderBean order = new OrderBean();
        order.setDataAcquisto(LocalDate.now());
        assertThrows(SQLException.class, () -> orderDao.saveOrder(order));
    }

    @Test
    void testDeleteOrderSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        OrderBean order = new OrderBean();
        order.setId_ordine(1);

        int result = orderDao.deleteOrder(order);

        assertEquals(1, result);
        verify(preparedStatement).setInt(1, 1);
    }

    @Test
    void testDeleteOrderException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        OrderBean order = new OrderBean();
        assertThrows(SQLException.class, () -> orderDao.deleteOrder(order));
    }

    @Test
    void testFindByKeySuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID_Ordine")).thenReturn(1);
        when(resultSet.getDate("dataAcquisto")).thenReturn(Date.valueOf(LocalDate.now()));

        OrderBean result = orderDao.findByKey(1);

        assertNotNull(result);
        assertEquals(1, result.getId_ordine());
    }

    @Test
    void testFindByKeyNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false);

        OrderBean result = orderDao.findByKey(1);

        assertNull(result);
    }

    @Test
    void testFindByKeyException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> orderDao.findByKey(1));
    }

    @Test
    void testGetIdfromDBSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("max_id")).thenReturn(10);

        int id = orderDao.getIdfromDB();

        assertEquals(10, id);
    }

    @Test
    void testGetIdfromDBEmpty() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(false);

        int id = orderDao.getIdfromDB();

        assertEquals(-1, id);
    }

    @Test
    void testGetIdfromDBException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> orderDao.getIdfromDB());
    }

    @Test
    void testFindByIdUtenteSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID_Ordine")).thenReturn(1);
        when(resultSet.getDate("dataAcquisto")).thenReturn(Date.valueOf(LocalDate.now()));

        ArrayList<OrderBean> results = orderDao.findByIdUtente(1);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void testFindByIdUtenteNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false);

        ArrayList<OrderBean> results = orderDao.findByIdUtente(1);

        assertNull(results);
    }

    @Test
    void testFindByIdUtenteException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> orderDao.findByIdUtente(1));
    }
}
