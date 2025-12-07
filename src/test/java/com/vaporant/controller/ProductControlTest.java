package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.vaporant.model.ProductBean;
import com.vaporant.repository.ProductModel;

@WebMvcTest(ProductControl.class)
class ProductControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductModel productModel;

    @Test
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
    void testDeleteProduct() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tipo", "admin");

        mockMvc.perform(post("/product")
                .session(session)
                .param("action", "delete")
                .param("id", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductViewAdmin.jsp"));

        verify(productModel).doDelete(123);
    }

    @Test
    void testInsertProduct() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tipo", "admin");

        mockMvc.perform(post("/product")
                .session(session)
                .param("action", "insert")
                .param("name", "New Product")
                .param("description", "Desc")
                .param("price", "100")
                .param("quantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductViewAdmin.jsp"));

        verify(productModel).doSave(any(ProductBean.class));
    }

    @Test
    void testSQLExceptionHandling() throws Exception {
        doThrow(new SQLException("DB Error")).when(productModel).doRetrieveAll(anyString());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tipo", "user");

        mockMvc.perform(get("/product")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductView.jsp"));
    }
}
