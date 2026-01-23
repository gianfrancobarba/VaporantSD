package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
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
import com.vaporant.model.Cart;
import com.vaporant.model.ContenutoBean;
import com.vaporant.model.ProductBean;
import com.vaporant.model.UserBean;
import com.vaporant.repository.AddressDAO;
import com.vaporant.repository.ContenutoDAO;
import com.vaporant.repository.OrderDAO;
import com.vaporant.repository.ProductModel;
import com.vaporant.repository.UserDAO;

@WebMvcTest(OrderControl.class)
class OrderControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderDAO orderDao;
    @MockBean
    private ContenutoDAO contDao;
    @MockBean
    private UserDAO userDao;
    @MockBean
    private AddressDAO addressDao;
    @MockBean
    private ProductModel productDao;

    @Test
    @DisplayName("Order - Flow checkout completo salva ordine e contenuti")
    void testOrderCreationSuccess() throws Exception {
        // Arrange
        UserBean user = createTestUser();
        Cart cart = createTestCart();

        AddressBean addressMock = org.mockito.Mockito.mock(AddressBean.class);
        when(addressMock.toStringScript()).thenReturn("Via Roma, 10");

        when(addressDao.findAddressByID(anyInt())).thenReturn(addressMock);
        when(orderDao.getIdfromDB()).thenReturn(1);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        session.setAttribute("cart", cart);

        // Act & Assert
        mockMvc.perform(post("/Ordine")
                .session(session)
                .param("payment", "PayPal")
                .param("addressDropdown", "4"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ordine.jsp"));

        // Verify flow completo

        verify(orderDao).saveOrder(argThat(order -> order.getId_utente() == user.getId() &&
                order.getMetodoPagamento().equals("PayPal") &&
                order.getId_indirizzo() == 4 &&
                Math.abs(order.getPrezzoTot() - cart.getPrezzoTotale()) < 0.01));

        verify(orderDao).getIdfromDB();
        verify(contDao, times(cart.getProducts().size())).saveContenuto(any(ContenutoBean.class));

        // === Verify EXACT quantity calculations ===
        // Product 1: storage=100, ordered=2 → remaining = 100 - 2 = 98
        verify(productDao).updateQuantityStorage(
                argThat(p -> p.getCode() == 1),
                eq(98) // EXACT VALUE: 100 - 2 = 98
        );

        // Product 2: storage=50, ordered=1 → remaining = 50 - 1 = 49
        verify(productDao).updateQuantityStorage(
                argThat(p -> p.getCode() == 2),
                eq(49) // EXACT VALUE: 50 - 1 = 49
        );
    }

    @Test
    @DisplayName("Order - SQLException durante creazione ordine - Gestione errore gracefully")
    void testOrderCreationExceptions() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        Cart cart = new Cart();

        // Simulate SQLException during saveOrder
        // Since we are mocking, we can't easily throw checked SQLException from void
        // method without more setup,
        // but we can throw RuntimeException or mock the behavior.
        // Actually saveOrder throws SQLException, so we can use doThrow.

        org.mockito.Mockito.doThrow(new SQLException("Simulated SQL Error")).when(orderDao).saveOrder(any());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        session.setAttribute("cart", cart);

        mockMvc.perform(post("/Ordine")
                .session(session)
                .param("payment", "Carta di credito/debito")
                .param("addressDropdown", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ordine.jsp"));
    }

    @Test
    @DisplayName("Order - Sessione mancante (user) - Gestione gracefully (NPE)")
    void testOrderMissingUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("cart", new Cart());
        // Missing "user"

        mockMvc.perform(post("/Ordine")
                .session(session)
                .param("payment", "PayPal")
                .param("addressDropdown", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Order - Parametro indirizzo non numerico - Gestione gracefully (NFE)")
    void testOrderInvalidAddressId() throws Exception {
        UserBean user = createTestUser();
        Cart cart = createTestCart();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        session.setAttribute("cart", cart);

        mockMvc.perform(post("/Ordine")
                .session(session)
                .param("payment", "PayPal")
                .param("addressDropdown", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // Helper methods
    private Cart createTestCart() {
        Cart cart = new Cart();

        // Product 1
        ProductBean p1 = new ProductBean();
        p1.setCode(1);
        p1.setName("Product 1");
        p1.setPrice(10.0f);
        p1.setQuantity(2);
        p1.setQuantityStorage(100);
        p1.setDescription("Desc 1");
        cart.addProduct(p1);

        // Product 2
        ProductBean p2 = new ProductBean();
        p2.setCode(2);
        p2.setName("Product 2");
        p2.setPrice(15.0f);
        p2.setQuantity(1);
        p2.setQuantityStorage(50);
        p2.setDescription("Desc 2");
        cart.addProduct(p2);

        return cart; // Total: 2*10 + 1*15 = 35.0
    }

    private UserBean createTestUser() {
        UserBean user = new UserBean();
        user.setId(1);
        user.setNome("Test");
        user.setCognome("User");
        user.setEmail("test@test.com");
        return user;
    }
}
