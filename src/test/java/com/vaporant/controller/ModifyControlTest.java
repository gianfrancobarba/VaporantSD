package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.vaporant.model.UserBean;
import com.vaporant.repository.UserDAO;

@WebMvcTest(ModifyControl.class)
class ModifyControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDao;

    @Test
    @DisplayName("Modify - Modifica email utente - Restituisce JSON con nuova email")
    void testModifyEmail() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        user.setEmail("new@test.com");

        when(userDao.findById(1)).thenReturn(user);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "modificaEmail")
                .param("nuovaEmail", "new@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"email\": \"new@test.com\" }"));

        verify(userDao).modifyMail(any(UserBean.class), eq("new@test.com"));
    }

    @Test
    @DisplayName("Modify - Modifica telefono utente - Restituisce JSON con nuovo telefono")
    void testModifyTelefono() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        user.setNumTelefono("987654321");

        when(userDao.findById(1)).thenReturn(user);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "modificaTelefono")
                .param("nuovoTelefono", "987654321"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"numTelefono\": \"987654321\" }"));

        verify(userDao).modifyTelefono(any(UserBean.class), eq("987654321"));
    }

    @Test
    @DisplayName("Modify - Modifica password con successo - Restituisce success true")
    void testModifyPasswordSuccess() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);

        when(userDao.modifyPsw(anyString(), anyString(), any(UserBean.class))).thenReturn(1);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "modificaPassword")
                .param("nuovaPassword", "newPass")
                .param("vecchiaPassword", "oldPass"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\": true}"));
    }

    @Test
    @DisplayName("Modify - Modifica password fallita - Restituisce success false")
    void testModifyPasswordFailure() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);

        when(userDao.modifyPsw(anyString(), anyString(), any(UserBean.class))).thenReturn(0);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "modificaPassword")
                .param("nuovaPassword", "newPass")
                .param("vecchiaPassword", "oldPass"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\": false}"));
    }

    @Test
    @DisplayName("Modify - Action sconosciuta - Restituisce Bad Request")
    void testDefaultAction() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "unknown"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Modify - Sessione utente mancante - Gestione gracefully (NPE)")
    void testModifyMissingUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        // Missing "user"

        mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "modificaEmail")
                .param("nuovaEmail", "new@test.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Modify - Action null - Gestione gracefully (NPE)")
    void testModifyNullAction() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/modify")
                .session(session))
                // NO action param
                .andExpect(status().isBadRequest());
    }
}
