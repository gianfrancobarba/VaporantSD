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

import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("doRetrieveAll - Ordine specificato - Restituisce lista prodotti ordinata")
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

        assertNotNull(products, "doRetrieveAll dovrebbe ritornare Collection di prodotti");
        assertEquals(1, products.size(), "Collection dovrebbe contenere 1 prodotto");
        ProductBean p = products.iterator().next();
        assertEquals("Product 1", p.getName(), "Nome prodotto dovrebbe essere 'Product 1'");
    }

    @Test
    @DisplayName("doSave - Prodotto valido - Inserimento con successo")
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

        // ✅ Verify TUTTI i 4 setters (kill VoidMethodCallMutator)
        verify(preparedStatement).setString(1, "New Product"); // Nome
        verify(preparedStatement).setString(2, "Desc"); // Description
        verify(preparedStatement).setInt(3, 10); // QuantityStorage
        verify(preparedStatement).setFloat(4, 100); // Price
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("doDelete - ID esistente - Cancellazione con successo")
    void testDoDelete() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(connection.createStatement()).thenReturn(statement);

        boolean result = productModel.doDelete(1);

        assertTrue(result, "doDelete dovrebbe ritornare true per ID esistente");
        verify(preparedStatement).setInt(1, 1);
        verify(statement).executeUpdate(anyString());
    }

    @Test
    @DisplayName("doDelete - ID non esistente - Restituisce false")
    void testDoDeleteFailure() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        when(connection.createStatement()).thenReturn(statement);

        boolean result = productModel.doDelete(999);

        assertFalse(result, "doDelete dovrebbe ritornare false per ID non esistente");
    }

    @Test
    @DisplayName("doDelete - SQLException dal DataSource - Propaga eccezione")
    void testDoDeleteException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> productModel.doDelete(1));
    }

    @Test
    @DisplayName("doRetrieveByKey - ID esistente - Restituisce ProductBean popolato")
    void testDoRetrieveByKeySuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("nome")).thenReturn("Product 1");

        ProductBean result = productModel.doRetrieveByKey(1);

        assertNotNull(result, "doRetrieveByKey dovrebbe ritornare ProductBean per ID esistente");
        assertEquals(1, result.getCode(), "Code dovrebbe essere 1");
        assertEquals("Product 1", result.getName(), "Nome dovrebbe essere 'Product 1'");
    }

    @Test
    @DisplayName("doRetrieveByKey - SQLException dal DataSource - Propaga eccezione")
    void testDoRetrieveByKeyException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> productModel.doRetrieveByKey(1));
    }

    @Test
    @DisplayName("updateQuantityStorage - Quantità valida - Aggiorna con successo")
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
    @DisplayName("updateQuantityStorage - SQLException dal DataSource - Propaga eccezione")
    void testUpdateQuantityStorageException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        ProductBean prod = new ProductBean();
        prod.setCode(1);

        assertThrows(SQLException.class, () -> productModel.updateQuantityStorage(prod, 5));
    }

    @Test
    @DisplayName("doRetrieveAll - Nessun ordine specificato - Restituisce lista prodotti")
    void testDoRetrieveAllNoOrder() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Collection<ProductBean> products = productModel.doRetrieveAll(null);
        assertNotNull(products, "doRetrieveAll dovrebbe ritornare Collection anche senza ordine");

        products = productModel.doRetrieveAll("");
        assertNotNull(products, "doRetrieveAll dovrebbe ritornare Collection per ordine vuoto");
    }

    @Test
    @DisplayName("doRetrieveByKey - ID non esistente - Restituisce bean vuoto")
    void testDoRetrieveByKeyNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        ProductBean result = productModel.doRetrieveByKey(999);

        // ProductModelDM returns an empty bean (not null)if not found, but fields are
        // default.
        assertNotNull(result, "doRetrieveByKey dovrebbe ritornare bean vuoto per ID non esistente");
        assertEquals(0, result.getCode(), "Code dovrebbe essere 0 per bean vuoto");
        assertNull(result.getName(), "Nome dovrebbe essere null per bean vuoto");
    }

    @Test
    @DisplayName("Resource Management - SQLException chiude correttamente Connection e PreparedStatement")
    void doRetrieveByKey_sqlException_closesResources() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Test database error"));

        // Act & Assert
        assertThrows(SQLException.class, () -> productModel.doRetrieveByKey(1),
                "SQLException dovrebbe essere propagata");

        // Verify resource cleanup (try-with-resources)
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @Test
    @DisplayName("doDelete - Verifica setInt parametro code")
    void testDoDeleteParameterSet() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(connection.createStatement()).thenReturn(statement);

        // Act
        boolean result = productModel.doDelete(999);

        // Assert
        assertTrue(result, "doDelete dovrebbe ritornare true per delete riuscito");
        // ✅ Verify setter parameter (kill VoidMethodCallMutator)
        verify(preparedStatement).setInt(1, 999);
    }
}
