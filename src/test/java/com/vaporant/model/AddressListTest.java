package com.vaporant.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaporant.repository.AddressDAO;
import com.vaporant.repository.AddressScript;

public class AddressListTest {

    private AddressList addressList;

    @Mock
    private AddressDAO mockDao;

    @Mock
    private UserBean mockUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test default constructor")
    public void testDefaultConstructor() {
        addressList = new AddressList();
        assertNotNull(addressList.getListaIndirizzi());
        assertNotNull(addressList.getAddressList());
        assertEquals(0, addressList.size());
    }

    @Test
    @DisplayName("Test constructor with data fetching - Success")
    public void testConstructorWithDataSuccess() throws SQLException {
        when(mockUser.getId()).thenReturn(1);

        ArrayList<AddressBean> beans = new ArrayList<>();
        AddressBean bean = new AddressBean();
        bean.setVia("Via Roma");
        bean.setNumCivico("10");
        bean.setCitta("Milano");
        bean.setProvincia("MI");
        bean.setCap("20100");
        bean.setStato("Italia");
        beans.add(bean);

        when(mockDao.findByID(1)).thenReturn(beans);

        addressList = new AddressList(mockUser, mockDao);

        assertEquals(1, addressList.size());
        // AddressScript uses AddressBean.toStringScript() for the 'indirizzo' field
        assertEquals(bean.toStringScript(), addressList.get(0).getIndirizzo());
        assertEquals(1, addressList.getAddressList().size());
    }

    @Test
    @DisplayName("Test constructor handling SQLException")
    public void testConstructorWithException() throws SQLException {
        when(mockUser.getId()).thenReturn(1);
        when(mockDao.findByID(1)).thenThrow(new SQLException("DB Error"));

        // Should handle the exception gracefully (prints stack trace internally)
        addressList = new AddressList(mockUser, mockDao);

        assertNotNull(addressList.getListaIndirizzi());
        assertEquals(0, addressList.size());
    }

    @Test
    @DisplayName("Test constructor with null Address list")
    public void testConstructorWithNullReturn() throws SQLException {
        when(mockUser.getId()).thenReturn(1);
        when(mockDao.findByID(1)).thenReturn(null);

        addressList = new AddressList(mockUser, mockDao);

        assertEquals(0, addressList.size());
    }

    @Test
    @DisplayName("Test JSON generation via reflection")
    public void testGetJson() {
        addressList = new AddressList();
        AddressBean bean = new AddressBean();
        bean.setVia("Napoli");
        addressList.add(bean);

        String json = addressList.getJson();

        assertNotNull(json);
        assertTrue(json.contains("Napoli"));
    }

    @Test
    @DisplayName("Test list operations: add, remove, get, size")
    public void testListOperations() {
        addressList = new AddressList();

        AddressBean b1 = new AddressBean();
        b1.setVia("Via A");
        AddressBean b2 = new AddressBean();
        b2.setVia("Via B");

        addressList.add(b1);
        addressList.add(b2);

        assertEquals(2, addressList.size());
        assertEquals(b1.toStringScript(), addressList.get(0).getIndirizzo());

        addressList.remove(0);
        assertEquals(1, addressList.size());
        assertEquals(b2.toStringScript(), addressList.get(0).getIndirizzo());
    }

    @Test
    @DisplayName("Test get with invalid indices")
    public void testGetInvalidIndex() {
        addressList = new AddressList();
        assertNull(addressList.get(-1));
        assertNull(addressList.get(0));

        addressList.add(new AddressBean());
        assertNull(addressList.get(1));
    }

    @Test
    @DisplayName("Test setter for listaIndirizzi")
    public void testSetListaIndirizzi() {
        addressList = new AddressList();
        ArrayList<AddressScript> newList = new ArrayList<>();
        newList.add(new AddressScript(new AddressBean()));

        addressList.setListaIndirizzi(newList);
        assertEquals(newList, addressList.getListaIndirizzi());
    }
}
