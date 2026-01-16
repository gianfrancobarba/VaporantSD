package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @Test
    @DisplayName("Login - Credenziali valide utente - Redirect a ProductView")
    void testLoginSuccessUser() throws Exception {
        UserBean user = new UserBean();
        user.setTipo("user");
        user.setEmail("test@test.com");

        when(userDao.findByCred("test@test.com", "password")).thenReturn(user);

        mockMvc.perform(post("/login")
                .param("email", "test@test.com")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductView.jsp"));
    }

    @Test
    @DisplayName("Login - Credenziali valide admin - Redirect a ProductViewAdmin")
    void testLoginSuccessAdmin() throws Exception {
        UserBean user = new UserBean();
        user.setTipo("admin");
        user.setEmail("admin@test.com");

        when(userDao.findByCred("admin@test.com", "password")).thenReturn(user);

        mockMvc.perform(post("/login")
                .param("email", "admin@test.com")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductViewAdmin.jsp"));
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
        session.setAttribute("cart", new Cart());

        mockMvc.perform(post("/login")
                .session(session)
                .param("email", "test@test.com")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("checkout.jsp"));
    }

    @Test
    @DisplayName("Login - Credenziali non valide - Redirect a loginForm con errore")
    void testLoginFailure() throws Exception {
        when(userDao.findByCred(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(post("/login")
                .param("email", "wrong")
                .param("password", "wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));
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
}
