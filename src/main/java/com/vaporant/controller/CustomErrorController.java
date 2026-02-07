package com.vaporant.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;

@Controller
public class CustomErrorController implements ErrorController {
    private static final String ERROR_STATUS_CODE = "error.status.code";
    private static final String ERROR_MESSAGE = "error.message";

    @org.springframework.web.bind.annotation.GetMapping("/error")
    public String handleErrorGet(HttpServletRequest request) {
        return processError(request);
    }

    @org.springframework.web.bind.annotation.PostMapping("/error")
    public String handleErrorPost(HttpServletRequest request) {
        return processError(request);
    }

    private String processError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            request.setAttribute(ERROR_STATUS_CODE, statusCode);

            if (statusCode == 404) {
                request.setAttribute(ERROR_MESSAGE, "Pagina non trovata");
            } else if (statusCode == 500) {
                request.setAttribute(ERROR_MESSAGE, "Errore interno del server");
            } else {
                request.setAttribute(ERROR_MESSAGE, "Si è verificato un errore imprevisto");
            }
        }
        return "error";
    }
}
