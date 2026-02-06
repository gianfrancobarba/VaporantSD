package com.vaporant.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.vaporant.model.UserBean;
import com.vaporant.repository.UserDAO;

@WebMvcTest(ModifyControl.class)
class ModifyControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDao;

    // For unit tests
    private ModifyControl modifyControl;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter mockWriter;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() {
        modifyControl = new ModifyControl();
        // Use reflection to inject the mocked UserDAO
        try {
            java.lang.reflect.Field field = ModifyControl.class.getDeclaredField("need");
            field.setAccessible(true);
            field.set(modifyControl, userDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        stringWriter = new StringWriter();
        mockWriter = spy(new PrintWriter(stringWriter));
    }

    // ========== INTEGRATION TESTS (MockMvc) ==========

    @Test
    @DisplayName("Modify - Modifica email utente - Restituisce JSON con nuova email")
    void testModifyEmail() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        user.setEmail("new@test.com");

        when(userDao.findById(1)).thenReturn(user);

        MockHttpSession session = spy(new MockHttpSession());
        session.setAttribute("user", user);

        MvcResult result = mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "modificaEmail")
                .param("nuovaEmail", "new@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"email\": \"new@test.com\" }"))
                .andReturn();

        verify(userDao).modifyMail(any(UserBean.class), eq("new@test.com"));

        MockHttpServletResponse response = result.getResponse();
        String contentType = response.getContentType();
        assertNotNull(contentType);
        assertTrue(contentType.contains("application/json"));
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Modify - Modifica telefono utente - Restituisce JSON con nuovo telefono")
    void testModifyTelefono() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        user.setNumTelefono("987654321");

        when(userDao.findById(1)).thenReturn(user);

        MockHttpSession session = spy(new MockHttpSession());
        session.setAttribute("user", user);

        MvcResult result = mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "modificaTelefono")
                .param("nuovoTelefono", "987654321"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"numTelefono\": \"987654321\" }"))
                .andReturn();

        verify(userDao).modifyTelefono(any(UserBean.class), eq("987654321"));

        MockHttpServletResponse response = result.getResponse();
        String contentType = response.getContentType();
        assertNotNull(contentType);
        assertTrue(contentType.contains("application/json"));
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Modify - Modifica password con successo - Restituisce success true")
    void testModifyPasswordSuccess() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);

        when(userDao.modifyPsw(anyString(), anyString(), any(UserBean.class))).thenReturn(1);

        MockHttpSession session = spy(new MockHttpSession());
        session.setAttribute("user", user);

        MvcResult result = mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "modificaPassword")
                .param("nuovaPassword", "newPass")
                .param("vecchiaPassword", "oldPass"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\": true}"))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String contentType = response.getContentType();
        assertNotNull(contentType);
        assertTrue(contentType.contains("application/json"));
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Modify - Modifica password fallita - Restituisce success false")
    void testModifyPasswordFailure() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);

        when(userDao.modifyPsw(anyString(), anyString(), any(UserBean.class))).thenReturn(0);

        MockHttpSession session = spy(new MockHttpSession());
        session.setAttribute("user", user);

        MvcResult result = mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "modificaPassword")
                .param("nuovaPassword", "newPass")
                .param("vecchiaPassword", "oldPass"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\": false}"))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String contentType = response.getContentType();
        assertNotNull(contentType);
        assertTrue(contentType.contains("application/json"));
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Modify - Action sconosciuta - Restituisce Bad Request")
    void testDefaultAction() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "unknown"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Modify -Sessione utente mancante - Gestione gracefully (NPE)")
    void testModifyMissingUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        // Missing "user"

        mockMvc.perform(post("/modify")
                .session(session)
                .param("action", "modificaEmail")
                .param("nuovaEmail", "new@test.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Modify - Action null - Gestione gracefully (NPE)")
    void testModifyNullAction() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/modify")
                .session(session))
                // NO action param
                .andExpect(status().isBadRequest());
    }

    // ========== UNIT TESTS (Direct method calls with mocks) ==========

    @Test
    @DisplayName("Modify Unit - modificaEmail - Verifica tutte le chiamate void")
    void testModifyEmailUnitVerifyVoidCalls() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        user.setEmail("old@test.com");

        UserBean updatedUser = new UserBean();
        updatedUser.setId(1);
        updatedUser.setEmail("new@test.com");

        when(mockRequest.getParameter("action")).thenReturn("modificaEmail");
        when(mockRequest.getParameter("nuovaEmail")).thenReturn("new@test.com");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("user")).thenReturn(user);
        when(mockResponse.getWriter()).thenReturn(mockWriter);
        when(userDao.findById(1)).thenReturn(updatedUser);

        modifyControl.execute(mockRequest, mockResponse);

        // Verify all void method calls
        verify(mockSession, times(1)).setAttribute(eq("user"), any(UserBean.class));
        verify(mockResponse, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(mockResponse, times(1)).setContentType("application/json");
        verify(mockWriter, times(1)).flush();
        verify(userDao, times(1)).modifyMail(any(UserBean.class), eq("new@test.com"));
    }

    @Test
    @DisplayName("Modify Unit - modificaTelefono - Verifica tutte le chiamate void")
    void testModifyTelefonoUnitVerifyVoidCalls() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);
        user.setNumTelefono("123456789");

        UserBean updatedUser = new UserBean();
        updatedUser.setId(1);
        updatedUser.setNumTelefono("987654321");

        when(mockRequest.getParameter("action")).thenReturn("modificaTelefono");
        when(mockRequest.getParameter("nuovoTelefono")).thenReturn("987654321");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("user")).thenReturn(user);
        when(mockResponse.getWriter()).thenReturn(mockWriter);
        when(userDao.findById(1)).thenReturn(updatedUser);

        modifyControl.execute(mockRequest, mockResponse);

        // PHASE 2 FIX: Verify all void method calls
        verify(mockSession, times(1)).setAttribute(eq("user"), any(UserBean.class));
        verify(mockResponse, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(mockResponse, times(1)).setContentType("application/json");
        verify(mockWriter, times(1)).flush();
        verify(userDao, times(1)).modifyTelefono(any(UserBean.class), eq("987654321"));
    }

    @Test
    @DisplayName("Modify Unit - modificaPassword Success - Verifica tutte le chiamate void")
    void testModifyPasswordSuccessUnitVerifyVoidCalls() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);

        UserBean updatedUser = new UserBean();
        updatedUser.setId(1);

        when(mockRequest.getParameter("action")).thenReturn("modificaPassword");
        when(mockRequest.getParameter("nuovaPassword")).thenReturn("newPass");
        when(mockRequest.getParameter("vecchiaPassword")).thenReturn("oldPass");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("user")).thenReturn(user);
        when(mockResponse.getWriter()).thenReturn(mockWriter);
        when(userDao.modifyPsw(eq("newPass"), eq("oldPass"), any(UserBean.class))).thenReturn(1);
        when(userDao.findById(1)).thenReturn(updatedUser);

        modifyControl.execute(mockRequest, mockResponse);

        // Verify all void method calls
        verify(mockSession, times(1)).setAttribute(eq("user"), any(UserBean.class));
        verify(mockResponse, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(mockResponse, times(1)).setContentType("application/json");
        verify(mockResponse, times(1)).setCharacterEncoding("UTF-8");

        String jsonOutput = stringWriter.toString();
        assertTrue(jsonOutput.contains("\"success\": true"));
    }

    @Test
    @DisplayName("Modify Unit - modificaPassword Failure - Verifica chiamate void (NO setAttribute)")
    void testModifyPasswordFailureUnitVerifyVoidCalls() throws Exception {
        UserBean user = new UserBean();
        user.setId(1);

        when(mockRequest.getParameter("action")).thenReturn("modificaPassword");
        when(mockRequest.getParameter("nuovaPassword")).thenReturn("newPass");
        when(mockRequest.getParameter("vecchiaPassword")).thenReturn("wrongOldPass");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("user")).thenReturn(user);
        when(mockResponse.getWriter()).thenReturn(mockWriter);
        when(userDao.modifyPsw(eq("newPass"), eq("wrongOldPass"), any(UserBean.class))).thenReturn(0);

        modifyControl.execute(mockRequest, mockResponse);

        // Verify void method calls
        verify(mockSession, times(0)).setAttribute(eq("user"), any(UserBean.class));
        verify(mockResponse, times(0)).setStatus(HttpServletResponse.SC_OK);
        verify(mockResponse, times(1)).setContentType("application/json");
        verify(mockResponse, times(1)).setCharacterEncoding("UTF-8");

        String jsonOutput = stringWriter.toString();
        assertTrue(jsonOutput.contains("\"success\": false"));
    }
}
