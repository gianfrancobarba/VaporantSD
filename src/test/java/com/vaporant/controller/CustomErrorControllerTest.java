package com.vaporant.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    @InjectMocks
    private CustomErrorController controller;

    @Test
    void testHandleError404() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);

        String viewName = controller.handleError(request);

        assertEquals("error", viewName);
        verify(request).setAttribute("errorCode", 404);
        verify(request).setAttribute("errorMessage", "Pagina non trovata");
    }

    @Test
    void testHandleError500() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);

        String viewName = controller.handleError(request);

        assertEquals("error", viewName);
        verify(request).setAttribute("errorCode", 500);
        verify(request).setAttribute("errorMessage", "Errore interno del server");
    }

    @Test
    void testHandleErrorOther() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(403);

        String viewName = controller.handleError(request);

        assertEquals("error", viewName);
        verify(request).setAttribute("errorCode", 403);
        verify(request).setAttribute("errorMessage", "Si è verificato un errore imprevisto");
    }

    @Test
    void testHandleErrorNullStatus() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);

        String viewName = controller.handleError(request);

        assertEquals("error", viewName);
        // Should not set attributes
    }
}
