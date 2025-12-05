package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;

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
    void testLoginFailure() throws Exception {
        when(userDao.findByCred(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(post("/login")
                .param("email", "wrong")
                .param("password", "wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));
    }

    @Test
    void testLoginException() throws Exception {
        when(userDao.findByCred(anyString(), anyString())).thenThrow(new SQLException("DB Error"));

        mockMvc.perform(post("/login")
                .param("email", "test")
                .param("password", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));
    }

    @Test
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
