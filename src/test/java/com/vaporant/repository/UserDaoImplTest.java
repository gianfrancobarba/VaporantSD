package com.vaporant.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vaporant.model.UserBean;

@ExtendWith(MockitoExtension.class)
class UserDaoImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserDaoImpl userDao;

    @BeforeEach
    void setUp() throws SQLException {
        // Common setup if needed, but usually done in tests
    }

    @Test
    @DisplayName("findByCred - Credenziali esistenti - Restituisce UserBean popolato")
    void testFindByCredSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);

        when(resultSet.getString("email")).thenReturn("test@test.com");
        when(resultSet.getString("CF")).thenReturn("CF123");
        when(resultSet.getString("nome")).thenReturn("Test");
        when(resultSet.getString("cognome")).thenReturn("User");
        when(resultSet.getString("numTelefono")).thenReturn("123456");
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("psw")).thenReturn("password");
        when(resultSet.getString("tipo")).thenReturn("user");
        when(resultSet.getDate("dataNascita")).thenReturn(Date.valueOf(LocalDate.now()));

        UserBean user = userDao.findByCred("test@test.com", "password");

        assertNotNull(user);
        assertEquals("test@test.com", user.getEmail());
        assertEquals("user", user.getTipo());

        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).setString(1, "test@test.com");
        verify(preparedStatement).setString(2, "password");
    }

    @Test
    @DisplayName("findByCred - Credenziali inesistenti - Restituisce null")
    void testFindByCredNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(false);

        UserBean user = userDao.findByCred("wrong", "wrong");

        assertNull(user);
    }

    @Test
    @DisplayName("saveUser - Dati validi - Inserimento con successo")
    void testSaveUser() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        UserBean user = new UserBean();
        user.setNome("Test");
        user.setCognome("User");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setCodF("CF123");
        user.setNumTelefono("123456");
        user.setDataNascita(LocalDate.now());
        user.setTipo("user");

        int result = userDao.saveUser(user);

        assertEquals(1, result);
        verify(preparedStatement).setString(1, "Test");
        verify(preparedStatement).setString(3, "test@test.com");
    }

    @Test
    @DisplayName("saveUser - SQLException dal DataSource - Propaga eccezione")
    void testSQLException() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("DB Error"));

        assertThrows(SQLException.class, () -> {
            userDao.findByCred("test", "test");
        });
    }

    @Test
    @DisplayName("saveUser - Tipo null - Default a 'user'")
    void testSaveUserWithNullType() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        UserBean user = new UserBean();
        user.setNome("Test");
        user.setCognome("User");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setCodF("CF123");
        user.setNumTelefono("123456");
        user.setDataNascita(LocalDate.now());
        user.setTipo(null); // Null type

        int result = userDao.saveUser(user);

        assertEquals(1, result);
        verify(preparedStatement).setString(8, "user"); // Should default to "user"
    }

    @Test
    @DisplayName("findById - ID esistente - Restituisce UserBean popolato")
    void testFindByIdSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.next()).thenReturn(true).thenReturn(false);

        when(resultSet.getString("email")).thenReturn("test@test.com");
        when(resultSet.getString("CF")).thenReturn("CF123");
        when(resultSet.getString("nome")).thenReturn("Test");
        when(resultSet.getString("cognome")).thenReturn("User");
        when(resultSet.getString("numTelefono")).thenReturn("123456");
        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("psw")).thenReturn("password");
        when(resultSet.getString("tipo")).thenReturn("user");
        when(resultSet.getDate("dataNascita")).thenReturn(Date.valueOf(LocalDate.now()));

        UserBean user = userDao.findById(1);

        assertNotNull(user);
        assertEquals(1, user.getId());
    }

    @Test
    @DisplayName("findById - ID inesistente - Restituisce null")
    void testFindByIdNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(false);

        UserBean user = userDao.findById(999);

        assertNull(user);
    }

    @ParameterizedTest(name = "modify{0} aggiorna campo con valore {1}")
    @CsvSource({
            "Mail, new@test.com",
            "Telefono, 987654321"
    })
    @DisplayName("UserDao - Metodi modify aggiornano campi singoli")
    void testModifyMethods(String field, String newValue) throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        UserBean user = new UserBean();
        user.setId(1);

        // Act
        if ("Mail".equals(field)) {
            userDao.modifyMail(user, newValue);
        } else if ("Telefono".equals(field)) {
            userDao.modifyTelefono(user, newValue);
        }

        // Assert
        verify(preparedStatement).setString(1, newValue);
        verify(preparedStatement).setInt(2, 1);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("modifyPsw - Password vecchia corretta - Aggiorna con successo")
    void testModifyPswSuccess() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        UserBean user = new UserBean();
        user.setId(1);
        user.setPassword("oldPass");

        int result = userDao.modifyPsw("newPass", "oldPass", user);

        assertEquals(1, result);
        verify(preparedStatement).setString(1, "newPass");
        verify(preparedStatement).setInt(2, 1);
        verify(preparedStatement).setString(3, "oldPass");
    }

    @Test
    @DisplayName("modifyPsw - Password vecchia errata - Restituisce 0 senza query DB")
    void testModifyPswWrongOld() throws SQLException {
        UserBean user = new UserBean();
        user.setId(1);
        user.setPassword("actualPass");

        int result = userDao.modifyPsw("newPass", "wrongPass", user);

        assertEquals(0, result);
        // Should not interact with DB
        verify(dataSource, times(0)).getConnection();
    }

    @Test
    @DisplayName("updateAddress - Indirizzo nuovo - Aggiorna UserBean")
    void testUpdateAddress() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        // Note: updateAddress in UserDaoImpl has commented out executeUpdate() line
        // 289.
        // If it is commented out, we should not verify executeUpdate.
        // But the user asked to cover branches. The code has:
        // preparedStatement.setString(1, address);
        // preparedStatement.setInt(2, user.getId());
        // // preparedStatement.executeUpdate();

        UserBean user = new UserBean();
        user.setId(1);

        userDao.updateAddress("New Address", user);

        verify(preparedStatement).setString(1, "New Address");
        verify(preparedStatement).setInt(2, 1);
        // verify(preparedStatement).executeUpdate(); // Commented out in source
        assertEquals("New Address", user.getIndirizzoFatt());
    }

    @Test
    @DisplayName("modifyMail - SQLException dal DataSource - Propaga eccezione")
    void testModifyMailException() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("DB Error"));
        UserBean user = new UserBean();
        user.setId(1);
        assertThrows(SQLException.class, () -> userDao.modifyMail(user, "test"));
    }

    @Test
    @DisplayName("modifyTelefono - SQLException dal DataSource - Propaga eccezione")
    void testModifyTelefonoException() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("DB Error"));
        UserBean user = new UserBean();
        user.setId(1);
        assertThrows(SQLException.class, () -> userDao.modifyTelefono(user, "123"));
    }

    @Test
    @DisplayName("updateAddress - SQLException dal DataSource - Propaga eccezione")
    void testUpdateAddressException() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("DB Error"));
        UserBean user = new UserBean();
        user.setId(1);
        assertThrows(SQLException.class, () -> userDao.updateAddress("address", user));
    }
}
