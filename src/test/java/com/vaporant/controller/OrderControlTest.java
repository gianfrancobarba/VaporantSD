package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import com.vaporant.model.OrderBean;
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
    @DisplayName("Order - Creazione ordine con successo - Salva ordine e contenuti")
    void testOrderCreationSuccess() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);

        Cart cart = new Cart();
        ProductBean product = new ProductBean();
        product.setCode(1);
        product.setPrice(10.0f);
        product.setQuantity(1);
        product.setQuantityStorage(10);
        product.setName("Product 1");
        product.setDescription("Desc");
        cart.addProduct(product);

        AddressBean address = org.mockito.Mockito.mock(AddressBean.class);
        when(address.toStringScript()).thenReturn("Via Roma, 10");

        when(addressDao.findAddressByID(anyInt())).thenReturn(address);
        when(orderDao.getIdfromDB()).thenReturn(1);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        session.setAttribute("cart", cart);

        mockMvc.perform(post("/Ordine")
                .session(session)
                .param("payment", "Carta")
                .param("addressDropdown", "1")
                .param("addressDropdown2", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ordine.jsp"));

        verify(userDao).updateAddress(anyString(), eq(user));
        verify(orderDao).saveOrder(any(OrderBean.class));
        verify(contDao).saveContenuto(any(ContenutoBean.class));
        verify(productDao).updateQuantityStorage(any(ProductBean.class), anyInt());
    }

    @Test
    @DisplayName("Order - SQLException durante creazione ordine - Gestione errore gracefully")
    void testOrderCreationExceptions() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        Cart cart = new Cart();

        when(addressDao.findAddressByID(anyInt())).thenThrow(new SQLException("DB Error"));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        session.setAttribute("cart", cart);

        mockMvc.perform(post("/Ordine")
                .session(session)
                .param("payment", "Carta")
                .param("addressDropdown", "1")
                .param("addressDropdown2", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ordine.jsp"));
    }
}
