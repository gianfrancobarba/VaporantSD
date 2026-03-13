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

    @org.junit.jupiter.params.ParameterizedTest(name = "Error - HTTP {0} - Mostra pagina errore ''{1}''")
    @org.junit.jupiter.params.provider.CsvSource({
        "404, Pagina non trovata",
        "500, Errore interno del server",
        "403, Si è verificato un errore imprevisto"
    })
    void testHandleErrorParameterized(int statusCode, String expectedMessage) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(statusCode);

        String viewName = controller.handleErrorGet(request);

        assertEquals("error", viewName);
        verify(request).setAttribute("error.status.code", statusCode);
        verify(request).setAttribute("error.message", expectedMessage);
    }

    @Test
    @DisplayName("Error - Status code null - Mostra error view senza attributi")
    void testHandleErrorNullStatus() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);

        String viewName = controller.handleErrorGet(request);

        assertEquals("error", viewName);
        // Should not set attributes
    }

    @Test
    @DisplayName("Error POST - HTTP 500 - Mostra pagina errore 'Errore interno del server'")
    void testHandleErrorPost() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);

        String viewName = controller.handleErrorPost(request);

        assertEquals("error", viewName);
        verify(request).setAttribute("error.status.code", 500);
        verify(request).setAttribute("error.message", "Errore interno del server");
    }
}
