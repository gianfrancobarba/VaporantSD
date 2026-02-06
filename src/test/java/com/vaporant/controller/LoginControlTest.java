package com.vaporant.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.vaporant.model.Cart;
import com.vaporant.model.UserBean;
import com.vaporant.repository.UserDAO;

@WebMvcTest(LoginControl.class)
class LoginControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDao;

    @ParameterizedTest(name = "Login tipo {0} con email {1} redirect a {2}")
    @CsvSource({
            "user, test@test.com, ProductView.jsp",
            "admin, admin@test.com, ProductViewAdmin.jsp"
    })
    @DisplayName("Login - Redirect basato su tipo utente")
    @SuppressWarnings("null")
    void testLoginRedirectByUserType(String userType, String email, String expectedUrl) throws Exception {
        // Arrange
        UserBean user = new UserBean();
        user.setTipo(userType);
        user.setEmail(email);

        when(userDao.findByCred(email, "password")).thenReturn(user);

        MockHttpSession session = new MockHttpSession();

        // Act & Assert
        var result = mockMvc.perform(post("/login")
                .session(session)
                .param("email", email)
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedUrl))
                .andReturn();

        // Verify Session State (kill VoidMethodCallMutator)
        // LoginControl invalidates old session and creates new one
        // Must access the NEW session from result, not the passed session
        var newSession = result.getRequest().getSession();
        UserBean sessionUser = (UserBean) newSession.getAttribute("user");
        assertNotNull(sessionUser, "User should be in session after successful login");
        assertEquals(email, sessionUser.getEmail(), "Email should match");
        assertEquals(userType, sessionUser.getTipo(), "User type should match");

        String sessionTipo = (String) newSession.getAttribute("tipo");
        assertEquals(userType, sessionTipo, "Tipo attribute should be set in session");
    }

    @Test
    @DisplayName("Login - Checkout flow attivo - Redirect diretto a checkout")
    void testLoginSuccessCheckout() throws Exception {
        UserBean user = new UserBean();
        user.setTipo("user");
        user.setEmail("test@test.com");

        when(userDao.findByCred("test@test.com", "password")).thenReturn(user);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("action", "checkout");
        Cart initialCart = new Cart();
        session.setAttribute("cart", initialCart);

        var result = mockMvc.perform(post("/login")
                .session(session)
                .param("email", "test@test.com")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("checkout.jsp"))
                .andReturn();

        // Verify Session State - action preserved across session invalidation
        // LoginControl preserves action/cart by copying to new session
        var newSession = result.getRequest().getSession();
        assertNotNull(newSession, "Session should exist after login");
        String actionAfterLogin = (String) newSession.getAttribute("action");
        assertEquals("checkout", actionAfterLogin, "Action should be preserved after login");

        Cart cartAfterLogin = (Cart) newSession.getAttribute("cart");
        assertNotNull(cartAfterLogin, "Cart should be preserved after login");
    }

    @Test
    @DisplayName("Login - Credenziali non valide - Redirect a loginForm con errore")
    void testLoginFailure() throws Exception {
        when(userDao.findByCred(anyString(), anyString())).thenReturn(null);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/login")
                .session(session)
                .param("email", "wrong")
                .param("password", "wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));

        // Verify Session State - no user in session after login failure
        UserBean sessionUser = (UserBean) session.getAttribute("user");
        assertNull(sessionUser, "User should NOT be in session after login failure");
    }

    @Test
    @DisplayName("Login - SQLException dal DAO - Gestione errore gracefully")
    void testLoginException() throws Exception {
        when(userDao.findByCred(anyString(), anyString())).thenThrow(new SQLException("DB Error"));

        mockMvc.perform(post("/login")
                .param("email", "test")
                .param("password", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));
    }

    @Test
    @DisplayName("Login - Action diversa da checkout - Redirect a ProductView")
    void testLoginSuccessActionNotCheckout() throws Exception {
        UserBean user = new UserBean();
        user.setTipo("user");
        user.setEmail("test@test.com");

        when(userDao.findByCred("test@test.com", "password")).thenReturn(user);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("action", "other");
        session.setAttribute("cart", new Cart());

        mockMvc.perform(post("/login")
                .session(session)
                .param("email", "test@test.com")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductView.jsp"));
    }

    @Test
    @DisplayName("Login - Parametri email null - Gestione gracefully")
    void testLoginWithNullEmail() throws Exception {
        // Act & Assert - email null dovrebbe causare findByCred(null, password)
        when(userDao.findByCred(null, "password")).thenReturn(null);

        mockMvc.perform(post("/login")
                .param("password", "password")) // NO email param
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));
    }

    @Test
    @DisplayName("Login - Action null in session - Redirect a ProductView default")
    void testLoginWithActionNull() throws Exception {
        UserBean user = new UserBean();
        user.setTipo("user");
        user.setEmail("test@test.com");

        when(userDao.findByCred("test@test.com", "password")).thenReturn(user);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("action", null); // ✅ Action null
        session.setAttribute("cart", new Cart());

        mockMvc.perform(post("/login")
                .session(session)
                .param("email", "test@test.com")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductView.jsp"));
    }
}
