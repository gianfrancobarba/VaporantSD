package com.vaporant.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    private ResultSet resultSet;

    @InjectMocks
    private ProductModelDM productModel;

    @Test
    @DisplayName("doRetrieveAll - Ordine specificato - Restituisce lista prodotti ordinata")
    void testDoRetrieveAll() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        // Kill SQL Logic Mutants: Verify correct ORDER BY clause
        when(connection.prepareStatement(contains("ORDER BY nome"))).thenReturn(preparedStatement);
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

        verify(connection).prepareStatement(contains("ORDER BY nome"));
    }

    @Test
    @DisplayName("doSave - Prodotto valido - Inserimento con successo")
    void testDoSave() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        // Verify INSERT statement structure
        when(connection.prepareStatement(startsWith("INSERT INTO prodotto"))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        ProductBean p = new ProductBean();
        p.setName("New Product");
        p.setDescription("Desc");
        p.setQuantityStorage(10);
        p.setPrice(100);

        productModel.doSave(p);

        // Verify all setters
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
        when(connection.prepareStatement(startsWith("DELETE FROM prodotto"))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = productModel.doDelete(1);

        assertTrue(result, "doDelete dovrebbe ritornare true per ID esistente");
        verify(preparedStatement).setInt(1, 1);

    }

    @Test
    @DisplayName("doDelete - ID non esistente - Restituisce false")
    void testDoDeleteFailure() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(startsWith("DELETE FROM prodotto"))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

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
        when(connection.prepareStatement(contains("WHERE ID = ?"))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("nome")).thenReturn("Product 1");
        when(resultSet.getFloat("prezzoAttuale")).thenReturn(10.0f);

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
        when(connection.prepareStatement(contains("UPDATE "))).thenReturn(preparedStatement);
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
        // Verify default query (No ORDER BY)
        when(connection.prepareStatement(startsWith("SELECT * FROM prodotto"))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Collection<ProductBean> products = productModel.doRetrieveAll(null);
        assertNotNull(products, "doRetrieveAll dovrebbe ritornare Collection anche senza ordine");
        verify(connection).prepareStatement(startsWith("SELECT * FROM prodotto")); // Strict check

        // Reset for next call logic
        when(connection.prepareStatement(contains("ORDER BY custom"))).thenReturn(preparedStatement);
        products = productModel.doRetrieveAll("custom");
        assertNotNull(products, "doRetrieveAll dovrebbe ritornare Collection per ordine");
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

        // Act
        boolean result = productModel.doDelete(999);

        // Assert
        assertTrue(result, "doDelete dovrebbe ritornare true per delete riuscito");
        // Verify setter parameter (kill VoidMethodCallMutator)
        verify(preparedStatement).setInt(1, 999);
    }

    @Test
    @DisplayName("doRetrieveByKey - Verify mapping TUTTI 5 campi ProductBean")
    void testDoRetrieveByKey_AllFieldsMapped() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("nome")).thenReturn("Prodotto Test");
        when(resultSet.getString("descrizione")).thenReturn("Descrizione completa");
        when(resultSet.getFloat("prezzoAttuale")).thenReturn(19.99f);
        when(resultSet.getInt("quantita")).thenReturn(50);
        when(resultSet.getString("tipo")).thenReturn("Svapo");
        when(resultSet.getString("colore")).thenReturn("Nero");

        ProductBean result = productModel.doRetrieveByKey(1);
        assertNotNull(result, "doRetrieveByKey dovrebbe ritornare ProductBean");
        assertEquals(1, result.getCode(), "Codice dovrebbe essere 1");
        assertEquals("Prodotto Test", result.getName(), "Nome dovrebbe essere 'Prodotto Test'");
        assertEquals("Descrizione completa", result.getDescription(), "Descrizione dovrebbe essere completa");
        assertEquals(19.99f, result.getPrice(), 0.0001f, "Prezzo dovrebbe essere 19.99");
        assertEquals(50, result.getQuantityStorage(), "Quantità dovrebbe essere 50");
        assertEquals("Svapo", result.getTipo(), "Tipo dovrebbe essere 'Svapo'");
        assertEquals("Nero", result.getColore(), "Colore dovrebbe essere 'Nero'");

        verify(preparedStatement).setInt(1, 1);
    }

    @Test
    @DisplayName("doRetrieveAll - Database vuoto - Restituisce Collection vuota")
    void testDoRetrieveAll_EmptyDatabase_ReturnsEmptyList() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Collection<ProductBean> results = productModel.doRetrieveAll(null);

        assertNotNull(results, "doRetrieveAll dovrebbe ritornare Collection non null");
        assertTrue(results.isEmpty(), "doRetrieveAll dovrebbe ritornare Collection vuota");
    }

    @Test
    @DisplayName("doRetrieveAll - Multiple products - Verify 2+ prodotti mappati")
    void testDoRetrieveAll_MultipleProducts_AllMapped() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("ID")).thenReturn(1, 2);
        when(resultSet.getString("nome")).thenReturn("Prodotto A", "Prodotto B");
        when(resultSet.getString("descrizione")).thenReturn("Desc A", "Desc B");
        when(resultSet.getFloat("prezzoAttuale")).thenReturn(10.0f, 20.0f);
        when(resultSet.getInt("quantita")).thenReturn(100, 200);

        Collection<ProductBean> results = productModel.doRetrieveAll(null);

        assertTrue(results.size() >= 2, "Dovrebbe avere almeno 2 prodotti");

        ProductBean[] products = results.toArray(new ProductBean[0]);
        assertEquals(1, products[0].getCode(), "Primo prodotto code=1");
        assertEquals("Prodotto A", products[0].getName(), "Primo prodotto nome='Prodotto A'");
        assertEquals(10.0f, products[0].getPrice(), 0.0001f, "Primo prodotto price=10.0");

        assertEquals(2, products[1].getCode(), "Secondo prodotto code=2");
        assertEquals("Prodotto B", products[1].getName(), "Secondo prodotto nome='Prodotto B'");
        assertEquals(20.0f, products[1].getPrice(), 0.0001f, "Secondo prodotto price=20.0");
    }

    @Test
    @DisplayName("doDelete - Prodotto non esistente - Restituisce false")
    void testDoDelete_NotFound_ReturnsFalse() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0); // 0 rows affected

        boolean result = productModel.doDelete(999);

        assertFalse(result, "doDelete dovrebbe ritornare false per prodotto non esistente");
        verify(preparedStatement).setInt(1, 999);
    }

    @Test
    @DisplayName("doRetrieveAll - ORDER BY prezzoAttuale - Verify sort ascending")
    void testDoRetrieveAll_OrderByPrice_VerifySortAscending() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, true, false);
        when(resultSet.getInt("ID")).thenReturn(1, 2, 3);
        when(resultSet.getString("nome")).thenReturn("P1", "P2", "P3");
        when(resultSet.getString("descrizione")).thenReturn("Desc1", "Desc2", "Desc3");
        when(resultSet.getFloat("prezzoAttuale")).thenReturn(10.0f, 20.0f, 30.0f); // Sorted ASC
        when(resultSet.getInt("quantita")).thenReturn(5, 10, 15);

        Collection<ProductBean> results = productModel.doRetrieveAll("prezzoAttuale");

        // Assert collection size
        assertEquals(3, results.size(), "Dovrebbe ritornare 3 prodotti");

        // Verify sort order
        ProductBean[] products = results.toArray(new ProductBean[0]);
        assertTrue(products[0].getPrice() <= products[1].getPrice(),
                "Primo prodotto prezzo (<= secondo prezzo (ascending order)");
        assertTrue(products[1].getPrice() <= products[2].getPrice(),
                "Secondo prodotto prezzo <= terzo prezzo (ascending order)");

        // Verify exact values
        assertEquals(10.0f, products[0].getPrice(), 0.01f, "Primo prodotto price=10.0");
        assertEquals(20.0f, products[1].getPrice(), 0.01f, "Secondo prodotto price=20.0");
        assertEquals(30.0f, products[2].getPrice(), 0.01f, "Terzo prodotto price=30.0");
    }
}
