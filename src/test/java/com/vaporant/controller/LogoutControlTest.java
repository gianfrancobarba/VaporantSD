package com.vaporant.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LogoutControl.class)
class LogoutControlTest {

    @Autowired
    private MockMvc mockMvc;

    private LogoutControl logoutControl;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockOldSession;
    private HttpSession mockNewSession;

    @BeforeEach
    void setUp() {
        logoutControl = new LogoutControl();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockOldSession = mock(HttpSession.class);
        mockNewSession = mock(HttpSession.class);
    }

    @Test
    @DisplayName("Logout - Integration test - Redirect a ProductView")
    void testLogoutIntegration() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("ProductView.jsp"));
    }

    @Test
    @DisplayName("Logout - Unit test - Verifica invalidate e setAttribute")
    void testLogoutUnitVerifySessionCalls() throws Exception {
        // Setup: first getSession() returns old session, second returns new session
        when(mockRequest.getSession())
                .thenReturn(mockOldSession) // First call
                .thenReturn(mockNewSession); // Second call

        // Execute
        String result = logoutControl.execute(mockRequest, mockResponse);

        // Verify redirect
        assertEquals("redirect:ProductView.jsp", result);

        // Verify session.invalidate() was called on OLD session
        verify(mockOldSession, times(1)).invalidate();

        // Verify setAttribute calls on NEW session
        verify(mockNewSession, times(1)).setAttribute(eq("user"), eq(null));
        verify(mockNewSession, times(1)).setAttribute(eq("tipo"), eq(null));
        verify(mockNewSession, times(1)).setAttribute(eq("cart"), eq(null));

        // Verify getSession was called twice (once to get old, once to get new)
        verify(mockRequest, times(2)).getSession();
    }
}
