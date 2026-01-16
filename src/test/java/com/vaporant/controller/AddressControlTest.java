package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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

import com.vaporant.model.AddressBean;
import com.vaporant.model.UserBean;
import com.vaporant.repository.AddressDAO;

@WebMvcTest(AddressControl.class)
class AddressControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressDAO addressDao;

    @Test
    @DisplayName("Address - Salvataggio indirizzo con successo - Redirect a Utente.jsp")
    void testSaveAddressSuccess() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        when(addressDao.saveAddress(any(AddressBean.class))).thenReturn(1);

        mockMvc.perform(post("/AddressControl")
                .session(session)
                .param("citta", "Milano")
                .param("provincia", "MI")
                .param("via", "Via Roma")
                .param("cap", "20100")
                .param("numero_civico", "10")
                .param("stato", "Italia"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("Utente.jsp"));

        verify(addressDao).saveAddress(any(AddressBean.class));
    }

    @Test
    @DisplayName("Address - Utente null in sessione - Redirect a Utente.jsp senza salvataggio")
    void testSaveAddressUserNull() throws Exception {
        mockMvc.perform(post("/AddressControl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("Utente.jsp"));
    }

    @Test
    @DisplayName("Address - SQLException durante salvataggio - Gestione errore gracefully")
    void testSaveAddressException() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        when(addressDao.saveAddress(any(AddressBean.class))).thenThrow(new SQLException("DB Error"));

        mockMvc.perform(post("/AddressControl")
                .session(session)
                .param("citta", "Milano")
                .param("provincia", "MI")
                .param("via", "Via Roma")
                .param("cap", "20100")
                .param("numero_civico", "10")
                .param("stato", "Italia"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("Utente.jsp"));
    }
}
