package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.test.web.servlet.MockMvc;

import com.vaporant.model.UserBean;
import com.vaporant.repository.UserDAO;

@WebMvcTest(SignControl.class)
class SignControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDao;

    @Test
    @DisplayName("Sign - Registrazione con successo - Redirect a loginForm")
    void testSignSuccess() throws Exception {
        when(userDao.saveUser(any(UserBean.class))).thenReturn(1);

        mockMvc.perform(post("/SignControl")
                .param("nome", "Test")
                .param("cognome", "User")
                .param("data_nascita", "2000-01-01")
                .param("codice_fiscale", "CF123")
                .param("telefono", "123456")
                .param("email", "test@test.com")
                .param("password", "password")
                .param("indirizzoFatt", "Via Roma"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));
    }

    @Test
    @DisplayName("Sign - Registrazione fallita - Redirect a SignForm")
    void testSignFailure() throws Exception {
        when(userDao.saveUser(any(UserBean.class))).thenReturn(0);

        mockMvc.perform(post("/SignControl")
                .param("nome", "Test")
                .param("cognome", "User")
                .param("data_nascita", "2000-01-01")
                .param("codice_fiscale", "CF123")
                .param("telefono", "123456")
                .param("email", "test@test.com")
                .param("password", "password")
                .param("indirizzoFatt", "Via Roma"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("SignForm.jsp"));
    }

    @Test
    @DisplayName("Sign - SQLException durante registrazione - Gestione errore gracefully")
    void testSignException() throws Exception {
        when(userDao.saveUser(any(UserBean.class))).thenThrow(new SQLException("DB Error"));

        mockMvc.perform(post("/SignControl")
                .param("nome", "Test")
                .param("cognome", "User")
                .param("data_nascita", "2000-01-01")
                .param("codice_fiscale", "CF123")
                .param("telefono", "123456")
                .param("email", "test@test.com")
                .param("password", "password")
                .param("indirizzoFatt", "Via Roma"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("SignForm.jsp"));
    }
}
