package com.vaporant.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.vaporant.model.Cart;
import com.vaporant.model.ProductBean;
import com.vaporant.model.UserBean;
import com.vaporant.repository.ProductModel;

@WebMvcTest(CartControl.class)
class CartControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductModel productModel;

    @Test
    @DisplayName("Cart - Aggiunta prodotto al carrello - Redirect a CartView")
    void testAddProductToCart() throws Exception {
        ProductBean product = new ProductBean();
        product.setCode(1);
        product.setPrice(10.0f);
        when(productModel.doRetrieveByKey(1)).thenReturn(product);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new UserBean());

        mockMvc.perform(post("/cart")
                .session(session)
                .param("action", "addC")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("CartView.jsp"));

        verify(productModel).doRetrieveByKey(1);

        // Verify Session State (kill logic mutants)
        Cart updatedCart = (Cart) session.getAttribute("cart");
        assertEquals(1, updatedCart.getProducts().size(), "Cart should contain 1 product");
        assertEquals(1, updatedCart.getProducts().get(0).getCode(), "Product ID matches");
    }

    @Test
    @DisplayName("Cart - Rimozione prodotto dal carrello - Redirect a CartView")
    void testDeleteProductFromCart() throws Exception {
        ProductBean product = new ProductBean();
        product.setCode(1);
        when(productModel.doRetrieveByKey(1)).thenReturn(product);

        Cart cart = new Cart();
        cart.addProduct(product);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("cart", cart);
        session.setAttribute("user", new UserBean());

        mockMvc.perform(post("/cart")
                .session(session)
                .param("action", "deleteC")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("CartView.jsp"));

        verify(productModel).doRetrieveByKey(1);

        // Verify Session State
        Cart updatedCart = (Cart) session.getAttribute("cart");
        assertEquals(0, updatedCart.getProducts().size(), "Cart should be empty after delete");
    }

    @Test
    @DisplayName("Cart - Aggiornamento quantità prodotto - Redirect a CartView")
    void testUpdateQuantity() throws Exception {
        ProductBean product = new ProductBean();
        product.setCode(1);
        when(productModel.doRetrieveByKey(1)).thenReturn(product);

        Cart cart = new Cart();
        cart.addProduct(product);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("cart", cart);
        session.setAttribute("user", new UserBean());

        mockMvc.perform(post("/cart")
                .session(session)
                .param("action", "aggiorna")
                .param("id", "1")
                .param("quantita", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("CartView.jsp"));
    }

    @Test
    @DisplayName("Cart - Aggiornamento quantità e checkout - Redirect diretto a checkout")
    void testUpdateQuantityAndCheckout() throws Exception {
        ProductBean product = new ProductBean();
        product.setCode(1);
        when(productModel.doRetrieveByKey(1)).thenReturn(product);

        Cart cart = new Cart();
        cart.addProduct(product);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("cart", cart);
        session.setAttribute("user", new UserBean());

        mockMvc.perform(post("/cart")
                .session(session)
                .param("action", "aggiornaCheck")
                .param("id", "1")
                .param("quantita", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("checkout.jsp"));
    }

    @Test
    @DisplayName("Cart - Checkout diretto senza modifiche - Redirect a checkout")
    void testDirectCheckout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new UserBean());

        mockMvc.perform(post("/cart")
                .session(session)
                .param("action", "checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("checkout.jsp"));
    }

    @Test
    @DisplayName("Cart - Nessuna action specificata - Redirect a CartView")
    void testNoAction() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new UserBean());

        mockMvc.perform(post("/cart")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("CartView.jsp"));
    }

    @Test
    @DisplayName("Cart - SQLException durante recupero prodotto - Gestione errore gracefully")
    void testSQLException() throws Exception {
        when(productModel.doRetrieveByKey(anyInt())).thenThrow(new SQLException("DB Error"));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new UserBean());

        mockMvc.perform(post("/cart")
                .session(session)
                .param("action", "addC")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("CartView.jsp"));
    }

    @ParameterizedTest(name = "Cart con {0} prodotti calcola totale correttamente")
    @ValueSource(ints = { 1, 5, 10, 20 })
    @DisplayName("Cart - Calcolo prezzo totale con diverse quantità prodotti")
    void testCartTotalWithMultipleProducts(int productCount) {
        // Arrange
        Cart cart = new Cart();
        double expectedTotal = 0;

        for (int i = 0; i < productCount; i++) {
            ProductBean p = new ProductBean();
            p.setCode(i);
            p.setName("Product" + i);
            p.setPrice(10.0f);
            p.setQuantity(1);
            cart.addProduct(p);
            expectedTotal += 10.0;
        }

        // Assert
        assertEquals(expectedTotal, cart.getPrezzoTotale(), 0.01,
                "Totale deve essere somma di tutti i prodotti: " + productCount + " prodotti x 10.00 = "
                        + expectedTotal);
    }
}
