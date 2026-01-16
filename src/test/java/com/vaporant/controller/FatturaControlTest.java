package com.vaporant.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.vaporant.model.OrderBean;
import com.vaporant.model.ProductBean;
import com.vaporant.model.UserBean;

@WebMvcTest(FatturaControl.class)
class FatturaControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Fattura - Visualizzazione fattura con successo - Mostra view fattura")
    void testShowFatturaSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("order", new OrderBean());
        session.setAttribute("user", new UserBean());

        mockMvc.perform(get("/fattura")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("fattura"));
    }

    @Test
    @DisplayName("Fattura - Utente non loggato - Redirect a loginForm")
    void testShowFatturaRedirect() throws Exception {
        mockMvc.perform(get("/fattura"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));
    }

    @Test
    @DisplayName("Fattura - Download PDF con successo - Genera PDF con header corretti")
    void testDownloadFatturaPDFSuccess() throws Exception {
        OrderBean order = new OrderBean();
        order.setId_ordine(1);
        order.setDataAcquisto(LocalDate.now());
        order.setMetodoPagamento("Carta");

        List<ProductBean> products = new ArrayList<>();
        ProductBean p = new ProductBean();
        p.setName("Test Product");
        p.setPrice(10.0f);
        p.setQuantity(1);
        products.add(p);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("order", order);
        session.setAttribute("listaProd", products);

        mockMvc.perform(get("/fattura/download")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=fattura_1.pdf"));
    }

    @Test
    @DisplayName("Fattura - Download PDF senza login - Redirect a loginForm")
    void testDownloadFatturaPDFRedirect() throws Exception {
        mockMvc.perform(get("/fattura/download"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("loginForm.jsp"));
    }

    @Test
    @DisplayName("Fattura - Download PDF con nome prodotto lungo - Gestione truncate correttamente")
    void testDownloadFatturaPDFLongProductName() throws Exception {
        OrderBean order = new OrderBean();
        order.setId_ordine(1);
        order.setDataAcquisto(LocalDate.now());
        order.setMetodoPagamento("Carta");

        List<ProductBean> products = new ArrayList<>();
        ProductBean p = new ProductBean();
        p.setName("This is a very long product name that exceeds thirty five characters");
        p.setPrice(10.0f);
        p.setQuantity(1);
        products.add(p);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("order", order);
        session.setAttribute("listaProd", products);

        mockMvc.perform(get("/fattura/download")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }

    @Test
    @DisplayName("Fattura - Download PDF con eccezione - Restituisce 500 Internal Server Error")
    void testDownloadFatturaPDFException() throws Exception {
        OrderBean order = new OrderBean();
        order.setId_ordine(1);
        order.setDataAcquisto(null); // This will cause NPE when calling toString()
        order.setMetodoPagamento("Carta");

        List<ProductBean> products = new ArrayList<>();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("order", order);
        session.setAttribute("listaProd", products);

        mockMvc.perform(get("/fattura/download")
                .session(session))
                .andExpect(status().isInternalServerError());
    }
}
