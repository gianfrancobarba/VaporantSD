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
        product.setPrice(10.0f);
        product.setQuantity(1); // Initial quantity in cart
        when(productModel.doRetrieveByKey(1)).thenReturn(product);

        Cart cart = new Cart();
        // Add product with initial quantity = 1
        ProductBean cartProduct = new ProductBean();
        cartProduct.setCode(1);
        cartProduct.setPrice(10.0f);
        cartProduct.setQuantity(1);
        cart.addProduct(cartProduct);

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

        // Verify Session State - quantity and price updated
        // Cart.aggiorna() updates product quantity and recalculates total
        Cart updatedCart = (Cart) session.getAttribute("cart");
        assertEquals(1, updatedCart.getProducts().size(), "Cart should still have 1 product");
        assertEquals(5, updatedCart.getProducts().get(0).getQuantity(), "Quantity should be updated to 5");
        // Total = (10.0 * 5) = 50.0
        assertEquals(50.0, updatedCart.getPrezzoTotale(), 0.01, "Total price should be 5 x 10.0 = 50");
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

    @ParameterizedTest(name = "Update quantity to {0}")
    @ValueSource(ints = { 1, 2, 5, 10 })
    @DisplayName("Cart - Update quantity con boundary values - verifica calcolo prezzo")
    void testUpdateQuantityBoundaries(int newQuantity) throws Exception {
        ProductBean product = new ProductBean();
        product.setCode(1);
        product.setPrice(10.0f);
        product.setQuantity(20); // Large stock available
        when(productModel.doRetrieveByKey(1)).thenReturn(product);

        // Setup cart with initial product (quantity=3, price=10)
        Cart cart = new Cart();
        ProductBean cartProduct = new ProductBean();
        cartProduct.setCode(1);
        cartProduct.setPrice(10.0f);
        cartProduct.setQuantity(3); // Initial quantity
        cart.getProducts().add(cartProduct);
        cart.setPrezzoTotale(30.0); // Initial total = 3 * 10

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("cart", cart);
        session.setAttribute("user", new UserBean());

        mockMvc.perform(post("/cart")
                .session(session)
                .param("action", "aggiorna")
                .param("id", "1")
                .param("quantita", String.valueOf(newQuantity)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("CartView.jsp"));

        Cart updatedCart = (Cart) session.getAttribute("cart");

        // Cart.aggiorna() always updates quantity regardless of value
        // It recalculates: newTotal = oldTotal - (oldQty * price) + (newQty * price)
        // = 30 - (3 * 10) + (newQuantity * 10)
        // = 30 - 30 + (newQuantity * 10)
        // = newQuantity * 10
        assertEquals(1, updatedCart.getProducts().size(), "Cart should have 1 product");
        assertEquals(newQuantity, updatedCart.getProducts().get(0).getQuantity(),
                "Quantity should be updated to " + newQuantity);

        double expectedTotal = newQuantity * 10.0;
        assertEquals(expectedTotal, updatedCart.getPrezzoTotale(), 0.01,
                "Total should be " + newQuantity + " x 10.0 = " + expectedTotal);
    }

    @Test
    @DisplayName("Cart - Delete product con indice boundary (primo prodotto)")
    void testDeleteFirstProduct() throws Exception {
        ProductBean product1 = new ProductBean();
        product1.setCode(1);
        product1.setPrice(10.0f);
        ProductBean product2 = new ProductBean();
        product2.setCode(2);
        product2.setPrice(20.0f);

        when(productModel.doRetrieveByKey(1)).thenReturn(product1);

        Cart cart = new Cart();
        cart.addProduct(product1);
        cart.addProduct(product2);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("cart", cart);
        session.setAttribute("user", new UserBean());

        mockMvc.perform(post("/cart")
                .session(session)
                .param("action", "deleteC")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("CartView.jsp"));

        Cart updatedCart = (Cart) session.getAttribute("cart");
        assertEquals(1, updatedCart.getProducts().size(),
                "Cart should have 1 product after deleting first");
        assertEquals(2, updatedCart.getProducts().get(0).getCode(),
                "Remaining product should be product2");
        assertEquals(20.0, updatedCart.getPrezzoTotale(), 0.01,
                "Total price should be updated to 20.0");
    }
}
