package com.vaporant.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.vaporant.model.OrderBean;
import com.vaporant.model.UserBean;

@WebMvcTest(FatturaControl.class)
class FatturaControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("showFattura - Sessione valida - Restituisce vista fattura")
    void testShowFatturaSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("order", new OrderBean());
        session.setAttribute("user", new UserBean());

        mockMvc.perform(get("/fattura")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("fattura.jsp"));
    }

    @Test
    @DisplayName("showFattura - Sessione mancante ordine - Redirect a login")
    void testShowFatturaNoOrder() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new UserBean());

        mockMvc.perform(get("/fattura")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));
    }

    @Test
    @DisplayName("showFattura - Sessione mancante utente - Redirect a login")
    void testShowFatturaNoUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("order", new OrderBean());

        mockMvc.perform(get("/fattura")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));
    }
}
