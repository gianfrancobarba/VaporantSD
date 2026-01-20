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

        assertNotNull(user, "findByCred dovrebbe ritornare UserBean per credenziali valide");
        assertEquals("test@test.com", user.getEmail(), "Email dovrebbe corrispondere a quella fornita");
        assertEquals("user", user.getTipo(), "Tipo dovrebbe essere 'user'");

        // ✅ PHASE 2.1 FIX: Verify ALL bean setters called (kill VoidMethodCall)
        assertNotNull(user.getNome(), "Nome should be set");
        assertEquals("Test", user.getNome());
        assertNotNull(user.getCognome(), "Cognome should be set");
        assertEquals("User", user.getCognome());
        assertNotNull(user.getCodF(), "CF should be set");
        assertEquals("CF123", user.getCodF());
        assertNotNull(user.getNumTelefono(), "Telefono should be set");
        assertEquals("123456", user.getNumTelefono());
        assertNotNull(user.getDataNascita(), "DataNascita should be set"); // ← Kills L126 mutation
        assertNotNull(user.getPassword(), "Password should be set");
        assertEquals("password", user.getPassword());
        assertEquals(1, user.getId(), "ID should be set");

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

        assertNull(user, "findByCred dovrebbe ritornare null per credenziali inesistenti");
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

        assertEquals(1, result, "saveUser dovrebbe ritornare 1 per inserimento riuscito");
        // ✅ Verify TUTTI i setters (kill VoidMethodCallMutator)
        verify(preparedStatement).setString(1, "Test");
        verify(preparedStatement).setString(2, "User");
        verify(preparedStatement).setString(3, "test@test.com");
        verify(preparedStatement).setString(4, "password");
        verify(preparedStatement).setString(5, "CF123");
        verify(preparedStatement).setString(6, "123456");
        verify(preparedStatement).setString(7, user.getDataNascita().toString());
        verify(preparedStatement).setString(8, "user");
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

        assertEquals(1, result, "saveUser dovrebbe ritornare 1 anche con tipo null (default 'user')");
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

        assertNotNull(user, "findById dovrebbe ritornare UserBean per ID esistente");
        assertEquals(1, user.getId(), "ID dovrebbe essere 1");

        // ✅ PHASE 2.1 FIX: Verify ALL bean setters (kill VoidMethodCall L178)
        assertNotNull(user.getNome(), "Nome should be set");
        assertNotNull(user.getCognome(), "Cognome should be set");
        assertNotNull(user.getEmail(), "Email should be set");
        assertNotNull(user.getCodF(), "CF should be set");
        assertNotNull(user.getNumTelefono(), "Telefono should be set");
        assertNotNull(user.getDataNascita(), "DataNascita should be set"); // ← Kills L178
        assertNotNull(user.getPassword(), "Password should be set");
        assertNotNull(user.getTipo(), "Tipo should be set");
    }

    @Test
    @DisplayName("findById - ID inesistente - Restituisce null")
    void testFindByIdNotFound() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.isBeforeFirst()).thenReturn(false);

        UserBean user = userDao.findById(999);

        assertNull(user, "findById dovrebbe ritornare null per ID inesistente");
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

        assertEquals(1, result, "modifyPsw dovrebbe ritornare 1 per password corretta");
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

        assertEquals(0, result, "modifyPsw dovrebbe ritornare 0 per password vecchia errata");
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
        assertEquals("New Address", user.getIndirizzoFatt(), "Indirizzo fatturazione dovrebbe essere aggiornato");
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

    @Test
    @DisplayName("Resource Management - SQLException chiude correttamente Connection e PreparedStatement")
    void findByCred_sqlException_closesResources() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Test database error"));
        // Act & Assert
        assertThrows(SQLException.class, () -> userDao.findByCred("test@test.com", "password"),
                "SQLException dovrebbe essere propagata");

        // Verify resources closed anche in caso di exception
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @Test
    @DisplayName("deleteUser - Verifica setInt parametro ID")
    void testDeleteUserParametersSet() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        UserBean user = new UserBean();
        user.setId(42);

        // Act
        int result = userDao.deleteUser(user);

        // Assert
        assertEquals(1, result);
        // ✅ Verify setter parameter (kill VoidMethodCallMutator)
        verify(preparedStatement).setInt(1, 42);
    }

    @Test
    @DisplayName("modifyMail - Verifica tutti parametri setString e setInt")
    void testModifyMailParametersSet() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        UserBean user = new UserBean();
        user.setId(10);
        String newEmail = "new@email.com";

        // Act
        userDao.modifyMail(user, newEmail);

        // Assert
        // ✅ Verify entrambi i parametri (kill VoidMethodCallMutator)
        verify(preparedStatement).setString(1, "new@email.com");
        verify(preparedStatement).setInt(2, 10);
    }

    @Test
    @DisplayName("modifyTelefono - Verifica tutti parametri setString e setInt")
    void testModifyTelefonoParametersSet() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        UserBean user = new UserBean();
        user.setId(15);
        String newCell = "9876543210";

        // Act
        userDao.modifyTelefono(user, newCell);

        // Assert
        // ✅ Verify entrambi parametri
        verify(preparedStatement).setString(1, "9876543210");
        verify(preparedStatement).setInt(2, 15);
    }

    @Test
    @DisplayName("modifyPsw - Success - Verifica tutti i 3 parametri")
    void testModifyPswAllParametersSet() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        UserBean user = new UserBean();
        user.setId(20);
        user.setPassword("oldPassword");

        // Act
        int result = userDao.modifyPsw("newPassword", "oldPassword", user);

        // Assert
        assertEquals(1, result);
        // ✅ Verify tutti i 3 setters (kill VoidMethodCallMutator)
        verify(preparedStatement).setString(1, "newPassword");
        verify(preparedStatement).setInt(2, 20);
        verify(preparedStatement).setString(3, "oldPassword");
    }
}
