package com.vaporant.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    @InjectMocks
    private CustomErrorController controller;

    @Test
    @DisplayName("Error - HTTP 404 - Mostra pagina errore 'Pagina non trovata'")
    void testHandleError404() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);

        String viewName = controller.handleError(request);

        assertEquals("error", viewName);
        verify(request).setAttribute("error.status.code", 404);
        verify(request).setAttribute("error.message", "Pagina non trovata");
    }

    @Test
    @DisplayName("Error - HTTP 500 - Mostra pagina errore 'Errore interno del server'")
    void testHandleError500() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);

        String viewName = controller.handleError(request);

        assertEquals("error", viewName);
        verify(request).setAttribute("error.status.code", 500);
        verify(request).setAttribute("error.message", "Errore interno del server");
    }

    @Test
    @DisplayName("Error - HTTP 403 - Mostra pagina errore generico")
    void testHandleErrorOther() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(403);

        String viewName = controller.handleError(request);

        assertEquals("error", viewName);
        verify(request).setAttribute("error.status.code", 403);
        verify(request).setAttribute("error.message", "Si è verificato un errore imprevisto");
    }

    @Test
    @DisplayName("Error - Status code null - Mostra error view senza attributi")
    void testHandleErrorNullStatus() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);

        String viewName = controller.handleError(request);

        assertEquals("error", viewName);
        // Should not set attributes
    }
}
