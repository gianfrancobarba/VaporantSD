package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.vaporant.repository.ProductModel;

@WebMvcTest(ProductControl.class)
class ProductControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductModel productModel;

    @Test
    @DisplayName("Product - Lista prodotti per utente - Redirect a ProductView")
    void testListProductsUser() throws Exception {
        when(productModel.doRetrieveAll(anyString())).thenReturn(new ArrayList<>());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tipo", "user");

        mockMvc.perform(get("/product")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductView.jsp"));

        verify(productModel).doRetrieveAll(null);
    }

    @Test
    @DisplayName("Product - Lista prodotti per admin - Redirect a ProductViewAdmin")
    void testListProductsAdmin() throws Exception {
        when(productModel.doRetrieveAll(anyString())).thenReturn(new ArrayList<>());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tipo", "admin");

        mockMvc.perform(get("/product")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductViewAdmin.jsp"));
    }

    @Test
    @DisplayName("Product - action=delete rimuove prodotto")
    void testDeleteProduct() throws Exception {
        // Arrange
        int productId = 1;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tipo", "admin");

        when(productModel.doRetrieveAll(null)).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(post("/product")
                .session(session)
                .param("action", "delete")
                .param("id", String.valueOf(productId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductViewAdmin.jsp"));

        verify(productModel).doDelete(productId);
        verify(productModel).doRetrieveAll(null);
    }

    @Test
    @DisplayName("Product - action=insert aggiunge prodotto")
    void testInsertProduct() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tipo", "admin");

        when(productModel.doRetrieveAll(null)).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(post("/product")
                .session(session)
                .param("action", "insert")
                .param("name", "New Product")
                .param("description", "Test description")
                .param("price", "29")
                .param("quantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductViewAdmin.jsp"));

        verify(productModel).doSave(argThat(product -> product.getName().equals("New Product") &&
                product.getDescription().equals("Test description") &&
                product.getPrice() == 29.0f &&
                product.getQuantityStorage() == 10));
        verify(productModel).doRetrieveAll(null);
    }

    @Test
    @DisplayName("Product - SQLException durante retrieve - Gestione errore gracefully")
    void testSQLExceptionHandling() throws Exception {
        doThrow(new SQLException("DB Error")).when(productModel).doRetrieveAll(anyString());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tipo", "user");

        mockMvc.perform(get("/product")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductView.jsp"));
    }

    @Test
    @DisplayName("Product - SQLException in delete gestita correttamente")
    void testDeleteThrowsSQLException_handlesGracefully() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tipo", "admin");

        doThrow(new SQLException("Database error")).when(productModel).doDelete(anyInt());
        when(productModel.doRetrieveAll(null)).thenReturn(new ArrayList<>());

        // Act
        mockMvc.perform(post("/product")
                .session(session)
                .param("action", "delete")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductViewAdmin.jsp"));

        // Assert: SQLException caught, doRetrieveAll ancora chiamato
        verify(productModel).doDelete(1);
        verify(productModel).doRetrieveAll(null);
    }
}
