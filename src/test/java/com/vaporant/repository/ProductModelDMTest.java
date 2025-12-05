package com.vaporant.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vaporant.model.ProductBean;

@ExtendWith(MockitoExtension.class)
class ProductModelDMTest {

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
    private ProductModelDM productModel;

    @Test
    void testDoRetrieveAll() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("nome")).thenReturn("Product 1");
        when(resultSet.getString("descrizione")).thenReturn("Desc");
        when(resultSet.getFloat("prezzoAttuale")).thenReturn(10.0f);
        when(resultSet.getInt("quantita")).thenReturn(5);

        Collection<ProductBean> products = productModel.doRetrieveAll("nome");

        assertNotNull(products);
        assertEquals(1, products.size());
        ProductBean p = products.iterator().next();
        assertEquals("Product 1", p.getName());
    }

    @Test
    void testDoSave() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        ProductBean p = new ProductBean();
        p.setName("New Product");
        p.setDescription("Desc");
        p.setQuantityStorage(10);
        p.setPrice(100);

        productModel.doSave(p);

        verify(preparedStatement).setString(1, "New Product");
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testDoDelete() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(connection.createStatement()).thenReturn(statement);

        boolean result = productModel.doDelete(1);

        assertTrue(result);
        verify(preparedStatement).setInt(1, 1);
        verify(statement).executeUpdate(anyString());
    }

    @Test
    void testDoDeleteFailure() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        when(connection.createStatement()).thenReturn(statement);

        boolean result = productModel.doDelete(999);

        assertFalse(result);
    }

    @Test
    void testDoDeleteException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> productModel.doDelete(1));
    }

    @Test
    void testDoRetrieveByKeySuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("nome")).thenReturn("Product 1");

        ProductBean result = productModel.doRetrieveByKey(1);

        assertNotNull(result);
        assertEquals(1, result.getCode());
        assertEquals("Product 1", result.getName());
    }

    @Test
    void testDoRetrieveByKeyException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> productModel.doRetrieveByKey(1));
    }

    @Test
    void testUpdateQuantityStorageSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        ProductBean prod = new ProductBean();
        prod.setCode(1);

        productModel.updateQuantityStorage(prod, 5);

        verify(preparedStatement).setInt(1, 5);
        verify(preparedStatement).setInt(2, 1);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testUpdateQuantityStorageException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        ProductBean prod = new ProductBean();
        prod.setCode(1);

        assertThrows(SQLException.class, () -> productModel.updateQuantityStorage(prod, 5));
    }

    @Test
    void testDoRetrieveAllNoOrder() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Collection<ProductBean> products = productModel.doRetrieveAll(null);
        assertNotNull(products);

        products = productModel.doRetrieveAll("");
        assertNotNull(products);
    }

    @Test
    void testDoRetrieveByKeyNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        ProductBean result = productModel.doRetrieveByKey(999);

        // ProductModelDM returns an empty bean (not null) if not found, but fields are
        // default.
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getName());
    }
}
