package com.vaporant.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vaporant.model.ContenutoBean;

@ExtendWith(MockitoExtension.class)
class ContenutoDaoImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private ContenutoDaoImpl contenutoDao;

    @Test
    void testSaveContenutoSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        ContenutoBean bean = new ContenutoBean();
        bean.setId_ordine(1);
        bean.setId_prodotto(1);
        bean.setQuantita(2);
        bean.setPrezzoAcquisto(10.0f);
        bean.setIvaAcquisto(22);

        int result = contenutoDao.saveContenuto(bean);

        assertEquals(1, result);
        // Verify 2 updates: insert and updateStorage
        verify(preparedStatement, times(2)).executeUpdate();
    }

    @Test
    void testSaveContenutoException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        ContenutoBean bean = new ContenutoBean();
        assertThrows(SQLException.class, () -> contenutoDao.saveContenuto(bean));
    }

    @Test
    void testSaveContenutoUpdateStorageException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        // First call (INSERT) succeeds
        when(connection.prepareStatement(startsWith("INSERT"))).thenReturn(preparedStatement);
        // Second call (UPDATE) fails
        when(connection.prepareStatement(startsWith("UPDATE"))).thenThrow(new SQLException("Update Error"));

        when(preparedStatement.executeUpdate()).thenReturn(1);

        ContenutoBean bean = new ContenutoBean();
        bean.setId_ordine(1);
        bean.setId_prodotto(1);
        bean.setQuantita(2);
        bean.setPrezzoAcquisto(10.0f);
        bean.setIvaAcquisto(22);

        assertThrows(SQLException.class, () -> contenutoDao.saveContenuto(bean));
    }

    @Test
    void testDeleteContenutoSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        ContenutoBean bean = new ContenutoBean();
        bean.setId_ordine(1);
        bean.setId_prodotto(1);

        int result = contenutoDao.deleteContenuto(bean);

        assertEquals(1, result);
    }

    @Test
    void testDeleteContenutoException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        ContenutoBean bean = new ContenutoBean();
        assertThrows(SQLException.class, () -> contenutoDao.deleteContenuto(bean));
    }

    @Test
    void testFindByKeySuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("ID_Ordine")).thenReturn(1);
        when(resultSet.getInt("ID_Prodotto")).thenReturn(1);

        ContenutoBean result = contenutoDao.findByKey(1, 1);

        assertNotNull(result);
        assertEquals(1, result.getId_ordine());
        assertEquals(1, result.getId_prodotto());
    }

    @Test
    void testFindByKeyNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false);

        ContenutoBean result = contenutoDao.findByKey(1, 1);

        assertNull(result);
    }

    @Test
    void testFindByKeyException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> contenutoDao.findByKey(1, 1));
    }
}
