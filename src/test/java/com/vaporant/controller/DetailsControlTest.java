package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.vaporant.model.ProductBean;
import com.vaporant.repository.ProductModel;

@WebMvcTest(DetailsControl.class)
class DetailsControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductModel productModel;

    @Test
    @DisplayName("Details - Lettura dettagli prodotto - Redirect a DetailsView")
    void testReadDetails() throws Exception {
        ProductBean product = new ProductBean();
        product.setCode(1);
        when(productModel.doRetrieveByKey(1)).thenReturn(product);

        mockMvc.perform(get("/details")
                .param("action", "read")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("DetailsView.jsp"));

        verify(productModel).doRetrieveByKey(1);
    }

    @Test
    @DisplayName("Details - Nessuna action specificata - Redirect a DetailsView")
    void testNoAction() throws Exception {
        mockMvc.perform(get("/details"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("DetailsView.jsp"));
    }

    @Test
    @DisplayName("Details - SQLException durante lettura dettagli - Gestione errore gracefully")
    void testSQLException() throws Exception {
        when(productModel.doRetrieveByKey(anyInt())).thenThrow(new SQLException("DB Error"));

        mockMvc.perform(get("/details")
                .param("action", "read")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("DetailsView.jsp"));
    }

    @Test
    @DisplayName("Details - Parametro ID non numerico - Operazione saltata (graceful reload)")
    void testInvalidId() throws Exception {
        mockMvc.perform(get("/details")
                .param("action", "read")
                .param("id", "invalid"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("DetailsView.jsp"));
    }
}
